package com.unleqitq.videocall.sharedclasses;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {
	
	private final int port;
	private ServerSocket serverSocket;
	private Thread acceptThread;
	private ConcurrentLinkedQueue<Socket> sockets = new ConcurrentLinkedQueue<>();
	private RSAPrivateKey rsaPrivateKey;
	private RSAPublicKey rsaPublicKey;
	private KeyPair keyPair;
	
	public Server(int port) throws IOException {
		this.port = port;
		serverSocket = new ServerSocket(port);
		acceptThread = new Thread(this::loop);
	}
	
	public void start() {
		acceptThread.start();
	}
	
	public void loop() {
		while (!serverSocket.isClosed()) {
			run();
		}
	}
	
	public void generateKey() throws NoSuchAlgorithmException {
		keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
		rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
	}
	
	public void run() {
		try {
			Socket socket = serverSocket.accept();
			sockets.add(socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ConcurrentLinkedQueue<Socket> getSockets() {
		return sockets;
	}
	
	public RSAPrivateKey getRsaPrivateKey() {
		return rsaPrivateKey;
	}
	
	public RSAPublicKey getRsaPublicKey() {
		return rsaPublicKey;
	}
	
}
