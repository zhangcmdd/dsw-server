package com.szu.insight.dsw.core;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class QueryMap extends HashMap<String , Object> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public QueryMap(Map<String , String[]> parameterMap){
		super(parameterMap);
	}

	@Override
	public Object get(Object key) {
		Object val = super.get(key);
		if(val instanceof Object[] && Array.getLength(val) == 1){
			return ((Object[])val)[0];
		}
		return super.get(key);
	}
	
	
	
}
