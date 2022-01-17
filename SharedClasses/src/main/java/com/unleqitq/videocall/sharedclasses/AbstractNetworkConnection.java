package com.unleqitq.videocall.sharedclasses;

import com.unleqitq.videocall.transferclasses.SendData;
import com.unleqitq.videocall.transferclasses.crypt.AESKeyData;
import com.unleqitq.videocall.transferclasses.crypt.CryptData;
import com.unleqitq.videocall.transferclasses.crypt.RSAKeyData;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public abstract class AbstractNetworkConnection {
	
	private Socket socket;
	private SecretKey aesKey;
	private ReceiveListener listener;
	protected InputStream inputStream;
	protected OutputStream outputStream;
	protected ObjectOutputStream objectOutputStream;
	protected ObjectInputStream objectInputStream;
	
	public AbstractNetworkConnection(Socket socket) {
		this.socket = socket;
	}
	
	public void createObjectStreams() throws IOException {
		objectInputStream = new ObjectInputStream(inputStream);
		objectOutputStream = new ObjectOutputStream(outputStream);
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void generateKey() throws NoSuchAlgorithmException {
		aesKey = KeyGenerator.getInstance("AES").generateKey();
	}
	
	public SecretKey getAesKey() {
		return aesKey;
	}
	
	public void setListener(ReceiveListener listener) {
		this.listener = listener;
	}
	
	public void setAesKey(SecretKey aesKey) {
		this.aesKey = aesKey;
	}
	
	public void send(Serializable data) {
		if (data instanceof SendData) {
			sendEnqueue(new MessageData(this, (SendData) data));
		}
		else {
			try {
				sendEnqueue(new MessageData(this, CryptoHandler.encrypt(data, aesKey)));
			} catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected abstract void sendEnqueue(MessageData messageData);
	
	protected void write(SendData data) {
		try {
			objectOutputStream.writeUnshared(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void onAES(AESKeyData data);
	
	public abstract void onRSA(RSAKeyData data);
	
	public void runReceive() {
		try {
			SendData sendData = (SendData) objectInputStream.readUnshared();
			if (sendData instanceof CryptData) {
				Serializable data = CryptoHandler.decrypt((CryptData) sendData, aesKey);
				listener.onReceive(data);
			}
			else if (sendData instanceof AESKeyData) {
				onAES((AESKeyData) sendData);
			}
			else if (sendData instanceof RSAKeyData) {
				onRSA((RSAKeyData) sendData);
			}
		} catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
