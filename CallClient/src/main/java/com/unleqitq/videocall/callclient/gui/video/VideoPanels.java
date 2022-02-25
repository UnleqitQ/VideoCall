package com.unleqitq.videocall.callclient.gui.video;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.sharedclasses.user.CallUser;
import com.unleqitq.videocall.transferclasses.call.ScreenVideoData;
import com.unleqitq.videocall.transferclasses.call.VideoData;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class VideoPanels {
	
	public static VideoPanels instance;
	
	public JPanel panel;
	public SmallVideoPanels smallVideoPanels;
	public BigVideoPanels bigVideoPanels;
	public boolean focusSingle;
	public boolean focusScreen;
	public UUID focusedUser;
	
	public Map<UUID, VideoPanel> videoPanelMap;
	public List<UUID> videoPriorityList;
	
	public VideoPanels() {
		instance = this;
		panel = new JPanel();
		smallVideoPanels = new SmallVideoPanels();
		bigVideoPanels = new BigVideoPanels();
		videoPanelMap = new HashMap<>();
		videoPriorityList = new ArrayList<>();
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(bigVideoPanels.panel);
		panel.add(smallVideoPanels.scrollPane);
	}
	
	public void addPanel(UUID user) {
		if (videoPanelMap.containsKey(user))
			return;
		System.out.println("Added Panel: " + user);
		VideoPanel videoPanel = new VideoPanel(user);
		videoPanelMap.put(user, videoPanel);
		videoPriorityList.add(user);
		smallVideoPanels.panel.add(videoPanel.smallPanel);
		bigVideoPanels.bottomPanel.add(videoPanel.bigPanel);
	}
	
	public void removePanel(UUID user) {
		if (!videoPanelMap.containsKey(user))
			return;
		VideoPanel videoPanel = videoPanelMap.remove(user);
		videoPriorityList.remove(user);
		smallVideoPanels.panel.remove(videoPanel.smallPanel);
		bigVideoPanels.bottomPanel.remove(videoPanel.bigPanel);
	}
	
	public void realignPanels() {
		videoPriorityList.sort((u1, u2) -> {
			if (u1 == CallClient.getInstance().userUuid)
				return -1;
			if (u2 == CallClient.getInstance().userUuid)
				return 1;
			CallUser user1 = CallClient.getInstance().users.get(u1);
			CallUser user2 = CallClient.getInstance().users.get(u2);
			if (user1.muted != user2.muted)
				return user1.muted ? 1 : -1;
			if (user1.handRaised != user2.handRaised)
				return user1.handRaised ? -1 : 1;
			if (user1.video != user2.video)
				return user1.video ? -1 : 1;
			return (user1.getUsername() + u1).compareTo(user2.getUsername() + u2);
		});
		for (int i = 0; i < videoPriorityList.size(); i++) {
			UUID uuid = videoPriorityList.get(i);
			VideoPanel videoPanel = videoPanelMap.get(uuid);
			smallVideoPanels.panel.setComponentZOrder(videoPanel.smallPanel, i);
			bigVideoPanels.bottomPanel.setComponentZOrder(videoPanel.bigPanel, i);
		}
	}
	
	public void update() {
		smallVideoPanels.setVisible(!focusSingle);
		bigVideoPanels.setVisible(focusSingle);
		try {
			realignPanels();
		} catch (Exception ignored) {
		}
		try {
			videoPanelMap.values().forEach(VideoPanel::draw);
		} catch (Exception ignored) {
		}
		try {
			bigVideoPanels.bigVideoPanel.draw();
		} catch (Exception ignored) {
		}
	}
	
	public void receiveVideo(VideoData videoData) throws IOException {
		if (focusSingle) {
			if (!focusScreen) {
				if (videoData.user().equals(focusedUser)) {
					bigVideoPanels.bigVideoPanel.draw(videoData.getImage());
					return;
				}
			}
		}
		videoPanelMap.get(videoData.user()).draw(videoData.getImage());
	}
	
	public void receiveVideo(ScreenVideoData videoData) throws IOException {
		if (focusSingle) {
			if (focusScreen) {
				if (videoData.user().equals(focusedUser)) {
					bigVideoPanels.bigVideoPanel.draw(videoData.getImage());
					return;
				}
			}
			else {
				videoPanelMap.get(videoData.user()).draw(videoData.getImage());
				return;
			}
		}
		videoPanelMap.get(videoData.user()).draw(videoData.getImage());
	}
	
}
