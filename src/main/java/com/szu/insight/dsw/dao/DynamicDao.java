package com.szu.insight.dsw.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.szu.insight.dsw.service.DynamicSqlSessionFactoryBean;

@Repository
public class DynamicDao {
	
	@Autowired
	private DynamicSqlSessionFactoryBean dynamicSqlSessionFactoryBean;
	
	public List selectList(String dsAlias , String sql , Object params) throws Exception{	
		return dynamicSqlSessionFactoryBean.getWithCreateSqlSession(dsAlias).selectList(getStatement(sql), params);
	}
	
	public List selectList(SqlSession session , String sql , Object params){
		
		return session.selectList(getStatement(sql) , params);
	}
	
	private String getStatement(String sql){
		return "com.szu.insight.mapper.dynamic." + sql;
	}
}
