package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonParser;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.account.Account;
import org.apache.commons.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class AccountData implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -3526151276793127630L;
	@NotNull
	private final String json;
	
	public AccountData(@NotNull Account account) {
		json = account.save().toString();
	}
	
	public String getJson() {
		return json;
	}
	
	public Account getAccount(IManagerHandler managerHandler) throws DecoderException {
		return Account.load(managerHandler, JsonParser.parseString(json).getAsJsonObject());
	}
	
}
