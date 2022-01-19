package com.unleqitq.videocall.client;

import org.apache.commons.collections4.SetUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UnknownValues {
	
	@NotNull
	public Set<UUID> users = SetUtils.synchronizedSet(new HashSet<>());
	
	@NotNull
	public Set<UUID> teams = SetUtils.synchronizedSet(new HashSet<>());
	
	@NotNull
	public Set<UUID> calls = SetUtils.synchronizedSet(new HashSet<>());
	
}
