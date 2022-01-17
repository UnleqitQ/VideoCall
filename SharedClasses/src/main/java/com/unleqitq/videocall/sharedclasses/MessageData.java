package com.unleqitq.videocall.sharedclasses;

import com.unleqitq.videocall.transferclasses.SendData;

import java.net.Socket;

public class MessageData {
	
	public AbstractNetworkConnection connection;
	public SendData data;
	
	public MessageData(AbstractNetworkConnection connection, SendData data) {
		this.connection = connection;
		this.data = data;
	}
	
}
