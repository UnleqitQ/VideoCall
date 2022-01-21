package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.sharedclasses.account.Account;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.ListData;
import com.unleqitq.videocall.transferclasses.base.data.AccountData;
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
			array[i++] = new CallData(call);
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
	
	
}
