package com.unleqitq.videocall.accessserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import org.jetbrains.annotations.NotNull;

public class ClientConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	AccessServer rootServer;
	
	public ClientConnection(@NotNull ServerNetworkConnection connection, AccessServer rootServer) {
		this.connection = connection;
		connection.setListener(this);
		this.rootServer = rootServer;
		
		System.out.println("Established Client Connection: " + connection.getSocket());
		
	}
	
	@Override
	public void onReceive(Data data) {
	
	}
	
}
