package com.unleqitq.videocall.sharedclasses.call;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.unleqitq.videocall.sharedclasses.ObjectHandler;
import org.jetbrains.annotations.NotNull;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

public class CallInformation implements Externalizable {
	
	UUID uuid;
	UUID serverUuid;
	String host;
	int port;
	
	public CallInformation() {}
	
	public CallInformation(@NotNull UUID uuid, @NotNull UUID serverUuid, @NotNull String host, int port) {
		this.uuid = uuid;
		this.serverUuid = serverUuid;
		this.port = port;
		this.host = host;
	}
	
	@NotNull
	public UUID getUuid() {
		return uuid;
	}
	
	@NotNull
	public UUID getServerUuid() {
		return serverUuid;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getHost() {
		return host;
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
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ObjectHandler.ObjectOutputHandler h = new ObjectHandler.ObjectOutputHandler(out);
		h.writeUuid(uuid);
		h.writeUuid(serverUuid);
		h.writeString(host);
		out.writeInt(port);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException {
		ObjectHandler.ObjectInputHandler h = new ObjectHandler.ObjectInputHandler(in);
		uuid = h.readUuid();
		serverUuid = h.readUuid();
		host = h.readString();
		port = in.readInt();
	}
	
}
