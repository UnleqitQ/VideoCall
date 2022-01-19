package com.unleqitq.videocall.transferclasses.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record CallInformation(@NotNull String host, @NotNull int port, @NotNull UUID call) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -2772669104831032800L;
	
	@NotNull
	public String getHost() {
		return host;
	}
	
	@NotNull
	public int getPort() {
		return port;
	}
	
	@NotNull
	public UUID getCall() {
		return call;
	}
	
}
