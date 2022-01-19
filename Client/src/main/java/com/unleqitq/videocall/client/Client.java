package com.unleqitq.videocall.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.unleqitq.videocall.client.managers.*;
import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.ClientListRequest;
import com.unleqitq.videocall.transferclasses.base.ListData;
import com.unleqitq.videocall.transferclasses.base.PackRequest;
import com.unleqitq.videocall.transferclasses.base.data.CallData;
import com.unleqitq.videocall.transferclasses.base.data.TeamData;
import com.unleqitq.videocall.transferclasses.base.data.UserData;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Client implements ReceiveListener {
	
	@NotNull
	private static Client instance;
	
	YAMLConfiguration configuration = new YAMLConfiguration();
	ClientNetworkConnection connection;
	
	@NotNull
	private final String username;
	@NotNull
	private final String password;
	private UUID userUuid;
	
	@NotNull
	public Cache<UUID, User> userCache;
	@NotNull
	public Cache<UUID, Team> teamCache;
	@NotNull
	public Cache<UUID, CallDefinition> callCache;
	@NotNull
	public ManagerHandler managerHandler;
	@NotNull String host;
	int port;
	@NotNull
	public UnknownValues unknownValues = new UnknownValues();
	
	@NotNull
	public Thread refreshThread;
	
	public Client(@NotNull String username, @NotNull String password, @NotNull String host, int port) throws
			IOException {
		instance = this;
		
		loadConfig();
		refreshThread = new Thread(this::refreshLoop);
		
		managerHandler = new ManagerHandler();
		managerHandler.setCallManager(new CallManager(managerHandler)).setTeamManager(
				new TeamManager(managerHandler)).setUserManager(new UserManager(managerHandler)).setAccountManager(
				new AccountManager(managerHandler)).setConfiguration(configuration);
		
		Config.cacheDuration = clamp(configuration.getInt("cacheDuration", 120), 20, 300);
		Config.unknownValuesRequestInterval = clamp(configuration.getInt("unknownValuesRequestInterval", 1000), 100,
				3000);
		//Config.videoMaxTimeDifference = clamp(configuration.getInt("cacheDuration", 120), 20, 300);
		//Config.cacheDuration = clamp(configuration.getInt("cacheDuration", 120), 20, 300);
		Config.refreshInterval = clamp(configuration.getInt("refreshInterval", 10), 5,
				Math.min(120, Config.cacheDuration * 4 / 5));
		
		userCache = CacheBuilder.newBuilder().expireAfterAccess(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).expireAfterWrite(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).build();
		teamCache = CacheBuilder.newBuilder().expireAfterAccess(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).expireAfterWrite(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).build();
		callCache = CacheBuilder.newBuilder().expireAfterAccess(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).expireAfterWrite(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).build();
		
		this.username = username;
		this.password = password;
		
		
		ClientNetworkConnection.maxTimeDifference = configuration.getInt("network.maxTimeDifference", 4) * 1000;
		ClientNetworkConnection.maxTimeDifference = Math.max(1,
				Math.min(30, ClientNetworkConnection.maxTimeDifference)) * 1000;
		
		
		this.host = host;
		this.port = port;
		
		Socket socket = new Socket(host, port);
		connection = new ClientNetworkConnection(socket);
		connection.setListener(this);
		connection.init();
		
		try {
			Thread.sleep(1000 * 4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		connection.send(new ConnectionInformation(ConnectionInformation.ClientType.CLIENT));
		
		refreshThread.start();
	}
	
	@NotNull
	public static Client getInstance() {
		return instance;
	}
	
	@Nullable
	public User getUser(UUID uuid) {
		User user = userCache.asMap().get(uuid);
		if (user == null)
			unknownValues.users.add(uuid);
		return user;
	}
	
	@Nullable
	public Team getTeam(UUID uuid) {
		Team team = teamCache.asMap().get(uuid);
		if (team == null)
			unknownValues.teams.add(uuid);
		return team;
	}
	
	@Nullable
	public CallDefinition getCall(UUID uuid) {
		CallDefinition call = callCache.asMap().get(uuid);
		if (call == null)
			unknownValues.calls.add(uuid);
		return call;
	}
	
	private void unknownRequestLoop() {
		while (true) {
			sendUnknownRequest();
			try {
				Thread.sleep(Config.unknownValuesRequestInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendUnknownRequest() {
		Set<UUID> users = new HashSet<>(unknownValues.users);
		unknownValues.users.clear();
		Set<UUID> teams = new HashSet<>(unknownValues.teams);
		unknownValues.teams.clear();
		Set<UUID> calls = new HashSet<>(unknownValues.calls);
		unknownValues.calls.clear();
		connection.send(PackRequest.create(users, calls, teams));
	}
	
	private void refreshLoop() {
		while (true) {
			sendRefreshRequest();
			try {
				Thread.sleep(Config.refreshInterval * 1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendRefreshRequest() {
		connection.send(new ClientListRequest(userUuid));
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
	
	@NotNull
	public String getUsername() {
		return username;
	}
	
	@NotNull
	public String getPassword() {
		return password;
	}
	
	
	@Override
	public void onReceive(Data data) {
		System.out.println(data.getData());
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
	
	
	public static final class Config {
		
		public static String networkServerRootHost;
		public static int networkServerRootPort;
		public static int networkMaxTimeDifference;
		public static int videoMaxTimeDifference;
		public static int cacheDuration;
		public static int refreshInterval;
		public static int unknownValuesRequestInterval;
		
	}
	
	public static int clamp(int v, int min, int max) {
		if (min > max)
			return (min + max) / 2;
		if (v > max)
			return max;
		if (v < min)
			return min;
		return v;
	}
	
}
