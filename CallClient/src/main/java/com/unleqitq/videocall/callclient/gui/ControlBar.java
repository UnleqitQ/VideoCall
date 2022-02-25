package com.unleqitq.videocall.callclient.gui;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.callclient.ClientCallUser;
import com.unleqitq.videocall.callclient.gui.video.VideoPanels;

import javax.swing.*;

public class ControlBar {
	
	public JToolBar toolBar = new JToolBar("Control", JToolBar.HORIZONTAL);
	
	public JToggleButton videoButton = new JToggleButton("Video");
	public JToggleButton muteButton = new JToggleButton("Mute");
	public JButton screenButton = new JButton("Share Screen");
	public JButton layoutButton = new JButton("Layout");
	public JToggleButton focusScreenButton = new JToggleButton("Focus Screen");
	public JComboBox<ClientCallUser> focusUserBox = new JComboBox<>();
	public ScreenSettings screenSettings = new ScreenSettings();
	int i = 0;
	
	public ControlBar() {
		toolBar.add(videoButton);
		toolBar.add(muteButton);
		toolBar.add(screenButton);
		toolBar.add(layoutButton);
		toolBar.add(focusScreenButton);
		toolBar.add(focusUserBox);
		videoButton.addActionListener(a -> {
			CallClient.getInstance().video = videoButton.isSelected();
			System.out.println("Video: " + videoButton.isSelected());
		});
		muteButton.addActionListener(a -> {
			CallClient.getInstance().mute = muteButton.isSelected();
			System.out.println("Mute: " + muteButton.isSelected());
		});
		videoButton.setSelected(CallClient.getInstance().video);
		muteButton.setSelected(CallClient.getInstance().mute);
		screenButton.addActionListener(a -> {
			screenSettings.internalFrame.setLocation(
					screenButton.getLocationOnScreen().x - MainWindow.instance.frame.getLayeredPane().getLocationOnScreen().x,
					screenButton.getLocationOnScreen().y + screenButton.getHeight() - MainWindow.instance.frame.getLayeredPane().getLocationOnScreen().y);
			screenSettings.internalFrame.show();
		});
		focusUserBox.addItemListener(
				e -> VideoPanels.instance.focusedUser = ((ClientCallUser) focusUserBox.getSelectedItem()).uuid);
		focusScreenButton.addActionListener(e -> VideoPanels.instance.focusScreen = focusScreenButton.isSelected());
		layoutButton.addActionListener(e -> VideoPanels.instance.focusSingle = !VideoPanels.instance.focusSingle);
	}
	
	public void update() {
		screenSettings.update();
		if (i % 200 == 100) {
			focusUserBox.removeAllItems();
			CallClient.getInstance().clientCallUsers.values().forEach(focusUserBox::addItem);
		}
		
		i++;
	}
	
}
