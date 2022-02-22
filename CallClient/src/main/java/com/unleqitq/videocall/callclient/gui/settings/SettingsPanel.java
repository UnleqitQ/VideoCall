package com.unleqitq.videocall.callclient.gui.settings;

import javax.swing.*;

public class SettingsPanel {
	
	public static SettingsPanel instance;
	public JScrollPane scrollPane = new JScrollPane();
	public JPanel panel = new JPanel();
	
	public DevicesPanel devicesPanel = new DevicesPanel();
	
	public SettingsPanel() {
		instance = this;
		scrollPane.setViewportView(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(devicesPanel.panel);
	}
	
}
