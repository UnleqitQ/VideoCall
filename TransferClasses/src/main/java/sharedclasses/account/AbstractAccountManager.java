package sharedclasses.account;

import sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractAccountManager {
	
	
	@NotNull
	public abstract IManagerHandler getManagerHandler();
	
	@NotNull
	public abstract Map<UUID, Account> getAccountMap();
	
	@NotNull
	public abstract Map<String, Account> getAccountNameMap();
	
	@Nullable
	public abstract Account getAccount(@NotNull UUID uuid);
	
	@Nullable
	public abstract Account getAccount(@NotNull String username);
	
	public abstract void addAccount(@NotNull Account account);
	
	public abstract void save(@NotNull File file) throws IOException;
	
	public abstract void load(@NotNull File file) throws IOException;
	
}
