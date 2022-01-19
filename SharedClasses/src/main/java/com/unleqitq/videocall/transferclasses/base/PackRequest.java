package com.unleqitq.videocall.transferclasses.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public record PackRequest(@NotNull Serializable[] users, @NotNull Serializable[] calls, @NotNull Serializable[] teams)
		implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -3354943223074060260L;
	
	public static PackRequest create(Set<UUID> usersIn, Set<UUID> callsIn, Set<UUID> teamsIn) {
		Serializable[] usersOut = new Serializable[usersIn.size()];
		Serializable[] callsOut = new Serializable[callsIn.size()];
		Serializable[] teamsOut = new Serializable[teamsIn.size()];
		int i;
		i = 0;
		for (UUID uuid :
				usersIn) {
			usersOut[i++] = uuid;
		}
		i = 0;
		for (UUID uuid :
				callsIn) {
			callsOut[i++] = uuid;
		}
		i = 0;
		for (UUID uuid :
				teamsIn) {
			teamsOut[i++] = uuid;
		}
		return new PackRequest(usersOut, callsOut, teamsOut);
	}
	
}
