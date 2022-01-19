package com.unleqitq.videocall.accessserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.ClientListRequest;
import com.unleqitq.videocall.transferclasses.base.ListData;
import com.unleqitq.videocall.transferclasses.base.PackRequest;
import com.unleqitq.videocall.transferclasses.base.data.CallData;
import com.unleqitq.videocall.transferclasses.base.data.TeamData;
import com.unleqitq.videocall.transferclasses.base.data.UserData;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	AccessServer rootServer;
	
	public ClientConnection(@NotNull ServerNetworkConnection connection, AccessServer rootServer) {
		this.connection = connection;
		connection.setListener(this);
		this.rootServer = rootServer;
		
		System.out.println("Established Client Connection: " + connection.getSocket());
		
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof ClientListRequest request) {
			Set<Serializable> set = new HashSet<>();
			for (Team team : rootServer.getManagerHandler().getTeamManager().getTeamMap().values()) {
				if (team.getMembers().contains(request.user())) {
					set.add(new TeamData(team));
				}
			}
			for (CallDefinition call : rootServer.getManagerHandler().getCallManager().getCallMap().values()) {
				if (call.testMember(request.user())) {
					set.add(new CallData(call));
				}
			}
			Serializable[] array = new Serializable[set.size()];
			int i = 0;
			for (Serializable d : set) {
				array[i++] = d;
			}
			connection.send(new ListData(array));
		}
		if (data.getData() instanceof PackRequest request) {
			Set<Serializable> set = new HashSet<>();
			for (Serializable v : request.users()) {
				UUID uuid = (UUID) v;
				User user = rootServer.getManagerHandler().getUserManager().getUser(uuid);
				if (user != null) {
					set.add(new UserData(user));
				}
			}
			for (Serializable v : request.calls()) {
				UUID uuid = (UUID) v;
				CallDefinition call = rootServer.getManagerHandler().getCallManager().getCall(uuid);
				if (call != null) {
					set.add(new CallData(call));
				}
			}
			for (Serializable v : request.teams()) {
				UUID uuid = (UUID) v;
				Team team = rootServer.getManagerHandler().getTeamManager().getTeam(uuid);
				if (team != null) {
					set.add(new TeamData(team));
				}
			}
			Serializable[] array = new Serializable[set.size()];
			int i = 0;
			for (Serializable d : set) {
				array[i++] = d;
			}
			connection.send(new ListData(array));
		}
	}
	
}
