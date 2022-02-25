package com.unleqitq.videocall.callserver.call;

import com.unleqitq.videocall.callserver.CallServer;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.user.CallGroupPermission;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import com.unleqitq.videocall.sharedclasses.user.CallUserPermission;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.base.ListData;
import com.unleqitq.videocall.transferclasses.base.data.CallUserData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Call {
	
	@NotNull
	public final UUID uuid;
	public Map<UUID, CallClientConnection> clientConnections = new ConcurrentHashMap<>();
	public Map<UUID, CallUser> callUsers = new ConcurrentHashMap<>();
	public long lastCheck = System.currentTimeMillis();
	
	public Call(@NotNull UUID uuid) {
		this.uuid = uuid;
		CallDefinition callDefinition = getCallDefinition();
		for (UUID userUuid : callDefinition.getMembers()) {
			User user = CallServer.getInstance().getManagerHandler().getUserManager().getUser(userUuid);
			callUsers.put(userUuid, new CallUser(userUuid, user.getFirstname(), user.getLastname(), user.getUsername(),
					userUuid.equals(callDefinition.getCreator()) ? new CallUserPermission(10,
							CallGroupPermission.fullPerms) : new CallUserPermission(0, CallGroupPermission.noPerms)));
		}
	}
	
	@NotNull
	public CallDefinition getCallDefinition() {
		return Objects.requireNonNull(CallServer.getInstance().getManagerHandler().getCallManager().getCall(uuid));
	}
	
	public boolean check() {
		if (clientConnections.isEmpty()) {
			if (System.currentTimeMillis() - lastCheck > CallServer.getInstance().getConfiguration().getInt("callDecay",
					10) * 1000L) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			lastCheck = System.currentTimeMillis();
			return false;
		}
	}
	
	public CallUser getCallUser(UUID userUuid) {
		if (!callUsers.containsKey(userUuid)) {
			User user = CallServer.getInstance().getManagerHandler().getUserManager().getUser(userUuid);
			CallUser callUser = new CallUser(userUuid, user.getFirstname(), user.getLastname(), user.getUsername(),
					getCallDefinition().getCreator().equals(userUuid) ? new CallUserPermission(10,
							CallGroupPermission.fullPerms) : new CallUserPermission(0, CallGroupPermission.noPerms));
			callUsers.put(userUuid, callUser);
			return callUser;
		}
		return callUsers.get(userUuid);
	}
	
	public void addUser(UUID userUuid) {
		if (!getCallDefinition().testMember(userUuid)) {
			try {
				clientConnections.get(userUuid).connection.getSocket().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		CallUserData callUserData = new CallUserData(getCallUser(userUuid));
		System.out.println("Sending: " + callUserData);
		for (CallClientConnection connection : clientConnections.values()) {
			connection.connection.send(callUserData);
		}
		
		Serializable[] array = new Serializable[clientConnections.size()];
		int i = 0;
		for (UUID userUuid1 : clientConnections.keySet()) {
			array[i++] = getCallUser(userUuid1);
		}
		ListData listData = new ListData(array);
		System.out.println("Sending: " + listData);
		clientConnections.get(userUuid).connection.send(listData);
	}
	
}
