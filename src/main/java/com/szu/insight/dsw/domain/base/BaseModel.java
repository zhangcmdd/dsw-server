package com.szu.insight.dsw.domain.base;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 通用的Model类
 *
 * @author Cloud
 *
 * @time: 2016年8月22日 下午4:50:36
 *
 */
public class BaseModel implements Serializable {

	/** 
	 * @Description: serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String changedBy;
	
	private String createdBy;
	
	private Date createdAt;
	
	private Date changedAt;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getChangedBy() {
		return changedBy;
	}
	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getChangedAt() {
		return changedAt;
	}
	public void setChangedAt(Date changedAt) {
		this.changedAt = changedAt;
	}
	
	
}
