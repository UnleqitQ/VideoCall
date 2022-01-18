package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.connection.MachineInformation;

public class CallConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	RootServer rootServer;
	
	public long freeMemory;
	public int port;
	
	public CallConnection(ServerNetworkConnection connection, RootServer rootServer) {
		this.connection = connection;
		connection.setListener(this);
		this.rootServer = rootServer;
		
		System.out.println("Established Call Connection: " + connection.getSocket());
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof MachineInformation) {
			MachineInformation info = (MachineInformation) data.getData();
			port = info.getPort();
			freeMemory = info.getFreeMemory();
		}
	}
	
}
