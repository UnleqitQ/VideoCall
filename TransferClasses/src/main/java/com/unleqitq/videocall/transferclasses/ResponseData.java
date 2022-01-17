package com.unleqitq.videocall.transferclasses;

public class ResponseData {
	
	private int responseValue;
	private int requestValue;
	
	public int getResponseValue() {
		return responseValue;
	}
	
	public int getRequestValue() {
		return requestValue;
	}
	
	public void setResponseValue(int intValue) {
		this.responseValue = intValue;
	}
	
	public void setRequestValue(int intValue) {
		this.requestValue = intValue;
	}
	
	@Override
	public String toString() {
		return "ResponseData{ response=" + responseValue + ", request=" + requestValue + " }";
	}
	
}
