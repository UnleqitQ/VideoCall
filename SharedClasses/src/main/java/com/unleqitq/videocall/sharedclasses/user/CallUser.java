package com.unleqitq.videocall.sharedclasses.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.unleqitq.videocall.sharedclasses.ObjectHandler;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.UUID;

public class CallUser implements Externalizable {
	
	@Serial
	private static final long serialVersionUID = 3011340175187922666L;
	@NotNull
	private UUID uuid;
	@NotNull
	public String firstname;
	@NotNull
	public String lastname;
	@NotNull
	public String username;
	public boolean muted = true;
	public boolean handRaised;
	public boolean video;
	public CallUserPermission permission;
	
	public CallUser() {}
	
	public CallUser(@NotNull UUID uuid, @NotNull String firstname, @NotNull String lastname, @NotNull String username, CallUserPermission permission) {
		this.uuid = uuid;
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.permission = permission;
	}
	
	@NotNull
	public String getFirstname() {
		return firstname;
	}
	
	public void setFirstname(@NotNull String firstname) {
		this.firstname = firstname;
	}
	
	@NotNull
	public String getLastname() {
		return lastname;
	}
	
	public void setLastname(@NotNull String lastname) {
		this.lastname = lastname;
	}
	
	@NotNull
	public String getUsername() {
		return username;
	}
	
	public void setUsername(@NotNull String username) {
		this.username = username;
	}
	
	@NotNull
	public UUID getUuid() {
		return uuid;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CallUser user))
			return false;
		return getUuid().equals(user.getUuid());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getUuid());
	}
	
	@NotNull
	public static CallUser load(@NotNull JsonObject section) {
		CallUser callUser = new CallUser(UUID.fromString(section.get("uuid").getAsString()),
				section.get("firstname").getAsString(), section.get("lastname").getAsString(),
				section.get("username").getAsString(),
				CallUserPermission.load(section.get("permission").getAsJsonObject()));
		callUser.muted = section.get("muted").getAsBoolean();
		callUser.video = section.get("video").getAsBoolean();
		callUser.handRaised = section.get("handRaised").getAsBoolean();
		return callUser;
	}
	
	@NotNull
	public JsonObject save() {
		JsonObject object = new JsonObject();
		object.add("uuid", new JsonPrimitive(getUuid().toString()));
		object.add("firstname", new JsonPrimitive(firstname));
		object.add("lastname", new JsonPrimitive(lastname));
		object.add("username", new JsonPrimitive(username));
		object.add("muted", new JsonPrimitive(muted));
		object.add("video", new JsonPrimitive(video));
		object.add("handRaised", new JsonPrimitive(handRaised));
		object.add("permission", permission.save());
		return object;
	}
	
	@Override
	public String toString() {
		return "CallUser" + save();
	}
	
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ObjectHandler.ObjectOutputHandler h = new ObjectHandler.ObjectOutputHandler(out);
		h.writeUuid(uuid);
		h.writeString(firstname);
		h.writeString(lastname);
		h.writeString(username);
		out.writeBoolean(muted);
		out.writeBoolean(handRaised);
		out.writeBoolean(video);
		permission.writeExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException {
		ObjectHandler.ObjectInputHandler h = new ObjectHandler.ObjectInputHandler(in);
		uuid = h.readUuid();
		firstname = h.readString();
		lastname = h.readString();
		username = h.readString();
		muted = in.readBoolean();
		handRaised = in.readBoolean();
		video = in.readBoolean();
		permission = new CallUserPermission();
		permission.readExternal(in);
	}
	
}
