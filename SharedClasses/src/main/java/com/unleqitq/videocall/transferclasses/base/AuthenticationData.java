package com.unleqitq.videocall.transferclasses.base;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public record AuthenticationData(@NotNull String username, byte[] passphrase, long time) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -1419725401994418912L;
	
	public static AuthenticationData create(String username, String password) throws NoSuchAlgorithmException {
		long time = System.currentTimeMillis();
		MessageDigest md = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_1);
		md.update(password.getBytes());
		byte[] hash = md.digest();
		String pwd = Hex.encodeHexString(hash) + (time & ((1 << 13) - 1));
		md = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
		md.update(pwd.getBytes());
		byte[] passphrase = md.digest();
		AuthenticationData data = new AuthenticationData(username, passphrase, time);
		return data;
	}
	
	
}
