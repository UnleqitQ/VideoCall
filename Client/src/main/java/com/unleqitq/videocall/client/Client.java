package com.unleqitq.videocall.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.unleqitq.videocall.client.gui.MainWindow;
import com.unleqitq.videocall.client.gui.login.LoginGui;
import com.unleqitq.videocall.client.managers.*;
import com.unleqitq.videocall.sharedclasses.ClientNetworkConnection;
import com.unleqitq.videocall.sharedclasses.ReceiveListener;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.Data;
import com.unleqitq.videocall.transferclasses.base.*;
import com.unleqitq.videocall.transferclasses.base.data.CallData;
import com.unleqitq.videocall.transferclasses.base.data.TeamData;
import com.unleqitq.videocall.transferclasses.base.data.UserData;
import com.unleqitq.videocall.transferclasses.connection.ConnectionInformation;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Client implements ReceiveListener {
	
	@NotNull
	private static Client instance;
	
	private boolean stopping;
	
	YAMLConfiguration configuration = new YAMLConfiguration();
	public ClientNetworkConnection connection;
	
	@NotNull
	private final LoginGui loginGui = new LoginGui();
	
	@Nullable
	private String username;
	@Nullable
	private String password;
	public UUID userUuid;
	
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
	@NotNull
	public Thread unknownRequestThread;
	MainWindow window;
	
	@NotNull
	public static ThreadGroup threadGroup = new ThreadGroup("Client");
	
	public LookAndFeel laf;
	
	public Client(@NotNull String host, int port) throws IOException {
		instance = this;
		
		loadConfig();
		refreshThread = new Thread(threadGroup, this::refreshLoop);
		unknownRequestThread = new Thread(threadGroup, this::unknownRequestLoop);
		
		managerHandler = new ManagerHandler();
		managerHandler.setCallManager(new CallManager(managerHandler)).setTeamManager(
				new TeamManager(managerHandler)).setUserManager(new UserManager(managerHandler)).setAccountManager(
				new AccountManager(managerHandler)).setConfiguration(configuration);
		
		Config.cacheDuration = clamp(configuration.getInt("cacheDuration", 120), 20, 300);
		Config.guiUpdateSpeed = clamp(configuration.getInt("guiUpdateSpeed", 100), 4, 500);
		Config.unknownValuesRequestInterval = clamp(configuration.getInt("unknownValuesRequestInterval", 1000), 100,
				3000);
		Config.videoMaxTimeDifference = clamp(configuration.getInt("video.maxTimeDifference", 120), 20, 300);
		//Config.cacheDuration = clamp(configuration.getInt("cacheDuration", 120), 20, 300);
		Config.refreshInterval = clamp(configuration.getInt("refreshInterval", 10), 5,
				Math.min(120, Config.cacheDuration * 4 / 5));
		Config.audioDuration = clamp(configuration.getFloat("audio.duration", 0.5f), 0.2f, 4f);
		
		userCache = CacheBuilder.newBuilder().expireAfterAccess(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).expireAfterWrite(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).build();
		teamCache = CacheBuilder.newBuilder().expireAfterAccess(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).expireAfterWrite(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).build();
		callCache = CacheBuilder.newBuilder().expireAfterAccess(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).expireAfterWrite(
				Duration.ofSeconds(Config.cacheDuration * 1000L)).build();
		
		
		ClientNetworkConnection.maxTimeDifference = configuration.getInt("network.maxTimeDifference", 4) * 1000;
		ClientNetworkConnection.maxTimeDifference = Math.max(1,
				Math.min(30, ClientNetworkConnection.maxTimeDifference)) * 1000;
		
		
		this.host = host;
		this.port = port;
		
		Socket socket;
		try {
			socket = new Socket(host, port);
		} catch (ConnectException e) {
			stop(e);
			return;
		}
		connection = new ClientNetworkConnection(socket);
		connection.setListener(this);
		connection.init();
		
		try {
			Thread.sleep(1000 * 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			connection.send(new ConnectionInformation(ConnectionInformation.ClientType.CLIENT));
		} catch (IllegalStateException e) {
			stop(e);
		}
		
		try {
			Thread.sleep(1000 * 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		loginGui.show();
	}
	
	public void finishInit() {
		refreshThread.start();
		unknownRequestThread.start();
		window = new MainWindow();
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
		while (!Thread.currentThread().isInterrupted()) {
			sendUnknownRequest();
			try {
				Thread.sleep(Config.unknownValuesRequestInterval);
			} catch (InterruptedException ignored) {
				return;
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
		try {
			connection.send(PackRequest.create(users, calls, teams));
		} catch (IllegalStateException e) {
			stop(e);
		}
	}
	
	private void refreshLoop() {
		while (!Thread.currentThread().isInterrupted()) {
			sendRefreshRequest();
			try {
				Thread.sleep(Config.refreshInterval * 1000L);
			} catch (InterruptedException ignored) {
				return;
			}
		}
	}
	
	private void sendRefreshRequest() {
		try {
			connection.send(new ClientListRequest());
		} catch (IllegalStateException e) {
			stop(e);
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
	
	@Nullable
	public String getUsername() {
		return username;
	}
	
	@Nullable
	public String getPassword() {
		return password;
	}
	
	
	@Override
	public void onReceive(Data data) {
		//System.out.println(data.getData());
		if (data.getData() instanceof ListData) {
			for (Serializable d0 : ((ListData) data.getData()).data()) {
				if (d0 instanceof UserData) {
					User user = ((UserData) d0).getUser(managerHandler);
					managerHandler.getUserManager().addUser(user);
					unknownValues.users.remove(user.getUuid());
					System.out.println(user);
				}
				if (d0 instanceof TeamData) {
					Team team = ((TeamData) d0).getTeam(managerHandler);
					managerHandler.getTeamManager().addTeam(team);
					window.teamsPane.teamsList.add(team.getUuid());
					unknownValues.teams.remove(team.getUuid());
					System.out.println(team);
				}
				if (d0 instanceof CallData) {
					CallDefinition call = ((CallData) d0).getCall(managerHandler);
					managerHandler.getCallManager().addCall(call);
					window.callsPane.callsList.add(call.getUuid());
					unknownValues.calls.remove(call.getUuid());
					System.out.println(call);
				}
			}
			window.callsPane.callsList.updateList();
			window.callsPane.callsList.updatePanels();
			window.teamsPane.teamsList.updateList();
			window.teamsPane.teamsList.updatePanels();
		}
		if (data.getData() instanceof AuthenticationResult result) {
			switch (result.result()) {
				case -2 -> System.out.println("User doesn't exist");
				case -1 -> System.out.println("Some weird error");
				case 0 -> System.out.println("Password is wrong");
				case 1 -> userUuid = result.userUuid();
			}
			try {
				loginGui.resultQueue.put(result);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (result.result() > 0)
				finishInit();
		}
	}
	
	public void sendAuthentication() {
		try {
			if (password != null && username != null) {
				try {
					connection.send(AuthenticationData.create(username, password));
				} catch (IllegalStateException e) {
					stop(e);
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public synchronized void stop(Exception e) {
		if (stopping)
			return;
		stopping = true;
		new Thread(() -> new Thread(() -> {
			Thread[] threads = new Thread[threadGroup.activeCount() + 2];
			threadGroup.enumerate(threads);
			for (Thread thread : threads) {
				try {
					thread.interrupt();
				} catch (SecurityException | NullPointerException ignored) {
				}
			}
			if (window != null)
				window.frame.dispose();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String s = e.getMessage() + "\n\n" + sw + "\n\nRerun?";
			int response = errorDialog(s, "Houston, we have a problem", JOptionPane.OK_CANCEL_OPTION);
			if (response == JOptionPane.CLOSED_OPTION || response == JOptionPane.CANCEL_OPTION)
				System.exit(0);
			else {
				try {
					new Client(host, port);
				} catch (IOException ex) {
					ex.printStackTrace();
					System.exit(0);
				}
			}
		}).start()).start();
	}
	
	private static boolean stoppingG = false;
	
	public static synchronized void stopG(Exception e) {
		if (stoppingG)
			return;
		stoppingG = true;
		new Thread(() -> new Thread(() -> {
			Thread[] threads = new Thread[threadGroup.activeCount() + 2];
			threadGroup.enumerate(threads);
			for (Thread thread : threads) {
				try {
					thread.interrupt();
				} catch (SecurityException | NullPointerException ignored) {
				}
			}
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String s = e.getMessage() + "\n\n" + sw;
			errorDialog(s, "Houston, we have a problem", JOptionPane.DEFAULT_OPTION);
			System.exit(0);
		}).start()).start();
	}
	
	public static synchronized void stopGAndSend(Exception e) {
		if (stoppingG)
			return;
		stoppingG = true;
		new Thread(() -> new Thread(() -> {
			Thread[] threads = new Thread[threadGroup.activeCount() + 2];
			threadGroup.enumerate(threads);
			for (Thread thread : threads) {
				try {
					thread.interrupt();
				} catch (SecurityException | NullPointerException ignored) {
				}
			}
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String s = e.getMessage() + "\n\n" + sw + "\n\nSend Report?";
			int r = errorDialog(s, "Houston, we have a problem", JOptionPane.OK_CANCEL_OPTION);
			if (r == JOptionPane.OK_OPTION) {
				sendReport(e);
			}
			System.exit(0);
		}).start()).start();
	}
	
	public static void errorG(Exception e) {
		new Thread(() -> new Thread(() -> {
			Thread[] threads = new Thread[threadGroup.activeCount() + 2];
			threadGroup.enumerate(threads);
			for (Thread thread : threads) {
				try {
					thread.interrupt();
				} catch (SecurityException | NullPointerException ignored) {
				}
			}
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String s = e.getMessage() + "\n\n" + sw;
			errorDialog(s, "Houston, we have a problem", JOptionPane.DEFAULT_OPTION);
		}).start()).start();
	}
	
	public static void errorGAndSend(Exception e) {
		new Thread(() -> new Thread(() -> {
			Thread[] threads = new Thread[threadGroup.activeCount() + 2];
			threadGroup.enumerate(threads);
			for (Thread thread : threads) {
				try {
					thread.interrupt();
				} catch (SecurityException | NullPointerException ignored) {
				}
			}
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String s = e.getMessage() + "\n\n" + sw + "\n\nSend Report?";
			int r = errorDialog(s, "Houston, we have a problem", JOptionPane.OK_CANCEL_OPTION);
			if (r == JOptionPane.OK_OPTION) {
				sendReport(e);
			}
		}).start()).start();
	}
	
	public void setUsername(@Nullable String username) {
		this.username = username;
	}
	
	public void setPassword(@Nullable String password) {
		this.password = password;
	}
	
	public static final class Config {
		
		public static String networkServerRootHost;
		public static int networkServerRootPort;
		public static int networkMaxTimeDifference;
		public static int videoMaxTimeDifference;
		public static float audioDuration;
		public static int cacheDuration;
		public static int guiUpdateSpeed;
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
	
	@SuppressWarnings ({"deprecation", "UnnecessaryUnboxing", "RedundantSuppression"})
	public static int errorDialog(Object message, String title, int optionType) throws HeadlessException {
		int messageType = JOptionPane.ERROR_MESSAGE;
		int response = JOptionPane.showOptionDialog(null, message, title, optionType, messageType, null, null, null);
		return response;
		/*JOptionPane pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);
		pane.setInitialValue(initialValue);
		pane.setComponentOrientation(
				((parentComponent == null) ? JOptionPane.getRootFrame() : parentComponent).getComponentOrientation());
		
		int style = 0;
		try {
			style = new ReflectionClassSession(JOptionPane.class).getMethod("styleFromMessageType").invoke(null,
					messageType).hashCode();
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		JDialog dialog;
		try {
			dialog = (JDialog) new ReflectionSession(pane).getMethod("createDialog").invoke(pane, parentComponent,
					title, style);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			return JOptionPane.CLOSED_OPTION;
		}
		
		pane.selectInitialValue();
		dialog.show();
		dialog.dispose();
		
		Object selectedValue = pane.getValue();
		
		if (selectedValue == null)
			return JOptionPane.CLOSED_OPTION;
		if (options == null) {
			if (selectedValue instanceof Integer)
				return ((Integer) selectedValue).intValue();
			return JOptionPane.CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
			if (options[counter].equals(selectedValue))
				return counter;
		}
		return JOptionPane.CLOSED_OPTION;
		*/
	}
	
	private static void sendReport(Exception e) {
	
	}
	
	private static void sendReportToRoot(Exception e) {
	
	}
	
}
