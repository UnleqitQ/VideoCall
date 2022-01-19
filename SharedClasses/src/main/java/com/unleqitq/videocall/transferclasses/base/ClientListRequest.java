package com.unleqitq.videocall.transferclasses.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record ClientListRequest(@NotNull UUID user) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -5779380221148362137L;
	
}
