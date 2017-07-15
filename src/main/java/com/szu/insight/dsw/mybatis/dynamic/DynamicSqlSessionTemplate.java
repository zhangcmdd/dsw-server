package com.szu.insight.dsw.mybatis.dynamic;

import java.util.List;

import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

import com.szu.insight.dsw.domain.StatementConfig;
/**
 * 
 * @author Cloud
 *
 */
public class DynamicSqlSessionTemplate extends SqlSessionTemplate {
	private String ns = "com.szu.insight.mapper.dynamic";
	private StatementLoader statementLoader;
	
	private Configuration configuration;
	
	public DynamicSqlSessionTemplate(SqlSessionFactory sqlSessionFactory , StatementLoader statementLoader) {
		super(sqlSessionFactory);
		this.configuration = sqlSessionFactory.getConfiguration();
		this.statementLoader = statementLoader;
	}
	
	public <E> List<E> selectList(String statement) {
		loadStatement(statement);
		return super.selectList(statement);
	}
	
	public <E> List<E> selectList(String statement , Object parameter) {
		loadStatement(statement);
		return super.selectList(statement, parameter);
	}
	
	public <T> T selectOne(String statement) {
		loadStatement(statement);
		return super.selectOne(statement);
	}
	  
	public <T> T selectOne(String statement, Object parameter) {
		loadStatement(statement);
	    return super.selectOne(statement, parameter);
	}
	
	public void loadStatement(String statement){
		
		if(!configuration.hasStatement(statement)){
			String xml = statementLoader.getXml(statement);
			loadFromXml(statement, xml);
		}
	}
	
	public void loadFromXml(String statement , String xml){
		if(!configuration.hasStatement(statement)){
			XPathParser parser = new XPathParser(xml);
			MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(configuration, "com/szu/insight/dsw/mapper/DynamicMapper.xml");
			builderAssistant.setCurrentNamespace(ns);
			buildStatement(parser.evalNodes("select|insert|update|delete") , builderAssistant);	
		}
	}
	
	private void buildStatement(List<XNode> list , MapperBuilderAssistant builderAssistant){
		for(XNode context : list){
			final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context, null);
			try {
		        statementParser.parseStatementNode();
		    } catch (IncompleteElementException e) {
		        configuration.addIncompleteStatement(statementParser);
		    }
		}
	}
	
	public void clearStatement(String id){
		//clear short
		if(configuration.hasStatement(id)){
			MappedStatement stmt1 = configuration.getMappedStatement(id, false);
			
			String key = ns + "." + id;
			MappedStatement stmt2 = configuration.getMappedStatement(key, false);
			
			if(stmt1 != null)
				configuration.getMappedStatements().remove(stmt1);
			
			if(stmt1 != null){
				configuration.getMappedStatements().remove(stmt2);
			}
		}
		
	}
	
	
	public void replaceStatement(StatementConfig config , StatementConfig old){
		clearStatement(old.getAlias());
		loadStatement(config.getAlias());
	}

}
