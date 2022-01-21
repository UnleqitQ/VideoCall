package com.unleqitq.videocall.client.gui.calls;

import javax.swing.*;
import java.util.UUID;

public class CallPanel {
	
	public JPanel panel = new JPanel();
	public JLabel uuidLabel = new JLabel();
	UUID callUuid;
	
	public CallPanel(UUID uuid) {
		callUuid = uuid;
	}
	
	public void update(boolean selected, boolean hovered) {
	
	}
	
}
