package com.unleqitq.videocall.callclient;

import com.unleqitq.videocall.sharedclasses.user.CallUser;

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
	
	@Override
	public String toString() {
		CallUser user = CallClient.getInstance().users.get(uuid);
		return user.getFirstname() + " " + user.getLastname();
	}
	
}
