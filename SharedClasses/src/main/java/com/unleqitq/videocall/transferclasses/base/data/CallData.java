package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unleqitq.videocall.sharedclasses.call.CallInformation;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class CallData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 3297709917483886053L;
	
	@NotNull
	private final String json;
	
	public CallData(@NotNull CallInformation call) {
		json = call.save().toString();
	}
	
	@NotNull
	public String getJson() {
		return json;
	}
	
	public CallInformation getCall() {
		return CallInformation.load(JsonParser.parseString(json).getAsJsonObject());
	}
	
	public JsonObject getJsonObject() {
		return JsonParser.parseString(json).getAsJsonObject();
	}
	
	public UUID getUUID() {
		return getJsonObject().has("uuid") ? UUID.fromString(getJsonObject().get("uuid").getAsString()) : null;
	}
	
}
