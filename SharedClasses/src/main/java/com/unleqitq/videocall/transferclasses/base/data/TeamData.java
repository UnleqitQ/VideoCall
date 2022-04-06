package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonObject;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.team.Team;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.UUID;

public class TeamData implements Externalizable {
	
	@Serial
	private static final long serialVersionUID = 383092861518339908L;
	
	@NotNull
	private Team team;
	
	public TeamData() {
	
	}
	
	public TeamData(@NotNull Team team) {
		this.team = team;
	}
	
	public String getJson() {
		return team.save().toString();
	}
	
	public JsonObject getJsonObject() {
		return team.save();
	}
	
	public UUID getUUID() {
		return team.getUuid();
	}
	
	public Team getTeam(IManagerHandler managerHandler) {
		return team;
	}
	
	public Team getTeam() {
		return team;
	}
	
	@Override
	public String toString() {
		return "TeamData " + getJson();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		team.writeExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException {
		team = new Team();
		team.readExternal(in);
	}
	
}
