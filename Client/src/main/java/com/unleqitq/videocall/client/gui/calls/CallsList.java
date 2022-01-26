package com.unleqitq.videocall.client.gui.calls;

import com.github.weisj.darklaf.listener.MouseClickListener;
import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.client.gui.editor.call.BasicCallEditor;
import com.unleqitq.videocall.client.gui.editor.call.TeamCallEditor;
import com.unleqitq.videocall.transferclasses.base.RequestAllUser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class CallsList implements MouseClickListener {
	
	public JPanel root = new JPanel();
	JScrollPane scrollPane = new JScrollPane();
	JPanel panel = new JPanel();
	UUID selected = null;
	JButton createBasicButton = new JButton("Create Basic");
	JButton createTeamButton = new JButton("Create Team");
	Set<UUID> callSet = new HashSet<>();
	Map<UUID, CallListPanel> callPanelMap = new HashMap<>();
	
	public CallsList() {
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.add(createBasicButton);
		root.add(createTeamButton);
		root.add(scrollPane);
		createBasicButton.addActionListener((event) -> new Thread(() -> {
			createBasicButton.setEnabled(false);
			Client.getInstance().connection.send(new RequestAllUser());
			try {
				Thread.sleep(1000 * 4);
			} catch (InterruptedException ignored) {
			}
			new BasicCallEditor(new HashMap<>(Client.getInstance().userCache.asMap()));
			createBasicButton.setEnabled(true);
		}).start());
		createTeamButton.addActionListener((event) -> new Thread(() -> {
			createTeamButton.setEnabled(false);
			Client.getInstance().connection.send(new RequestAllUser());
			try {
				Thread.sleep(1000 * 4);
			} catch (InterruptedException ignored) {
			}
			new TeamCallEditor(new HashMap<>(Client.getInstance().userCache.asMap()),
					new HashMap<>(Client.getInstance().teamCache.asMap()));
			createTeamButton.setEnabled(true);
		}).start());
		scrollPane.setViewportView(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.addMouseListener(this);
	}
	
	@NotNull
	public CallListPanel getCallPanel(@NotNull UUID uuid) {
		if (!callPanelMap.containsKey(uuid))
			callPanelMap.put(uuid, new CallListPanel(uuid, this));
		return callPanelMap.get(uuid);
	}
	
	public void remove(UUID uuid) {
		callSet.remove(uuid);
		callPanelMap.remove(uuid);
	}
	
	public void updateList() {
		List<UUID> l = new ArrayList<>(callSet);
		l.sort(new Comparator<UUID>() {
			
			@Override
			public int compare(UUID o1, UUID o2) {
				return (int) (Client.getInstance().callCache.asMap().get(
						o1).getTime() - Client.getInstance().callCache.asMap().get(o2).getTime());
			}
		});
		
		panel.removeAll();
		
		for (UUID uuid : l) {
			panel.add(getCallPanel(uuid).panel);
		}
	}
	
	public void add(UUID uuid) {
		callSet.add(uuid);
	}
	
	public void updatePanels() {
		Point pos = MouseInfo.getPointerInfo().getLocation();
		for (CallListPanel p : callPanelMap.values()) {
			boolean hovering = false;
			try {
				hovering = new Rectangle(p.panel.getLocationOnScreen(), p.panel.getSize()).contains(pos);
			} catch (Exception ignored) {
			}
			p.update(p.callUuid.equals(selected), hovering);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		selected = null;
		updatePanels();
	}
	
	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		selected = null;
		updatePanels();
	}
	
}
