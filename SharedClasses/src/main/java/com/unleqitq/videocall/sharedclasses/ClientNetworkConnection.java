package com.unleqitq.videocall.sharedclasses;

import com.unleqitq.videocall.transferclasses.initialize.crypt.AESKeyData;
import com.unleqitq.videocall.transferclasses.initialize.crypt.RSAKeyData;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ClientNetworkConnection extends AbstractNetworkConnection {
	
	
	public ClientNetworkConnection(Socket socket) throws IOException {
		super(socket);
		outputStream = socket.getOutputStream();
		inputStream = socket.getInputStream();
		objectOutputStream = new ObjectOutputStream(outputStream);
		objectInputStream = new ObjectInputStream(inputStream);
	}
	
	@Override
	protected void sendEnqueue(MessageData messageData) {
		write(messageData.data);
	}
	
	@Override
	public void onCreate() {
	
	}
	
	@Override
	public void onAES(AESKeyData data) {
	
	}
	
	@Override
	public void onRSA(RSAKeyData data) {
		try {
			generateKey();
			AESKeyData aesKeyData = new AESKeyData(getAesKey(), data.getKey());
			send(aesKeyData);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			e.printStackTrace();
		}
		System.out.println("Received RSA key");
	}
	
	@Override
	public void onConfirmation() {
		ready = true;
		System.out.println("Connection ready");
	}
	
}
