package com.unleqitq.videocall.transferclasses.base.data;

import com.unleqitq.videocall.sharedclasses.user.CallUser;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class CallUserData implements Externalizable {
	
	@Serial
	private static final long serialVersionUID = -8907663600321918952L;
	
	@NotNull
	private CallUser user;
	
	public CallUserData() {
	
	}
	
	public CallUserData(@NotNull CallUser user) {
		this.user = user;
	}
	
	public String getJson() {
		return user.save().toString();
	}
	
	public CallUser getUser() {
		return user;
	}
	
	@Override
	public String toString() {
		return "CallUserData {" + getJson() + "}";
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		user.writeExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		user = new CallUser();
		user.readExternal(in);
	}
	
}
