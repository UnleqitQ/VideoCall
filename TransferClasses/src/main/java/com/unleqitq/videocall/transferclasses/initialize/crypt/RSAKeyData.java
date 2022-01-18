package com.unleqitq.videocall.transferclasses.initialize.crypt;

import com.unleqitq.videocall.transferclasses.initialize.SendData;

import java.io.Serial;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSAKeyData extends SendData {
	
	@Serial
	private static final long serialVersionUID = 5116037157730716838L;
	private final BigInteger modulus;
	private final BigInteger publicExponent;
	
	public RSAKeyData(RSAPublicKey key) {
		modulus = key.getModulus();
		publicExponent = key.getPublicExponent();
	}
	
	public RSAPublicKey getKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		KeySpec publicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
		return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
	}
	
}
