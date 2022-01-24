package com.unleqitq.videocall.client.gui.teams.list;

import com.github.weisj.darklaf.listener.MouseClickListener;
import com.unleqitq.videocall.client.Client;
import com.unleqitq.videocall.client.gui.editor.team.TeamEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class TeamsList implements MouseClickListener {
	
	public JPanel root = new JPanel();
	JScrollPane scrollPane = new JScrollPane();
	JPanel panel = new JPanel();
	JButton createButton = new JButton("Create");
	public UUID selected = null;
	Set<UUID> teamSet = new HashSet<>();
	Map<UUID, TeamListPanel> teamPanelMap = new HashMap<>();
	
	public TeamsList() {
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.add(createButton);
		createButton.addActionListener((event) -> new TeamEditor());
		root.add(scrollPane);
		scrollPane.setViewportView(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.addMouseListener(this);
	}
	
	@NotNull
	public TeamListPanel getCallPanel(@NotNull UUID uuid) {
		if (!teamPanelMap.containsKey(uuid))
			teamPanelMap.put(uuid, new TeamListPanel(uuid, this));
		return teamPanelMap.get(uuid);
	}
	
	public void remove(UUID uuid) {
		teamSet.remove(uuid);
		teamPanelMap.remove(uuid);
	}
	
	public void updateList() {
		List<UUID> l = new ArrayList<>(teamSet);
		l.sort(Comparator.comparing(o -> Client.getInstance().teamCache.asMap().get(o).getName()));
		
		panel.removeAll();
		
		for (UUID uuid : l) {
			panel.add(getCallPanel(uuid).panel);
		}
	}
	
	public void add(UUID uuid) {
		teamSet.add(uuid);
	}
	
	public void updatePanels() {
		Point pos = MouseInfo.getPointerInfo().getLocation();
		for (TeamListPanel p : teamPanelMap.values()) {
			boolean hovering = false;
			try {
				hovering = new Rectangle(p.panel.getLocationOnScreen(), p.panel.getSize()).contains(pos);
			} catch (Exception ignored) {
			}
			p.update(p.teamUuid.equals(selected), hovering);
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
