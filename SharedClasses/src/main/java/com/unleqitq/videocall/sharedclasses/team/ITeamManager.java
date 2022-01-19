package com.unleqitq.videocall.sharedclasses.team;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface ITeamManager {
	
	@NotNull
	public IManagerHandler getManagerHandler();
	
	@NotNull
	public Map<UUID, Team> getTeamMap();
	
	@Nullable
	public Team getTeam(@NotNull UUID uuid);
	
}
