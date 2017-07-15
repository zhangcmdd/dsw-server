package com.szu.insight.dsw.dao;

import org.springframework.stereotype.Repository;

import com.szu.insight.dsw.dao.base.BaseDao;
import com.szu.insight.dsw.domain.DataSourceConfig;

@Repository
public class DataSourceConfigDao extends BaseDao {

	@Override
	public String getMapperNamespace() {
		return DataSourceConfig.class.getName();
	}
	
	public DataSourceConfig getByAlias(String alias){
		return selectOne("getByAlias", alias);
	}

}
