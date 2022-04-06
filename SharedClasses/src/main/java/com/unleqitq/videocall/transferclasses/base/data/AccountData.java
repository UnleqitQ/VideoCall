package com.unleqitq.videocall.transferclasses.base.data;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.account.Account;
import org.apache.commons.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class AccountData implements Externalizable {
	
	@Serial
	private static final long serialVersionUID = -3526151276793127630L;
	@NotNull
	private Account account;
	
	public AccountData() {
		
	}
	
	public AccountData(@NotNull Account account) {
		this.account = account;
	}
	
	public String getJson() {
		return account.save().toString();
	}
	
	public Account getAccount(IManagerHandler managerHandler) throws DecoderException {
		return account;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		account.writeExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		account = new Account();
		account.readExternal(in);
	}
	
}
