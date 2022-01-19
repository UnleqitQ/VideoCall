package com.unleqitq.videocall.transferclasses.connection;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record MachineInformation(@NotNull int port, @NotNull long freeMemory) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 772573162126488624L;
	
	@NotNull
	public int getPort() {
		return port;
	}
	
	@NotNull
	public long getFreeMemory() {
		return freeMemory;
	}
	
	@Override
	public String toString() {
		return "MachineInformation{ port=" + port + ", freeMemory=" + freeMemory + " }";
	}
	
}
