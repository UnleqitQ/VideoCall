package com.unleqitq.videocall.transferclasses;

import java.io.Serial;
import java.io.Serializable;

public class Data implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 3601307879760657168L;
	private final Serializable data;
	private long sent;
	
	public Data(Serializable data) {
		this.data = data;
	}
	
	public long getDifference() {
		return System.currentTimeMillis() - sent;
	}
	
	public Serializable getData() {
		return data;
	}
	
	public void timestamp() {
		sent = System.currentTimeMillis();
	}
	
}
