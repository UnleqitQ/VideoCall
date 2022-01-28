package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;
import org.jetbrains.annotations.NotNull;

public class BaseConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	RootServer rootServer;
	
	public BaseConnection(@NotNull ServerNetworkConnection connection, RootServer rootServer) {
		this.connection = connection;
		connection.setReceiveListener(this);
		this.rootServer = rootServer;
	}
	
	@Override
	public void onReceive(@NotNull Data data) {
		if (data.getData() instanceof ConnectionInformation) {
			ConnectionInformation info = (ConnectionInformation) data.getData();
			switch (info.getType()) {
				case CALL -> rootServer.addCall(this);
				case CLIENT -> rootServer.addClient(this);
				case ACCESS -> rootServer.addAccess(this);
			}
		}
	}
	
}
