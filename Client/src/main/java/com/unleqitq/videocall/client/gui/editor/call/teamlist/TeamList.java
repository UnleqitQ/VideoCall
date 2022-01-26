package com.unleqitq.videocall.client.gui.editor.call.teamlist;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.swingutils.QTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TeamList implements KeyListener {
	
	public Map<UUID, Team> teams;
	CacheLoader cacheLoader = new CacheLoader<UUID, TeamListPanel>() {
		
		@Override
		public TeamListPanel load(UUID key) {
			return new TeamListPanel(key, TeamList.this);
		}
	};
	public LoadingCache<UUID, TeamListPanel> panelCache = CacheBuilder.newBuilder().expireAfterAccess(30,
			TimeUnit.SECONDS).build(cacheLoader);
	public Set<UUID> members = new HashSet<>();
	public Set<UUID> denied = new HashSet<>();
	public JPanel panel = new JPanel();
	QTextField searchField = new QTextField();
	JLabel memberLabel = new JLabel("Members");
	JScrollPane memberScrollPane = new JScrollPane();
	JLabel allLabel = new JLabel("All");
	JScrollPane allScrollPane = new JScrollPane();
	
	JPanel memberPanel = new JPanel();
	JPanel allPanel = new JPanel();
	
	public TeamList(Map<UUID, Team> teams) {
		this.teams = teams;
		panel.setMinimumSize(new Dimension(200, 200));
		panel.setPreferredSize(new Dimension(220, 400));
		GridBagLayout gbl = new GridBagLayout();
		gbl.rowWeights = new double[]{0.5, 0.1, 1, 0.1, 1, 0.1, 1};
		gbl.columnWeights = new double[]{1};
		panel.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 0;
		panel.add(searchField, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 1;
		panel.add(memberLabel, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 2;
		panel.add(memberScrollPane, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 5;
		panel.add(allLabel, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 6;
		panel.add(allScrollPane, gbc);
		
		memberScrollPane.setViewportView(memberPanel);
		allScrollPane.setViewportView(allPanel);
		memberPanel.setLayout(new BoxLayout(memberPanel, BoxLayout.Y_AXIS));
		allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));
		
		searchField.addKeyListener(this);
	}
	
	public void updateList() {
		allPanel.removeAll();
		memberPanel.removeAll();
		for (UUID uuid : members) {
			try {
				Team team = teams.get(uuid);
				if (team.getName().toLowerCase().contains(searchField.getText().strip().toLowerCase()))
					memberPanel.add(panelCache.get(uuid).memberPanel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (searchField.getText().strip().length() > 1) {
			for (Team team : teams.values()) {
				if (team.getName().toLowerCase().contains(searchField.getText().strip().toLowerCase())) {
					try {
						allPanel.add(panelCache.get(team.getUuid()).panel);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}
		update();
	}
	
	public void update() {
		Point pos = MouseInfo.getPointerInfo().getLocation();
		for (TeamListPanel p : panelCache.asMap().values()) {
			boolean hovering = false;
			try {
				hovering = new Rectangle(p.panel.getLocationOnScreen(), p.panel.getSize()).contains(pos);
			} catch (Exception ignored) {
			}
			try {
				hovering |= new Rectangle(p.memberPanel.getLocationOnScreen(), p.memberPanel.getSize()).contains(pos);
			} catch (Exception ignored) {
			}
			p.update(hovering);
		}
		panel.setVisible(true);
		memberPanel.setVisible(true);
		allPanel.setVisible(true);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		updateList();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		updateList();
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		updateList();
	}
	
}
