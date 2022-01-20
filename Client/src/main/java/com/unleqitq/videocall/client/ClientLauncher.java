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
	
	private UUID userUuid;
	@NotNull
	private UnknownValues unknownValues;
	
	public ClientLauncher() throws IOException {
		loadConfig();
		
		unknownValues = new UnknownValues();
		
		ClientNetworkConnection.maxTimeDifference = configuration.getInt("network.maxTimeDifference", 4) * 1000;
		String host = configuration.getString("network.server.root.host", "localhost");
		int port = configuration.getInt("network.server.root.port", 1000);
		
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
	
	
	public static void main(String @NotNull [] args) throws IOException {
		ClientLauncher client = new ClientLauncher();
	}
	
	@Override
	public void onReceive(Data data) {
		System.out.println(data);
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
					Client client = new Client(info.host(), info.port());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			thread0.start();
		}
	}
	
}
