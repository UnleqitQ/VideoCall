package com.unleqitq.videocall.client.gui.calls;

import javax.swing.*;

public class CallsPane {
	
	public JSplitPane pane = new JSplitPane();
	public CallsList callsList = new CallsList();
	public CallInfoPanel callInfoPanel = new CallInfoPanel(this);
	
	public CallsPane() {
	}
	
	public void init() {
		pane.setLeftComponent(callsList.root);
		pane.setRightComponent(callInfoPanel.panel);
		pane.setDividerLocation(220);
	}
	
	public void update() {
		try {
			callsList.updatePanels();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			callInfoPanel.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
