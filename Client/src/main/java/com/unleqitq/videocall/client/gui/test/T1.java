package com.unleqitq.videocall.client.gui.test;

import org.jdesktop.swingx.JXLoginPane;

public class T1 {
	
	JXLoginPane.JXLoginFrame frame;
	JXLoginPane pane;
	JXLoginPane.JXLoginDialog dialog;
	
	public T1() {
		pane = new JXLoginPane();
		frame = new JXLoginPane.JXLoginFrame(pane);
		pane.setVisible(true);
		dialog = new JXLoginPane.JXLoginDialog(frame, pane);
	}
	
	public static void main(String[] args) {
		new T1();
		
	}
	
}
