package com.unleqitq.videocall.callclient.gui.settings;

import com.github.sarxos.webcam.Webcam;
import com.unleqitq.videocall.callclient.CallClient;

import javax.sound.sampled.Mixer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DevicesPanel {
	
	public JPanel panel = new JPanel();
	
	public JLabel audioInputLabel = new JLabel("Audio Input");
	public JComboBox<Mixer.Info> audioInputBox = new JComboBox<>();
	public JLabel audioOutputLabel = new JLabel("Audio Output");
	public JComboBox<Mixer.Info> audioOutputBox = new JComboBox<>();
	public JLabel videoInputLabel = new JLabel("Video Input");
	public JComboBox<Webcam> videoInputBox = new JComboBox<>();
	
	public DevicesPanel() {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		audioInputLabel.setLabelFor(audioInputBox);
		audioOutputLabel.setLabelFor(audioOutputBox);
		videoInputLabel.setLabelFor(videoInputBox);
		
		audioInputBox.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					updateAudioInputs();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		audioOutputBox.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				updateAudioOutputs();
			}
		});
		
		audioInputBox.setMaximumSize(new Dimension(2000, 30));
		audioOutputBox.setMaximumSize(new Dimension(2000, 30));
		videoInputBox.setMaximumSize(new Dimension(2000, 30));
		
		panel.add(audioInputLabel);
		panel.add(audioInputBox);
		panel.add(audioOutputLabel);
		panel.add(audioOutputBox);
		panel.add(videoInputLabel);
		panel.add(videoInputBox);
		
		updateCams();
		updateAudioInputs();
		updateAudioOutputs();
		
		audioInputBox.addItemListener(e -> {
			try {
				CallClient.getInstance().audioUtils.setMicrophoneInfo((Mixer.Info) audioInputBox.getSelectedItem());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		audioOutputBox.addItemListener(e -> CallClient.getInstance().audioUtils.setSpeakersInfo(
				(Mixer.Info) audioOutputBox.getSelectedItem()));
		videoInputBox.addItemListener(
				e -> CallClient.getInstance().videoUtils.connect((Webcam) videoInputBox.getSelectedItem()));
	}
	
	public void updateCams() {
		videoInputBox.removeAllItems();
		CallClient.getInstance().videoUtils.getWebcams().forEach(videoInputBox::addItem);
	}
	
	public void updateAudioInputs() {
		audioInputBox.removeAllItems();
		CallClient.getInstance().audioUtils.getMicrophones().forEach(audioInputBox::addItem);
	}
	
	public void updateAudioOutputs() {
		audioOutputBox.removeAllItems();
		CallClient.getInstance().audioUtils.getSpeakersList().forEach(audioOutputBox::addItem);
	}
	
}
