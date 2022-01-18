package com.unleqitq.videocall.callserver;

import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.Server;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;
import com.unleqitq.videocall.transferclasses.connection.MachineInformation;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CallServer {
	
	ClientNetworkConnection rootConnection;
	YAMLConfiguration configuration = new YAMLConfiguration();
	Server server;
	Set<BaseConnection> baseConnections = Collections.synchronizedSet(new HashSet<>());
	Set<ClientConnection> clientConnections = Collections.synchronizedSet(new HashSet<>());
	Thread thread;
	long lastInfo = System.currentTimeMillis();
	
	public CallServer() throws IOException, NoSuchAlgorithmException {
		loadConfig();
		
		ClientNetworkConnection.maxTimeDifference = configuration.getInt("network.maxTimeDifference", 4000);
		
		int port = configuration.getInt("network.server.call.port", 1200);
		server = new Server(port);
		
		server.start();
		thread = new Thread(this::loop);
		thread.start();
		
		String host = configuration.getString("network.server.root.host", "localhost");
		port = configuration.getInt("network.server.root.port", 1000);
		
		Socket socket = new Socket(host, port);
		rootConnection = new ClientNetworkConnection(socket);
		
		try {
			Thread.sleep(1000 * 6);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		rootConnection.send(new ConnectionInformation(ConnectionInformation.ClientType.CALL));
	}
	
	private void loadConfig() {
		File file = new File(new File("./").getAbsoluteFile().getParent(), "properties.yml");
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				configuration.read(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			} finally {
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		else {
			InputStream is = null;
			FileWriter writer = null;
			try {
				is = getClass().getClassLoader().getResourceAsStream("properties.yml");
				configuration.read(is);
				file.getParentFile().mkdirs();
				file.createNewFile();
				writer = new FileWriter(file);
				configuration.write(writer);
			} catch (ConfigurationException | IOException e) {
				e.printStackTrace();
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if (writer != null)
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	public void loop() {
		while (true) {
			run();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		runAddConnection();
		if (System.currentTimeMillis() - lastInfo > configuration.getInt("pushInfo", 1000)) {
			rootConnection.send(
					new MachineInformation(server.getServerSocket().getLocalPort(), Runtime.getRuntime().freeMemory()));
			lastInfo = System.currentTimeMillis();
		}
	}
	
	public void runAddConnection() {
		if (!server.getConnections().isEmpty()) {
			ServerNetworkConnection connection = server.getConnections().poll();
			assert connection != null;
			BaseConnection baseConnection = new BaseConnection(connection, this);
			baseConnections.add(baseConnection);
		}
	}
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		CallServer rootServer = new CallServer();
	}
	
	public void addClient(BaseConnection baseConnection) {
		baseConnections.remove(baseConnection);
		clientConnections.add(new ClientConnection(baseConnection.connection, this));
	}
	
}
