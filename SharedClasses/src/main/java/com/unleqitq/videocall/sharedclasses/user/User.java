package com.unleqitq.videocall.sharedclasses.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.ObjectHandler;
import org.jetbrains.annotations.NotNull;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.UUID;

public class User implements Externalizable {
	
	@NotNull
	private final IManagerHandler managerHandler;
	
	@NotNull
	private UUID uuid;
	@NotNull
	private String firstname;
	@NotNull
	private String lastname;
	@NotNull
	private String username;
	
	public User() {
		managerHandler = IManagerHandler.HANDLER[0];
	}
	
	public User(@NotNull IManagerHandler managerHandler, @NotNull UUID uuid, @NotNull String firstname, @NotNull String lastname, @NotNull String username) {
		this.managerHandler = managerHandler;
		this.uuid = uuid;
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
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
		if (!(o instanceof User user))
			return false;
		return getUuid().equals(user.getUuid());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getUuid());
	}
	
	@NotNull
	public static User load(@NotNull IManagerHandler managerHandler, @NotNull JsonObject section) {
		return new User(managerHandler, UUID.fromString(section.get("uuid").getAsString()),
				section.get("firstname").getAsString(), section.get("lastname").getAsString(),
				section.get("username").getAsString());
	}
	
	@NotNull
	public JsonObject save() {
		JsonObject object = new JsonObject();
		object.add("uuid", new JsonPrimitive(getUuid().toString()));
		object.add("firstname", new JsonPrimitive(firstname));
		object.add("lastname", new JsonPrimitive(lastname));
		object.add("username", new JsonPrimitive(username));
		return object;
	}
	
	@Override
	public String toString() {
		return "User" + save();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ObjectHandler.ObjectOutputHandler h = new ObjectHandler.ObjectOutputHandler(out);
		h.writeUuid(uuid);
		h.writeString(firstname);
		h.writeString(lastname);
		h.writeString(username);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException {
		ObjectHandler.ObjectInputHandler h = new ObjectHandler.ObjectInputHandler(in);
		uuid = h.readUuid();
		firstname = h.readString();
		lastname = h.readString();
		username = h.readString();
	}
	
}
