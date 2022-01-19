package com.unleqitq.videocall.accessserver;

import com.unleqitq.videocall.accessserver.managers.*;
import sharedclasses.ClientNetworkConnection;
import sharedclasses.ReceiveListener;
import sharedclasses.Server;
import sharedclasses.ServerNetworkConnection;
import sharedclasses.call.CallDefinition;
import sharedclasses.team.Team;
import sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.ListData;
import com.unleqitq.videocall.transferclasses.base.data.CallData;
import com.unleqitq.videocall.transferclasses.base.data.TeamData;
import com.unleqitq.videocall.transferclasses.base.data.UserData;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;
import com.unleqitq.videocall.transferclasses.connection.MachineInformation;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AccessServer implements ReceiveListener {
	
	@NotNull
	private static AccessServer instance;
	
	
	@NotNull
	private final ManagerHandler managerHandler;
	
	@NotNull YAMLConfiguration configuration = new YAMLConfiguration();
	@NotNull Server server;
	@NotNull Set<BaseConnection> baseConnections = Collections.synchronizedSet(new HashSet<>());
	@NotNull Set<ClientConnection> clientConnections = Collections.synchronizedSet(new HashSet<>());
	
	@NotNull Thread thread;
	long lastInfo = System.currentTimeMillis();
	
	ClientNetworkConnection rootConnection;
	
	public AccessServer() throws IOException, NoSuchAlgorithmException {
		instance = this;
		
		loadConfig();
		
		managerHandler = new ManagerHandler();
		managerHandler.setCallManager(new CallManager(managerHandler)).setTeamManager(
				new TeamManager(managerHandler)).setUserManager(new UserManager(managerHandler)).setAccountManager(
				new AccountManager(managerHandler)).setConfiguration(configuration);
		
		ClientNetworkConnection.maxTimeDifference = configuration.getInt("network.maxTimeDifference", 4000);
		
		int port = configuration.getInt("network.server.access.port", 1100);
		server = new Server(port);
		
		server.start();
		thread = new Thread(this::loop);
		thread.start();
		
		String host = configuration.getString("network.server.root.host", "localhost");
		port = configuration.getInt("network.server.root.port", 1000);
		
		Socket socket = new Socket(host, port);
		rootConnection = new ClientNetworkConnection(socket);
		
		rootConnection.setListener(this);
		
		try {
			Thread.sleep(1000 * 6);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		rootConnection.send(new ConnectionInformation(ConnectionInformation.ClientType.ACCESS));
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
		AccessServer rootServer = new AccessServer();
	}
	
	public void addClient(BaseConnection baseConnection) {
		baseConnections.remove(baseConnection);
		clientConnections.add(new ClientConnection(baseConnection.connection, this));
	}
	
	public static AccessServer getInstance() {
		return instance;
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof ListData) {
			for (Serializable d0 : ((ListData) data.getData()).data()) {
				if (d0 instanceof UserData) {
					User user = ((UserData) d0).getUser(managerHandler);
					managerHandler.getUserManager().addUser(user);
					System.out.println(user);
				}
				if (d0 instanceof TeamData) {
					Team team = ((TeamData) d0).getTeam(managerHandler);
					managerHandler.getTeamManager().addTeam(team);
					System.out.println(team);
				}
				if (d0 instanceof CallData) {
					CallDefinition call = ((CallData) d0).getCall(managerHandler);
					managerHandler.getCallManager().addCall(call);
					System.out.println(call);
				}
			}
		}
	}
	
}
