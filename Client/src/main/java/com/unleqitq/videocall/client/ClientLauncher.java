package com.unleqitq.videocall.client;

import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.AccessInformation;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientLauncher implements ReceiveListener {
	
	
	YAMLConfiguration configuration = new YAMLConfiguration();
	ClientNetworkConnection connection;
	
	@NotNull
	private final String username;
	@NotNull
	private final String password;
	private UUID userUuid;
	
	public ClientLauncher(@NotNull String username, @NotNull String password) throws IOException {
		loadConfig();
		
		this.username = username;
		this.password = password;
		
		ClientNetworkConnection.maxTimeDifference = configuration.getInt("network.maxTimeDifference", 4000);
		String host = configuration.getString("network.server.root.host", "localhost");
		int port = configuration.getInt("network.server.root.port", 1000);
		
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
	
	public static void main(String @NotNull [] args) throws IOException {
		ClientLauncher client = new ClientLauncher(args[0], args[1]);
	}
	
	@Override
	public void onReceive(Data data) {
		if (data.getData() instanceof AccessInformation info) {
			Thread thread0 = new Thread(() -> {
				try {
					connection.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					connection.getOutputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					connection.getSocket().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				connection.getReceiveThread().interrupt();
				
				try {
					Client client = new Client(username, password, info.host(), info.port());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			thread0.start();
		}
	}
	
}
