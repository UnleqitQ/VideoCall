package com.unleqitq.videocall.sharedclasses.call;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.ObjectHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeamCallDefinition extends CallDefinition {
	
	@NotNull
	private final Set<UUID> teams = new HashSet<>();
	@NotNull
	private final Set<UUID> members = new HashSet<>();
	@NotNull
	private final Set<UUID> denied = new HashSet<>();
	
	public TeamCallDefinition() {}
	
	public TeamCallDefinition(@NotNull IManagerHandler managerHandler, UUID uuid, @NotNull UUID creator, long time, String name) {
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
	
	public void addTeam(@NotNull UUID team) {
		teams.add(team);
	}
	
	public void removeTeam(@NotNull UUID team) {
		teams.remove(team);
	}
	
	public Set<UUID> getTeams() {
		return teams;
	}
	
	@Override
	public boolean testMember(@NotNull UUID user) {
		if (user.equals(getCreator()))
			return true;
		if (denied.contains(user))
			return false;
		if (members.contains(user))
			return true;
		for (UUID team : teams) {
			if (getManagerHandler().getTeamManager().getTeam(
					team) != null && getManagerHandler().getTeamManager().getTeam(team).getMembers().contains(user))
				return true;
		}
		return false;
	}
	
	@Override
	@NotNull
	public Set<UUID> getMembers() {
		Set<UUID> result = new HashSet<>();
		result.addAll(members);
		for (UUID team : teams) {
			if (getManagerHandler().getTeamManager().getTeam(team) != null)
				result.addAll(getManagerHandler().getTeamManager().getTeam(team).getMembers());
		}
		result.removeAll(denied);
		result.add(getCreator());
		return result;
	}
	
	@NotNull
	public static TeamCallDefinition load(@NotNull IManagerHandler managerHandler, @NotNull JsonObject section) {
		TeamCallDefinition callDefinition = new TeamCallDefinition(managerHandler,
				section.has("uuid") ? UUID.fromString(section.get("uuid").getAsString()) : null,
				UUID.fromString(section.get("creator").getAsString()), section.get("time").getAsLong(),
				section.get("name").getAsString());
		callDefinition.setDescription(section.get("description").getAsString());
		for (JsonElement element : section.getAsJsonArray("members")) {
			callDefinition.addMember(UUID.fromString(element.getAsString()));
		}
		for (JsonElement element : section.getAsJsonArray("denied")) {
			callDefinition.denyMember(UUID.fromString(element.getAsString()));
		}
		for (JsonElement element : section.getAsJsonArray("teams")) {
			callDefinition.addTeam(UUID.fromString(element.getAsString()));
		}
		return callDefinition;
	}
	
	@NotNull
	@Override
	public JsonObject save() {
		JsonObject object = new JsonObject();
		if (getUuid() != null)
			object.add("uuid", new JsonPrimitive(getUuid().toString()));
		object.add("creator", new JsonPrimitive(getCreator().toString()));
		JsonArray memberArray = new JsonArray(members.size());
		JsonArray deniedArray = new JsonArray(denied.size());
		JsonArray teamArray = new JsonArray(teams.size());
		for (UUID user : members) {
			memberArray.add(user.toString());
		}
		for (UUID user : denied) {
			deniedArray.add(user.toString());
		}
		for (UUID team : teams) {
			teamArray.add(team.toString());
		}
		object.add("members", memberArray);
		object.add("denied", deniedArray);
		object.add("teams", teamArray);
		object.add("time", new JsonPrimitive(getTime()));
		object.add("name", new JsonPrimitive(getName()));
		object.add("created", new JsonPrimitive(getCreated()));
		object.add("changed", new JsonPrimitive(getChanged()));
		object.add("type", new JsonPrimitive("team"));
		object.add("description", new JsonPrimitive(getDescription()));
		return object;
	}
	
	@Override
	public String toString() {
		return "TeamCallDefinition" + save();
	}
	
	public Set<UUID> members() {
		return Collections.unmodifiableSet(members);
	}
	
	public Set<UUID> denied() {
		return Collections.unmodifiableSet(denied);
	}
	
	public Set<UUID> teams() {
		return Collections.unmodifiableSet(teams);
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		ObjectHandler.ObjectOutputHandler h = new ObjectHandler.ObjectOutputHandler(out);
		out.writeInt(members.size());
		members.forEach(v -> {
			try {
				h.writeUuid(v);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		out.writeInt(denied.size());
		denied.forEach(v -> {
			try {
				h.writeUuid(v);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		out.writeInt(teams.size());
		teams.forEach(v -> {
			try {
				h.writeUuid(v);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException {
		super.readExternal(in);
		ObjectHandler.ObjectInputHandler h = new ObjectHandler.ObjectInputHandler(in);
		int m = in.readInt();
		for (int i = 0; i < m; i++) {
			members.add(h.readUuid());
		}
		int d = in.readInt();
		for (int i = 0; i < d; i++) {
			denied.add(h.readUuid());
		}
		int t = in.readInt();
		for (int i = 0; i < t; i++) {
			teams.add(h.readUuid());
		}
	}
	
}
