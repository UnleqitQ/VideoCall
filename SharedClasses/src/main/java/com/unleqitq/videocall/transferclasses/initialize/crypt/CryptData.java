package com.unleqitq.videocall.transferclasses.initialize.crypt;

import com.unleqitq.videocall.transferclasses.initialize.SendData;

import java.io.Serial;

public class CryptData extends SendData {
	
	@Serial
	private static final long serialVersionUID = -666745588636725637L;
	private final byte[] data;
	
	public CryptData(byte[] data) {
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}
	
}
