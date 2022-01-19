package com.unleqitq.videocall.sharedclasses.team;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.call.Call;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Team {
	
	@NotNull
	private IManagerHandler managerHandler;
	@NotNull
	private final UUID uuid;
	@NotNull
	private UUID creator;
	@NotNull
	private final Set<UUID> members = new HashSet<>();
	
	public Team(@NotNull IManagerHandler managerHandler, @NotNull UUID uuid, @NotNull UUID creator) {
		this.managerHandler = managerHandler;
		this.uuid = uuid;
		this.creator = creator;
	}
	
	public void setCreatorId(@NotNull UUID creator) {
		this.creator = creator;
	}
	
	@NotNull
	public UUID getCreator() {
		return creator;
	}
	
	public void addUser(@NotNull UUID user) {
		members.add(user);
	}
	
	public void removeUser(@NotNull UUID user) {
		members.remove(user);
	}
	
	@NotNull
	public UUID getUuid() {
		return uuid;
	}
	
	@NotNull
	public Set<UUID> getMembers() {
		return members;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Call call))
			return false;
		return getUuid().equals(call.getUuid());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getUuid());
	}
	
}
