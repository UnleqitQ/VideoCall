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
	public Map<UUID, Call> getCallMap();
	
	@Nullable
	public <T extends Call> T getCall(@NotNull UUID callId);
	
}
