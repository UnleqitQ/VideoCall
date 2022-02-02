package com.unleqitq.videocall.callserver.call;

import com.unleqitq.videocall.callserver.CallServer;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.user.CallGroupPermission;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import com.unleqitq.videocall.sharedclasses.user.CallUserPermission;
import com.unleqitq.videocall.sharedclasses.user.User;
import org.jetbrains.annotations.NotNull;

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
			callUsers.put(userUuid, new CallUser(uuid, user.getFirstname(), user.getLastname(), user.getUsername(),
					userUuid == callDefinition.getCreator() ? new CallUserPermission(10,
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
	
}
