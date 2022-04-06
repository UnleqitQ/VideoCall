package com.unleqitq.videocall.callclient.gui;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.callclient.gui.settings.SettingsPanel;
import com.unleqitq.videocall.callclient.gui.video.VideoPanels;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
	
	public static MainWindow instance;
	
	public JFrame frame;
	public JPanel panel = new JPanel();
	public ControlBar controlBar;
	public VideoPanels videoPanels;
	public SettingsPanel settingsPanel;
	
	Thread updateThread;
	
	public MainWindow() {
		/*try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}*/
		instance = this;
		frame = new JFrame("VideoCall | Logged in as: " + CallClient.getInstance().getUsername());
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(screen.width / 4, screen.height / 4, screen.width / 2, screen.height / 2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setIconImage(CallClient.getInstance().icon);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
		videoPanels = new VideoPanels();
		controlBar = new ControlBar();
		settingsPanel = new SettingsPanel();
		
		frame.add(panel);
		
		panel.setLayout(new BorderLayout());
		
		panel.add(videoPanels.panel, BorderLayout.CENTER);
		panel.add(controlBar.toolBar, BorderLayout.NORTH);
		panel.add(settingsPanel.panel, BorderLayout.EAST);
		
		frame.setVisible(true);
		updateThread = new Thread(CallClient.threadGroup, this::updateLoop);
		//updateThread.setDaemon(true);
		updateThread.start();
	}
	
	public void updateLoop() {
		while (!Thread.currentThread().isInterrupted()) {
			update();
			if (frame.isActive())
				frame.setVisible(true);
			try {
				Thread.sleep(1000 / clamp(CallClient.getInstance().configuration.getInt("gui.updateSpeed"), 1, 100));
			} catch (InterruptedException ignored) {
				return;
			}
		}
	}
	
	public void update() {
		try {
			videoPanels.update();
		} catch (Exception ignored) {
		}
		try {
			controlBar.update();
		} catch (Exception ignored) {
		}
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
