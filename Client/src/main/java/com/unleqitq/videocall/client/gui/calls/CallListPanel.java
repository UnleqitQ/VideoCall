package com.unleqitq.videocall.client.gui.calls;

import com.github.weisj.darklaf.listener.MouseClickListener;
import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.sharedclasses.call.CallDefinition;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class CallListPanel implements MouseClickListener, KeyListener {
	
	CallsList callsList;
	public JPanel panel = new JPanel();
	public JLabel nameLabel = new JLabel();
	public JLabel timeLabel = new JLabel();
	public JLabel activeLabel = new JLabel();
	UUID callUuid;
	
	private static final CompoundBorder lowered = new CompoundBorder(
			new BevelBorder(BevelBorder.LOWERED, Color.lightGray, Color.darkGray), new EmptyBorder(5, 5, 5, 5));
	private static final CompoundBorder raised = new CompoundBorder(
			new BevelBorder(BevelBorder.RAISED, Color.lightGray, Color.darkGray), new EmptyBorder(5, 5, 5, 5));
	
	
	public CallListPanel(UUID uuid, CallsList callsList) {
		this.callsList = callsList;
		callUuid = uuid;
		
		init();
	}
	
	public void init() {
		panel.setLayout(new GridLayout(3, 1));
		panel.add(nameLabel);
		panel.add(timeLabel);
		panel.add(activeLabel);
		panel.setBorder(raised);
		panel.setMaximumSize(new Dimension(2000, 100));
		panel.setMinimumSize(new Dimension(100, 100));
		panel.setPreferredSize(new Dimension(200, 100));
		
		panel.addMouseListener(this);
		nameLabel.addMouseListener(this);
		timeLabel.addMouseListener(this);
		activeLabel.addMouseListener(this);
		
		panel.addKeyListener(this);
		nameLabel.addKeyListener(this);
		timeLabel.addKeyListener(this);
		activeLabel.addKeyListener(this);
	}
	
	public void update(boolean selected, boolean hovered) {
		CallDefinition call = Client.getInstance().getCall(callUuid);
		if (call == null) {
			nameLabel.setText("UUID: " + callUuid);
			timeLabel.setText("No Info");
			activeLabel.setText("No Info");
			return;
		}
		nameLabel.setText(call.getName());
		{
			long difference = call.getTime() - System.currentTimeMillis();
			long d0 = difference;
			String s = "";
			if (difference < 0) {
				d0 *= -1;
			}
			else
				s += "in ";
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
				timeLabel.setText(s);
			}
			else
				timeLabel.setText("Starting now");
		}
		activeLabel.setText(call.getMembers().size() + " members");
		panel.setBorder(raised);
		
		if (selected) {
			panel.setBorder(lowered);
		}
		if (hovered)
			panel.setBackground(UIManager.getColor("Panel.background").darker());
		else
			panel.setBackground(UIManager.getColor("Panel.background"));
	}
	
	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		callsList.selected = callUuid;
		callsList.updatePanels();
	}
	
	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		callsList.selected = callUuid;
		callsList.updatePanels();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			callsList.selected = null;
		callsList.updatePanels();
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			callsList.selected = null;
		callsList.updatePanels();
	}
	
}
