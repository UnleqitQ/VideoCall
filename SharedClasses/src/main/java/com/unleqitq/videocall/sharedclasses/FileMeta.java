package com.unleqitq.videocall.sharedclasses;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FileMeta {
	
	private UUID uuid;
	/**
	 * Relative to virtual Root
	 */
	private String path;
	private String realPath;
	private UUID team;
	private String name;
	private String type;
	private String description;
	
	public FileMeta(UUID uuid, String path, UUID team, String name, String type) {
		this.uuid = uuid;
		this.path = path;
		this.team = team;
		this.name = name;
		this.type = type;
		realPath = team + "/" + team + "_" + uuid;
		description = "";
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getRealPath() {
		return realPath;
	}
	
	public UUID getTeam() {
		return team;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getType() {
		return type;
	}
	
	public JsonObject save() {
		JsonObject object = new JsonObject();
		object.add("description", new JsonPrimitive(description));
		object.add("name", new JsonPrimitive(name));
		object.add("team", new JsonPrimitive(team.toString()));
		object.add("uuid", new JsonPrimitive(uuid.toString()));
		object.add("path", new JsonPrimitive(path));
		object.add("type", new JsonPrimitive(type));
		return object;
	}
	
	@Override
	public String toString() {
		return "FileData " + save();
	}
	
	public static FileMeta load(@NotNull JsonObject json) {
		FileMeta fileMeta = new FileMeta(UUID.fromString(json.get("uuid").getAsString()),
				json.get("path").getAsString(), UUID.fromString(json.get("uuid").getAsString()),
				json.get("name").getAsString(), json.get("type").getAsString());
		if (json.has("description"))
			fileMeta.setDescription(json.get("description").getAsString());
		return fileMeta;
	}
	
}
