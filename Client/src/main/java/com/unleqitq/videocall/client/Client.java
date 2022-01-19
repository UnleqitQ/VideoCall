package com.unleqitq.videocall.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.ClientListRequest;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.util.UUID;

public class Client implements ReceiveListener {
	
	
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
	@NotNull String host;
	int port;
	
	@NotNull
	public Thread refreshThread;
	
	public Client(@NotNull String username, @NotNull String password, @NotNull String host, int port) throws
			IOException {
		loadConfig();
		refreshThread = new Thread(this::refreshLoop);
		
		Config.cacheDuration = clamp(configuration.getInt("cacheDuration", 120), 20, 300);
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
	}
	
	
	public final class Config {
		
		public static String networkServerRootHost;
		public static int networkServerRootPort;
		public static int networkMaxTimeDifference;
		public static int videoMaxTimeDifference;
		public static int cacheDuration;
		public static int refreshInterval;
		
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
