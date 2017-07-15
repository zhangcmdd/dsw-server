package com.szu.insight.dsw.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import com.szu.insight.dsw.domain.StatementConfig;
import com.szu.insight.dsw.mybatis.dynamic.DynamicSqlSessionTemplate;

@Service
public class DynamicSqlSessionFactoryBean {
	
	@Autowired
	private DynamicService dynamicService;
	
	@Autowired
	private DataSourceStatementService statementLoader;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Value("${mybatis.dynamic.config}")
	private String config;
	
	
	private Map<String , DynamicSqlSessionFactoryEntity> entities = new HashMap<String, DynamicSqlSessionFactoryBean.DynamicSqlSessionFactoryEntity>();

	public boolean hasSqlSession(String dsId){
		return entities.containsKey(dsId);
	}
	
	public SqlSession getWithCreateSqlSession(String dsId) throws Exception{
		if(!hasSqlSession(dsId)){
			createSqlSessionFactory(dsId);
		}
		return getSqlSession(dsId);
	}
	
	public DynamicSqlSessionTemplate getSqlSession(String dsId){
		return entities.get(dsId).getSqlSession(); 
	}
	
	public SqlSessionFactory getSqlSessionFactory(Long dsId){
		return entities.get(dsId).getSqlSessionFactory();
	}

	/**
	 * 添加分页功能
	 * @param factory
	 */
	private void createPageInterceptor(SqlSessionFactoryBean factory){
		PageInterceptor pageInterceptor = new PageInterceptor();
		Properties pageProperites = new Properties();
		//pageProperites.setProperty("helperDialect", "mysql");
		pageProperites.setProperty("autoRuntimeDialect", "true");
		pageInterceptor.setProperties(pageProperites);
		Interceptor[] plugins = new PageInterceptor[1];
		plugins[0] = pageInterceptor;
		factory.setPlugins(plugins);
	}
	
	private void createSqlSessionFactory(String dsId) throws Exception{
		DataSource dataSource = dynamicService.getDataSource(dsId);
		if(dataSource == null)
			throw new Exception("Not found the DataSource");

		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setVfs(SpringBootVFS.class);
		
		factory.setConfigLocation(this.resourceLoader.getResource(config));
		
		createPageInterceptor(factory);
		
		SqlSessionFactory sqlSessionFactory = factory.getObject();
	
		DynamicSqlSessionTemplate sqlSession = createSqlSession(sqlSessionFactory);
		
		DynamicSqlSessionFactoryEntity entity = new DynamicSqlSessionFactoryEntity();
		entity.setSqlSession(sqlSession);
		entity.setSqlSessionFactory(sqlSessionFactory);
		entity.setSqlSessionFactoryBean(factory);
		entities.put(dsId, entity);
	}
	public void replaceDataSource(String dsAlias) throws ClassNotFoundException{
		DynamicSqlSessionFactoryEntity entity = entities.get(dsAlias);
		if(entity != null){
			SqlSessionFactory sessionFactory = entity.getSqlSessionFactory();
			Configuration configuration = sessionFactory.getConfiguration();
			Environment env = configuration.getEnvironment();
			//close the origninal datasource
			((DruidDataSource)env.getDataSource()).close();
			
			DataSource dataSource = dynamicService.getDataSource(dsAlias);
			
			//set new datasource
			configuration.setEnvironment(new Environment(env.getId(), env.getTransactionFactory(), dataSource));
		}
	}
	private DynamicSqlSessionTemplate createSqlSession(SqlSessionFactory sqlSessionFactory){
		return new DynamicSqlSessionTemplate(sqlSessionFactory , statementLoader);
	}
	
	public void clearStatement(String dsAlias , String qAlias){
		getSqlSession(dsAlias).clearStatement(qAlias);
	}
	
	public void replaceStatement(StatementConfig config , StatementConfig old){
		for(DynamicSqlSessionFactoryEntity entity : entities.values()){
			replaceStatement(entity.getSqlSession() , config , old);
		}
	}
	
	public void clearStatement(String alias){
		for(DynamicSqlSessionFactoryEntity entity : entities.values()){
			clearStatment(entity.getSqlSession() , alias);
		}
	}
	public void clearStatment(DynamicSqlSessionTemplate sqlSession , String alias){
		sqlSession.clearStatement(alias);
	}
	private void replaceStatement(DynamicSqlSessionTemplate sqlSession , StatementConfig config , StatementConfig old){
		sqlSession.replaceStatement(config , old);
	}
	
	public class DynamicSqlSessionFactoryEntity{
		
		private DynamicSqlSessionTemplate sqlSession;
		
		private SqlSessionFactory sqlSessionFactory;
		
		private SqlSessionFactoryBean sqlSessionFactoryBean;

		public DynamicSqlSessionTemplate getSqlSession() {
			return sqlSession;
		}

		public void setSqlSession(DynamicSqlSessionTemplate sqlSession) {
			this.sqlSession = sqlSession;
		}

		public SqlSessionFactory getSqlSessionFactory() {
			return sqlSessionFactory;
		}

		public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
			this.sqlSessionFactory = sqlSessionFactory;
		}

		public SqlSessionFactoryBean getSqlSessionFactoryBean() {
			return sqlSessionFactoryBean;
		}

		public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
			this.sqlSessionFactoryBean = sqlSessionFactoryBean;
		}
		
		
	}
}
