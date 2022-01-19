package com.unleqitq.videocall.rootserver.managers;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.call.BasicCallDefinition;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.call.ICallManager;
import com.unleqitq.videocall.sharedclasses.call.TeamCallDefinition;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallManager implements ICallManager {
	
	@NotNull
	private final Map<UUID, CallDefinition> callMap = MapUtils.synchronizedMap(new HashMap<>());
	@NotNull
	private final IManagerHandler managerHandler;
	
	
	public CallManager(@NotNull IManagerHandler managerHandler) {
		this.managerHandler = managerHandler;
	}
	
	@NotNull
	@Override
	public IManagerHandler getManagerHandler() {
		return managerHandler;
	}
	
	@NotNull
	@Override
	public Map<UUID, CallDefinition> getCallMap() {
		return callMap;
	}
	
	@Nullable
	@Override
	public <T extends CallDefinition> T getCall(@NotNull UUID callId) {
		return (T) getCallMap().get(callId);
	}
	
	@NotNull
	public TeamCallDefinition createTeamCall(@NotNull UUID creator) {
		UUID callId;
		do {
			callId = UUID.randomUUID();
		} while (getCallMap().containsKey(callId));
		TeamCallDefinition call = new TeamCallDefinition(managerHandler, callId, creator);
		getCallMap().put(callId, call);
		return call;
	}
	
	@NotNull
	public BasicCallDefinition createBasicCall(@NotNull UUID creator) {
		UUID callId;
		do {
			callId = UUID.randomUUID();
		} while (getCallMap().containsKey(callId));
		BasicCallDefinition call = new BasicCallDefinition(managerHandler, callId, creator);
		getCallMap().put(callId, call);
		return call;
	}
	
}
