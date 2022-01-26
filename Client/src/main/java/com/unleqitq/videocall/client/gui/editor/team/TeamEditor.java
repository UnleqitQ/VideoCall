package com.unleqitq.videocall.client.gui.editor.team;

import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.client.gui.editor.team.userlist.UserList;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.swingutils.QTextField;
import com.unleqitq.videocall.transferclasses.base.data.TeamData;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.UUID;

public class TeamEditor {
	
	public UUID uuid = null;
	public UUID creatorUuid = Client.getInstance().userUuid;
	
	public JFrame frame = new JFrame("TeamEditor");
	public JTextField nameField = new QTextField();
	public JEditorPane editorPane = new JEditorPane();
	public JButton saveButton = new JButton("Save");
	
	public Map<UUID, User> users;
	public UserList userList;
	public Thread updateThread;
	
	public TeamEditor(Map<UUID, User> users) {
		this.users = users;
		userList = new UserList(users);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(50, 50, 500, 500);
		frame.setLayout(new BorderLayout());
		frame.add(nameField, BorderLayout.NORTH);
		frame.add(editorPane, BorderLayout.CENTER);
		frame.add(saveButton, BorderLayout.SOUTH);
		frame.add(userList.panel, BorderLayout.EAST);
		frame.setVisible(true);
		saveButton.addActionListener(e -> {
			if (nameField.getText().strip().length() < 2) {
				new Thread(() -> Client.errorDialog("Please input a name with at least Two Characters", "Wrong input",
						JOptionPane.DEFAULT_OPTION)).start();
				return;
			}
			TeamData teamData = new TeamData(
					new Team(Client.getInstance().managerHandler, uuid, creatorUuid,
							nameField.getText()));
			Client.getInstance().connection.send(teamData);
			frame.dispose();
			updateThread.interrupt();
		});
		
		updateThread = new Thread(this::updateLoop);
		updateThread.start();
	}
	
	public void setEdit(@NotNull Team team) {
		uuid = team.getUuid();
		creatorUuid = team.getCreator();
		userList.members.addAll(team.getMembers());
		nameField.setText(team.getName());
	}
	
	public void updateLoop() {
		while (true) {
			userList.updateList();
			if (frame.isActive())
				frame.setVisible(frame.isVisible());
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {
				return;
			}
		}
	}
	
}
