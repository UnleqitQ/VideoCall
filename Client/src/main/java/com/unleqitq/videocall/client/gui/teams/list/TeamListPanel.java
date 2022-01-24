package com.unleqitq.videocall.client.gui.teams.list;

import com.github.weisj.darklaf.listener.MouseClickListener;
import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class TeamListPanel implements MouseClickListener, KeyListener {
	
	TeamsList teamsList;
	public JPanel panel = new JPanel();
	public JLabel nameLabel = new JLabel();
	public JLabel creatorLabel = new JLabel();
	UUID teamUuid;
	
	private static final CompoundBorder lowered = new CompoundBorder(
			new BevelBorder(BevelBorder.LOWERED, Color.lightGray, Color.darkGray), new EmptyBorder(5, 5, 5, 5));
	private static final CompoundBorder raised = new CompoundBorder(
			new BevelBorder(BevelBorder.RAISED, Color.lightGray, Color.darkGray), new EmptyBorder(5, 5, 5, 5));
	
	
	public TeamListPanel(UUID uuid, TeamsList teamsList) {
		this.teamsList = teamsList;
		teamUuid = uuid;
		
		init();
	}
	
	public void init() {
		panel.setLayout(new GridLayout(2, 1));
		panel.add(nameLabel);
		panel.add(creatorLabel);
		panel.setBorder(raised);
		panel.setMaximumSize(new Dimension(2000, 100));
		panel.setMinimumSize(new Dimension(100, 100));
		panel.setPreferredSize(new Dimension(200, 100));
		
		panel.addMouseListener(this);
		nameLabel.addMouseListener(this);
		creatorLabel.addMouseListener(this);
		
		panel.addKeyListener(this);
		nameLabel.addKeyListener(this);
		creatorLabel.addKeyListener(this);
	}
	
	public void update(boolean selected, boolean hovered) {
		Team team = Client.getInstance().getTeam(teamUuid);
		if (team == null) {
			nameLabel.setText("UUID: " + teamUuid);
			creatorLabel.setText("No Info");
			return;
		}
		nameLabel.setText(team.getName());
		{
			User user = Client.getInstance().getUser(team.getCreator());
			if (user != null)
				creatorLabel.setText(user.getUsername());
			else
				creatorLabel.setText(team.getCreator().toString());
		}
		panel.setBorder(raised);
		
		if (selected) {
			panel.setBorder(lowered);
		}
		if (hovered)
			panel.setBackground(UIManager.getColor("Panel.background").darker());
		else
			panel.setBackground(UIManager.getColor("Panel.background"));
	}
	
	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		teamsList.selected = teamUuid;
		teamsList.updatePanels();
	}
	
	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		teamsList.selected = teamUuid;
		teamsList.updatePanels();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			teamsList.selected = null;
		teamsList.updatePanels();
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			teamsList.selected = null;
		teamsList.updatePanels();
	}
	
}
