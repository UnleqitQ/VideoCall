package com.unleqitq.videocall.callclient.gui.video;

import com.unleqitq.videocall.swingutils.QBoxLayout;

import javax.swing.*;

public class SmallVideoPanels {
	
	public JScrollPane scrollPane;
	public JPanel panel;
	
	public SmallVideoPanels() {
		scrollPane = new JScrollPane();
		panel = new JPanel();
		panel.setLayout(new QBoxLayout(panel, BoxLayout.Y_AXIS, 900));
		scrollPane.setViewportView(panel);
	}
	
	public void setVisible(boolean flag) {
		scrollPane.setVisible(flag);
	}
	
}
