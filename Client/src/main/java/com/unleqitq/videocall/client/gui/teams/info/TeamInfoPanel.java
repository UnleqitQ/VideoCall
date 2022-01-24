package com.unleqitq.videocall.client.gui.teams.info;

import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.client.gui.teams.TeamsPane;
import com.unleqitq.videocall.sharedclasses.team.Team;
import com.unleqitq.videocall.sharedclasses.user.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.UUID;

public class TeamInfoPanel {
	
	TeamsPane parent;
	public JPanel panel = new JPanel();
	JPanel internalPanel = new JPanel();
	JLabel nameLabel = new JLabel();
	JLabel creatorLabel = new JLabel();
	JLabel createdLabel = new JLabel();
	JPanel midPanel = new JPanel();
	
	JTextArea infoArea = new JTextArea();
	JList<String> membersList = new JList<>();
	
	public TeamInfoPanel(TeamsPane parent) {
		this.parent = parent;
		
		GridBagLayout gbl = new GridBagLayout();
		gbl.rowWeights = new double[2];
		gbl.columnWeights = new double[3];
		gbl.rowWeights[0] = 1;
		gbl.rowWeights[1] = 1;
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
		gbc.gridx = 2;
		midPanel.add(createdLabel, gbc);
		internalPanel.setLayout(new BorderLayout());
		internalPanel.add(midPanel, BorderLayout.CENTER);
		internalPanel.add(membersList, BorderLayout.EAST);
		internalPanel.add(infoArea, BorderLayout.SOUTH);
		
		
		membersList.setMinimumSize(new Dimension(200, 200));
		membersList.setPreferredSize(new Dimension(200, 900));
		membersList.setMaximumSize(new Dimension(200, 2000));
		
		midPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		nameLabel.setFont(new Font("", Font.BOLD, 30));
		createdLabel.setFont(new Font("", Font.PLAIN, 15));
		creatorLabel.setFont(new Font("", Font.PLAIN, 15));
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
	}
	
	public void update() {
		UUID sel = parent.teamsList.selected;
		if (sel == null) {
			internalPanel.setVisible(false);
			return;
		}
		internalPanel.setVisible(true);
		Team team = Client.getInstance().getTeam(sel);
		if (team == null)
			return;
		nameLabel.setText(team.getName());
		/*createdLabel.setText(
				"Created: " + DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(call.getCreatedLDT()));*/
		{
			User user = Client.getInstance().getUser(team.getCreator());
			if (user != null)
				creatorLabel.setText("Owner: " + user.getUsername());
			else
				creatorLabel.setText("Owner (No UserInfo): " + team.getCreator());
		}
		{
			String[] array = new String[team.getMembers().size()];
			int i = 0;
			for (UUID uuid : team.getMembers()) {
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
