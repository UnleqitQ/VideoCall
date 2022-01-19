package sharedclasses.team;

import sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractTeamManager {
	
	@NotNull
	public abstract IManagerHandler getManagerHandler();
	
	@NotNull
	public abstract Map<UUID, Team> getTeamMap();
	
	@Nullable
	public abstract Team getTeam(@NotNull UUID uuid);
	
	public abstract void save(@NotNull File file) throws IOException;
	
	public abstract void addTeam(@NotNull Team team);
	
	public abstract void load(@NotNull File file) throws IOException;
	
}
