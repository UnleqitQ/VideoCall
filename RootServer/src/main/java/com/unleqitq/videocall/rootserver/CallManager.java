package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.call.BasicCall;
import com.unleqitq.videocall.sharedclasses.call.Call;
import com.unleqitq.videocall.sharedclasses.call.ICallManager;
import com.unleqitq.videocall.sharedclasses.call.TeamCall;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallManager implements ICallManager {
	
	@NotNull
	private final Map<UUID, Call> callMap = MapUtils.synchronizedMap(new HashMap<>());
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
	public Map<UUID, Call> getCallMap() {
		return callMap;
	}
	
	@Nullable
	@Override
	public <T extends Call> T getCall(@NotNull UUID callId) {
		return (T) getCallMap().get(callId);
	}
	
	@NotNull
	public TeamCall createTeamCall(@NotNull UUID creator) {
		UUID callId;
		do {
			callId = UUID.randomUUID();
		} while (getCallMap().containsKey(callId));
		TeamCall call = new TeamCall(managerHandler, callId, creator);
		getCallMap().put(callId, call);
		return call;
	}
	
	@NotNull
	public BasicCall createBasicCall(@NotNull UUID creator) {
		UUID callId;
		do {
			callId = UUID.randomUUID();
		} while (getCallMap().containsKey(callId));
		BasicCall call = new BasicCall(managerHandler, callId, creator);
		getCallMap().put(callId, call);
		return call;
	}
	
}
