package com.unleqitq.videocall.transferclasses.call;

import com.google.gson.JsonParser;
import com.unleqitq.videocall.sharedclasses.user.CallUserPermission;
import org.apache.commons.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class CallUserPermissionData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -8172622355656646783L;
	@NotNull
	private final String json;
	
	public CallUserPermissionData(@NotNull CallUserPermission permission) {
		json = permission.save().toString();
	}
	
	public String getJson() {
		return json;
	}
	
	public CallUserPermission getAccount() throws DecoderException {
		return CallUserPermission.load(JsonParser.parseString(json).getAsJsonObject());
	}
	
}
