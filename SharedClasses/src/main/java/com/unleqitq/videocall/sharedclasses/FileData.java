package com.unleqitq.videocall.sharedclasses;

import java.io.Serial;
import java.io.Serializable;

public record FileData(FileMeta meta, byte[] data) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 4337417041907173162L;
	
	
}
