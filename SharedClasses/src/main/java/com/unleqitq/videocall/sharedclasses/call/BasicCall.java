package com.unleqitq.videocall.sharedclasses.call;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BasicCall extends Call {
	
	
	private Set<UUID> members = new HashSet<>();
	private Set<UUID> denied = new HashSet<>();
	
	public BasicCall(@NotNull IManagerHandler managerHandler, @NotNull UUID uuid, @NotNull UUID creator) {
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
		if (denied.contains(user))
			return false;
		if (members.contains(user))
			return true;
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
