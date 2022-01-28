package com.unleqitq.videocall.transferclasses.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record AccessInformation(@NotNull String host, int port) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -2772669104831032800L;
	
	@NotNull
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return "AccessInformation{host='" + getHost() + "', port=" + getPort() + "}";
	}
	
}
