package com.unleqitq.videocall.rootserver.managers;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.jetbrains.annotations.NotNull;

public class ManagerHandler implements IManagerHandler {
	
	@NotNull
	private UserManager userManager;
	@NotNull
	private TeamManager teamManager;
	@NotNull
	private CallManager callManager;
	@NotNull
	private AccountManager accountManager;
	@NotNull
	private YAMLConfiguration configuration;
	
	public ManagerHandler() {
	}
	
	public ManagerHandler setCallManager(@NotNull CallManager callManager) {
		this.callManager = callManager;
		return this;
	}
	
	public ManagerHandler setTeamManager(@NotNull TeamManager teamManager) {
		this.teamManager = teamManager;
		return this;
	}
	
	public ManagerHandler setUserManager(@NotNull UserManager userManager) {
		this.userManager = userManager;
		return this;
	}
	
	public ManagerHandler setConfiguration(@NotNull YAMLConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}
	
	public ManagerHandler setAccountManager(@NotNull AccountManager accountManager) {
		this.accountManager = accountManager;
		return this;
	}
	
	@NotNull
	@Override
	public CallManager getCallManager() {
		return callManager;
	}
	
	@NotNull
	@Override
	public TeamManager getTeamManager() {
		return teamManager;
	}
	
	@NotNull
	@Override
	public UserManager getUserManager() {
		return userManager;
	}
	
	@NotNull
	@Override
	public YAMLConfiguration getConfiguration() {
		return configuration;
	}
	
	@NotNull
	@Override
	public AccountManager getAccountManager() {
		return accountManager;
	}
	
}
