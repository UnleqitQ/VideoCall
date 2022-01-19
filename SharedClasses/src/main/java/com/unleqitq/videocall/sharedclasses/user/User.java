package com.unleqitq.videocall.sharedclasses.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class User {
	
	@NotNull
	private final IManagerHandler managerHandler;
	
	@NotNull
	private final UUID uuid;
	@NotNull
	private String firstname;
	@NotNull
	private String lastname;
	@NotNull
	private String username;
	
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
	
}
