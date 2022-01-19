package com.unleqitq.videocall.sharedclasses.user;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractUserManager {
	
	@NotNull
	public abstract IManagerHandler getManagerHandler();
	
	@NotNull
	public abstract Map<UUID, User> getUserMap();
	
	@Nullable
	public abstract User getUser(@NotNull UUID uuid);
	
	public void save(@NotNull File file) throws IOException {
		if (!file.exists()) {
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			file.createNewFile();
		}
		JsonArray array = new JsonArray();
		for (User user : getUserMap().values()) {
			array.add(user.save());
		}
		JsonWriter writer = new JsonWriter(new FileWriter(file));
		new Gson().toJson(array, writer);
		writer.close();
	}
	
	public abstract void addUser(@NotNull User user);
	
	public void load(@NotNull File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		JsonReader reader = new JsonReader(new FileReader(file));
		JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
		for (JsonElement element : array) {
			addUser(User.load(getManagerHandler(), element.getAsJsonObject()));
		}
		reader.close();
	}
	
}
