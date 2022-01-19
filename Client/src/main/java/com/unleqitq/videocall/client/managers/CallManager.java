package com.unleqitq.videocall.client.managers;

import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.call.AbstractCallManager;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class CallManager extends AbstractCallManager {
	
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
		return Client.getInstance().callCache.asMap();
	}
	
	@Nullable
	@Override
	public <T extends CallDefinition> T getCall(@NotNull UUID callId) {
		return (T) Client.getInstance().getCall(callId);
	}
	
	
	public void save(@NotNull File file) throws IOException {
	}
	
	@Override
	public void load(@NotNull File file) throws IOException {
	}
	
	@Override
	public void addCall(@NotNull CallDefinition call) {
		Client.getInstance().callCache.put(call.getUuid(), call);
	}
	
}
