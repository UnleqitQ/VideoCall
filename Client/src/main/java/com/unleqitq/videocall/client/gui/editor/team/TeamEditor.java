package com.unleqitq.videocall.client.gui.editor.team;

import com.github.lgooddatepicker.components.DatePicker;
import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.swingutils.QTextField;
import com.unleqitq.videocall.transferclasses.base.data.TeamData;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class TeamEditor {
	
	public JFrame frame = new JFrame("TeamEditor");
	public JTextField nameField = new QTextField();
	public JSpinner timeSpinner;
	public DatePicker datePicker = new DatePicker();
	public JEditorPane editorPane = new JEditorPane();
	public JButton saveButton = new JButton("Save");
	
	public TeamEditor() {
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
	}
	
}
