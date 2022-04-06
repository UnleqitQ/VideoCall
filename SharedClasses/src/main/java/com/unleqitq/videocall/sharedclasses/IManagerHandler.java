package com.unleqitq.videocall.sharedclasses;

import com.unleqitq.videocall.sharedclasses.account.AbstractAccountManager;
import com.unleqitq.videocall.sharedclasses.call.AbstractCallManager;
import com.unleqitq.videocall.sharedclasses.team.AbstractTeamManager;
import com.unleqitq.videocall.sharedclasses.user.AbstractUserManager;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.jetbrains.annotations.NotNull;

public interface IManagerHandler {
	
	IManagerHandler[] HANDLER = new IManagerHandler[1];
	
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
