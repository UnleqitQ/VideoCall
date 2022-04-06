package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonObject;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.call.BasicCallDefinition;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.call.TeamCallDefinition;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.UUID;

public class CallDefData implements Externalizable {
	
	@Serial
	private static final long serialVersionUID = 6758191692789788047L;
	
	@NotNull
	private CallDefinition definition;
	
	public CallDefData() {
	
	}
	
	public CallDefData(@NotNull CallDefinition call) {
		definition = call;
	}
	
	public String getJson() {
		return definition.save().toString();
	}
	
	public CallDefinition getCall(IManagerHandler managerHandler) {
		return definition;
	}
	
	public CallDefinition getCall() {
		return definition;
	}
	
	public JsonObject getJsonObject() {
		return definition.save();
	}
	
	public UUID getUUID() {
		return definition.getUuid();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeBoolean(definition instanceof BasicCallDefinition);
		definition.writeExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if (in.readBoolean())
			definition = new BasicCallDefinition();
		else
			definition = new TeamCallDefinition();
		definition.readExternal(in);
	}
	
}
