package com.szu.insight.dsw.dao.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.szu.insight.dsw.core.DataSourceResult;

/**
 * @Description: 通用的DAO类，数据业务的处理，包含增删改查
 *
 * @author Cloud
 *
 * @time: 2016年8月22日 下午4:28:42
 *
 */
public abstract class BaseDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	private SqlSession getSqlSession() {
		return sqlSessionTemplate;
	}

	public abstract String getMapperNamespace();

	public <E> List<E> getAll() {
		return getSqlSession().selectList(getStatement("getAll"));
	}

	public <T> T getByPK(Long id) {
		return getSqlSession().selectOne(getStatement("getByPK"), id);
	}

	public int deleteByPK(Long id) {
		return getSqlSession().delete(getStatement("delByPK"), id);
	}

	/**
	 * 删除关联表的关联关系
	 * 
	 * @param paramString
	 *            sql语句的id
	 * @param id
	 *            主id，根据该id值删除关联
	 * @param idlList
	 *            将要删除的关联关系的id列表
	 * @return
	 */
	public int deleteByParam(String paramString, Long id, List<Long> idlList) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("list", idlList);
		return getSqlSession().delete(getStatement(paramString), map);
	}

	/**
	 * 改变关联表中的关联关系
	 * 
	 * @param paramString
	 *            sql语句的id
	 * @param id
	 *            主id，根据该id值改变关联
	 * @param oldlList
	 *            旧的关联id列表
	 * @param newlList
	 *            新的关联id列表
	 * @return
	 */
	public int changeTo(String paramString, Long id, List<Long> oldlList, List<Long> newlList) {
		for (int i = 0; i < oldlList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("oldId", oldlList.get(i));
			map.put("newId", newlList.get(i));
			if (getSqlSession().update(getStatement(paramString), map) == 0)
				return 0;
		}
		return 1;
	}

	/**
	 * 新增关联表中的关联关系
	 * 
	 * @param paramString
	 *            sql语句的id
	 * @param id
	 *            主id，根据该id值新增关联
	 * @param idlList
	 *            要新增关联的id列表
	 * @return
	 */
	public int insertByParam(String paramString, Long id, List<Long> idlList) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("list", idlList);
		return getSqlSession().insert(getStatement(paramString), map);
	}

	public int updateByPK(Long id) {
		return getSqlSession().update(getStatement("updateByPK"), id);
	}

	public <T> T selectOne(String paramString, Object paramObject) {
		return getSqlSession().selectOne(getStatement(paramString), paramObject);
	}

	public <T> T selectOne(String paramString) {
		return getSqlSession().selectOne(getStatement(paramString));
	}

	public <E> List<E> selectList(String paramString) {
		return getSqlSession().selectList(getStatement(paramString));
	}

	public <E> List<E> selectList(String paramString, Object paramObject) {
		return getSqlSession().selectList(getStatement(paramString), paramObject);
	}

	public DataSourceResult selectPage(String paramString, String totalString, Object paramObject) {
		DataSourceResult result = new DataSourceResult();
		result.setData(selectList(paramString, paramObject));
		Long total = selectOne(totalString, paramObject);
		result.setTotal(total);
		return result;
	}

	public <E> List<E> selectList(String paramString, Object paramObject, RowBounds paramRowBounds) {
		return getSqlSession().selectList(getStatement(paramString), paramObject, paramRowBounds);
	}

	public int insert(String paramString) {
		return getSqlSession().insert(getStatement(paramString));
	}

	public int insert(String paramString, Object paramObject) {
		return getSqlSession().insert(getStatement(paramString), paramObject);
	}

	public int update(String paramString) {
		return getSqlSession().update(getStatement(paramString));
	}

	public int update(String paramString, Object paramObject) {
		return getSqlSession().update(getStatement(paramString), paramObject);
	}

	public int delete(String paramString) {
		return getSqlSession().delete(getStatement(paramString));
	}

	public int delete(String paramString, Object paramObject) {
		return getSqlSession().delete(getStatement(paramString), paramObject);
	}

	private String getStatement(String statement) {
		return getMapperNamespace() + "." + statement;
	}

}
