package com.unleqitq.videocall.client.gui.editor.userlist;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.swingutils.QTextField;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserList {
	
	public Map<UUID, User> users;
	CacheLoader cacheLoader = new CacheLoader<UUID, UserListPanel>() {
		
		@Override
		public UserListPanel load(UUID key) {
			return new UserListPanel(key, UserList.this);
		}
	};
	public LoadingCache<UUID, UserListPanel> panelCache = CacheBuilder.newBuilder().expireAfterAccess(30,
			TimeUnit.SECONDS).build(cacheLoader);
	public Set<UUID> members;
	public Set<UUID> denied;
	JPanel panel = new JPanel();
	QTextField searchField = new QTextField();
	JScrollPane memberScrollPane = new JScrollPane();
	JScrollPane deniedScrollPane = new JScrollPane();
	JScrollPane allScrollPane = new JScrollPane();
	
	JPanel memberPanel = new JPanel();
	JPanel deniedPanel = new JPanel();
	JPanel allPanel = new JPanel();
	
	public UserList(Map<UUID, User> users) {
		this.users = users;
		GridBagLayout gbl = new GridBagLayout();
		gbl.rowWeights = new double[]{0.5, 1, 1, 1};
		gbl.columnWeights = new double[]{1};
		panel.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		panel.add(searchField, gbc);
		gbc.gridy = 1;
		panel.add(memberScrollPane, gbc);
		gbc.gridy = 2;
		panel.add(deniedScrollPane, gbc);
		gbc.gridy = 3;
		panel.add(allScrollPane, gbc);
		memberScrollPane.setViewportView(memberPanel);
		allScrollPane.setViewportView(allPanel);
		deniedScrollPane.setViewportView(deniedPanel);
		memberPanel.setLayout(new BoxLayout(memberPanel, BoxLayout.Y_AXIS));
		allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));
		deniedPanel.setLayout(new BoxLayout(deniedPanel, BoxLayout.Y_AXIS));
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
		if (searchField.getText().strip().length() > 2) {
			for (User user : users.values()) {
				if (user.getUsername().toLowerCase().contains(searchField.getText().strip().toLowerCase())) {
					try {
						memberPanel.add(panelCache.get(user.getUuid()).panel);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void update() {
		Point pos = MouseInfo.getPointerInfo().getLocation();
		for (UserListPanel p : panelCache.asMap().values()) {
			boolean hovering = false;
			try {
				hovering = new Rectangle(p.panel.getLocationOnScreen(), p.panel.getSize()).contains(pos);
				hovering |= new Rectangle(p.memberPanel.getLocationOnScreen(), p.memberPanel.getSize()).contains(pos);
				hovering |= new Rectangle(p.deniedPanel.getLocationOnScreen(), p.deniedPanel.getSize()).contains(pos);
			} catch (Exception ignored) {
			}
			p.update(hovering);
		}
	}
	
}
