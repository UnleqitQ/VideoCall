package com.unleqitq.videocall.callclient.gui.video;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.callclient.ClientCallUser;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import com.unleqitq.videocall.transferclasses.base.data.CallUserData;
import com.unleqitq.videocall.transferclasses.call.UserBanData;
import com.unleqitq.videocall.transferclasses.call.UserLeaveData;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class UserSettings {
	
	public UUID uuid;
	
	public JPopupMenu popupMenu;
	//public JInternalFrame internalFrame;
	//public JScrollPane scrollPane = new JScrollPane();
	public JPanel gainPanel = new JPanel();
	
	public JLabel gainLabel = new JLabel("Gain ()") {
		
		@Override
		public void paint(Graphics g) {
			setText(String.format("Gain (%03.01f %%)", gainSlider.getValue() * 0.1));
			super.paint(g);
		}
	};
	public JSlider gainSlider = new JSlider(0, 1000, 1000);
	
	public JButton muteButton = new JButton("Mute");
	public JButton kickButton = new JButton("Kick");
	public JButton banButton = new JButton("Ban");
	
	public UserSettings(UUID uuid) {
		this.uuid = uuid;
		//internalFrame = new JInternalFrame("Settings (" + CallClient.getInstance().users.get(
		//		uuid).getFirstname() + " " + CallClient.getInstance().users.get(uuid).getLastname() + ")");
		//internalFrame.add(scrollPane);
		//internalFrame.setSize(200, 400);
		popupMenu = new JPopupMenu("Settings (" + CallClient.getInstance().users.get(
				uuid).getFirstname() + " " + CallClient.getInstance().users.get(uuid).getLastname() + ")");
		//popupMenu.add(scrollPane);
		//scrollPane.setViewportView(gainPanel);
		popupMenu.add(gainPanel);
		popupMenu.add(muteButton);
		popupMenu.add(kickButton);
		popupMenu.add(banButton);
		
		gainPanel.setLayout(new BoxLayout(gainPanel, BoxLayout.Y_AXIS));
		gainLabel.setLabelFor(gainSlider);
		if (!CallClient.getInstance().clientCallUsers.containsKey(uuid))
			CallClient.getInstance().clientCallUsers.put(uuid, new ClientCallUser(uuid));
		gainSlider.setValue((int) (CallClient.getInstance().clientCallUsers.get(uuid).gain * 1000));
		gainSlider.addChangeListener(e -> {
			gainLabel.repaint();
			CallClient.getInstance().clientCallUsers.get(this.uuid).gain = gainSlider.getValue() * 0.001f;
			CallClient.getInstance().audioUtils.updateGain(this.uuid);
		});
		gainPanel.add(gainLabel);
		gainPanel.add(gainSlider);
		
		muteButton.addActionListener(e -> {
			if (CallClient.getInstance().users.get(CallClient.getInstance().userUuid).permission.isMuteOthers()) {
				CallUser callUser = CallClient.getInstance().users.get(this.uuid);
				callUser.muted = true;
				CallClient.getInstance().connection.send(new CallUserData(callUser));
			}
			else
				System.out.println("no perm");
		});
		kickButton.addActionListener(e -> {
			System.out.println(CallClient.getInstance().users.get(CallClient.getInstance().userUuid).permission);
			if (CallClient.getInstance().users.get(CallClient.getInstance().userUuid).permission.isKick()) {
				CallClient.getInstance().connection.send(new UserLeaveData(this.uuid));
			}
			else
				System.out.println("no perm");
		});
		banButton.addActionListener(e -> {
			System.out.println(CallClient.getInstance().users.get(CallClient.getInstance().userUuid).permission);
			if (CallClient.getInstance().users.get(CallClient.getInstance().userUuid).permission.isBan()) {
				CallClient.getInstance().connection.send(new UserBanData(this.uuid));
			}
			else
				System.out.println("no perm");
		});
		//MainWindow.instance.frame.add(internalFrame);
		//internalFrame.setClosable(true);
		//internalFrame.setContentPane(MainWindow.instance.frame.getContentPane());
		//internalFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		//internalFrame.setMaximizable(false);
		//internalFrame.hide();
	}
	
	public void dispose() {
		//MainWindow.instance.frame.remove(internalFrame);
	}
	
}
