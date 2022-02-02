package com.unleqitq.videocall.callserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;

public class BaseConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	
	public BaseConnection(ServerNetworkConnection connection) {
		this.connection = connection;
		connection.setReceiveListener(this);
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof ConnectionInformation) {
			ConnectionInformation info = (ConnectionInformation) data.getData();
			switch (info.getType()) {
				case CLIENT -> CallServer.getInstance().addClient(this);
			}
		}
	}
	
}
