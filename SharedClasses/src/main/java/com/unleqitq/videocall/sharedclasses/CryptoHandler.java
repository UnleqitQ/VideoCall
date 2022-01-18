package com.unleqitq.videocall.sharedclasses;

import com.unleqitq.videocall.transferclasses.initialize.crypt.CryptData;
import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class CryptoHandler {
	
	private CryptoHandler() {}
	
	@NotNull
	public static CryptData encrypt(Serializable data, SecretKey key) throws IOException,
			NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeUnshared(data);
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return new CryptData(cipher.doFinal(baos.toByteArray()));
	}
	
	public static <T extends Serializable> T decrypt(@NotNull CryptData data, SecretKey key) throws IOException,
			NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
			ClassNotFoundException {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		ByteArrayInputStream bais = new ByteArrayInputStream(cipher.doFinal(data.getData()));
		ObjectInputStream ois = new ObjectInputStream(bais);
		return (T) ois.readUnshared();
	}
	
}
