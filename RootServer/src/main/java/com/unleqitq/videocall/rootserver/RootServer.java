package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.rootserver.managers.CallManager;
import com.unleqitq.videocall.rootserver.managers.ManagerHandler;
import com.unleqitq.videocall.rootserver.managers.TeamManager;
import com.unleqitq.videocall.rootserver.managers.UserManager;
import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.Server;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class RootServer {
	
	@NotNull
	private static RootServer instance;
	
	
	@NotNull
	private final ManagerHandler managerHandler;
	@NotNull
	private final YAMLConfiguration configuration = new YAMLConfiguration();
	@NotNull
	private final Server server;
	@NotNull
	private final Set<BaseConnection> baseConnections = Collections.synchronizedSet(new HashSet<>());
	@NotNull
	private final Set<ClientConnection> clientConnections = Collections.synchronizedSet(new HashSet<>());
	@NotNull
	private final Set<CallConnection> callConnections = Collections.synchronizedSet(new HashSet<>());
	@NotNull
	private final Set<AccessConnection> accessConnections = Collections.synchronizedSet(new HashSet<>());
	@NotNull
	public PriorityQueue<CallConnection> callQueue = new PriorityQueue<>(
			(c1, c2) -> (int) (c1.freeMemory - c2.freeMemory));
	@NotNull
	public PriorityQueue<AccessConnection> accessQueue = new PriorityQueue<>(
			(c1, c2) -> (int) (c1.freeMemory - c2.freeMemory));
	@NotNull
	private final Thread thread;
	
	public RootServer() throws IOException, NoSuchAlgorithmException {
		instance = this;
		
		loadConfig();
		
		ClientNetworkConnection.maxTimeDifference = configuration.getInt("network.maxTimeDifference", 4000);
		
		int port = configuration.getInt("network.server.root.port", 1000);
		server = new Server(port);
		
		server.start();
		thread = new Thread(this::loop);
		thread.start();
		managerHandler = new ManagerHandler();
		managerHandler.setCallManager(new CallManager(managerHandler)).setTeamManager(
				new TeamManager(managerHandler)).setUserManager(new UserManager(managerHandler));
	}
	
	private void loadConfig() {
		File file = new File(new File("./").getAbsoluteFile().getParent(), "properties.yml");
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				configuration.read(fis);
			} catch (FileNotFoundException | ConfigurationException e) {
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
	
	public void addCall(@NotNull BaseConnection baseConnection) {
		baseConnections.remove(baseConnection);
		CallConnection callConnection = new CallConnection(baseConnection.connection, this);
		callConnections.add(callConnection);
		callQueue.add(callConnection);
	}
	
	public void addClient(@NotNull BaseConnection baseConnection) {
		baseConnections.remove(baseConnection);
		clientConnections.add(new ClientConnection(baseConnection.connection, this));
	}
	
	public void addAccess(@NotNull BaseConnection baseConnection) {
		baseConnections.remove(baseConnection);
		AccessConnection accessConnection = new AccessConnection(baseConnection.connection, this);
		accessConnections.add(accessConnection);
		accessQueue.add(accessConnection);
	}
	
	public ManagerHandler getManagerHandler() {
		return managerHandler;
	}
	
	@NotNull
	public static RootServer getInstance() {
		return instance;
	}
	
}
