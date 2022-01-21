package com.unleqitq.videocall.sharedclasses.call;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BasicCallDefinition extends CallDefinition {
	
	
	private Set<UUID> members = new HashSet<>();
	private Set<UUID> denied = new HashSet<>();
	
	public BasicCallDefinition(@NotNull IManagerHandler managerHandler, @NotNull UUID uuid, @NotNull UUID creator, long time, String name) {
		super(managerHandler, uuid, creator, time, name);
	}
	
	@Override
	public void addMember(@NotNull UUID user) {
		members.add(user);
		denied.remove(user);
	}
	
	@Override
	public void removeMember(@NotNull UUID user) {
		members.remove(user);
		denied.remove(user);
	}
	
	@Override
	public void denyMember(@NotNull UUID user) {
		denied.add(user);
		members.remove(user);
	}
	
	@Override
	public boolean testMember(@NotNull UUID user) {
		if (denied.contains(user))
			return false;
		if (members.contains(user))
			return true;
		return false;
	}
	
	@Override
	@NotNull
	public Set<UUID> getMembers() {
		Set<UUID> result = new HashSet<>();
		result.addAll(members);
		result.removeAll(denied);
		return result;
	}
	
	@NotNull
	public static BasicCallDefinition load(@NotNull IManagerHandler managerHandler, @NotNull JsonObject section) {
		BasicCallDefinition callDefinition = new BasicCallDefinition(managerHandler,
				UUID.fromString(section.get("uuid").getAsString()),
				UUID.fromString(section.get("creator").getAsString()), section.get("time").getAsLong(),
				section.get("name").getAsString());
		for (JsonElement element : section.getAsJsonArray("members")) {
			callDefinition.addMember(UUID.fromString(element.getAsString()));
		}
		for (JsonElement element : section.getAsJsonArray("denied")) {
			callDefinition.denyMember(UUID.fromString(element.getAsString()));
		}
		return callDefinition;
	}
	
	@NotNull
	@Override
	public JsonObject save() {
		JsonObject object = new JsonObject();
		object.add("uuid", new JsonPrimitive(getUuid().toString()));
		object.add("creator", new JsonPrimitive(getCreator().toString()));
		JsonArray memberArray = new JsonArray(members.size());
		JsonArray deniedArray = new JsonArray(denied.size());
		for (UUID user : members) {
			memberArray.add(user.toString());
		}
		for (UUID user : denied) {
			deniedArray.add(user.toString());
		}
		object.add("members", memberArray);
		object.add("denied", deniedArray);
		object.add("time", new JsonPrimitive(getTime()));
		object.add("name", new JsonPrimitive(getName()));
		object.add("created", new JsonPrimitive(getCreated()));
		object.add("changed", new JsonPrimitive(getChanged()));
		object.add("type", new JsonPrimitive("basic"));
		return object;
	}
	
	@Override
	public String toString() {
		return "BasicCallDefinition" + save();
	}
	
}
