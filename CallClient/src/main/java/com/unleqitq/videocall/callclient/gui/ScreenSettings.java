package com.unleqitq.videocall.callclient.gui;

import com.unleqitq.videocall.callclient.utils.ScreenUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ScreenSettings {
	
	public JInternalFrame internalFrame;
	public JPanel devicePanel = new JPanel();
	public JComboBox<GraphicsDevice> deviceBox = new JComboBox<GraphicsDevice>();
	public Canvas previewCanvas = new Canvas();
	public JButton shareButton = new JButton("Share Screen/Update");
	public JButton stopButton = new JButton("Stop Sharing");
	public boolean preview = false;
	public ScreenUtils screenUtils = new ScreenUtils();
	
	public ScreenSettings() {
		internalFrame = new JInternalFrame();
		internalFrame.setSize(300, 500);
		internalFrame.setClosable(true);
		internalFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		internalFrame.setMaximizable(false);
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = new double[]{1};
		gbl.rowWeights = new double[]{5, 1, 1};
		internalFrame.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		MainWindow.instance.frame.getLayeredPane().add(internalFrame);
		devicePanel.setLayout(new BorderLayout());
		devicePanel.add(previewCanvas, BorderLayout.CENTER);
		devicePanel.add(deviceBox, BorderLayout.NORTH);
		internalFrame.add(devicePanel, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 1;
		internalFrame.add(shareButton, gbc);
		gbc.gridy = 2;
		internalFrame.add(stopButton, gbc);
		previewCanvas.setSize(300, 200);
		
		deviceBox.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				updateDevices();
			}
		});
		deviceBox.addItemListener(e -> {
			try {
				screenUtils.connect((GraphicsDevice) e.getItem());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		previewCanvas.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				preview = !preview;
			}
		});
	}
	
	private void updateDevices() {
		deviceBox.removeAllItems();
		Arrays.stream(screenUtils.getDevices()).forEach(deviceBox::addItem);
	}
	
	public void update() {
		if (!internalFrame.isVisible())
			return;
		BufferedImage img;
		if (preview) {
			img = screenUtils.capture();
			if (img == null) {
				img = new BufferedImage(300, 200, BufferedImage.TYPE_BYTE_GRAY);
				Graphics2D g = img.createGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, 300, 200);
				g.setColor(Color.WHITE);
				g.drawString("Didn't get Screen", 20, 150);
			}
		}
		else {
			img = new BufferedImage(300, 200, BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g = img.createGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 300, 200);
			g.setColor(Color.WHITE);
			g.drawString("Preview Disabled", 20, 150);
		}
		previewCanvas.getGraphics().drawImage(img, 0, 0, 300, 200, null);
	}
	
}
