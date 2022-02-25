package com.unleqitq.videocall.transferclasses.call;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record UserBanData(UUID uuid) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 3668633869793917048L;
	
	
}
