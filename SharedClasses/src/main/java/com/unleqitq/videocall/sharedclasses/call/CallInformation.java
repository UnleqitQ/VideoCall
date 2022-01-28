package com.unleqitq.videocall.sharedclasses.call;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CallInformation(@NotNull UUID uuid, @NotNull UUID serverUuid, @NotNull String host, int port) {
	
	@NotNull
	public UUID getUuid() {
		return uuid;
	}
	
	@NotNull
	public UUID getServerUuid() {
		return serverUuid;
	}
	
	public JsonObject save() {
		JsonObject object = new JsonObject();
		object.add("uuid", new JsonPrimitive(uuid.toString()));
		object.add("server", new JsonPrimitive(serverUuid.toString()));
		object.add("host", new JsonPrimitive(host));
		object.add("port", new JsonPrimitive(port));
		return object;
	}
	
	public static CallInformation load(JsonObject object) {
		return new CallInformation(UUID.fromString(object.get("uuid").getAsString()),
				UUID.fromString(object.get("server").getAsString()), object.get("host").getAsString(),
				object.get("port").getAsInt());
	}
	
	@Override
	public String toString() {
		return "CallInformation{ " + save() + " }";
	}
	
}
