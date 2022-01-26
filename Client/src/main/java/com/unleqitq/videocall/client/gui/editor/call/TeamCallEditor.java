package com.unleqitq.videocall.client.gui.editor.call;

import com.github.lgooddatepicker.components.DatePicker;
import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.client.gui.editor.call.teamlist.TeamList;
import com.unleqitq.videocall.client.gui.editor.call.userlist.UserList;
import com.unleqitq.videocall.sharedclasses.call.TeamCallDefinition;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.swingutils.QTextField;
import com.unleqitq.videocall.transferclasses.base.data.CallData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamCallEditor {
	
	public JFrame frame = new JFrame("BasicCallEditor");
	public JTextField nameField = new QTextField();
	public JSpinner hourSpinner = new JSpinner(
			new SpinnerNumberModel((Instant.now().atZone(ZoneId.systemDefault()).getHour() + 1) % 24, 0, 23, 1));
	public JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	public DatePicker datePicker = new DatePicker();
	public JEditorPane editorPane = new JEditorPane();
	
	public Map<UUID, User> users;
	public UserList userList;
	public Map<UUID, Team> teams;
	public TeamList teamList;
	
	public JButton saveButton = new JButton("Save");
	public Thread updateThread;
	public JPanel topPanel = new JPanel();
	public JPanel listPanel = new JPanel();
	
	public TeamCallEditor(HashMap<UUID, User> users, HashMap<UUID, Team> teams) {
		this.teams = teams;
		this.users = users;
		userList = new UserList(users);
		teamList = new TeamList(teams);
		datePicker.setDate(LocalDate.now());
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		topPanel.add(nameField);
		topPanel.add(hourSpinner);
		topPanel.add(new JLabel(":"));
		topPanel.add(minuteSpinner);
		topPanel.add(datePicker);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(50, 50, 500, 500);
		frame.setLayout(new BorderLayout());
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(editorPane, BorderLayout.CENTER);
		frame.add(saveButton, BorderLayout.SOUTH);
		frame.add(listPanel, BorderLayout.EAST);
		listPanel.setLayout(new GridLayout(1, 2));
		listPanel.add(userList.panel);
		listPanel.add(teamList.panel);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				updateThread.interrupt();
			}
		});
		saveButton.addActionListener(e -> {
			if (nameField.getText().strip().length() < 2) {
				new Thread(() -> Client.errorDialog("Please input a name with at least Two Characters", "Wrong input",
						JOptionPane.DEFAULT_OPTION)).start();
				return;
			}
			long time = Instant.from(
					datePicker.getDate().atTime((int) hourSpinner.getValue(), (int) minuteSpinner.getValue(),
							0)).getEpochSecond() * 1000;
			TeamCallDefinition callDefinition = new TeamCallDefinition(Client.getInstance().managerHandler, null,
					Client.getInstance().userUuid, time, nameField.getText());
			for (UUID uuid : userList.members) {
				callDefinition.addMember(uuid);
			}
			for (UUID uuid : userList.denied) {
				callDefinition.denyMember(uuid);
			}
			for (UUID uuid : teamList.members) {
				callDefinition.addTeam(uuid);
			}
			CallData callData = new CallData(callDefinition);
			Client.getInstance().connection.send(callData);
			frame.dispose();
			updateThread.interrupt();
		});
		
		updateThread = new Thread(this::updateLoop);
		updateThread.start();
	}
	
	public void updateLoop() {
		while (true) {
			userList.update();
			teamList.update();
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
