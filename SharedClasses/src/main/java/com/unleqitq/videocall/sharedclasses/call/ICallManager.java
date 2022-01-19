package com.unleqitq.videocall.sharedclasses.call;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface ICallManager {
	
	@NotNull
	public Map<UUID, Call> getCallMap();
	
	@Nullable
	public <T extends Call> T getCall(@NotNull UUID callId);
	
}
