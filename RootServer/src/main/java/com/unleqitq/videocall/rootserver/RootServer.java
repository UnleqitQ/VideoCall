package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.Server;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RootServer {
	
	YAMLConfiguration configuration = new YAMLConfiguration();
	Server server;
	Set<BaseConnection> baseConnections = Collections.synchronizedSet(new HashSet<>());
	Set<ClientConnection> clientConnections = Collections.synchronizedSet(new HashSet<>());
	Set<CallConnection> callConnections = Collections.synchronizedSet(new HashSet<>());
	Set<AccessConnection> accessConnections = Collections.synchronizedSet(new HashSet<>());
	Thread thread;
	
	public RootServer() throws IOException, NoSuchAlgorithmException {
		loadConfig();
		
		ClientNetworkConnection.maxTimeDifference = configuration.getInt("network.maxTimeDifference", 4000);
		
		int port = configuration.getInt("network.server.root.port", 1000);
		server = new Server(port);
		
		server.start();
		thread = new Thread(this::loop);
		thread.start();
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
		RootServer rootServer = new RootServer();
	}
	
	public void addCall(BaseConnection baseConnection) {
		baseConnections.remove(baseConnection);
		callConnections.add(new CallConnection(baseConnection.connection, this));
	}
	
	public void addClient(BaseConnection baseConnection) {
		baseConnections.remove(baseConnection);
		clientConnections.add(new ClientConnection(baseConnection.connection, this));
	}
	
	public void addAccess(BaseConnection baseConnection) {
		baseConnections.remove(baseConnection);
		accessConnections.add(new AccessConnection(baseConnection.connection, this));
	}
	
}
