package com.unleqitq.videocall.accessserver;

import com.unleqitq.videocall.sharedclasses.DisconnectListener;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.sharedclasses.account.Account;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.*;
import com.unleqitq.videocall.transferclasses.base.data.CallDefData;
import com.unleqitq.videocall.transferclasses.base.data.TeamData;
import com.unleqitq.videocall.transferclasses.base.data.UserData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientConnection implements ReceiveListener, DisconnectListener {
	
	public ServerNetworkConnection connection;
	@Nullable
	public UUID user;
	
	public ClientConnection(@NotNull ServerNetworkConnection connection) {
		this.connection = connection;
		connection.setDisconnectListener(this);
		connection.setReceiveListener(this);
		
		System.out.println("Established Client Connection: " + connection.getSocket());
		
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof AuthenticationData authenticationData) {
			try {
				System.out.printf("%s: %s\n", authenticationData.username(),
						Arrays.toString(authenticationData.passphrase()));
				Account account = AccessServer.getInstance().getManagerHandler().getAccountManager().getAccount(
						authenticationData.username());
				if (account == null) {
					System.out.println("Not Found");
					connection.send(new AuthenticationResult(-2, null));
					return;
				}
				System.out.println("Found");
				boolean flag = account.test(authenticationData.passphrase(), authenticationData.time());
				System.out.println("Found " + flag);
				if (flag) {
					connection.send(new AuthenticationResult(1, account.getUuid()));
					user = account.getUuid();
					AccessServer.getInstance().preConnections.remove(this);
					if (AccessServer.getInstance().clientConnections.containsKey(user)) {
						ClientConnection existing = AccessServer.getInstance().clientConnections.get(user);
						//System.out.println("User already Logged In");
						//System.out.println("New Connection: " + connection);
						//System.out.println("Preexisting Connection: " + existing.connection);
						AccessServer.getInstance().clientConnections.remove(user);
						connection.getReceiveThread().interrupt();
						AccessServer.getInstance().clientConnections.get(
								user).connection.getReceiveThread().interrupt();
						try {
							connection.getSocket().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							existing.connection.getSocket().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					AccessServer.getInstance().clientConnections.put(user, this);
				}
				else
					connection.send(new AuthenticationResult(0, null));
			} catch (NullPointerException e) {
				e.printStackTrace();
				connection.send(new AuthenticationResult(-2, null));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				connection.send(new AuthenticationResult(-1, null));
			}
		}
		if (user == null)
			return;
		if (data.getData() instanceof ClientListRequest && user != null) {
			Set<Serializable> set = new HashSet<>();
			for (Team team : AccessServer.getInstance().getManagerHandler().getTeamManager().getTeamMap().values()) {
				if (team.getMembers().contains(user)) {
					set.add(new TeamData(team));
				}
			}
			for (CallDefinition call : AccessServer.getInstance().getManagerHandler().getCallManager().getCallMap().values()) {
				if (call.testMember(user)) {
					set.add(new CallDefData(call));
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
				User user = AccessServer.getInstance().getManagerHandler().getUserManager().getUser(uuid);
				if (user != null) {
					set.add(new UserData(user));
				}
			}
			for (Serializable v : request.calls()) {
				UUID uuid = (UUID) v;
				CallDefinition call = AccessServer.getInstance().getManagerHandler().getCallManager().getCall(uuid);
				if (call != null) {
					set.add(new CallDefData(call));
				}
			}
			for (Serializable v : request.teams()) {
				UUID uuid = (UUID) v;
				Team team = AccessServer.getInstance().getManagerHandler().getTeamManager().getTeam(uuid);
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
		if (data.getData() instanceof RequestAllUser) {
			Serializable[] array = new Serializable[AccessServer.getInstance().getManagerHandler().getUserManager().getUserMap().size()];
			int i = 0;
			for (User user : AccessServer.getInstance().getManagerHandler().getUserManager().getUserMap().values()) {
				array[i++] = new UserData(user);
			}
			connection.send(new ListData(array));
		}
		if (data.getData() instanceof TeamData teamData) {
			//System.out.println(teamData);
			AccessServer.getInstance().rootConnection.send(teamData);
		}
		if (data.getData() instanceof CallDefData callDefData) {
			//System.out.println(callDefData);
			AccessServer.getInstance().rootConnection.send(callDefData);
		}
		if (data.getData() instanceof CallRequest callRequest) {
			//System.out.println(callRequest);
			if (AccessServer.getInstance().getManagerHandler().getCallManager().getCall(callRequest.uuid()).testMember(
					user)) {
				AccessServer.getInstance().rootConnection.send(callRequest);
				if (!AccessServer.getInstance().callRequestMap.containsKey(callRequest.uuid()))
					AccessServer.getInstance().callRequestMap.put(callRequest.uuid(), new ConcurrentLinkedQueue<>());
				AccessServer.getInstance().callRequestMap.get(callRequest.uuid()).add(user);
			}
		}
	}
	
	@Override
	public String toString() {
		return "ClientConnection{ connection=" + connection + ", user=" + user + " }";
	}
	
	@Override
	public void onDisconnect() {
		AccessServer.getInstance().preConnections.remove(this);
		if (user != null)
			AccessServer.getInstance().clientConnections.remove(user);
	}
	
}
