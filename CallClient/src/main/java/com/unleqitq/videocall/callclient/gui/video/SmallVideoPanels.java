package com.unleqitq.videocall.callclient.gui.video;

import com.unleqitq.videocall.swingutils.QBoxLayout;

import javax.swing.*;

public class SmallVideoPanels {
	
	public JScrollPane scrollPane;
	public JPanel panel;
	
	public SmallVideoPanels() {
		scrollPane = new JScrollPane();
		panel = new JPanel();
		panel.setLayout(new QBoxLayout(panel, BoxLayout.X_AXIS));
		scrollPane.setViewportView(panel);
	}
	
	public void setVisible(boolean flag) {
		scrollPane.setVisible(flag);
	}
	
}
