package com.unleqitq.videocall.transferclasses.call;

import com.unleqitq.videocall.sharedclasses.user.CallGroupPermission;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class CallGroupPermissionData implements Externalizable {
	
	@Serial
	private static final long serialVersionUID = -1860983368243273096L;
	@NotNull
	private CallGroupPermission permission;
	
	public CallGroupPermissionData() {}
	
	public CallGroupPermissionData(@NotNull CallGroupPermission permission) {
		this.permission = permission;
	}
	
	public String getJson() {
		return permission.toString();
	}
	
	public CallGroupPermission getPermission() {
		return permission;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		permission.writeExternal(out);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		permission = new CallGroupPermission();
		permission.readExternal(in);
	}
	
}
