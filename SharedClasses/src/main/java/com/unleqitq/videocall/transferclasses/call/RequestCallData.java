package com.unleqitq.videocall.transferclasses.call;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record RequestCallData(@NotNull UUID call) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 2646906509575630680L;
	
}
