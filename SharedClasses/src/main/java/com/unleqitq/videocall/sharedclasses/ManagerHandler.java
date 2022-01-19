package com.unleqitq.videocall.sharedclasses;

import com.unleqitq.videocall.sharedclasses.call.ICallManager;
import com.unleqitq.videocall.sharedclasses.team.ITeamManager;
import com.unleqitq.videocall.sharedclasses.user.IUserManager;
import org.jetbrains.annotations.NotNull;

public class ManagerHandler implements IManagerHandler {
	
	@NotNull
	private final IUserManager userManager;
	@NotNull
	private final ITeamManager teamManager;
	@NotNull
	private final ICallManager callManager;
	
	public ManagerHandler(@NotNull IUserManager userManager, @NotNull ITeamManager teamManager, @NotNull ICallManager callManager) {
		this.callManager = callManager;
		this.teamManager = teamManager;
		this.userManager = userManager;
	}
	
	@NotNull
	@Override
	public ICallManager getCallManager() {
		return callManager;
	}
	
	@NotNull
	@Override
	public ITeamManager getTeamManager() {
		return teamManager;
	}
	
	@NotNull
	@Override
	public IUserManager getUserManager() {
		return userManager;
	}
	
}
