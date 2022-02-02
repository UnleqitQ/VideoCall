package com.unleqitq.videocall.transferclasses.call;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record AudioData(byte[] data, int offset, int bufferSize, UUID user) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 5429719147396827011L;
	
}
