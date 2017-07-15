package com.szu.insight.dsw.core;

import java.util.List;
import java.util.Map;

/**
 * @Description: TODO
 *
 * @author Cloud
 *
 * @time: 2016年8月22日 下午4:35:31
 *
 */
public class DataSourceResult {
    private long total;
    
    private List<?> data;
    
    private Map<String, Object> aggregates;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public Map<String, Object> getAggregates() {
        return aggregates;
    }

    public void setAggregates(Map<String, Object> aggregates) {
        this.aggregates = aggregates;
    }
}
