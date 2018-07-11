/**
 * Project Name:ccopsms
 * File Name:HttpResult.java
 * Package Name:com.ccop.common.util
 * Date:2016-3-12下午5:11:05
 * Copyright (c) 2016, wanglz All Rights Reserved.
 *
*/

package com.sdk.utils;
/**
 * ClassName:HTTP请求状态 响应结果 <br/>
 * Function: HTTP请求状态 响应结果 <br/>
 * Reason:	 HTTP请求状态 响应结果<br/>
 * Date:     2016-3-12 下午5:11:05 <br/>
 * @author   wanglz
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class HttpResult {
	
	private Integer status;		// 状态码
	private String  result;		// 响应结果
	private Long time;			// 耗时
	
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

