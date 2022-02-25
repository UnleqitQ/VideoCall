package com.unleqitq.videocall.callclient.gui.video;

import javax.swing.*;
import java.awt.*;

public class BigVideoPanels {
	
	public JScrollPane scrollPane;
	public JPanel bottomPanel;
	public JPanel panel;
	public BigVideoPanel bigVideoPanel;
	
	public BigVideoPanels() {
		scrollPane = new JScrollPane();
		bottomPanel = new JPanel();
		panel = new JPanel();
		bigVideoPanel = new BigVideoPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(bigVideoPanel.panel);
		panel.add(scrollPane);
		scrollPane.setViewportView(bottomPanel);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		scrollPane.setMinimumSize(new Dimension(400, 260));
	}
	
	public void setVisible(boolean flag) {
		panel.setVisible(flag);
	}
	
}
