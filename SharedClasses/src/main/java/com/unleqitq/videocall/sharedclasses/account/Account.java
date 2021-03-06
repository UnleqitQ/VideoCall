package com.unleqitq.videocall.sharedclasses.account;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.ObjectHandler;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.jetbrains.annotations.NotNull;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

public class Account implements Externalizable {
	
	@NotNull
	private final IManagerHandler managerHandler;
	@NotNull
	private UUID uuid;
	@NotNull
	private String username;
	@NotNull
	private byte[] password;
	
	public Account() {
		managerHandler = IManagerHandler.HANDLER[0];
	}
	
	public Account(@NotNull IManagerHandler managerHandler, @NotNull final UUID uuid, @NotNull String username, byte[] password) {
		this.managerHandler = managerHandler;
		this.uuid = uuid;
		this.username = username;
		this.password = password;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public void setPassword(byte[] password) {
		this.password = password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean test(byte[] input, long time) throws NoSuchAlgorithmException {
		long diff = System.currentTimeMillis() - time;
		if (diff < 100000 + managerHandler.getConfiguration().getLong("maxPasswordDelay", 1000) && diff > 0) {
			String pwd = Hex.encodeHexString(password) + (time & ((1 << 13) - 1));
			MessageDigest md = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
			md.update(pwd.getBytes());
			byte[] hash = md.digest();
			return Arrays.equals(input, hash);
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Account account)) {
			if (o instanceof UUID)
				return getUuid().equals(o);
			if (o instanceof String)
				return getUsername().equals(o);
			return false;
		}
		return getUuid().equals(account.getUuid());
	}
	
	@Override
	public int hashCode() {
		return getUuid().hashCode();
	}
	
	@NotNull
	public static Account load(@NotNull IManagerHandler managerHandler, @NotNull JsonObject section) throws
			DecoderException {
		return new Account(managerHandler, UUID.fromString(section.get("uuid").getAsString()),
				section.get("username").getAsString(), Hex.decodeHex(section.get("password").getAsString()));
	}
	
	@NotNull
	public JsonObject save() {
		JsonObject object = new JsonObject();
		object.add("uuid", new JsonPrimitive(getUuid().toString()));
		object.add("username", new JsonPrimitive(username));
		object.add("password", new JsonPrimitive(Hex.encodeHexString(password)));
		return object;
	}
	
	@Override
	public String toString() {
		return "Account" + save();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ObjectHandler.ObjectOutputHandler.writeUuid(out, uuid);
		ObjectHandler.ObjectOutputHandler.writeString(out, username);
		ObjectHandler.ObjectOutputHandler.writeByteArray(out, password);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException {
		uuid = ObjectHandler.ObjectInputHandler.readUuid(in);
		username = ObjectHandler.ObjectInputHandler.readString(in);
		password = ObjectHandler.ObjectInputHandler.readByteArray(in);
	}
	
}
