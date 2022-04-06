package com.unleqitq.videocall.transferclasses.base.data;

import com.google.gson.JsonParser;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.call.BasicCallDefinition;
import com.unleqitq.videocall.sharedclasses.call.TeamCallDefinition;
import com.unleqitq.videocall.sharedclasses.user.User;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class UserData implements Externalizable {
	
	@Serial
	private static final long serialVersionUID = -8907663600321918952L;
	
	@NotNull
	private User user;
	
	public UserData() {}
	
	public UserData(@NotNull User user) {
		this.user = user;
	}
	
	public String getJson() {
		return user.save().toString();
	}
	
	public User getUser(IManagerHandler managerHandler) {
		return user;
	}
	
	public User getUser() {
		return user;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		user.writeExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		user = new User();
		user.readExternal(in);
	}
	
}
