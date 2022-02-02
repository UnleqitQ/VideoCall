package com.unleqitq.videocall.sharedclasses.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.Serial;

public class CallUserPermission extends CallGroupPermission {
	
	@Serial
	private static final long serialVersionUID = -3928640090431289508L;
	
	public boolean overrideMuteOthers;
	public boolean overrideKick;
	public boolean overrideBan;
	public boolean overrideShareScreen;
	public CallGroupPermission groupPermission;
	
	public CallUserPermission(int level, CallGroupPermission groupPermission) {
		super(level);
		this.groupPermission = groupPermission;
	}
	
	public boolean isBan() {
		return overrideBan ? ban : groupPermission.ban;
	}
	
	public boolean isKick() {
		return overrideKick ? kick : groupPermission.kick;
	}
	
	public boolean isMuteOthers() {
		return overrideMuteOthers ? muteOthers : groupPermission.muteOthers;
	}
	
	public boolean isShareScreen() {
		return overrideShareScreen ? shareScreen : groupPermission.shareScreen;
	}
	
	@Override
	public String getName() {
		return groupPermission.name;
	}
	
	@Override
	public JsonObject save() {
		JsonObject json = super.save();
		json.add("overrideMuteOthers", new JsonPrimitive(overrideMuteOthers));
		json.add("overrideKick", new JsonPrimitive(overrideKick));
		json.add("overrideBan", new JsonPrimitive(overrideBan));
		json.add("overrideShareScreen", new JsonPrimitive(overrideShareScreen));
		json.add("group", groupPermission.save());
		return json;
	}
	
	public static CallUserPermission load(JsonObject json) {
		CallUserPermission callUserPermission = new CallUserPermission(json.get("level").getAsInt(),
				load(json.get("group").getAsJsonObject()));
		callUserPermission.ban = json.get("ban").getAsBoolean();
		callUserPermission.kick = json.get("kick").getAsBoolean();
		callUserPermission.muteOthers = json.get("muteOthers").getAsBoolean();
		callUserPermission.shareScreen = json.get("shareScreen").getAsBoolean();
		callUserPermission.overrideBan = json.get("overrideBan").getAsBoolean();
		callUserPermission.overrideKick = json.get("overrideKick").getAsBoolean();
		callUserPermission.overrideMuteOthers = json.get("overrideMuteOthers").getAsBoolean();
		callUserPermission.overrideShareScreen = json.get("overrideShareScreen").getAsBoolean();
		return callUserPermission;
	}
	
}
