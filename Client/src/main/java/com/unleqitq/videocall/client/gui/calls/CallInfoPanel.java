package com.unleqitq.videocall.client.gui.calls;

import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.client.gui.editor.call.BasicCallEditor;
import com.unleqitq.videocall.client.gui.editor.call.TeamCallEditor;
import com.unleqitq.videocall.sharedclasses.call.BasicCallDefinition;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;
import com.unleqitq.videocall.sharedclasses.call.TeamCallDefinition;
import com.unleqitq.videocall.sharedclasses.user.User;
import com.unleqitq.videocall.transferclasses.base.CallRequest;
import com.unleqitq.videocall.transferclasses.base.RequestAllUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class CallInfoPanel {
	
	CallsPane parent;
	JPanel panel = new JPanel();
	JPanel internalPanel = new JPanel();
	JLabel nameLabel = new JLabel();
	JLabel timeLabel = new JLabel();
	JLabel timeDifLabel = new JLabel();
	JLabel creatorLabel = new JLabel();
	JLabel createdLabel = new JLabel();
	JPanel midPanel = new JPanel();
	JPanel topPanel = new JPanel();
	
	JButton joinButton = new JButton("Join");
	JButton editButton = new JButton("Edit");
	
	JTextArea infoArea = new JTextArea();
	JList<String> membersList = new JList<>();
	
	public CallInfoPanel(CallsPane parent) {
		this.parent = parent;
		
		GridBagLayout gbl = new GridBagLayout();
		gbl.rowWeights = new double[3];
		gbl.columnWeights = new double[3];
		gbl.rowWeights[0] = 1;
		gbl.rowWeights[1] = 1;
		gbl.rowWeights[2] = 1;
		gbl.columnWeights[0] = 1;
		gbl.columnWeights[1] = 1;
		gbl.columnWeights[2] = 1;
		midPanel.setLayout(gbl);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 3;
		midPanel.add(nameLabel, gbc);
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		midPanel.add(creatorLabel, gbc);
		gbc.gridy = 2;
		midPanel.add(createdLabel, gbc);
		gbc.gridx = 2;
		gbc.gridy = 1;
		midPanel.add(timeDifLabel, gbc);
		gbc.gridy = 2;
		midPanel.add(timeLabel, gbc);
		internalPanel.setLayout(new BorderLayout());
		internalPanel.add(midPanel, BorderLayout.CENTER);
		internalPanel.add(membersList, BorderLayout.EAST);
		internalPanel.add(topPanel, BorderLayout.NORTH);
		internalPanel.add(infoArea, BorderLayout.SOUTH);
		
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		topPanel.add(editButton);
		topPanel.add(joinButton);
		
		membersList.setMinimumSize(new Dimension(200, 200));
		membersList.setPreferredSize(new Dimension(200, 900));
		membersList.setMaximumSize(new Dimension(200, 2000));
		
		midPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		nameLabel.setFont(new Font("", Font.BOLD, 30));
		createdLabel.setFont(new Font("", Font.PLAIN, 15));
		creatorLabel.setFont(new Font("", Font.PLAIN, 15));
		timeLabel.setFont(new Font("", Font.PLAIN, 15));
		timeDifLabel.setFont(new Font("", Font.PLAIN, 15));
		infoArea.setEditable(false);
		
		infoArea.setMinimumSize(new Dimension(200, 200));
		infoArea.setPreferredSize(new Dimension(1000, 400));
		infoArea.setMaximumSize(new Dimension(2000, 700));
		
		gbl.rowWeights = new double[]{1};
		gbl.columnWeights = new double[]{1};
		panel.setLayout(gbl);
		
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(internalPanel, gbc);
		
		editButton.addActionListener(e -> {
			UUID sel = parent.callsList.selected;
			if (sel == null) {
				return;
			}
			editButton.setEnabled(false);
			Client.getInstance().connection.send(new RequestAllUser());
			try {
				Thread.sleep(1000 * 4);
			} catch (InterruptedException ignored) {
			}
			CallDefinition call = Client.getInstance().getCall(sel);
			if (call instanceof TeamCallDefinition tc) {
				TeamCallEditor callEditor = new TeamCallEditor(new HashMap<>(Client.getInstance().userCache.asMap()),
						new HashMap<>(Client.getInstance().teamCache.asMap()));
				callEditor.setEdit(tc);
			}
			if (call instanceof BasicCallDefinition bc) {
				BasicCallEditor callEditor = new BasicCallEditor(new HashMap<>(Client.getInstance().userCache.asMap()));
				callEditor.setEdit(bc);
			}
			editButton.setEnabled(true);
		});
		joinButton.addActionListener(e -> {
			UUID sel = parent.callsList.selected;
			if (sel == null) {
				return;
			}
			Client.getInstance().connection.send(new CallRequest(sel));
		});
	}
	
	public void update() {
		UUID sel = parent.callsList.selected;
		if (sel == null) {
			internalPanel.setVisible(false);
			return;
		}
		internalPanel.setVisible(true);
		CallDefinition call = Client.getInstance().getCall(sel);
		if (call == null)
			return;
		nameLabel.setText(call.getName());
		createdLabel.setText(
				"Created: " + DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(call.getCreatedLDT()));
		timeLabel.setText(
				"Planned for: " + DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(call.getTimeLDT()));
		editButton.setEnabled(call.getCreator() == Client.getInstance().userUuid);
		infoArea.setText(call.getDescription());
		{
			long difference = call.getTime() - System.currentTimeMillis();
			long d0 = difference;
			String s = "";
			if (difference < 0) {
				d0 *= -1;
				s += "Started ";
			}
			else {
				s += "Starts in ";
			}
			if (d0 > 1000) {
				d0 /= 1000;
				int sec = (int) (d0 % 60);
				d0 /= 60;
				int min = (int) (d0 % 60);
				d0 /= 60;
				int hour = (int) (d0 % 24);
				d0 /= 24;
				int day = (int) d0;
				if (day > 0) {
					s += day + " ";
					if (day > 1)
						s += "days ";
					else
						s += "day ";
				}
				if (hour > 0) {
					s += hour + " ";
					if (hour > 1)
						s += "hours ";
					else
						s += "hour ";
				}
				if (min > 0) {
					s += min + " ";
					if (min > 1)
						s += "minutes ";
					else
						s += "minute ";
				}
				if (sec > 0) {
					s += sec + " ";
					if (sec > 1)
						s += "seconds ";
					else
						s += "second ";
				}
				if (difference < 0)
					s += "ago";
				timeDifLabel.setText(s);
			}
			else
				timeDifLabel.setText("Starting now");
		}
		{
			User user = Client.getInstance().getUser(call.getCreator());
			if (user != null)
				creatorLabel.setText("Owner: " + user.getUsername());
			else
				creatorLabel.setText("Owner (No UserInfo): " + call.getCreator());
		}
		{
			String[] array = new String[call.getMembers().size()];
			int i = 0;
			for (UUID uuid : call.getMembers()) {
				User user = Client.getInstance().getUser(uuid);
				if (user != null)
					array[i++] = user.getUsername();
				else
					array[i++] = uuid.toString();
			}
			Arrays.sort(array, String::compareTo);
			membersList.setListData(array);
		}
	}
	
}
