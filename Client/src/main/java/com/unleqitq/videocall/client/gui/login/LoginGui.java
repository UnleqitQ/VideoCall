package com.unleqitq.videocall.client.gui.login;

import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.transferclasses.base.AuthenticationResult;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginService;

import java.util.concurrent.SynchronousQueue;

public class LoginGui {
	
	JXLoginPane.JXLoginFrame loginFrame;
	JXLoginPane loginPane;
	public SynchronousQueue<AuthenticationResult> resultQueue = new SynchronousQueue<>();
	
	public LoginGui() {
		loginPane = new JXLoginPane();
		loginFrame = new JXLoginPane.JXLoginFrame(loginPane);
		loginPane.setSaveMode(JXLoginPane.SaveMode.USER_NAME);
		loginPane.setLoginService(new LoginService() {
			
			@Override
			public boolean authenticate(String un, char[] pw, String server) {
				Client.getInstance().setUsername(un);
				Client.getInstance().setPassword(String.valueOf(pw));
				Client.getInstance().sendAuthentication();
				AuthenticationResult result = resultQueue.poll();
				switch (result.result()) {
					case -2 -> loginPane.setErrorMessage("User doesn't exist");
					case -1 -> loginPane.setErrorMessage("Some weird error");
					case 0 -> loginPane.setErrorMessage("Password is wrong");
					case 1 -> loginPane.setErrorMessage("");
				}
				return result.result() > 0;
			}
		});
	}
	
	public void show() {
		loginFrame.setVisible(true);
	}
	
}
