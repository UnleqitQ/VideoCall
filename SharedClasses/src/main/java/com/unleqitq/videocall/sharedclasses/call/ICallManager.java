package com.unleqitq.videocall.sharedclasses.call;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface ICallManager {
	
	@NotNull
	public IManagerHandler getManagerHandler();
	
	@NotNull
	public Map<UUID, CallDefinition> getCallMap();
	
	@Nullable
	public <T extends CallDefinition> T getCall(@NotNull UUID callId);
	
}
