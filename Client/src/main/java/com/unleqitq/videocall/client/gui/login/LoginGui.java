package com.unleqitq.videocall.client.gui.login;

import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.swingutils.QPasswordField;
import com.unleqitq.videocall.swingutils.QTextField;
import com.unleqitq.videocall.transferclasses.base.AuthenticationResult;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;

public class LoginGui {
	
	public SynchronousQueue<AuthenticationResult> resultQueue = new SynchronousQueue<>();
	JFrame frame;
	QTextField usernameField;
	QPasswordField passwordField;
	JLabel errorLabel;
	JButton okButton;
	JButton cancelButton;
	GridBagLayout gbl;
	
	public LoginGui() {
		init();
	}
	
	public void init() {
		frame = new JFrame("Login");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(260, 140));
		usernameField = new QTextField();
		passwordField = new QPasswordField();
		errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);
		errorLabel.setHorizontalAlignment(JLabel.EAST);
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		okButton.setPreferredSize(new Dimension(50, 26));
		cancelButton.setPreferredSize(new Dimension(50, 26));
		usernameField.setPreferredSize(new Dimension(100, 26));
		passwordField.setPreferredSize(new Dimension(100, 26));
		
		gbl = new GridBagLayout();
		gbl.columnWeights = new double[3];
		Arrays.fill(gbl.columnWeights, 1);
		gbl.columnWeights[0] = 3;
		gbl.rowWeights = new double[4];
		Arrays.fill(gbl.rowWeights, 1);
		gbl.rowWeights[3] = 5;
		frame.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		gbc.gridy = 0;
		frame.add(usernameField, gbc);
		gbc.gridy = 1;
		frame.add(passwordField, gbc);
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		frame.add(okButton, gbc);
		gbc.gridx = 2;
		frame.add(cancelButton, gbc);
		
		usernameField.setPlaceholder("Username");
		passwordField.setPlaceholder("Password");
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
		frame.setBounds(screen.width / 2 - 200, screen.height / 2 - 150, 400, 300);
		
		cancelButton.addActionListener(e -> System.exit(0));
		okButton.addActionListener(e -> login());
		usernameField.addActionListener(e -> login());
		
	}
	
	public void login() {
		Client.getInstance().setUsername(usernameField.getText());
		Client.getInstance().setPassword(String.valueOf(passwordField.getPassword()));
		
		Client.getInstance().sendAuthentication();
		
		AuthenticationResult result = resultQueue.poll();
		
		if (result.result() > 0)
			frame.dispose();
		else
			switch (result.result()) {
				case -2 -> errorLabel.setText("User doesn't exist");
				case -1 -> errorLabel.setText("Some weird error");
				case 0 -> errorLabel.setText("Password is wrong");
			}
	}
	
	public void show() {
		frame.setVisible(true);
	}
	
}
