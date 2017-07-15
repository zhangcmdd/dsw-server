package com.szu.insight.dsw.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.szu.insight.dsw.dao.base.BaseDao;
import com.szu.insight.dsw.domain.StatementConfig;

/**
 * 
 * @author Cloud
 *
 */
@Repository
public class StatementConfigDao extends BaseDao {

	@Override
	public String getMapperNamespace() {
		return StatementConfig.class.getName();
	}
	
	public StatementConfig getByAlias(String alias){
		return selectOne("getByAlias", alias);
	}
	
	public List<StatementConfig> selectList(Object paramObject){
		return selectList("select", paramObject);
	}

}
