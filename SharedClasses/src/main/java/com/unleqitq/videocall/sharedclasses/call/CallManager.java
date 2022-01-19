package com.unleqitq.videocall.sharedclasses.call;

import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallManager implements ICallManager {
	
	@NotNull
	private final Map<UUID, Call> callMap = MapUtils.synchronizedMap(new HashMap<>());
	
	public CallManager() {}
	
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
		TeamCall call = new TeamCall(callId, creator);
		getCallMap().put(callId, call);
		return call;
	}
	
	@NotNull
	public BasicCall createBasicCall(@NotNull UUID creator) {
		UUID callId;
		do {
			callId = UUID.randomUUID();
		} while (getCallMap().containsKey(callId));
		BasicCall call = new BasicCall(callId, creator);
		getCallMap().put(callId, call);
		return call;
	}
	
}
