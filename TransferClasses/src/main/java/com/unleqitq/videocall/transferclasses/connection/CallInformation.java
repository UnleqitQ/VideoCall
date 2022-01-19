package com.unleqitq.videocall.transferclasses.connection;

import java.io.Serial;
import java.io.Serializable;

public class CallInformation implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -2772669104831032800L;
	
	private final String host;
	private final int port;
	private final int callId;
	
	public CallInformation(String host, int port, int callId) {
		this.host = host;
		this.port = port;
		this.callId = callId;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getCallId() {
		return callId;
	}
	
}
