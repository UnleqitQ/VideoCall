package com.unleqitq.videocall.callclient.gui.settings;

import com.unleqitq.videocall.callclient.CallClient;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel {
	
	public static SettingsPanel instance;
	public JScrollPane scrollPane = new JScrollPane();
	public JPanel panel = new JPanel();
	public JLabel gainLabel = new JLabel("Gain ()") {
		
		@Override
		public void paint(Graphics g) {
			setText(String.format("Gain (%03.01f %%)", gainSlider.getValue() * 0.1));
			super.paint(g);
		}
	};
	public JSlider gainSlider = new JSlider(0, 1000, 800);
	
	public DevicesPanel devicesPanel = new DevicesPanel();
	
	public SettingsPanel() {
		instance = this;
		scrollPane.setViewportView(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(devicesPanel.panel);
		gainLabel.setLabelFor(gainSlider);
		gainSlider.addChangeListener(e -> {
			gainLabel.repaint();
			CallClient.getInstance().audioUtils.updateGain();
		});
		panel.add(gainLabel);
		panel.add(gainSlider);
	}
	
}
