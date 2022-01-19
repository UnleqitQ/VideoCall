package com.unleqitq.videocall.sharedclasses;

import com.unleqitq.videocall.sharedclasses.call.ICallManager;
import com.unleqitq.videocall.sharedclasses.team.ITeamManager;
import com.unleqitq.videocall.sharedclasses.user.IUserManager;
import org.jetbrains.annotations.NotNull;

public interface IManagerHandler {
	
	@NotNull
	public IUserManager getUserManager();
	
	@NotNull
	public ITeamManager getTeamManager();
	
	@NotNull
	public ICallManager getCallManager();
	
}
