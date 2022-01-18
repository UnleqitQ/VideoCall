package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;

public class AccessConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	RootServer rootServer;
	
	public AccessConnection(ServerNetworkConnection connection, RootServer rootServer) {
		this.connection = connection;
		connection.setListener(this);
		this.rootServer = rootServer;
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof ConnectionInformation) {
			ConnectionInformation info = (ConnectionInformation) data.getData();
			
		}
	}
	
}
