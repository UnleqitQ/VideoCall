package com.unleqitq.videocall.transferclasses.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record ListData(@NotNull Serializable[] data) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -684427581272384073L;
	
	@NotNull
	public Serializable[] getData() {
		return data;
	}
	
}
