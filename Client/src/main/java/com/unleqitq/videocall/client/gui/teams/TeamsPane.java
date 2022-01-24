package com.unleqitq.videocall.client.gui.teams;

import com.unleqitq.videocall.client.gui.teams.info.TeamInfoPanel;
import com.unleqitq.videocall.client.gui.teams.list.TeamsList;

import javax.swing.*;

public class TeamsPane {
	
	public JSplitPane pane = new JSplitPane();
	public TeamsList teamsList = new TeamsList();
	public TeamInfoPanel teamInfoPanel = new TeamInfoPanel(this);
	
	public TeamsPane() {
	}
	
	public void init() {
		pane.setLeftComponent(teamsList.root);
		pane.setRightComponent(teamInfoPanel.panel);
		pane.setDividerLocation(220);
	}
	
	public void update() {
		try {
			teamsList.updatePanels();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			teamInfoPanel.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
