package com.unleqitq.videocall.sharedclasses.team;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Team {
	
	@NotNull
	private IManagerHandler managerHandler;
	
	private final UUID uuid;
	
	@NotNull
	private UUID creator;
	@NotNull
	private final Set<UUID> members = new HashSet<>();
	@NotNull
	private String name;
	
	public Team(@NotNull IManagerHandler managerHandler, UUID uuid, @NotNull UUID creator, @NotNull String name) {
		this.managerHandler = managerHandler;
		this.uuid = uuid;
		this.creator = creator;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public UUID getUuid() {
		return uuid;
	}
	
	@NotNull
	public Set<UUID> getMembers() {
		Set<UUID> members0 = new HashSet<>(members);
		members0.add(creator);
		return members0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CallDefinition call))
			return false;
		return getUuid().equals(call.getUuid());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getUuid());
	}
	
	@NotNull
	public static Team load(@NotNull IManagerHandler managerHandler, @NotNull JsonObject section) {
		Team team = new Team(managerHandler, UUID.fromString(section.get("uuid").getAsString()),
				UUID.fromString(section.get("creator").getAsString()), section.get("name").getAsString());
		for (JsonElement element : section.getAsJsonArray("members")) {
			team.addUser(UUID.fromString(element.getAsString()));
		}
		return team;
	}
	
	@NotNull
	public JsonObject save() {
		JsonObject object = new JsonObject();
		if (getUuid() != null)
			object.add("uuid", new JsonPrimitive(getUuid().toString()));
		object.add("name", new JsonPrimitive(getName()));
		object.add("creator", new JsonPrimitive(getCreator().toString()));
		JsonArray memberArray = new JsonArray(members.size());
		for (UUID user : members) {
			memberArray.add(user.toString());
		}
		object.add("members", memberArray);
		return object;
	}
	
	@Override
	public String toString() {
		return "Team" + save();
	}
	
}
