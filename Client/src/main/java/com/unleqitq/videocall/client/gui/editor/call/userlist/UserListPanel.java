package com.unleqitq.videocall.client.gui.editor.call.userlist;

import com.github.weisj.darklaf.listener.MouseClickListener;
import com.unleqitq.videocall.sharedclasses.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class UserListPanel implements MouseClickListener {
	
	UserList userList;
	public JPanel panel = new JPanel();
	public JLabel nameLabel = new JLabel();
	public JPanel deniedPanel = new JPanel();
	public JLabel deniedNameLabel = new JLabel();
	public JPanel memberPanel = new JPanel();
	public JLabel memberNameLabel = new JLabel();
	UUID userUuid;
	
	
	public UserListPanel(UUID uuid, UserList userList) {
		this.userList = userList;
		userUuid = uuid;
		
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
		
		deniedPanel.setLayout(new GridLayout(1, 1));
		deniedPanel.add(deniedNameLabel);
		deniedPanel.setMaximumSize(new Dimension(500, 50));
		deniedPanel.setMinimumSize(new Dimension(100, 50));
		deniedPanel.setPreferredSize(new Dimension(200, 50));
		
		deniedPanel.addMouseListener(this);
		deniedNameLabel.addMouseListener(this);
		
		memberPanel.setLayout(new GridLayout(1, 1));
		memberPanel.add(memberNameLabel);
		memberPanel.setMaximumSize(new Dimension(500, 50));
		memberPanel.setMinimumSize(new Dimension(100, 50));
		memberPanel.setPreferredSize(new Dimension(200, 50));
		
		memberPanel.addMouseListener(this);
		memberNameLabel.addMouseListener(this);
	}
	
	public void update(boolean hovered) {
		User user = userList.users.get(userUuid);
		if (user == null) {
			nameLabel.setText("UUID: " + userUuid);
			memberNameLabel.setText("UUID: " + userUuid);
			deniedNameLabel.setText("UUID: " + userUuid);
			return;
		}
		nameLabel.setText(user.getUsername());
		deniedNameLabel.setText(user.getUsername());
		memberNameLabel.setText(user.getUsername());
		Color c = UIManager.getColor("Panel.background");
		float[] comp = c.getColorComponents(new float[3]);
		comp[0] += 1 / 3f;
		Color c1 = Color.getHSBColor(comp[0], comp[1], comp[2]);
		comp = c.getColorComponents(new float[3]);
		comp[0] += 2 / 3f;
		Color c2 = Color.getHSBColor(comp[0], comp[1], comp[2]);
		
		if (userList.denied.contains(userUuid)) {
			if (hovered) {
				panel.setBackground(c1.darker());
				deniedPanel.setBackground(c1.darker());
				memberPanel.setBackground(c1.darker());
			}
			else {
				panel.setBackground(c1);
				deniedPanel.setBackground(c1);
				memberPanel.setBackground(c1);
			}
		}
		else if (userList.members.contains(userUuid)) {
			if (hovered) {
				panel.setBackground(c2.darker());
				deniedPanel.setBackground(c2.darker());
				memberPanel.setBackground(c2.darker());
			}
			else {
				panel.setBackground(c2);
				deniedPanel.setBackground(c2);
				memberPanel.setBackground(c2);
			}
		}
		else {
			if (hovered) {
				panel.setBackground(c.darker());
				deniedPanel.setBackground(c.darker());
				memberPanel.setBackground(c.darker());
			}
			else {
				panel.setBackground(c);
				deniedPanel.setBackground(c);
				memberPanel.setBackground(c);
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isControlDown()) {
			if (e.isShiftDown()) {
				userList.members.remove(userUuid);
				userList.denied.add(userUuid);
				userList.updateList();
			}
			else {
				userList.denied.remove(userUuid);
				userList.members.add(userUuid);
				userList.updateList();
			}
		}
		else {
			userList.denied.remove(userUuid);
			userList.members.remove(userUuid);
			userList.updateList();
		}
	}
	
}
