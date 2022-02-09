package com.unleqitq.videocall.callclient.gui.video;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.sharedclasses.user.CallUser;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;

public class VideoPanel {
	
	private final UUID uuid;
	public JPanel bigPanel;
	public Canvas bigCanvas;
	public JPanel smallPanel;
	public Canvas smallCanvas;
	
	public VideoPanel(UUID user) {
		this.uuid = user;
		bigPanel = new JPanel();
		smallPanel = new JPanel();
		bigCanvas = new Canvas();
		smallCanvas = new Canvas();
		bigPanel.add(bigCanvas);
		smallPanel.add(smallCanvas);
		bigCanvas.setBackground(Color.BLACK);
		smallCanvas.setBackground(Color.BLACK);
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public void draw(BufferedImage image) {
		Canvas canvas;
		if (VideoPanels.instance.focusScreen)
			canvas = smallCanvas;
		else
			canvas = bigCanvas;
		Graphics2D g = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		if (image != null) {
			double wc = canvas.getWidth();
			double hc = canvas.getHeight();
			double wi = image.getWidth();
			double hi = image.getHeight();
			double rw = wc / wi;
			double rh = hc / hi;
			double r = Math.min(rw, rh);
			int w = (int) (canvas.getWidth() * r);
			int h = (int) (canvas.getHeight() * r);
			int x = canvas.getWidth() / 2 - w / 2;
			int y = canvas.getHeight() / 2 - h / 2;
			g.drawImage(image, x, y, w, h, null);
		}
		
		CallUser user = CallClient.getInstance().users.get(uuid);
		g.setColor(new Color(0, 0, 0, 127));
		g.drawRect(0, canvas.getHeight() - 30, canvas.getWidth(), 30);
		g.setColor(Color.WHITE);
		g.drawString(user.firstname + " " + user.lastname, 5, canvas.getHeight() - 25);
		
		g.dispose();
		canvas.getBufferStrategy().show();
	}
	
}
