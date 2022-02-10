package com.unleqitq.videocall.callclient.gui;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.callclient.gui.video.VideoPanels;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
	
	public JFrame frame;
	public JPanel panel = new JPanel();
	VideoPanels videoPanels;
	
	Thread updateThread;
	
	public MainWindow() {
		frame = new JFrame("VideoCall | Logged in as: " + CallClient.getInstance().getUsername());
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(screen.width / 4, screen.height / 4, screen.width / 2, screen.height / 2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setIconImage(CallClient.getInstance().icon);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		videoPanels = new VideoPanels();
		
		frame.add(panel);
		
		panel.setLayout(new BorderLayout());
		
		panel.add(videoPanels.panel, BorderLayout.CENTER);
		
		frame.setVisible(true);
		updateThread = new Thread(CallClient.threadGroup, this::updateLoop);
		//updateThread.setDaemon(true);
		updateThread.start();
	}
	
	public void updateLoop() {
		while (!Thread.currentThread().isInterrupted()) {
			update();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {
				return;
			}
		}
	}
	
	public void update() {
		videoPanels.update();
	}
	
	
}
