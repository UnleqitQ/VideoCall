package com.unleqitq.videocall.client.gui;

import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.client.gui.calls.CallsPane;
import com.unleqitq.videocall.client.gui.teams.TeamsPane;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
	
	public JFrame frame;
	public JTabbedPane tabbedPane = new JTabbedPane();
	
	public CallsPane callsPane = new CallsPane();
	public TeamsPane teamsPane = new TeamsPane();
	
	Thread updateThread;
	
	public MainWindow() {
		frame = new JFrame("VideoCall | Logged in as: " + Client.getInstance().getUsername());
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(screen.width / 4, screen.height / 4, screen.width / 2, screen.height / 2);
		
		frame.setIconImage(Client.getInstance().icon);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		frame.add(tabbedPane);
		tabbedPane.addTab("Calls", callsPane.pane);
		tabbedPane.addTab("Teams", teamsPane.pane);
		
		callsPane.init();
		teamsPane.init();
		
		frame.setVisible(true);
		updateThread = new Thread(Client.threadGroup, this::updateLoop);
		//updateThread.setDaemon(true);
		updateThread.start();
	}
	
	public void updateLoop() {
		while (!Thread.currentThread().isInterrupted()) {
			update();
			try {
				Thread.sleep(Client.Config.guiUpdateSpeed);
			} catch (InterruptedException ignored) {
				return;
			}
		}
	}
	
	public void update() {
		if (tabbedPane.getSelectedComponent() == callsPane.pane)
			callsPane.update();
		if (tabbedPane.getSelectedComponent() == teamsPane.pane)
			teamsPane.update();
	}
	
}

