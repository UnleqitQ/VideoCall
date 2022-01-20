package com.unleqitq.videocall.sharedclasses.call;

import com.google.gson.JsonObject;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class CallDefinition {
	
	@NotNull
	private final IManagerHandler managerHandler;
	@NotNull
	private final UUID uuid;
	@NotNull
	private UUID creator;
	private long created;
	private long changed;
	private long time;
	private String name;
	
	public CallDefinition(@NotNull IManagerHandler managerHandler, @NotNull UUID uuid, @NotNull UUID creator, long time, String name) {
		this.managerHandler = managerHandler;
		this.uuid = uuid;
		this.creator = creator;
		created = System.currentTimeMillis();
		changed = System.currentTimeMillis();
		this.time = time;
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public String getName() {
		return name;
	}
	
	public long getChanged() {
		return changed;
	}
	
	public long getCreated() {
		return created;
	}
	
	public long getTime() {
		return time;
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
		if (!(o instanceof CallDefinition call))
			return false;
		return getUuid().equals(call.getUuid());
	}
	
	public IManagerHandler getManagerHandler() {
		return managerHandler;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getUuid());
	}
	
	@Nullable
	public static CallDefinition load(@NotNull IManagerHandler managerHandler, @NotNull JsonObject section) {
		CallDefinition callDefinition = switch (section.get("type").getAsString()) {
			case "team" -> TeamCallDefinition.load(managerHandler, section);
			case "basic" -> BasicCallDefinition.load(managerHandler, section);
			default -> null;
		};
		callDefinition.changed = section.get("changed").getAsLong();
		callDefinition.created = section.get("created").getAsLong();
		return callDefinition;
	}
	
	@NotNull
	public abstract JsonObject save();
	
	@Override
	public String toString() {
		return "CallDefinition" + save();
	}
	
}
