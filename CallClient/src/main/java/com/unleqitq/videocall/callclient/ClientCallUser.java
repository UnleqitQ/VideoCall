package com.unleqitq.videocall.callclient;

import java.util.UUID;

public class ClientCallUser {
	
	public UUID uuid;
	
	public float gain = 1;
	public boolean connected;
	
	public ClientCallUser(UUID uuid) {
		this.uuid = uuid;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
}
