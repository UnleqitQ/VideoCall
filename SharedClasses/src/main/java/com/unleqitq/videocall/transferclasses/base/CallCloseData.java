package com.unleqitq.videocall.transferclasses.base;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record CallCloseData(@Nullable UUID callUuid) implements Serializable {
	
	
	@Serial
	private static final long serialVersionUID = -6063566547391503988L;
	
}
