package com.unleqitq.videocall.client.gui.editor;

import com.github.lgooddatepicker.components.DatePicker;
import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.client.gui.editor.userlist.UserList;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.swingutils.QTextField;
import com.unleqitq.videocall.transferclasses.base.data.TeamData;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class BasicCallEditor {
	
	public JFrame frame = new JFrame("BasicCallEditor");
	public JTextField nameField = new QTextField();
	public JSpinner timeSpinner;
	public DatePicker datePicker = new DatePicker();
	public JEditorPane editorPane = new JEditorPane();
	
	public Map<UUID, User> users;
	public UserList userList;
	
	public JButton saveButton = new JButton("Save");
	public Thread updateThread;
	
	public BasicCallEditor(Map<UUID, User> users) {
		this.users = users;
		userList = new UserList(users);
		{
			SpinnerDateModel model = new SpinnerDateModel();
			model.setCalendarField(Calendar.MINUTE);
			timeSpinner = new JSpinner(model);
			timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "hh:mm:ss"));
		}
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(50, 50, 500, 500);
		frame.setLayout(new GridLayout(4, 1));
		frame.add(nameField);
		frame.add(timeSpinner);
		frame.add(datePicker);
		frame.add(editorPane);
		frame.add(saveButton);
		frame.setVisible(true);
		saveButton.addActionListener(e -> {
			TeamData teamData = new TeamData(
					new Team(Client.getInstance().managerHandler, null, Client.getInstance().userUuid,
							nameField.getText()));
			Client.getInstance().connection.send(teamData);
			frame.dispose();
		});
		
		updateThread = new Thread(this::updateLoop);
		updateThread.start();
	}
	
	public void updateLoop() {
		while (frame.isVisible()) {
			userList.update();
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {
				return;
			}
		}
	}
	
}
