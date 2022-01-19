package com.unleqitq.videocall.client.managers;

import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.team.AbstractTeamManager;
import com.unleqitq.videocall.sharedclasses.team.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
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
	@Override
	public Map<UUID, Team> getTeamMap() {
		return Client.getInstance().teamCache.asMap();
	}
	
	@Nullable
	@Override
	public Team getTeam(@NotNull UUID uuid) {
		return Client.getInstance().getTeam(uuid);
	}
	
	@Override
	public void save(@NotNull File file) throws IOException {
	}
	
	@Override
	public void addTeam(@NotNull Team team) {
		Client.getInstance().teamCache.put(team.getUuid(), team);
	}
	
	public void load(@NotNull File file) throws IOException {
	}
	
}
