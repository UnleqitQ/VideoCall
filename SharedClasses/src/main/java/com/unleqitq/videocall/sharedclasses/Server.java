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
	
	private ServerSocket serverSocket;
	private Thread acceptThread;
	private Thread sendThread;
	private ConcurrentLinkedQueue<ServerNetworkConnection> connections = new ConcurrentLinkedQueue<>();
	private RSAPrivateKey rsaPrivateKey;
	private RSAPublicKey rsaPublicKey;
	private KeyPair keyPair;
	
	public ConcurrentLinkedQueue<MessageData> sendQueue = new ConcurrentLinkedQueue<>();
	
	public Server(int port) throws IOException, NoSuchAlgorithmException {
		serverSocket = new ServerSocket(port);
		acceptThread = new Thread(this::loopAccept);
		sendThread = new Thread(this::loopSend);
		generateKey();
	}
	
	public void start() {
		acceptThread.start();
		sendThread.start();
	}
	
	public void loopAccept() {
		while (!serverSocket.isClosed()) {
			runAccept();
		}
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	public void generateKey() throws NoSuchAlgorithmException {
		keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
		rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
	}
	
	public void runAccept() {
		try {
			Socket socket = serverSocket.accept();
			add(socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void add(Socket socket) throws IOException {
		ServerNetworkConnection connection = new ServerNetworkConnection(this, socket);
		System.out.println("Connected: " + connection.getSocket());
	}
	
	public ConcurrentLinkedQueue<ServerNetworkConnection> getConnections() {
		return connections;
	}
	
	public RSAPrivateKey getRsaPrivateKey() {
		return rsaPrivateKey;
	}
	
	public RSAPublicKey getRsaPublicKey() {
		return rsaPublicKey;
	}
	
	public void loopSend() {
		while (true) {
			runSend();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void runSend() {
		while (!sendQueue.isEmpty()) {
			MessageData messageData = sendQueue.poll();
			messageData.connection.write(messageData.data);
		}
	}
	
}
