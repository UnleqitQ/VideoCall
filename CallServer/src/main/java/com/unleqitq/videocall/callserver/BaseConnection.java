package com.unleqitq.videocall.callserver;

import sharedclasses.ReceiveListener;
import sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;

public class BaseConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	CallServer rootServer;
	
	public BaseConnection(ServerNetworkConnection connection, CallServer rootServer) {
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
