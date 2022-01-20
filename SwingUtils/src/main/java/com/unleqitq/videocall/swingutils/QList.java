package com.unleqitq.videocall.swingutils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QList<E> implements MouseListener, KeyListener {
	
	public JScrollPane scrollPane;
	public JPanel panel;
	
	public Map<E, Panel> componentMap;
	
	public E selected;
	
	public List<E> values;
	
	public QList() {
		scrollPane = new JScrollPane();
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(panel);
		
		componentMap = new HashMap<>();
		selected = null;
		values = new ArrayList<>();
		
		panel.addKeyListener(this);
	}
	
	public void update() {
		panel.removeAll();
		for (int i = 0; i < values.size(); i++) {
			E value = values.get(i);
			Panel comp = componentMap.get(value);
			panel.add(comp);
			comp.addKeyListener(this);
			panel.setComponentZOrder(comp, i);
		}
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		if (values.size() == 0)
			return;
		if (e.getKeyCode() == KeyEvent.KEY_LAST) {
			selected = values.get(values.size() - 1);
		}
		else if (e.getKeyCode() == KeyEvent.KEY_FIRST) {
			selected = values.get(0);
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP) {
			int i = values.indexOf(selected);
			if (i == 0)
				return;
			selected = values.get(i--);
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			int i = values.indexOf(selected);
			if (i == values.size() - 1)
				return;
			selected = values.get(i++);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (values.size() == 0)
			return;
		if (e.getKeyCode() == KeyEvent.KEY_LAST) {
			selected = values.get(values.size() - 1);
		}
		else if (e.getKeyCode() == KeyEvent.KEY_FIRST) {
			selected = values.get(0);
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP) {
			int i = values.indexOf(selected);
			if (i == 0)
				return;
			selected = values.get(i--);
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			int i = values.indexOf(selected);
			if (i == values.size() - 1)
				return;
			selected = values.get(i++);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (values.size() == 0)
			return;
		if (e.getKeyCode() == KeyEvent.KEY_LAST) {
			selected = values.get(values.size() - 1);
		}
		else if (e.getKeyCode() == KeyEvent.KEY_FIRST) {
			selected = values.get(0);
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP) {
			int i = values.indexOf(selected);
			if (i == 0)
				return;
			selected = values.get(i--);
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			int i = values.indexOf(selected);
			if (i == values.size() - 1)
				return;
			selected = values.get(i++);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (values.size() == 0)
			return;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (values.size() == 0)
			return;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (values.size() == 0)
			return;
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		if (values.size() == 0)
			return;
		
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		if (values.size() == 0)
			return;
		
	}
	
}
