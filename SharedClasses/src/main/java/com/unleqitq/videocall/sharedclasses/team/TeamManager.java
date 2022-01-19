package com.unleqitq.videocall.sharedclasses.team;

import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamManager implements ITeamManager {
	
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
	
	
	@NotNull
	public Team createTeam(@NotNull UUID creator) {
		UUID uuid;
		do {
			uuid = UUID.randomUUID();
		} while (getTeamMap().containsKey(uuid));
		Team team = new Team(uuid, creator);
		getTeamMap().put(uuid, team);
		return team;
	}
	
}
