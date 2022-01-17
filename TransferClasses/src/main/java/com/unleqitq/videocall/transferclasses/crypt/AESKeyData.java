package com.unleqitq.videocall.transferclasses.crypt;

import com.unleqitq.videocall.transferclasses.SendData;
import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serial;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class AESKeyData extends SendData {
	
	@Serial
	private static final long serialVersionUID = 5659068876095914073L;
	
	private final byte[] data;
	
	public AESKeyData(@NotNull SecretKey key, RSAPublicKey rsaPublicKey) throws NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		data = encrypt(rsaPublicKey, key.getEncoded());
	}
	
	public SecretKey getKey(RSAPrivateKey rsaPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		KeyFactory keyFactory = KeyFactory.getInstance("AES");
		KeySpec keySpec = new SecretKeySpec(decrypt(rsaPrivateKey, data), "AES");
		return (SecretKey) keyFactory.generatePublic(keySpec);
	}
	
	private static byte[] encrypt(RSAPublicKey rsaPublicKey, byte[] data) throws NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
		return cipher.doFinal(data);
	}
	
	private static byte[] decrypt(RSAPrivateKey rsaPrivateKey, byte[] data) throws NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
		return cipher.doFinal(data);
	}
	
}
