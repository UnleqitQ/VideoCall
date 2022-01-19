package com.unleqitq.videocall.transferclasses.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record AccessInformation(@NotNull String host, @NotNull int port) implements Serializable {
	
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
	
}
