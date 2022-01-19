package com.unleqitq.videocall.accessserver;

import sharedclasses.ReceiveListener;
import sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;

public class BaseConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	AccessServer rootServer;
	
	public BaseConnection(ServerNetworkConnection connection, AccessServer rootServer) {
		this.connection = connection;
		connection.setListener(this);
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
