package com.unleqitq.videocall.callclient.gui.video;

import javax.swing.*;
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
	}
	
}
