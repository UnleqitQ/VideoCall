package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonParser;
import sharedclasses.IManagerHandler;
import sharedclasses.team.Team;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

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
	
	public Team getTeam(IManagerHandler managerHandler) {
		return Team.load(managerHandler, JsonParser.parseString(json).getAsJsonObject());
	}
	
}
