package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonObject;
import com.unleqitq.videocall.sharedclasses.call.CallInformation;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.UUID;

public class CallData implements Externalizable {
	
	@Serial
	private static final long serialVersionUID = 3297709917483886053L;
	
	@NotNull
	private CallInformation information;
	
	public CallData() {
	
	}
	
	public CallData(@NotNull CallInformation call) {
		information = call;
	}
	
	@NotNull
	public String getJson() {
		return information.save().toString();
	}
	
	public CallInformation getCall() {
		return information;
	}
	
	public JsonObject getJsonObject() {
		return information.save();
	}
	
	public UUID getUUID() {
		return information.getUuid();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		information.writeExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		information = new CallInformation();
		information.readExternal(in);
	}
	
}
