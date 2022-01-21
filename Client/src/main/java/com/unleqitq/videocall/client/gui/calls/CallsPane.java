package com.unleqitq.videocall.client.gui.calls;

import javax.swing.*;

public class CallsPane {
	
	public JSplitPane pane = new JSplitPane();
	public CallsList callsList = new CallsList();
	
	public CallsPane() {
	}
	
	public void init() {
		pane.setDividerLocation(0.3);
		pane.setLeftComponent(callsList.scrollPane);
	}
	
	public void update() {
		callsList.updatePanels();
	}
	
}
