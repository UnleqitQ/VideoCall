package com.unleqitq.videocall.accessserver.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.call.AbstractCallManager;
import com.unleqitq.videocall.sharedclasses.call.BasicCallDefinition;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.call.TeamCallDefinition;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallManager extends AbstractCallManager {
	
	@NotNull
	private final Map<UUID, CallDefinition> callMap = MapUtils.synchronizedMap(new HashMap<>());
	@NotNull
	private final IManagerHandler managerHandler;
	
	
	public CallManager(@NotNull IManagerHandler managerHandler) {
		this.managerHandler = managerHandler;
	}
	
	@NotNull
	@Override
	public IManagerHandler getManagerHandler() {
		return managerHandler;
	}
	
	@NotNull
	@Override
	public Map<UUID, CallDefinition> getCallMap() {
		return callMap;
	}
	
	@Nullable
	@Override
	public <T extends CallDefinition> T getCall(@NotNull UUID callId) {
		return (T) getCallMap().get(callId);
	}
	
	@NotNull
	public TeamCallDefinition createTeamCall(@NotNull UUID creator) {
		UUID callId;
		do {
			callId = UUID.randomUUID();
		} while (getCallMap().containsKey(callId));
		TeamCallDefinition call = new TeamCallDefinition(managerHandler, callId, creator);
		getCallMap().put(callId, call);
		return call;
	}
	
	@NotNull
	public BasicCallDefinition createBasicCall(@NotNull UUID creator) {
		UUID callId;
		do {
			callId = UUID.randomUUID();
		} while (getCallMap().containsKey(callId));
		BasicCallDefinition call = new BasicCallDefinition(managerHandler, callId, creator);
		getCallMap().put(callId, call);
		return call;
	}
	
	public void save(@NotNull File file) throws IOException {
		if (!file.exists()) {
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			file.createNewFile();
		}
		JsonArray array = new JsonArray();
		for (CallDefinition callDefinition : callMap.values()) {
			array.add(callDefinition.save());
		}
		JsonWriter writer = new JsonWriter(new FileWriter(file));
		new Gson().toJson(array, writer);
		writer.close();
	}
	
	@Override
	public void load(@NotNull File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		JsonReader reader = new JsonReader(new FileReader(file));
		JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
		for (JsonElement element : array) {
			addCall(CallDefinition.load(managerHandler, element.getAsJsonObject()));
		}
		reader.close();
	}
	
	@Override
	public void addCall(@NotNull CallDefinition call) {
		callMap.put(call.getUuid(), call);
	}
	
}
