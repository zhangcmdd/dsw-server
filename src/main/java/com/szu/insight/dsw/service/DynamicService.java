package com.szu.insight.dsw.service;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import com.alibaba.druid.pool.DruidDataSource;
import com.szu.insight.dsw.dao.DataSourceConfigDao;
import com.szu.insight.dsw.dao.StatementConfigDao;
import com.szu.insight.dsw.domain.DataSourceConfig;

@Service
public class DynamicService {

	@Autowired
	private DataSourceConfigDao dataSourceConfigDao;
	
	@Autowired
	private StatementConfigDao queryConfigDao;
	
	public DataSource getDataSource(String alias) throws ClassNotFoundException{
		DataSourceConfig config = dataSourceConfigDao.getByAlias(alias);
		
		DataSourceBuilder builder = DataSourceBuilder.create()
				.driverClassName(config.getDriver())
				.url(config.getUrl())
				.username(config.getUsername())
				.password(config.getPassword())
				.type((Class<? extends DataSource>) Class.forName(config.getType()));
		DruidDataSource ds = (DruidDataSource)builder.build();
		ds.setInitialSize(config.getInitialSize());
		ds.setMinIdle(config.getMinIdle());
		ds.setMaxIdle(config.getMaxIdle());
		ds.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
		ds.setValidationQuery(config.getValidationQuery());
		ds.setTestWhileIdle(config.isTestWhileIdle());
		ds.setPoolPreparedStatements(config.isPoolPreparedStatements());
		ds.setMaxPoolPreparedStatementPerConnectionSize(config.getMaxPoolPreparedStatementPerConnectionSize());
	
		return ds;
	}
	
}
