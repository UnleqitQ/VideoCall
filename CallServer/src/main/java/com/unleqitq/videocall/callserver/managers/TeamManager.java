package com.unleqitq.videocall.callserver.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.team.AbstractTeamManager;
import com.unleqitq.videocall.sharedclasses.team.Team;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamManager extends AbstractTeamManager {
	
	@NotNull
	private final IManagerHandler managerHandler;
	
	
	public TeamManager(@NotNull IManagerHandler managerHandler) {
		this.managerHandler = managerHandler;
	}
	
	@NotNull
	@Override
	public IManagerHandler getManagerHandler() {
		return managerHandler;
	}
	
	@NotNull
	private final Map<UUID, Team> teamMap = MapUtils.synchronizedMap(new HashMap<>());
	
	
	@NotNull
	@Override
	public Map<UUID, Team> getTeamMap() {
		return teamMap;
	}
	
	@Nullable
	@Override
	public Team getTeam(@NotNull UUID uuid) {
		return getTeamMap().get(uuid);
	}
	
	@Override
	public void save(@NotNull File file) throws IOException {
		if (!file.exists()) {
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			file.createNewFile();
		}
		JsonArray array = new JsonArray();
		for (Team team : teamMap.values()) {
			array.add(team.save());
		}
		JsonWriter writer = new JsonWriter(new FileWriter(file));
		new Gson().toJson(array, writer);
		writer.close();
	}
	
	@Override
	public void addTeam(@NotNull Team team) {
		teamMap.put(team.getUuid(), team);
	}
	
	public void load(@NotNull File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		JsonReader reader = new JsonReader(new FileReader(file));
		JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
		for (JsonElement element : array) {
			addTeam(Team.load(managerHandler, element.getAsJsonObject()));
		}
		reader.close();
	}
	
	
	@NotNull
	public Team createTeam(@NotNull UUID creator, @NotNull String name) {
		UUID uuid;
		do {
			uuid = UUID.randomUUID();
		} while (getTeamMap().containsKey(uuid));
		Team team = new Team(managerHandler, uuid, creator, name);
		getTeamMap().put(uuid, team);
		return team;
	}
	
}
