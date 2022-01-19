package com.unleqitq.videocall.sharedclasses.call;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class Call {
	
	@NotNull
	private final IManagerHandler managerHandler;
	@NotNull
	private final UUID uuid;
	@NotNull
	private UUID creator;
	
	public Call(@NotNull IManagerHandler managerHandler, @NotNull UUID uuid, @NotNull UUID creator) {
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
	
	public abstract void addMember(@NotNull UUID user);
	
	public abstract void removeMember(@NotNull UUID user);
	
	public abstract void denyMember(@NotNull UUID user);
	
	public abstract boolean testMember(@NotNull UUID user);
	
	@NotNull
	public abstract Set<UUID> getMembers();
	
	@NotNull
	public UUID getUuid() {
		return uuid;
	}
	
	@NotNull
	public Predicate<UUID> getTestPredicate() {
		return this::testMember;
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
