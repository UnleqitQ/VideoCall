package com.unleqitq.videocall.sharedclasses.call;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeamCall extends Call {
	
	@NotNull
	private final Set<UUID> teams = new HashSet<>();
	@NotNull
	private final Set<UUID> members = new HashSet<>();
	@NotNull
	private final Set<UUID> denied = new HashSet<>();
	
	public TeamCall(@NotNull IManagerHandler managerHandler, @NotNull UUID uuid, @NotNull UUID creator) {
		super(managerHandler, uuid, creator);
	}
	
	@Override
	public void addMember(@NotNull UUID user) {
		members.add(user);
		denied.remove(user);
	}
	
	@Override
	public void removeMember(@NotNull UUID user) {
		members.remove(user);
		denied.remove(user);
	}
	
	@Override
	public void denyMember(@NotNull UUID user) {
		denied.add(user);
		members.remove(user);
		
	}
	
	@Override
	public boolean testMember(@NotNull UUID user) {
		return false;
	}
	
	@Override
	@NotNull
	public Set<UUID> getMembers() {
		Set<UUID> result = new HashSet<>();
		result.addAll(members);
		result.removeAll(denied);
		return result;
	}
	
}
