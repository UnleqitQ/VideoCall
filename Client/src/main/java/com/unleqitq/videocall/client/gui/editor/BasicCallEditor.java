package com.unleqitq.videocall.client.gui.editor;

import com.github.lgooddatepicker.components.DatePicker;
import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.client.gui.editor.userlist.UserList;
import com.unleqitq.videocall.sharedclasses.call.BasicCallDefinition;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.swingutils.QTextField;
import com.unleqitq.videocall.transferclasses.base.data.CallData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Instant;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
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
	public JPanel topPanel = new JPanel();
	SpinnerDateModel model = new SpinnerDateModel();
	
	public BasicCallEditor(Map<UUID, User> users) {
		this.users = users;
		userList = new UserList(users);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		{
			model.setCalendarField(Calendar.MINUTE);
			timeSpinner = new JSpinner(model);
			timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "hh:mm:ss"));
		}
		topPanel.add(nameField);
		topPanel.add(timeSpinner);
		topPanel.add(datePicker);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(50, 50, 500, 500);
		frame.setLayout(new BorderLayout());
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(editorPane, BorderLayout.CENTER);
		frame.add(saveButton, BorderLayout.SOUTH);
		frame.add(userList.panel, BorderLayout.EAST);
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
			Date date = model.getDate();
			CallData callData = new CallData(
					new BasicCallDefinition(Client.getInstance().managerHandler, null, Client.getInstance().userUuid,
							datePicker.getDate().atTime(date.toInstant().get(ChronoField.CLOCK_HOUR_OF_DAY),
									date.toInstant().get(ChronoField.MINUTE_OF_HOUR),
									date.toInstant().get(ChronoField.SECOND_OF_MINUTE)).compareTo(
									ChronoLocalDateTime.from(Instant.ofEpochMilli(0))), nameField.getText()));
			Client.getInstance().connection.send(callData);
			frame.dispose();
			updateThread.interrupt();
		});
		
		updateThread = new Thread(this::updateLoop);
		updateThread.start();
	}
	
	public void updateLoop() {
		while (true) {
			userList.updateList();
			frame.setVisible(frame.isVisible());
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {
				return;
			}
		}
	}
	
}
