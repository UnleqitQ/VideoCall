package com.unleqitq.videocall.sharedclasses.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.unleqitq.videocall.sharedclasses.ObjectHandler;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class CallGroupPermission implements Externalizable {
	
	@Serial
	private static final long serialVersionUID = 388640699055766668L;
	
	public String name = "";
	public boolean muteOthers;
	public boolean kick;
	public boolean ban;
	public boolean shareScreen;
	public int level;
	
	public CallGroupPermission() {}
	
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
		json.add("level", new JsonPrimitive(level));
		return json;
	}
	
	@NotNull
	public static CallGroupPermission load(@NotNull JsonObject json) {
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
	
	@Override
	public String toString() {
		return "CallGroupPermission " + save();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ObjectHandler.ObjectOutputHandler h = new ObjectHandler.ObjectOutputHandler(out);
		h.writeString(name);
		out.writeBoolean(muteOthers);
		out.writeBoolean(kick);
		out.writeBoolean(ban);
		out.writeBoolean(shareScreen);
		out.writeInt(level);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException {
		ObjectHandler.ObjectInputHandler h = new ObjectHandler.ObjectInputHandler(in);
		name = h.readString();
		muteOthers = in.readBoolean();
		kick = in.readBoolean();
		ban = in.readBoolean();
		shareScreen = in.readBoolean();
		level = in.readInt();
	}
	
}
