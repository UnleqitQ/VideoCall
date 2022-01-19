package com.unleqitq.videocall.transferclasses.base.data;

import com.unleqitq.videocall.transferclasses.base.ListData;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record DataPack(@NotNull ListData calls, @NotNull ListData accounts, @NotNull ListData users,
                       @NotNull ListData teams) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -7920981748475733495L;
	
}
