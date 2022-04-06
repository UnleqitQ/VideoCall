package com.unleqitq.videocall.callserver.call;

import com.unleqitq.videocall.callserver.CallServer;
import com.unleqitq.videocall.sharedclasses.DisconnectListener;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.data.CallDefData;
import com.unleqitq.videocall.transferclasses.base.data.CallUserData;
import com.unleqitq.videocall.transferclasses.call.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public class CallClientConnection implements ReceiveListener, DisconnectListener {
	
	public ServerNetworkConnection connection;
	@NotNull
	public UUID user;
	@NotNull
	public Call call;
	
	public CallClientConnection(ServerNetworkConnection connection, @NotNull UUID user, @NotNull Call call) {
		this.connection = connection;
		this.user = user;
		this.call = call;
		connection.setDisconnectListener(this);
		connection.setReceiveListener(this);
	}
	
	public CallUser getCallUser() {
		return call.getCallUser(user);
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof CallUserData callUserData) {
			CallUser cu = callUserData.getUser();
			CallUser callUser;
			if (cu.getUuid() == user) {
				callUser = getCallUser();
				callUser.muted = cu.muted;
				callUser.handRaised = cu.handRaised;
				callUser.video = cu.video;
			}
			else {
				CallUser self = getCallUser();
				callUser = call.callUsers.get(cu.getUuid());
				if (callUser.permission.level > self.permission.level) {
					if (self.permission.isBan()) {
						callUser.permission.overrideBan = cu.permission.overrideBan;
						callUser.permission.ban = cu.permission.ban;
					}
					if (self.permission.isKick()) {
						callUser.permission.overrideKick = cu.permission.overrideKick;
						callUser.permission.kick = cu.permission.kick;
					}
					if (self.permission.isMuteOthers()) {
						callUser.permission.overrideMuteOthers = cu.permission.overrideMuteOthers;
						callUser.permission.muteOthers = cu.permission.muteOthers;
					}
					if (self.permission.isShareScreen()) {
						callUser.permission.overrideShareScreen = cu.permission.overrideShareScreen;
						callUser.permission.shareScreen = cu.permission.shareScreen;
					}
					
					if (self.permission.isMuteOthers()) {
						callUser.muted = cu.muted | callUser.muted;
					}
				}
			}
			CallUserData r = new CallUserData(callUser);
			call.clientConnections.values().forEach(c -> c.connection.send(r));
		}
		if (data.getData() instanceof VideoData videoData) {
			//System.out.println(videoData);
			VideoData r = new VideoData(videoData.creation(), videoData.imageData(), user);
			call.clientConnections.values().stream()/*.filter(c -> !c.user.equals(user))*/.forEach(
					c -> c.connection.send(r));
		}
		if (data.getData() instanceof ScreenVideoData videoData) {
			ScreenVideoData r = new ScreenVideoData(videoData.creation(), videoData.imageData(), user);
			if (call.getCallUser(user).permission.isShareScreen())
				call.clientConnections.values().stream()/*.filter(c -> !c.user.equals(user))*/.forEach(
						c -> c.connection.send(r));
		}
		if (data.getData() instanceof AudioData audioData) {
			//System.out.println(audioData);
			AudioData r = new AudioData(audioData.data(), audioData.offset(), audioData.bufferSize(), user);
			call.clientConnections.values().stream()/*.filter(c -> !c.user.equals(user))*/.forEach(
					c -> c.connection.send(r));
		}
		if (data.getData() instanceof UserLeaveData userLeaveData) {
			if (getCallUser().permission.isKick()) {
				try {
					//System.out.println("CallClientConnection.onReceive (98)");
					//System.out.println("Closed Connection to " + call.clientConnections.get(
					//		user).connection.getSocket().getInetAddress());
					call.clientConnections.get(user).connection.getSocket().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				call.clientConnections.remove(user);
				call.clientConnections.values().forEach(c -> c.connection.send(userLeaveData));
			}
		}
		if (data.getData() instanceof UserBanData userBanData) {
			if (getCallUser().permission.isBan()) {
				try {
					//System.out.println("CallClientConnection.onReceive (112)");
					//System.out.println("Closed Connection to " + call.clientConnections.get(
					//		user).connection.getSocket().getInetAddress());
					call.clientConnections.get(user).connection.getSocket().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				call.clientConnections.remove(user);
				UserLeaveData userLeaveData = new UserLeaveData(userBanData.uuid());
				call.clientConnections.values().forEach(c -> c.connection.send(userLeaveData));
				call.getCallDefinition().denyMember(userBanData.uuid());
				//System.out.println(call.getCallDefinition());
				CallServer.getInstance().rootConnection.send(new CallDefData(call.getCallDefinition()));
			}
		}
		
	}
	
	@Override
	public String toString() {
		return "CallClientConnection{ connection=" + connection + ", user=" + user + " }";
	}
	
	@Override
	public void onDisconnect() {
		call.clientConnections.remove(user);
		UserLeaveData userLeaveData = new UserLeaveData(user);
		call.clientConnections.values().forEach(c -> c.connection.send(userLeaveData));
	}
	
}
