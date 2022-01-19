package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonParser;
import sharedclasses.IManagerHandler;
import sharedclasses.user.User;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class UserData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -8907663600321918952L;
	
	@NotNull
	private final String json;
	
	public UserData(@NotNull User user) {
		json = user.save().toString();
	}
	
	public String getJson() {
		return json;
	}
	
	public User getUser(IManagerHandler managerHandler) {
		return User.load(managerHandler, JsonParser.parseString(json).getAsJsonObject());
	}
	
}
