package com.unleqitq.videocall.client.gui.editor.call.userlist;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.unleqitq.videocall.sharedclasses.user.User;
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

public class UserList implements KeyListener {
	
	public Map<UUID, User> users;
	CacheLoader cacheLoader = new CacheLoader<UUID, UserListPanel>() {
		
		@Override
		public UserListPanel load(UUID key) {
			return new UserListPanel(key, UserList.this);
		}
	};
	public LoadingCache<UUID, UserListPanel> panelCache = CacheBuilder.newBuilder().expireAfterAccess(30,
			TimeUnit.SECONDS).build(cacheLoader);
	public Set<UUID> members = new HashSet<>();
	public Set<UUID> denied = new HashSet<>();
	public JPanel panel = new JPanel();
	QTextField searchField = new QTextField();
	JLabel memberLabel = new JLabel("Members");
	JScrollPane memberScrollPane = new JScrollPane();
	JLabel deniedLabel = new JLabel("Denied");
	JScrollPane deniedScrollPane = new JScrollPane();
	JLabel allLabel = new JLabel("All");
	JScrollPane allScrollPane = new JScrollPane();
	
	JPanel memberPanel = new JPanel();
	JPanel deniedPanel = new JPanel();
	JPanel allPanel = new JPanel();
	
	public UserList(Map<UUID, User> users) {
		this.users = users;
		panel.setMinimumSize(new Dimension(200, 200));
		panel.setPreferredSize(new Dimension(220, 400));
		GridBagLayout gbl = new GridBagLayout();
		gbl.rowWeights = new double[]{1, 1, 100, 1, 100, 1, 100};
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
		gbc.gridy = 3;
		panel.add(deniedLabel, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 4;
		panel.add(deniedScrollPane, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 5;
		panel.add(allLabel, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 6;
		panel.add(allScrollPane, gbc);
		
		memberScrollPane.setViewportView(memberPanel);
		allScrollPane.setViewportView(allPanel);
		deniedScrollPane.setViewportView(deniedPanel);
		memberPanel.setLayout(new BoxLayout(memberPanel, BoxLayout.Y_AXIS));
		allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));
		deniedPanel.setLayout(new BoxLayout(deniedPanel, BoxLayout.Y_AXIS));
		
		searchField.addKeyListener(this);
	}
	
	public void updateList() {
		deniedPanel.removeAll();
		allPanel.removeAll();
		memberPanel.removeAll();
		for (UUID uuid : members) {
			try {
				User user = users.get(uuid);
				if (user.getUsername().toLowerCase().contains(searchField.getText().strip().toLowerCase()))
					memberPanel.add(panelCache.get(uuid).memberPanel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (UUID uuid : denied) {
			try {
				User user = users.get(uuid);
				if (user.getUsername().toLowerCase().contains(searchField.getText().strip().toLowerCase()))
					deniedPanel.add(panelCache.get(uuid).deniedPanel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (searchField.getText().strip().length() > 1) {
			for (User user : users.values()) {
				if (user.getUsername().toLowerCase().contains(searchField.getText().strip().toLowerCase())) {
					try {
						allPanel.add(panelCache.get(user.getUuid()).panel);
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
		for (UserListPanel p : panelCache.asMap().values()) {
			boolean hovering = false;
			try {
				hovering = new Rectangle(p.panel.getLocationOnScreen(), p.panel.getSize()).contains(pos);
			} catch (Exception ignored) {
			}
			try {
				hovering |= new Rectangle(p.memberPanel.getLocationOnScreen(), p.memberPanel.getSize()).contains(pos);
			} catch (Exception ignored) {
			}
			try {
				hovering |= new Rectangle(p.deniedPanel.getLocationOnScreen(), p.deniedPanel.getSize()).contains(pos);
			} catch (Exception ignored) {
			}
			p.update(hovering);
		}
		panel.setVisible(true);
		memberPanel.setVisible(true);
		allPanel.setVisible(true);
		deniedPanel.setVisible(true);
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
