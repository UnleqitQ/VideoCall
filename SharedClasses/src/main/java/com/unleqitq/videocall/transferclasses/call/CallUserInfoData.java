package com.unleqitq.videocall.transferclasses.call;

import com.google.gson.JsonParser;
import com.unleqitq.videocall.sharedclasses.call.BasicCallDefinition;
import com.unleqitq.videocall.sharedclasses.call.TeamCallDefinition;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import org.apache.commons.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class CallUserInfoData implements Externalizable {
	
	@Serial
	private static final long serialVersionUID = -7972444353523995499L;
	@NotNull
	private CallUser callUser;
	
	public CallUserInfoData() {}
	
	public CallUserInfoData(@NotNull CallUser callUser) {
		this.callUser = callUser;
	}
	
	public String getJson() {
		return callUser.save().toString();
	}
	
	public CallUser getAccount() throws DecoderException {
		return callUser;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		callUser.writeExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		callUser = new CallUser();
		callUser.readExternal(in);
	}
	
}
