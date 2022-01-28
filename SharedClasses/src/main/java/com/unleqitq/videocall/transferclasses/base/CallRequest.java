package com.unleqitq.videocall.transferclasses.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record CallRequest(@NotNull UUID uuid) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -8458757310920182061L;
	
	@Override
	public String toString() {
		return "CallRequest{ uuid=" + uuid + " }";
	}
	
}
