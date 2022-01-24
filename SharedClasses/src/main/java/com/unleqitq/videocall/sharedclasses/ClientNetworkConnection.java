package com.unleqitq.videocall.sharedclasses;

import com.unleqitq.videocall.transferclasses.initialize.SendData;
import com.unleqitq.videocall.transferclasses.initialize.crypt.AESKeyData;
import com.unleqitq.videocall.transferclasses.initialize.crypt.RSAKeyData;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientNetworkConnection extends AbstractNetworkConnection {
	
	private Thread sendThread;
	private ConcurrentLinkedQueue<SendData> sendQueue = new ConcurrentLinkedQueue<>();
	
	public ClientNetworkConnection(Socket socket) throws IOException {
		super(socket);
		outputStream = socket.getOutputStream();
		inputStream = socket.getInputStream();
		objectOutputStream = new ObjectOutputStream(outputStream);
		objectInputStream = new ObjectInputStream(inputStream);
		sendThread = new Thread(this::loopSend);
		sendThread.start();
	}
	
	@Override
	public void send(Serializable data) {
		if (!isConnected())
			throw new IllegalStateException("Not Connected");
		super.send(data);
	}
	
	@Override
	protected void sendEnqueue(MessageData messageData) {
		sendQueue.add(messageData.data);
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
	
	public void loopSend() {
		while (true) {
			while (!sendQueue.isEmpty()) {
				try {
					SendData data = sendQueue.poll();
					write(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {
			}
		}
	}
	
}
