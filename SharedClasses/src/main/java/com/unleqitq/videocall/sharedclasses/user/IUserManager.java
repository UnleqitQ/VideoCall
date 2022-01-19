package com.unleqitq.videocall.sharedclasses.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface IUserManager {
	
	@NotNull
	public Map<UUID, User> getUserMap();
	
	@Nullable
	public User getUser(@NotNull UUID uuid);
	
}
