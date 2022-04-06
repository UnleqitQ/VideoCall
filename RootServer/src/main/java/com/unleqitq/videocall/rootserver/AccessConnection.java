package com.unleqitq.videocall.rootserver;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.unleqitq.videocall.sharedclasses.DisconnectListener;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.sharedclasses.account.Account;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.call.CallInformation;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.CallRequest;
import com.unleqitq.videocall.transferclasses.base.ListData;
import com.unleqitq.videocall.transferclasses.base.data.*;
import com.unleqitq.videocall.transferclasses.connection.MachineInformation;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class AccessConnection implements ReceiveListener, DisconnectListener {
	
	public ServerNetworkConnection connection;
	RootServer rootServer;
	
	public long freeMemory;
	public int port;
	
	public AccessConnection(@NotNull ServerNetworkConnection connection, RootServer rootServer) {
		this.connection = connection;
		connection.setReceiveListener(this);
		connection.setDisconnectListener(this);
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
		}
		if (data.getData() instanceof TeamData teamData) {
			//System.out.println(teamData);
			if (teamData.getUUID() != null) {
				Team team = teamData.getTeam(rootServer.getManagerHandler());
				rootServer.getManagerHandler().getTeamManager().addTeam(team);
				for (AccessConnection connection : rootServer.accessQueue) {
					connection.connection.send(new TeamData(team));
				}
			}
			else {
				UUID uuid = rootServer.getManagerHandler().getTeamManager().getTeamUuid();
				JsonObject object = teamData.getJsonObject();
				object.add("uuid", new JsonPrimitive(uuid.toString()));
				Team team = Team.load(rootServer.getManagerHandler(), object);
				rootServer.getManagerHandler().getTeamManager().addTeam(team);
				for (AccessConnection connection : rootServer.accessQueue) {
					connection.connection.send(new TeamData(team));
				}
			}
		}
		if (data.getData() instanceof CallDefData callDefData) {
			//System.out.println(callDefData);
			if (callDefData.getUUID() != null) {
				CallDefinition callDefinition = callDefData.getCall(rootServer.getManagerHandler());
				rootServer.getManagerHandler().getCallManager().addCall(callDefinition);
				for (AccessConnection connection : rootServer.accessQueue) {
					connection.connection.send(new CallDefData(callDefinition));
				}
				for (CallConnection connection : rootServer.callConnections.values()) {
					connection.connection.send(new CallDefData(callDefinition));
				}
			}
			else {
				UUID uuid = rootServer.getManagerHandler().getCallManager().getCallUuid();
				JsonObject object = callDefData.getJsonObject();
				object.add("uuid", new JsonPrimitive(uuid.toString()));
				CallDefinition callDefinition = CallDefinition.load(rootServer.getManagerHandler(), object);
				rootServer.getManagerHandler().getCallManager().addCall(callDefinition);
				for (AccessConnection connection : rootServer.accessQueue) {
					connection.connection.send(new CallDefData(callDefinition));
				}
				for (CallConnection connection : rootServer.callConnections.values()) {
					connection.connection.send(new CallDefData(callDefinition));
				}
			}
		}
		if (data.getData() instanceof CallRequest callRequest) {
			UUID callUuid = callRequest.uuid();
			UUID serverUuid = rootServer.allocateServer(callUuid);
			CallConnection callConnection = rootServer.getCallConnections().get(serverUuid);
			connection.send(new CallData(new CallInformation(callUuid, serverUuid,
					callConnection.connection.getSocket().getInetAddress().getCanonicalHostName(),
					callConnection.port)));
		}
	}
	
	public void synchronize() {
		Collection<CallDefinition> calls = Collections.unmodifiableCollection(
				rootServer.getManagerHandler().getCallManager().getCallMap().values());
		Collection<User> users = Collections.unmodifiableCollection(
				rootServer.getManagerHandler().getUserManager().getUserMap().values());
		Collection<Team> teams = Collections.unmodifiableCollection(
				rootServer.getManagerHandler().getTeamManager().getTeamMap().values());
		Collection<Account> accounts = Collections.unmodifiableCollection(
				rootServer.getManagerHandler().getAccountManager().getAccountMap().values());
		Serializable[] array = new Serializable[calls.size() + users.size() + teams.size() + accounts.size()];
		
		int i = 0;
		for (CallDefinition call : calls) {
			array[i++] = new CallDefData(call);
		}
		for (User user : users) {
			array[i++] = new UserData(user);
		}
		for (Team team : teams) {
			array[i++] = new TeamData(team);
		}
		for (Account account : accounts) {
			array[i++] = new AccountData(account);
		}
		
		ListData listData = new ListData(array);
		connection.send(listData);
	}
	
	@Override
	public void onDisconnect() {
		System.out.println("Disconnected " + port);
		rootServer.accessQueue.remove(this);
		rootServer.accessConnections.remove(this);
	}
	
}
