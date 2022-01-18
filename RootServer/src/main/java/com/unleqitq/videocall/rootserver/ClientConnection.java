package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import org.jetbrains.annotations.NotNull;

public class ClientConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	RootServer rootServer;
	
	public ClientConnection(@NotNull ServerNetworkConnection connection, RootServer rootServer) {
		this.connection = connection;
		connection.setListener(this);
		this.rootServer = rootServer;
	}
	
	@Override
	public void onReceive(Data data) {
	
	}
	
}
