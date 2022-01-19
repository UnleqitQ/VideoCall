package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.sharedclasses.account.Account;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.AccessInformation;
import com.unleqitq.videocall.transferclasses.base.AuthenticationData;
import com.unleqitq.videocall.transferclasses.base.AuthenticationResult;
import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;

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
				new AccessInformation(accessConnection.connection.getSocket().getInetAddress().getCanonicalHostName(),
						accessConnection.port));
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof AuthenticationData authenticationData) {
			try {
				Account account = rootServer.getManagerHandler().getAccountManager().getAccount(
						authenticationData.username());
				if (account == null) {
					connection.send(new AuthenticationResult(-2, null));
					return;
				}
				boolean flag = account.test(authenticationData.passphrase(), authenticationData.time());
				if (flag)
					connection.send(new AuthenticationResult(1, account.getUuid()));
				else
					connection.send(new AuthenticationResult(0, null));
			} catch (NullPointerException e) {
				connection.send(new AuthenticationResult(-2, null));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				connection.send(new AuthenticationResult(-1, null));
			}
		}
	}
	
}
