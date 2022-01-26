package com.unleqitq.videocall.client.gui;

import com.unleqitq.videocall.client.Client;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TrayHandler {
	
	public SystemTray systemTray;
	public TrayIcon trayIcon;
	
	public TrayHandler() {
		systemTray = SystemTray.getSystemTray();
		
		BufferedImage icon = new BufferedImage(systemTray.getTrayIconSize().width, systemTray.getTrayIconSize().height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = icon.createGraphics();
		g.drawImage(Client.getInstance().icon, 0, 0, icon.getWidth(), icon.getHeight(), null);
		g.dispose();
		
		trayIcon = new TrayIcon(icon);
		try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
			Client.errorG(e);
		}
	}
	
}
