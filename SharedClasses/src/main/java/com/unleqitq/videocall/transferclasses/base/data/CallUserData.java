package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonParser;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class CallUserData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -8907663600321918952L;
	
	@NotNull
	private final String json;
	
	public CallUserData(@NotNull CallUser user) {
		json = user.save().toString();
	}
	
	public String getJson() {
		return json;
	}
	
	public CallUser getUser() {
		return CallUser.load(JsonParser.parseString(json).getAsJsonObject());
	}
	
}
