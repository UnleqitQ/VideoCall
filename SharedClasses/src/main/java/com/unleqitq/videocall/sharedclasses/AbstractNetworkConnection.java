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
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public abstract class AbstractNetworkConnection {
	
	private Socket socket;
	private SecretKey aesKey;
	private ReceiveListener receiveListener;
	private DisconnectListener disconnectListener;
	protected InputStream inputStream;
	protected OutputStream outputStream;
	protected ObjectOutputStream objectOutputStream;
	protected ObjectInputStream objectInputStream;
	protected boolean ready;
	private Thread receiveThread;
	public static int maxTimeDifference = 4000;
	private boolean connected = true;
	
	public AbstractNetworkConnection(Socket socket) {
		this.socket = socket;
		receiveThread = new Thread(this::loopReceive);
	}
	
	public boolean isConnected() {
		return connected;
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
	
	public void setReceiveListener(ReceiveListener receiveListener) {
		this.receiveListener = receiveListener;
	}
	
	public void setDisconnectListener(DisconnectListener disconnectListener) {
		this.disconnectListener = disconnectListener;
	}
	
	public void setAesKey(SecretKey aesKey) {
		this.aesKey = aesKey;
	}
	
	public void send(Serializable data) {
		if (!connected) {
			disconnectListener.onDisconnect();
			return;
		}
		if (data instanceof SendData) {
			sendEnqueue(new MessageData(this, (SendData) data));
		}
		else {
			try {
				Data data0 = new Data(data).timestamp();
				//System.out.println("Sending " + data0);
				sendEnqueue(new MessageData(this, CryptoHandler.encrypt(data0, aesKey)));
			} catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected abstract void sendEnqueue(MessageData messageData);
	
	protected void write(SendData data) {
		try {
			objectOutputStream.writeUnshared(data);
		} catch (SocketException ignored) {
			System.out.println("Connection reset " + socket.getInetAddress());
			connected = false;
			disconnectListener.onDisconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void onCreate();
	
	public abstract void onAES(AESKeyData data);
	
	public abstract void onRSA(RSAKeyData data);
	
	public abstract void onConfirmation();
	
	public void loopReceive() {
		while (connected) {
			runReceive();
		}
	}
	
	public void runReceive() {
		try {
			SendData sendData = (SendData) objectInputStream.readUnshared();
			if (sendData instanceof CryptData) {
				Data data = CryptoHandler.decrypt((CryptData) sendData, aesKey);
				//System.out.println("Received " + data);
				if (data.getDifference() <= maxTimeDifference)
					receiveListener.onReceive(data);
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
		} catch (SocketException | EOFException | StreamCorruptedException | OptionalDataException | ClassCastException e) {
			System.out.println("Connection is reset");
			connected = false;
		} catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}
	
	public ObjectInputStream getObjectInputStream() {
		return objectInputStream;
	}
	
	public ObjectOutputStream getObjectOutputStream() {
		return objectOutputStream;
	}
	
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	public Thread getReceiveThread() {
		return receiveThread;
	}
	
}
