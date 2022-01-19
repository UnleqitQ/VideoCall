package com.unleqitq.videocall.callserver;

import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.sharedclasses.account.Account;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.AuthenticationData;
import com.unleqitq.videocall.transferclasses.base.AuthenticationResult;
import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;

public class ClientConnection implements ReceiveListener {
	
	public ServerNetworkConnection connection;
	CallServer callServer;
	
	public ClientConnection(@NotNull ServerNetworkConnection connection, CallServer callServer) {
		this.connection = connection;
		connection.setListener(this);
		this.callServer = callServer;
		
		System.out.println("Established Client Connection: " + connection.getSocket());
		
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof AuthenticationData authenticationData) {
			try {
				Account account = callServer.getManagerHandler().getAccountManager().getAccount(
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
