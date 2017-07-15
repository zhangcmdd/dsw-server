package com.szu.insight.dsw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szu.insight.dsw.dao.StatementConfigDao;
import com.szu.insight.dsw.domain.StatementConfig;
import com.szu.insight.dsw.mybatis.dynamic.StatementLoader;

/**
 * 
 * @author Cloud
 *
 */
@Service("statementLoader")
public class DataSourceStatementService implements StatementLoader {

	@Autowired
	private StatementConfigDao statementConfigDao;
	@Override
	public String getXml(String alias) {
		String key = getQueryId(alias);
		StatementConfig queryConfig = statementConfigDao.getByAlias(key);
		if(queryConfig == null){
			throw new Error("Not found the Query");
		}
		return buildXml(queryConfig);
	}
	
	public String buildXml(StatementConfig query){
		switch(query.getType()){
		case "insert":
			return buildInsertXml(query);
		case "update":
			return buildUpdateXml(query);
		case "delete":
			return buildDeleteXml(query);
		default:
			return buildSelectXml(query);
		}
	}
	private String buildSelectXml(StatementConfig query){
		return String.format("<select id=\"%s\" resultType=\"%s\">%s</select>", query.getAlias() , query.getResultType() , query.getSql());
	}
	private String buildUpdateXml(StatementConfig query){
		return String.format("<update id=\"%s\">%s</update>", query.getAlias() , query.getSql());
	}
	private String buildDeleteXml(StatementConfig query){
		return String.format("<delete id=\"%s\">%s</delete>", query.getAlias() , query.getSql());
	}
	private String buildInsertXml(StatementConfig query){
		return String.format("<insert id=\"%s\">%s</insert>", query.getAlias() , query.getSql());
	}
	
	private String getQueryId(String statement){
		int s = statement.lastIndexOf(".");
		String key = statement.substring(s + 1);
		return key;
	}

}
