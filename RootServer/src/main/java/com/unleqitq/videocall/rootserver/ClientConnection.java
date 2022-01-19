package com.unleqitq.videocall.rootserver;

import sharedclasses.ReceiveListener;
import sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.AccessInformation;
import org.jetbrains.annotations.NotNull;

public class ClientConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	RootServer rootServer;
	
	public ClientConnection(@NotNull ServerNetworkConnection connection, RootServer rootServer) {
		this.connection = connection;
		connection.setListener(this);
		this.rootServer = rootServer;
		
		System.out.println("Established Client Connection: " + connection.getSocket());
		
		sendAccess();
	}
	
	public void sendAccess() {
		AccessConnection accessConnection = rootServer.accessQueue.peek();
		connection.send(
				new AccessInformation(accessConnection.connection.getSocket().getRemoteSocketAddress().toString(),
						accessConnection.port));
	}
	
	@Override
	public void onReceive(Data data) {
	
	}
	
}
