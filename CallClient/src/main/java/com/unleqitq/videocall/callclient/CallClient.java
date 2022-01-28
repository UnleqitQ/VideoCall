package com.unleqitq.videocall.callclient;

import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.AuthenticationData;
import com.unleqitq.videocall.transferclasses.base.AuthenticationResult;
import com.unleqitq.videocall.transferclasses.base.ListData;
import com.unleqitq.videocall.transferclasses.base.data.CallUserData;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallClient implements ReceiveListener {
	
	@NotNull
	private static CallClient instance;
	
	YAMLConfiguration configuration = new YAMLConfiguration();
	public ClientNetworkConnection connection;
	
	@NotNull
	private final String username;
	@NotNull
	private final String password;
	public UUID userUuid;
	
	@NotNull
	public Map<UUID, CallUser> users;
	@NotNull String host;
	int port;
	
	@NotNull
	public Thread refreshThread;
	
	public CallClient(@NotNull String username, @NotNull String password, @NotNull String host, int port) throws
			IOException {
		instance = this;
		
		loadConfig();
		
		Config.cacheDuration = clamp(configuration.getInt("cacheDuration", 120), 20, 300);
		Config.unknownValuesRequestInterval = clamp(configuration.getInt("unknownValuesRequestInterval", 1000), 100,
				3000);
		Config.videoMaxTimeDifference = clamp(configuration.getInt("video.maxTimeDifference", 120), 20, 300);
		//Config.cacheDuration = clamp(configuration.getInt("cacheDuration", 120), 20, 300);
		Config.refreshInterval = clamp(configuration.getInt("refreshInterval", 10), 5,
				Math.min(120, Config.cacheDuration * 4 / 5));
		Config.audioDuration = clamp(configuration.getFloat("audio.duration", 0.5f), 0.2f, 4f);
		
		users = new HashMap<>();
		
		this.username = username;
		this.password = password;
		
		
		ClientNetworkConnection.maxTimeDifference = configuration.getInt("network.maxTimeDifference", 4) * 1000;
		ClientNetworkConnection.maxTimeDifference = Math.max(1,
				Math.min(30, ClientNetworkConnection.maxTimeDifference)) * 1000;
		
		
		this.host = host;
		this.port = port;
		
		Socket socket = new Socket(host, port);
		connection = new ClientNetworkConnection(socket);
		connection.setReceiveListener(this);
		connection.init();
		
		try {
			Thread.sleep(1000 * 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		connection.send(new ConnectionInformation(ConnectionInformation.ClientType.CLIENT));
		
		refreshThread.start();
		try {
			Thread.sleep(1000 * 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			connection.send(AuthenticationData.create(username, password));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@NotNull
	public static CallClient getInstance() {
		return instance;
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
				if (d0 instanceof CallUserData) {
					CallUser user = ((CallUserData) d0).getUser();
					users.put(user.getUuid(), user);
					System.out.println(user);
				}
			}
		}
		if (data.getData() instanceof AuthenticationResult result) {
			switch (result.result()) {
				case -2:
					System.out.println("User doesn't exist");
					System.exit(0);
				case -1:
					System.out.println("Some weird error");
					System.exit(0);
				case 0:
					System.out.println("Password is wrong");
					System.exit(0);
				case 1:
					userUuid = result.userUuid();
			}
		}
	}
	
	
	public static final class Config {
		
		public static String networkServerRootHost;
		public static int networkServerRootPort;
		public static int networkMaxTimeDifference;
		public static int videoMaxTimeDifference;
		public static float audioDuration;
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
	
	public static float clamp(float v, float min, float max) {
		if (min > max)
			return (min + max) / 2;
		if (v > max)
			return max;
		if (v < min)
			return min;
		return v;
	}
	
}
