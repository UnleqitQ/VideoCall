package com.unleqitq.videocall.sharedclasses;

import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.initialize.SendData;
import com.unleqitq.videocall.transferclasses.initialize.crypt.AESKeyData;
import com.unleqitq.videocall.transferclasses.initialize.crypt.ConfirmationData;
import com.unleqitq.videocall.transferclasses.initialize.crypt.CryptData;
import com.unleqitq.videocall.transferclasses.initialize.crypt.RSAKeyData;

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
	protected boolean ready;
	private Thread receiveThread;
	public static int maxTimeDifference = 4000;
	
	public AbstractNetworkConnection(Socket socket) {
		this.socket = socket;
		receiveThread = new Thread(this::loopReceive);
	}
	
	public void init() throws IOException {
		onCreate();
		receiveThread.start();
	}
	
	public boolean isReady() {
		return ready;
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
				sendEnqueue(new MessageData(this, CryptoHandler.encrypt(new Data(data).timestamp(), aesKey)));
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
	
	public abstract void onCreate();
	
	public abstract void onAES(AESKeyData data);
	
	public abstract void onRSA(RSAKeyData data);
	
	public abstract void onConfirmation();
	
	public void loopReceive() {
		while (socket.isConnected()) {
			runReceive();
		}
	}
	
	public void runReceive() {
		try {
			SendData sendData = (SendData) objectInputStream.readUnshared();
			if (sendData instanceof CryptData) {
				Data data = CryptoHandler.decrypt((CryptData) sendData, aesKey);
				if (data.getDifference() <= maxTimeDifference)
					listener.onReceive(data);
			}
			else if (sendData instanceof AESKeyData) {
				onAES((AESKeyData) sendData);
			}
			else if (sendData instanceof RSAKeyData) {
				onRSA((RSAKeyData) sendData);
			}
			else if (sendData instanceof ConfirmationData) {
				onConfirmation();
			}
		} catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
		}
	}
	
}
