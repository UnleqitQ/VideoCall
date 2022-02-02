package com.unleqitq.videocall.transferclasses.call;

import com.google.gson.JsonParser;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import org.apache.commons.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class CallUserInfoData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -7972444353523995499L;
	@NotNull
	private final String json;
	
	public CallUserInfoData(@NotNull CallUser callUser) {
		json = callUser.save().toString();
	}
	
	public String getJson() {
		return json;
	}
	
	public CallUser getAccount() throws DecoderException {
		return CallUser.load(JsonParser.parseString(json).getAsJsonObject());
	}
	
}
