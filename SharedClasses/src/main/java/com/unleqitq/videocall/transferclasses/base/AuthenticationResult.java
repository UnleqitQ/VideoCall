package com.unleqitq.videocall.transferclasses.base;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record AuthenticationResult(int result, @Nullable UUID userUuid) implements Serializable {
	
	
	@Serial
	private static final long serialVersionUID = -4829113956035201290L;
	
}
