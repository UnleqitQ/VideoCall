package com.unleqitq.videocall.sharedclasses.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.Serial;
import java.io.Serializable;

public class CallGroupPermission implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 388640699055766668L;
	
	public String name = "";
	public boolean muteOthers;
	public boolean kick;
	public boolean ban;
	public boolean shareScreen;
	public int level;
	
	public CallGroupPermission(int level) {
		this.level = level;
	}
	
	public CallGroupPermission(int level, String name, boolean muteOthers, boolean kick, boolean ban, boolean shareScreen) {
		this(level);
		this.name = name;
		this.muteOthers = muteOthers;
		this.kick = kick;
		this.ban = ban;
		this.shareScreen = shareScreen;
	}
	
	public boolean isBan() {
		return ban;
	}
	
	public boolean isKick() {
		return kick;
	}
	
	public boolean isMuteOthers() {
		return muteOthers;
	}
	
	public boolean isShareScreen() {
		return shareScreen;
	}
	
	public String getName() {
		return name;
	}
	
	public JsonObject save() {
		JsonObject json = new JsonObject();
		json.add("muteOthers", new JsonPrimitive(muteOthers));
		json.add("kick", new JsonPrimitive(kick));
		json.add("ban", new JsonPrimitive(ban));
		json.add("shareScreen", new JsonPrimitive(shareScreen));
		json.add("name", new JsonPrimitive(name));
		return json;
	}
	
	public static CallGroupPermission load(JsonObject json) {
		CallGroupPermission callGroupPermission = new CallGroupPermission(json.get("level").getAsInt());
		callGroupPermission.ban = json.get("ban").getAsBoolean();
		callGroupPermission.kick = json.get("kick").getAsBoolean();
		callGroupPermission.muteOthers = json.get("muteOthers").getAsBoolean();
		callGroupPermission.shareScreen = json.get("shareScreen").getAsBoolean();
		callGroupPermission.name = json.get("name").getAsString();
		return callGroupPermission;
	}
	
	public static final CallGroupPermission noPerms = new CallGroupPermission(0, "noPerms", false, false, false, false);
	public static final CallGroupPermission fullPerms = new CallGroupPermission(10, "fullPerms", true, true, true,
			true);
	
}