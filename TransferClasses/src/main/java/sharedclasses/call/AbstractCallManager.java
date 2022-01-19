package sharedclasses.call;

import sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractCallManager {
	
	@NotNull
	public abstract IManagerHandler getManagerHandler();
	
	@NotNull
	public abstract Map<UUID, CallDefinition> getCallMap();
	
	@Nullable
	public abstract <T extends CallDefinition> T getCall(@NotNull UUID uuid);
	
	public abstract void save(@NotNull File file) throws IOException;
	
	public abstract void load(@NotNull File file) throws IOException;
	
	public abstract void addCall(@NotNull CallDefinition call);
	
}
