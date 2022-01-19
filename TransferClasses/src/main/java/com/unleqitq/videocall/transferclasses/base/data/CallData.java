package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonParser;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class CallData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 6758191692789788047L;
	
	@NotNull
	private final String json;
	
	public CallData(@NotNull CallDefinition call) {
		json = call.save().toString();
	}
	
	public String getJson() {
		return json;
	}
	
	public CallDefinition getCall(IManagerHandler managerHandler) {
		return CallDefinition.load(managerHandler, JsonParser.parseString(json).getAsJsonObject());
	}
	
}
