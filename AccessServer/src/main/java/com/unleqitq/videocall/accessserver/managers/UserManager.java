package com.unleqitq.videocall.accessserver.managers;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.user.AbstractUserManager;
import com.unleqitq.videocall.sharedclasses.user.User;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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
	private final Map<UUID, User> userMap = MapUtils.synchronizedMap(new HashMap<>());
	
	
	@NotNull
	@Override
	public Map<UUID, User> getUserMap() {
		return userMap;
	}
	
	@Nullable
	@Override
	public User getUser(@NotNull UUID uuid) {
		return getUserMap().get(uuid);
	}
	
	@Override
	public void addUser(@NotNull User user) {
		userMap.put(user.getUuid(), user);
	}
	
	
	@NotNull
	public User createUser(@NotNull String firstname, @NotNull String lastname, @NotNull String username) {
		UUID uuid;
		do {
			uuid = UUID.randomUUID();
		} while (getUserMap().containsKey(uuid));
		User user = new User(managerHandler, uuid, firstname, lastname, username);
		getUserMap().put(uuid, user);
		return user;
	}
	
	@NotNull
	public User createUser(@NotNull UUID uuid, @NotNull String firstname, @NotNull String lastname, @NotNull String username) {
		User user = new User(managerHandler, uuid, firstname, lastname, username);
		getUserMap().put(uuid, user);
		return user;
	}
	
}
