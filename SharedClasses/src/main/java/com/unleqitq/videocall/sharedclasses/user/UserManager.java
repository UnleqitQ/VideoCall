package com.unleqitq.videocall.sharedclasses.user;

import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager implements IUserManager {
	
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
	
	
	@NotNull
	public User createUser(@NotNull String firstname, @NotNull String lastname, @NotNull String username) {
		UUID uuid;
		do {
			uuid = UUID.randomUUID();
		} while (getUserMap().containsKey(uuid));
		User user = new User(this, uuid, firstname, lastname, username);
		getUserMap().put(uuid, user);
		return user;
	}
	
}
