package com.unleqitq.videocall.callserver;

import sharedclasses.ReceiveListener;
import sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import org.jetbrains.annotations.NotNull;

public class ClientConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	CallServer rootServer;
	
	public ClientConnection(@NotNull ServerNetworkConnection connection, CallServer rootServer) {
		this.connection = connection;
		connection.setListener(this);
		this.rootServer = rootServer;
		
		System.out.println("Established Client Connection: " + connection.getSocket());
		
	}
	
	@Override
	public void onReceive(Data data) {
	
	}
	
}
