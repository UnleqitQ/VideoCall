package com.unleqitq.videocall.rootserver;

import sharedclasses.ReceiveListener;
import sharedclasses.ServerNetworkConnection;
import sharedclasses.call.CallDefinition;
import sharedclasses.team.Team;
import sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.ListData;
import com.unleqitq.videocall.transferclasses.base.data.CallData;
import com.unleqitq.videocall.transferclasses.base.data.TeamData;
import com.unleqitq.videocall.transferclasses.base.data.UserData;
import com.unleqitq.videocall.transferclasses.connection.MachineInformation;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class AccessConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	RootServer rootServer;
	
	public long freeMemory;
	public int port;
	
	public AccessConnection(@NotNull ServerNetworkConnection connection, RootServer rootServer) {
		this.connection = connection;
		connection.setListener(this);
		this.rootServer = rootServer;
		
		synchronize();
		
		System.out.println("Established Access Connection: " + connection.getSocket());
	}
	
	@Override
	public void onReceive(@NotNull Data data) {
		if (data.getData() instanceof MachineInformation) {
			MachineInformation info = (MachineInformation) data.getData();
			port = info.getPort();
			freeMemory = info.getFreeMemory();
			System.out.println("Info: " + info);
		}
	}
	
	public void synchronize() {
		Collection<CallDefinition> calls = Collections.unmodifiableCollection(
				rootServer.getManagerHandler().getCallManager().getCallMap().values());
		Collection<User> users = Collections.unmodifiableCollection(
				rootServer.getManagerHandler().getUserManager().getUserMap().values());
		Collection<Team> teams = Collections.unmodifiableCollection(
				rootServer.getManagerHandler().getTeamManager().getTeamMap().values());
		Serializable[] array = new Serializable[calls.size() + users.size() + teams.size()];
		
		int i = 0;
		for (CallDefinition call : calls) {
			array[i++] = new CallData(call);
		}
		for (User user : users) {
			array[i++] = new UserData(user);
		}
		for (Team team : teams) {
			array[i++] = new TeamData(team);
		}
		
		ListData listData = new ListData(array);
		connection.send(listData);
	}
	
}
