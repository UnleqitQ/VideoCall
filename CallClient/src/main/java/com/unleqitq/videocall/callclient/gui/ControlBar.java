package com.unleqitq.videocall.callclient.gui;

import com.unleqitq.videocall.callclient.CallClient;

import javax.swing.*;

public class ControlBar {
	
	JToolBar toolBar = new JToolBar("Control", JToolBar.HORIZONTAL);
	
	JToggleButton videoButton = new JToggleButton("Video");
	JToggleButton muteButton = new JToggleButton("Mute");
	
	public ControlBar() {
		toolBar.add(videoButton);
		toolBar.add(muteButton);
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
	}
	
}
