package com.unleqitq.videocall.callclient.gui;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.callclient.ClientCallUser;
import com.unleqitq.videocall.callclient.gui.video.VideoPanels;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class ControlBar {
	
	public JToolBar toolBar = new JToolBar("Control", JToolBar.HORIZONTAL);
	
	public JToggleButton videoButton = new JToggleButton("Video");
	public JToggleButton muteButton = new JToggleButton("Mute");
	public JButton screenButton = new JButton("Share Screen");
	public JButton layoutButton = new JButton("Layout");
	public JToggleButton focusScreenButton = new JToggleButton("Focus Screen");
	public JComboBox<ClientCallUser> focusUserBox;
	public ScreenSettings screenSettings = new ScreenSettings();
	
	public ControlBar() {
		focusUserBox = new JComboBox<>() {
			
			@Override
			public void removeAllItems() {
				MutableComboBoxModel<ClientCallUser> model = (MutableComboBoxModel<ClientCallUser>) dataModel;
				Set<ClientCallUser> added = new HashSet<>();
				int size = model.getSize();
				int k = 0;
				for (int i = 0; i < size; ++i) {
					ClientCallUser element = model.getElementAt(k);
					if (CallClient.getInstance().clientCallUsers.containsKey(element.uuid)) {
						k++;
						added.add(element);
					}
					else {
						model.removeElement(element);
					}
				}
				CallClient.getInstance().clientCallUsers.values().stream().filter(c -> !added.contains(c)).forEach(
						model::addElement);
			}
		};
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
		focusUserBox.setMaximumSize(new Dimension(300, 300));
		focusScreenButton.addActionListener(e -> VideoPanels.instance.focusScreen = focusScreenButton.isSelected());
		layoutButton.addActionListener(e -> VideoPanels.instance.focusSingle = !VideoPanels.instance.focusSingle);
	}
	
	public void updateUsers() {
		//ClientCallUser sel = (ClientCallUser) focusUserBox.getSelectedItem();
		focusUserBox.removeAllItems();
		//CallClient.getInstance().clientCallUsers.values().forEach(focusUserBox::addItem);
		//focusUserBox.setSelectedItem(sel);
	}
	
	public void update() {
		screenSettings.update();
		updateUsers();
	}
	
}
