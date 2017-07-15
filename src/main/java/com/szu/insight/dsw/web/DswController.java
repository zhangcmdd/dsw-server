package com.szu.insight.dsw.web;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.szu.insight.dsw.core.QueryMap;
import com.szu.insight.dsw.dao.DataSourceConfigDao;
import com.szu.insight.dsw.dao.DynamicDao;
import com.szu.insight.dsw.dao.StatementConfigDao;
import com.szu.insight.dsw.domain.DataSourceConfig;
import com.szu.insight.dsw.domain.StatementConfig;
import com.szu.insight.dsw.service.DynamicSqlSessionFactoryBean;
import com.szu.insight.dsw.utls.DataSourceUtil;

@Controller
@CrossOrigin(origins = {"*"} , methods = { RequestMethod.GET , RequestMethod.POST})
public class DswController {
	
	@Autowired
	private DataSourceConfigDao dataSourceConfigDao;
	
	@Autowired
	private StatementConfigDao statementConfigDao;
	
	@Autowired
	private DynamicDao dynamicDao;
	
	@Autowired
	private DynamicSqlSessionFactoryBean dynamicSqlSessionFactoryBean;
	
	@RequestMapping("ds/list")
	@ResponseBody
	public List<DataSourceConfig> getAllDataSource(HttpServletResponse res){
		return dataSourceConfigDao.getAll();
	}
	
	@RequestMapping("ds/one/{id}")
	@ResponseBody
	public DataSourceConfig getDataSourceById(@PathVariable Long id){
		return dataSourceConfigDao.getByPK(id);
	}
	
	@RequestMapping("ds/one/save")
	@ResponseBody
	public DataSourceConfig saveDataSource(@RequestBody DataSourceConfig config) throws ClassNotFoundException{
		if(config.getId() != null){
			dataSourceConfigDao.update("update", config);
			dynamicSqlSessionFactoryBean.replaceDataSource(config.getAlias());
		}else{
			dataSourceConfigDao.insert("add", config);
		}
		return config;
	}
	
	@RequestMapping("ds/one/del/{id}")
	@ResponseBody
	public Long deleteDataSource(@PathVariable Long id) throws Exception{
		if(dataSourceConfigDao.deleteByPK(id) <= 0){
			throw new Exception("No this DataSource");
		}
		return id;
	}
	
	@RequestMapping("stmt/list")
	@ResponseBody
	public List<StatementConfig> getStatementsByDataSource(StatementConfig config){
		return statementConfigDao.selectList(config);
	}
	
	@RequestMapping("stmt/one/{id}")
	@ResponseBody
	public StatementConfig getStatementById(@PathVariable Long id){
		return statementConfigDao.getByPK(id);
	}
	
	@RequestMapping("stmt/one/del/{id}")
	@ResponseBody
	public Long deleteStatment(@PathVariable Long id) throws Exception{
		StatementConfig config = statementConfigDao.getByPK(id);
		if(statementConfigDao.deleteByPK(id) <= 0){
			throw new Exception("No this Statement");
		}
		dynamicSqlSessionFactoryBean.clearStatement(config.getAlias());
		return id;
	}
	
	@RequestMapping("stmt/one/save")
	@ResponseBody
	public StatementConfig saveStatement(@RequestBody StatementConfig config){
		if(config.getId() != null){
			StatementConfig old = statementConfigDao.getByPK(config.getId());
			statementConfigDao.update("update", config);
			if(!config.getAlias().equalsIgnoreCase(old.getAlias()) || !config.getSql().equalsIgnoreCase(old.getSql()))
				dynamicSqlSessionFactoryBean.clearStatement(old.getAlias());
		}else{
			statementConfigDao.insert("add", config);
		}
		return config;
	}
	
	/**
	 * 目前支持两种提交数据方式（也只会支持这两种方式）：application/x-www-form-urlencoded 和 application/json的方式
	 * @param dsAlias
	 * @param qAlias
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("dsw/{dsAlias}/{qAlias}/list")
	public String list(@PathVariable String dsAlias , @PathVariable String qAlias , @RequestHeader(value = "Content-Type" , required = false) String type) throws Exception{
		if(type != null && type.contains("application/json")){//json
			return String.format("forward:/dsw/%s/%s/list/json" , dsAlias , qAlias);
		}else{//x-www-form-urlencoded
			return String.format("forward:/dsw/%s/%s/list/form" , dsAlias , qAlias);
		}
	}
	
	@RequestMapping("dsw/{dsAlias}/{qAlias}/list/form")
	@ResponseBody
	public Object listByFormRequest(@PathVariable String dsAlias , @PathVariable String qAlias ,  HttpServletRequest req) throws Exception{
		return responseList(dsAlias, qAlias, new QueryMap(req.getParameterMap()));
	}
	
	@RequestMapping("dsw/{dsAlias}/{qAlias}/list/json")
	@ResponseBody
	public Object listByJsonRequest(@PathVariable String dsAlias , @PathVariable String qAlias , @RequestBody(required = false) Map<String , Object> params) throws Exception{
		return responseList(dsAlias , qAlias , params);
	}
	
	private Object responseList(String dsAlias , String qAlias , Map<String , Object> map) throws Exception{
		List list = dynamicDao.selectList(dsAlias, qAlias, map);
		if(map.get("pageNum") != null && map.get("pageSize") != null){
			return new PageInfo<Object>(list , 0);
		}else{
			return list;
		}
	}
	
	@RequestMapping("dsw/{dsAlias}/{qAlias}/clear-query")
	@ResponseBody
	public String clearStatement(@PathVariable String dsAlias , @PathVariable String qAlias){
		dynamicSqlSessionFactoryBean.clearStatement(dsAlias, qAlias);
		return "SUCCESS";
	}
	
	@RequestMapping("ds/test")
	@ResponseBody
	public String dataSourceTest(@RequestBody DataSourceConfig config) throws ClassNotFoundException, SQLException{
		DataSourceUtil.connect(config);
		return "success";
	}
	
}
