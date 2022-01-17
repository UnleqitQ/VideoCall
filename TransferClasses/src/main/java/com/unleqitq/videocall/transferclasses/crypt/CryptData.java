package com.unleqitq.videocall.transferclasses.crypt;

import com.unleqitq.videocall.transferclasses.SendData;

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
