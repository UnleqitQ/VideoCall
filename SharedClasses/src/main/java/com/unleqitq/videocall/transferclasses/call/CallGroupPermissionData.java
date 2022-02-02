package com.unleqitq.videocall.transferclasses.call;

import com.google.gson.JsonParser;
import com.unleqitq.videocall.sharedclasses.user.CallGroupPermission;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class CallGroupPermissionData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -1860983368243273096L;
	@NotNull
	private final String json;
	
	public CallGroupPermissionData(@NotNull CallGroupPermission permission) {
		json = permission.save().toString();
	}
	
	public String getJson() {
		return json;
	}
	
	public CallGroupPermission getPermission() {
		return CallGroupPermission.load(JsonParser.parseString(json).getAsJsonObject());
	}
	
}
