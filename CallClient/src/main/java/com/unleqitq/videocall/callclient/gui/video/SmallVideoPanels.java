package com.unleqitq.videocall.callclient.gui.video;

import javax.swing.*;

public class SmallVideoPanels {
	
	public JScrollPane scrollPane;
	public JPanel panel;
	
	public SmallVideoPanels() {
		scrollPane = new JScrollPane();
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(panel);
	}
	
	public void setVisible(boolean flag) {
		scrollPane.setVisible(flag);
	}
	
}
