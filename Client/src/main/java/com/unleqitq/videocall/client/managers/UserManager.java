package com.unleqitq.videocall.client.managers;

import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.user.AbstractUserManager;
import com.unleqitq.videocall.sharedclasses.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class UserManager extends AbstractUserManager {
	
	@NotNull
	private final IManagerHandler managerHandler;
	
	
	public UserManager(@NotNull IManagerHandler managerHandler) {
		this.managerHandler = managerHandler;
	}
	
	@NotNull
	@Override
	public IManagerHandler getManagerHandler() {
		return managerHandler;
	}
	
	
	@NotNull
	@Override
	public Map<UUID, User> getUserMap() {
		return Client.getInstance().userCache.asMap();
	}
	
	@Nullable
	@Override
	public User getUser(@NotNull UUID uuid) {
		return Client.getInstance().getUser(uuid);
	}
	
	@Override
	public void addUser(@NotNull User user) {
		Client.getInstance().userCache.put(user.getUuid(), user);
	}
	
	
}
