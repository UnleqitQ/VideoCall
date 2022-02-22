package com.unleqitq.videocall.callserver.call;

import com.unleqitq.videocall.sharedclasses.DisconnectListener;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.data.CallUserData;
import com.unleqitq.videocall.transferclasses.call.AudioData;
import com.unleqitq.videocall.transferclasses.call.ScreenVideoData;
import com.unleqitq.videocall.transferclasses.call.VideoData;
import org.jetbrains.annotations.NotNull;

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
			call.clientConnections.values().stream()/*.filter(c -> !c.user.equals(user))*/.forEach(
					c -> c.connection.send(r));
		}
		if (data.getData() instanceof AudioData audioData) {
			System.out.println(audioData);
			AudioData r = new AudioData(audioData.data(), audioData.offset(), audioData.bufferSize(),
					audioData.format(), user);
			call.clientConnections.values().stream()/*.filter(c -> !c.user.equals(user))*/.forEach(
					c -> c.connection.send(r));
		}
	}
	
	@Override
	public String toString() {
		return "CallClientConnection{ connection=" + connection + ", user=" + user + " }";
	}
	
	@Override
	public void onDisconnect() {
		call.clientConnections.remove(user);
	}
	
}
