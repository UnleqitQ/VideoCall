package com.unleqitq.videocall.transferclasses.connection;

import java.io.Serial;
import java.io.Serializable;

public class MachineInformation implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 772573162126488624L;
	
	private final int port;
	private final long freeMemory;
	
	public MachineInformation(int port, long freeMemory) {
		this.port = port;
		this.freeMemory = freeMemory;
	}
	
	public int getPort() {
		return port;
	}
	
	public long getFreeMemory() {
		return freeMemory;
	}
	
	@Override
	public String toString() {
		return "MachineInformation{ port=" + port + ", freeMemory=" + freeMemory + " }";
	}
	
}
