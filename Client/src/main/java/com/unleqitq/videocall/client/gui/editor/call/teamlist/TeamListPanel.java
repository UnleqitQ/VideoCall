package com.unleqitq.videocall.client.gui.editor.call.teamlist;

import com.github.weisj.darklaf.listener.MouseClickListener;
import com.unleqitq.videocall.sharedclasses.team.Team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class TeamListPanel implements MouseClickListener {
	
	TeamList teamList;
	public JPanel panel = new JPanel();
	public JLabel nameLabel = new JLabel();
	public JPanel memberPanel = new JPanel();
	public JLabel memberNameLabel = new JLabel();
	UUID teamUuid;
	
	
	public TeamListPanel(UUID uuid, TeamList teamList) {
		this.teamList = teamList;
		teamUuid = uuid;
		
		init();
	}
	
	public void init() {
		panel.setLayout(new GridLayout(1, 1));
		panel.add(nameLabel);
		panel.setMaximumSize(new Dimension(500, 50));
		panel.setMinimumSize(new Dimension(100, 50));
		panel.setPreferredSize(new Dimension(200, 50));
		
		panel.addMouseListener(this);
		nameLabel.addMouseListener(this);
		
		memberPanel.setLayout(new GridLayout(1, 1));
		memberPanel.add(memberNameLabel);
		memberPanel.setMaximumSize(new Dimension(500, 50));
		memberPanel.setMinimumSize(new Dimension(100, 50));
		memberPanel.setPreferredSize(new Dimension(200, 50));
		
		memberPanel.addMouseListener(this);
		memberNameLabel.addMouseListener(this);
	}
	
	public void update(boolean hovered) {
		Team team = teamList.teams.get(teamUuid);
		if (team == null) {
			nameLabel.setText("UUID: " + teamUuid);
			memberNameLabel.setText("UUID: " + teamUuid);
			return;
		}
		nameLabel.setText(team.getName());
		memberNameLabel.setText(team.getName());
		Color c = UIManager.getColor("Panel.background");
		float[] comp = c.getColorComponents(new float[3]);
		comp[0] += 1 / 3f;
		Color c1 = Color.getHSBColor(comp[0], comp[1], comp[2]);
		comp = c.getColorComponents(new float[3]);
		comp[0] += 2 / 3f;
		Color c2 = Color.getHSBColor(comp[0], comp[1], comp[2]);
		
		if (teamList.members.contains(teamUuid)) {
			if (hovered) {
				panel.setBackground(c2.darker());
				memberPanel.setBackground(c2.darker());
			}
			else {
				panel.setBackground(c2);
				memberPanel.setBackground(c2);
			}
		}
		else {
			if (hovered) {
				panel.setBackground(c.darker());
				memberPanel.setBackground(c.darker());
			}
			else {
				panel.setBackground(c);
				memberPanel.setBackground(c);
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isControlDown()) {
			if (e.isShiftDown()) {
				teamList.members.remove(teamUuid);
				teamList.updateList();
			}
			else {
				teamList.members.add(teamUuid);
				teamList.updateList();
			}
		}
		else {
			teamList.members.remove(teamUuid);
			teamList.updateList();
		}
	}
	
}
