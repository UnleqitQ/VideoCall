package sharedclasses;

import sharedclasses.account.AbstractAccountManager;
import sharedclasses.call.AbstractCallManager;
import sharedclasses.team.AbstractTeamManager;
import sharedclasses.user.AbstractUserManager;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.jetbrains.annotations.NotNull;

public interface IManagerHandler {
	
	@NotNull
	public AbstractUserManager getUserManager();
	
	@NotNull
	public AbstractTeamManager getTeamManager();
	
	@NotNull
	public AbstractCallManager getCallManager();
	
	@NotNull
	public AbstractAccountManager getAccountManager();
	
	@NotNull
	public YAMLConfiguration getConfiguration();
	
}
