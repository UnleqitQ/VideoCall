package com.unleqitq.videocall.transferclasses.connection;

import java.io.Serial;
import java.io.Serializable;

public class ConnectionInformation implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 3083824317143645009L;
	
	private int type;
	
	public ConnectionInformation(ClientType type) {
		this.type = type.ordinal();
	}
	
	public ClientType getType() {
		return ClientType.values()[type];
	}
	
	public enum ClientType {
		CLIENT, ACCESS, CALL;
	}
	
}
