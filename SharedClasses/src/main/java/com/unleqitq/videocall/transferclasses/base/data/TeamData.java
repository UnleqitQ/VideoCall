package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.team.Team;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class TeamData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 383092861518339908L;
	
	@NotNull
	private final String json;
	
	public TeamData(@NotNull Team team) {
		json = team.save().toString();
	}
	
	public String getJson() {
		return json;
	}
	
	public JsonObject getJsonObject() {
		return JsonParser.parseString(json).getAsJsonObject();
	}
	
	public UUID getUUID() {
		return getJsonObject().has("uuid") ? UUID.fromString(getJsonObject().get("uuid").getAsString()) : null;
	}
	
	public Team getTeam(IManagerHandler managerHandler) {
		return Team.load(managerHandler, JsonParser.parseString(json).getAsJsonObject());
	}
	
	@Override
	public String toString() {
		return "TeamData " + json;
	}
	
}
