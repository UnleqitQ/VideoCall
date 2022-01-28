package com.unleqitq.videocall.accessserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;

public class BaseConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	AccessServer rootServer;
	
	public BaseConnection(ServerNetworkConnection connection, AccessServer rootServer) {
		this.connection = connection;
		connection.setReceiveListener(this);
		this.rootServer = rootServer;
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof ConnectionInformation) {
			ConnectionInformation info = (ConnectionInformation) data.getData();
			switch (info.getType()) {
				case CLIENT -> rootServer.addClient(this);
			}
		}
	}
	
}
