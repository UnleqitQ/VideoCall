package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.rootserver.managers.*;
import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.Server;
import com.unleqitq.videocall.sharedclasses.ServerNetworkConnection;
import com.unleqitq.videocall.sharedclasses.account.Account;
import com.unleqitq.videocall.sharedclasses.call.BasicCallDefinition;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.base.ListData;
import com.unleqitq.videocall.transferclasses.base.data.CallDefData;
import com.unleqitq.videocall.transferclasses.base.data.TeamData;
import com.unleqitq.videocall.transferclasses.base.data.UserData;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
	public final Set<BaseConnection> baseConnections = Collections.synchronizedSet(new HashSet<>());
	@NotNull
	public final Set<ClientConnection> clientConnections = Collections.synchronizedSet(new HashSet<>());
	@NotNull
	public final Map<UUID, CallConnection> callConnections = new ConcurrentHashMap<>();
	@NotNull
	public final Set<AccessConnection> accessConnections = Collections.synchronizedSet(new HashSet<>());
	@NotNull
	public PriorityQueue<CallConnection> callQueue = new PriorityQueue<>(
			(c1, c2) -> (int) (c1.freeMemory - c2.freeMemory));
	@NotNull
	public PriorityQueue<AccessConnection> accessQueue = new PriorityQueue<>(
			(c1, c2) -> (int) (c1.freeMemory - c2.freeMemory));
	@NotNull
	private final Thread thread;
	
	@NotNull
	public final Map<UUID, UUID> callServerMap = new ConcurrentHashMap<>();
	
	public RootServer() throws IOException, NoSuchAlgorithmException {
		System.out.println("Root Server PID: " + ProcessHandle.current().pid());
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
				new TeamManager(managerHandler)).setUserManager(new UserManager(managerHandler)).setAccountManager(
				new AccountManager(managerHandler)).setConfiguration(configuration);
		try {
			File file = new File(new File("./").getAbsoluteFile().getParent(), "accounts.json");
			managerHandler.getAccountManager().load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			File file = new File(new File("./").getAbsoluteFile().getParent(), "users.json");
			managerHandler.getUserManager().load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			File file = new File(new File("./").getAbsoluteFile().getParent(), "calls.json");
			managerHandler.getCallManager().load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			File file = new File(new File("./").getAbsoluteFile().getParent(), "teams.json");
			managerHandler.getTeamManager().load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		{
			MessageDigest md = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_1);
			md.update("root".getBytes());
			createUser("root", md.digest(), "Root", "Tree");
		}
		
		saveAccounts();
		saveCalls();
		saveTeams();
		saveUsers();
		BasicCallDefinition call = managerHandler.getCallManager().createBasicCall(
				managerHandler.getUserManager().getUserMap().keySet().iterator().next(), System.currentTimeMillis(),
				"test");
		
		call.addMember(managerHandler.getUserManager().getUserMap().keySet().iterator().next());
		
		Team team = managerHandler.getTeamManager().createTeam(
				managerHandler.getUserManager().getUserMap().keySet().iterator().next(), "Test 0");
		
		System.out.println(managerHandler.getAccountManager().getAccount("root").save());
		System.out.println(call);
		System.out.println(team);
	}
	
	public void createUser(@NotNull String username, byte[] password, @NotNull String firstname, @NotNull String lastname) {
		Account account = managerHandler.getAccountManager().createAccount(username, password);
		if (account == null)
			return;
		managerHandler.getUserManager().createUser(account.getUuid(), firstname, lastname, username);
	}
	
	public void saveAccounts() {
		try {
			File file = new File(new File("./").getAbsoluteFile().getParent(), "accounts.json");
			managerHandler.getAccountManager().save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveCalls() {
		try {
			File file = new File(new File("./").getAbsoluteFile().getParent(), "calls.json");
			managerHandler.getCallManager().save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveTeams() {
		try {
			File file = new File(new File("./").getAbsoluteFile().getParent(), "teams.json");
			managerHandler.getTeamManager().save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveUsers() {
		try {
			File file = new File(new File("./").getAbsoluteFile().getParent(), "users.json");
			managerHandler.getUserManager().save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		UUID uuid;
		do {
			uuid = UUID.randomUUID();
		} while (callConnections.containsKey(uuid));
		CallConnection callConnection = new CallConnection(uuid, baseConnection.connection, this);
		callConnections.put(uuid, callConnection);
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
	
	@NotNull
	public ManagerHandler getManagerHandler() {
		return managerHandler;
	}
	
	@NotNull
	public static RootServer getInstance() {
		return instance;
	}
	
	public void broadcastUser(@NotNull UUID uuid) {
		User user = managerHandler.getUserManager().getUser(uuid);
		if (user != null) {
			broadcastUser(user);
		}
	}
	
	public void broadcastTeam(@NotNull UUID uuid) {
		Team team = managerHandler.getTeamManager().getTeam(uuid);
		if (team != null) {
			broadcastTeam(team);
		}
	}
	
	public void broadcastCall(@NotNull UUID uuid) {
		CallDefinition call = managerHandler.getCallManager().getCall(uuid);
		if (call != null) {
			broadcastCall(call);
		}
	}
	
	public void broadcastUser(@NotNull User user) {
		for (CallConnection callConnection : callQueue) {
			callConnection.connection.send(new UserData(user));
		}
		for (AccessConnection accessConnection : accessQueue) {
			accessConnection.connection.send(new UserData(user));
		}
	}
	
	public void broadcastTeam(@NotNull Team team) {
		for (CallConnection callConnection : callQueue) {
			callConnection.connection.send(new TeamData(team));
		}
		for (AccessConnection accessConnection : accessQueue) {
			accessConnection.connection.send(new TeamData(team));
		}
	}
	
	public void broadcastCall(@NotNull CallDefinition call) {
		for (CallConnection callConnection : callQueue) {
			callConnection.connection.send(new CallDefData(call));
		}
		for (AccessConnection accessConnection : accessQueue) {
			accessConnection.connection.send(new CallDefData(call));
		}
	}
	
	public void broadcastUsers(@NotNull Collection<User> users) {
		Serializable[] array = new Serializable[users.size()];
		int i = 0;
		for (User user : users) {
			array[i++] = new UserData(user);
		}
		ListData listData = new ListData(array);
		
		for (CallConnection callConnection : callQueue) {
			callConnection.connection.send(listData);
		}
		for (AccessConnection accessConnection : accessQueue) {
			accessConnection.connection.send(listData);
		}
	}
	
	public void broadcastTeams(@NotNull Collection<Team> teams) {
		Serializable[] array = new Serializable[teams.size()];
		int i = 0;
		for (Team team : teams) {
			array[i++] = new TeamData(team);
		}
		ListData listData = new ListData(array);
		
		for (CallConnection callConnection : callQueue) {
			callConnection.connection.send(listData);
		}
		for (AccessConnection accessConnection : accessQueue) {
			accessConnection.connection.send(listData);
		}
	}
	
	public void broadcastCalls(@NotNull Collection<CallDefinition> calls) {
		Serializable[] array = new Serializable[calls.size()];
		int i = 0;
		for (CallDefinition call : calls) {
			array[i++] = new CallDefData(call);
		}
		ListData listData = new ListData(array);
		
		for (CallConnection callConnection : callQueue) {
			callConnection.connection.send(listData);
		}
		for (AccessConnection accessConnection : accessQueue) {
			accessConnection.connection.send(listData);
		}
	}
	
	public synchronized UUID allocateServer(UUID callUuid) {
		if (callServerMap.containsKey(callUuid)) {
			UUID serverUuid = callServerMap.get(callUuid);
			if (!callConnections.containsKey(serverUuid) || !callConnections.get(serverUuid).connection.isConnected()) {
				callConnections.remove(serverUuid);
				callServerMap.remove(callUuid);
				return allocateServer(callUuid);
			}
			return serverUuid;
		}
		UUID serverUuid = callQueue.peek().uuid;
		callServerMap.put(callUuid, serverUuid);
		return serverUuid;
	}
	
	public Map<UUID, CallConnection> getCallConnections() {
		return callConnections;
	}
	
}
