package com.unleqitq.videocall.transferclasses.connection;

import java.io.Serial;
import java.io.Serializable;

public class AccessInformation implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -2772669104831032800L;
	
	private final String host;
	private final int port;
	
	public AccessInformation(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
}
