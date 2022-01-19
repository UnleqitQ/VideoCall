package com.unleqitq.videocall.rootserver.managers;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;

public class ManagerHandler implements IManagerHandler {
	
	@NotNull
	private UserManager userManager;
	@NotNull
	private TeamManager teamManager;
	@NotNull
	private CallManager callManager;
	
	public ManagerHandler() {
	}
	
	public ManagerHandler setCallManager(CallManager callManager) {
		this.callManager = callManager;
		return this;
	}
	
	public ManagerHandler setTeamManager(TeamManager teamManager) {
		this.teamManager = teamManager;
		return this;
	}
	
	public ManagerHandler setUserManager(UserManager userManager) {
		this.userManager = userManager;
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
	
}
