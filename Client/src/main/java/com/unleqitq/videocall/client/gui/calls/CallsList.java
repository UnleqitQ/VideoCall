package com.unleqitq.videocall.client.gui.calls;

import com.unleqitq.videocall.client.Client;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

public class CallsList {
	
	JScrollPane scrollPane = new JScrollPane();
	JPanel panel = new JPanel();
	UUID selected = null;
	Set<UUID> callSet = new HashSet<>();
	Map<UUID, CallPanel> callPanelMap = new HashMap<>();
	
	public CallsList() {
		scrollPane.setViewportView(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	}
	
	@NotNull
	public CallPanel getCallPanel(@NotNull UUID uuid) {
		if (!callPanelMap.containsKey(uuid))
			callPanelMap.put(uuid, new CallPanel(uuid));
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
		for (CallPanel p : callPanelMap.values()) {
			p.update(p.callUuid.equals(selected), false);
		}
	}
	
}
