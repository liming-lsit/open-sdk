

package com.sdk.utils;

public class HttpResult {
	
	private Integer status;	
	private String  result;		
	private Long time;			
	
	public HttpResult() {
		super();
	}
	public HttpResult(Integer status, String result, Long begin) {
		super();
		this.status = status;
		this.result = result;
		this.time = System.currentTimeMillis() - begin;
	}
	
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}

}

