package com.unleqitq.videocall.client.gui.editor.userlist;

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
	UUID userUuid;
	
	
	public UserListPanel(UUID uuid, UserList userList) {
		this.userList = userList;
		userUuid = uuid;
		
		init();
	}
	
	public void init() {
		panel.setLayout(new GridLayout(2, 1));
		panel.add(nameLabel);
		panel.setMaximumSize(new Dimension(500, 50));
		panel.setMinimumSize(new Dimension(100, 50));
		panel.setPreferredSize(new Dimension(200, 50));
		
		panel.addMouseListener(this);
		nameLabel.addMouseListener(this);
	}
	
	public void update(boolean hovered) {
		User user = userList.users.get(userUuid);
		if (user == null) {
			nameLabel.setText("UUID: " + userUuid);
			return;
		}
		nameLabel.setText(user.getUsername());
		Color c = UIManager.getColor("Panel.background");
		float[] comp = c.getColorComponents(new float[3]);
		comp[0] += 1 / 3f;
		Color c1 = Color.getHSBColor(comp[0], comp[1], comp[2]);
		comp = c.getColorComponents(new float[3]);
		comp[0] += 2 / 3f;
		Color c2 = Color.getHSBColor(comp[0], comp[1], comp[2]);
		
		if (userList.denied.contains(userUuid)) {
			if (hovered)
				panel.setBackground(c1.darker());
			else
				panel.setBackground(c1);
		}
		else if (userList.members.contains(userUuid)) {
			if (hovered)
				panel.setBackground(c2.darker());
			else
				panel.setBackground(c2);
		}
		else {
			if (hovered)
				panel.setBackground(c.darker());
			else
				panel.setBackground(c);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isControlDown()) {
			if (e.isShiftDown()) {
				userList.members.remove(userUuid);
				userList.denied.add(userUuid);
			}
			else {
				userList.denied.remove(userUuid);
				userList.members.add(userUuid);
			}
		}
		else {
			userList.denied.remove(userUuid);
			userList.members.remove(userUuid);
		}
	}
	
	
}
