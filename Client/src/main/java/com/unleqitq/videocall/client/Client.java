package com.unleqitq.videocall.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.util.UUID;

public class Client {
	
	
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
	String host;
	int port;
	
	public Client(@NotNull String username, @NotNull String password, @NotNull String host, int port) throws
			IOException {
		loadConfig();
		
		userCache = CacheBuilder.newBuilder().expireAfterAccess(
				Duration.ofSeconds(configuration.getLong("cacheDuration", 120))).expireAfterWrite(
				Duration.ofSeconds(configuration.getLong("cacheDuration", 120))).build();
		teamCache = CacheBuilder.newBuilder().expireAfterAccess(
				Duration.ofSeconds(configuration.getLong("cacheDuration", 120))).expireAfterWrite(
				Duration.ofSeconds(configuration.getLong("cacheDuration", 120))).build();
		callCache = CacheBuilder.newBuilder().expireAfterAccess(
				Duration.ofSeconds(configuration.getLong("cacheDuration", 120))).expireAfterWrite(
				Duration.ofSeconds(configuration.getLong("cacheDuration", 120))).build();
		
		this.username = username;
		this.password = password;
		
		ClientNetworkConnection.maxTimeDifference = configuration.getInt("network.maxTimeDifference", 4000);
		this.host = host;
		this.port = port;
		
		Socket socket = new Socket(host, port);
		connection = new ClientNetworkConnection(socket);
		
		try {
			Thread.sleep(1000 * 4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		connection.send(new ConnectionInformation(ConnectionInformation.ClientType.CLIENT));
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
	
	
}
