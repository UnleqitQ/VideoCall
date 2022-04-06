package com.unleqitq.videocall.callserver;

import com.unleqitq.videocall.callserver.call.Call;
import com.unleqitq.videocall.callserver.call.CallClientConnection;
import com.unleqitq.videocall.sharedclasses.DisconnectListener;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.sharedclasses.account.Account;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.AuthenticationData;
import com.unleqitq.videocall.transferclasses.base.AuthenticationResult;
import com.unleqitq.videocall.transferclasses.call.RequestCallData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ClientConnection implements ReceiveListener, DisconnectListener {
	
	public ServerNetworkConnection connection;
	@Nullable
	public UUID user;
	
	public ClientConnection(@NotNull ServerNetworkConnection connection) {
		this.connection = connection;
		connection.setReceiveListener(this);
		connection.setDisconnectListener(this);
		
		System.out.println("Established Client Connection: " + connection.getSocket());
		
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof AuthenticationData authenticationData) {
			try {
				Account account = CallServer.getInstance().getManagerHandler().getAccountManager().getAccount(
						authenticationData.username());
				if (account == null) {
					connection.send(new AuthenticationResult(-2, null));
					return;
				}
				boolean flag = account.test(authenticationData.passphrase(), authenticationData.time());
				if (flag) {
					connection.send(new AuthenticationResult(1, account.getUuid()));
					user = account.getUuid();
					CallServer.getInstance().preConnections.remove(this);
					if (CallServer.getInstance().clientConnections.containsKey(user)) {
						ClientConnection existing = CallServer.getInstance().clientConnections.get(user);
						//System.out.println("User already Logged In");
						//System.out.println("New Connection: " + connection);
						//System.out.println("Preexisting Connection: " + existing.connection);
						CallServer.getInstance().clientConnections.remove(user);
						connection.getReceiveThread().interrupt();
						CallServer.getInstance().clientConnections.get(user).connection.getReceiveThread().interrupt();
						try {
							//System.out.println("ClientConnection.onReceive (59)");
							//System.out.println("Closed Connection to " + connection.getSocket().getInetAddress());
							connection.getSocket().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							//System.out.println("ClientConnection.onReceive (66)");
							//System.out.println(
							//		"Closed Connection to " + existing.connection.getSocket().getInetAddress());
							existing.connection.getSocket().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					CallServer.getInstance().clientConnections.put(user, this);
				}
				else
					connection.send(new AuthenticationResult(0, null));
			} catch (NullPointerException e) {
				connection.send(new AuthenticationResult(-2, null));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				connection.send(new AuthenticationResult(-1, null));
			}
		}
		if (user == null) {
			return;
		}
		//System.out.println(data);
		if (data.getData() instanceof RequestCallData requestCallData) {
			Call call = CallServer.getInstance().getCall(requestCallData.call());
			if (!call.callUsers.containsKey(user)) {
				try {
					//System.out.println("ClientConnection.onReceive (93)");
					//System.out.println("Closed Connection to " + connection.getSocket().getInetAddress());
					connection.getSocket().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
			call.clientConnections.put(user, new CallClientConnection(connection, user, call));
			call.addUser(user);
			CallServer.getInstance().clientConnections.remove(user);
		}
	}
	
	@Override
	public String toString() {
		return "ClientConnection{ connection=" + connection + ", user=" + user + " }";
	}
	
	@Override
	public void onDisconnect() {
		CallServer.getInstance().preConnections.remove(this);
		if (user != null)
			CallServer.getInstance().clientConnections.remove(user);
	}
	
}
