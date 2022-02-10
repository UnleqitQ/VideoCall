package com.unleqitq.videocall.callclient;

import com.unleqitq.videocall.callclient.gui.MainWindow;
import com.unleqitq.videocall.callclient.gui.video.VideoPanels;
import com.unleqitq.videocall.callclient.utils.AudioUtils;
import com.unleqitq.videocall.callclient.utils.VideoUtils;
import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.AuthenticationData;
import com.unleqitq.videocall.transferclasses.base.AuthenticationResult;
import com.unleqitq.videocall.transferclasses.base.ListData;
import com.unleqitq.videocall.transferclasses.base.data.CallUserData;
import com.unleqitq.videocall.transferclasses.call.AudioData;
import com.unleqitq.videocall.transferclasses.call.RequestCallData;
import com.unleqitq.videocall.transferclasses.call.UserLeaveData;
import com.unleqitq.videocall.transferclasses.call.VideoData;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.Clip;
import javax.sound.sampled.Mixer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CallClient implements ReceiveListener {
	
	@NotNull
	private static CallClient instance;
	
	YAMLConfiguration configuration = new YAMLConfiguration();
	public ClientNetworkConnection connection;
	
	public MainWindow mainWindow;
	
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
	public UUID callUuid;
	
	@NotNull
	public Thread refreshThread;
	
	@NotNull
	public AudioUtils audioUtils;
	@NotNull
	public VideoUtils videoUtils;
	
	public Thread videoThread;
	public Thread audioThread;
	
	public BufferedImage icon;
	
	public boolean mute;
	public boolean video;
	
	@NotNull
	public static ThreadGroup threadGroup = new ThreadGroup("Client");
	
	public CallClient(@NotNull String username, @NotNull String password, @NotNull String host, int port, @NotNull UUID callUuid) throws
			IOException {
		this(username, password, host, port, callUuid, null);
	}
	
	public CallClient(@NotNull String username, @NotNull String password, @NotNull String host, int port, @NotNull UUID callUuid, UUID userUuid) throws
			IOException {
		System.out.println("Call Client PID: " + ProcessHandle.current().pid());
		instance = this;
		this.callUuid = callUuid;
		this.userUuid = userUuid;
		
		System.setProperty("webcam.debug", "false");
		System.setProperty("bridj.quiet", "true");
		
		icon = new BufferedImage(16, 16, BufferedImage.TYPE_3BYTE_BGR);
		
		{
			Graphics2D g = icon.createGraphics();
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, 128, 128);
			g.setColor(Color.RED);
			g.drawLine(0, 16, 16, 0);
			g.setStroke(new BasicStroke(2));
			g.dispose();
		}
		
		
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
		
		audioUtils = new AudioUtils();
		videoUtils = new VideoUtils();
		
		mainWindow = new MainWindow();
		
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
		
		try {
			Thread.sleep(1000 * 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		{
			List<Mixer.Info> speakers = audioUtils.getSpeakersList();
			if (speakers.size() > 0)
				audioUtils.setSpeakersInfo(speakers.get(0));
		}
		{
			List<Mixer.Info> microphones = audioUtils.getMicrophones();
			if (microphones.size() > 0)
				audioUtils.setMicrophoneInfo(microphones.get(0));
		}
		
		connection.send(new RequestCallData(callUuid));
		audioThread = new Thread(this::loopAudio);
		audioThread.start();
	}
	
	public void loopVideo() {
		while (true) {
			if (video) {
				try {
					BufferedImage image = videoUtils.capture();
					VideoData videoData = VideoData.create(image, userUuid);
					connection.send(videoData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1000 / clamp(configuration.getInt("video.fps"), 1, 50));
			} catch (InterruptedException ignored) {
				return;
			}
		}
	}
	
	public void loopAudio() {
		while (true) {
			while (!mute) {
				try {
					AudioData data = audioUtils.read();
					connection.send(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {
				return;
			}
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
					VideoPanels.instance.addPanel(user.getUuid());
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
		if (data.getData() instanceof CallUserData) {
			CallUser user = ((CallUserData) data.getData()).getUser();
			users.put(user.getUuid(), user);
			VideoPanels.instance.addPanel(user.getUuid());
			System.out.println(user);
		}
		if (data.getData() instanceof UserLeaveData leaveData) {
			VideoPanels.instance.removePanel(leaveData.uuid());
		}
		
		if (data.getData() instanceof AudioData audioData) {
			Clip clip = audioUtils.getClip(audioData.user());
			audioUtils.closeClip(audioUtils.stopClip(clip));
			audioUtils.openClip(clip, audioData.data(), audioData.offset(), audioData.bufferSize());
			audioUtils.startClip(clip);
		}
		if (data.getData() instanceof VideoData videoData) {
			try {
				VideoPanels.instance.receiveVideo(videoData);
			} catch (IOException e) {
				e.printStackTrace();
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
	
	public static void main(String[] args) {
		try {
			CallClient callClient = new CallClient(args[0], args[1], args[2], Integer.parseInt(args[3]),
					UUID.fromString(args[4]));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
