package com.unleqitq.videocall.callclient.gui.video;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.sharedclasses.user.CallUser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.UUID;

public class VideoPanel {
	
	private final UUID uuid;
	public JPanel bigPanel;
	public Canvas bigCanvas;
	public JPanel smallPanel;
	public Canvas smallCanvas;
	public int resizeOption = SIZE_FIT;
	
	public static final int SIZE_FIT = 0;
	public static final int SIZE_ZOOM = 1;
	public static final int SIZE_MEAN = 2;
	public static final int SIZE_ORIGINAL = 3;
	public static final int SIZE_STRETCH = 4;
	
	public VideoPanel(UUID user) {
		this.uuid = user;
		bigPanel = new JPanel();
		bigPanel.setPreferredSize(new Dimension(100, 70));
		smallPanel = new JPanel();
		smallPanel.setPreferredSize(new Dimension(500, 500));
		bigCanvas = new Canvas();
		smallCanvas = new Canvas();
		smallCanvas.setPreferredSize(smallPanel.getPreferredSize());
		bigCanvas.setPreferredSize(bigPanel.getPreferredSize());
		bigPanel.add(bigCanvas);
		smallPanel.add(smallCanvas);
		bigCanvas.setBackground(Color.BLACK);
		smallCanvas.setBackground(Color.BLACK);
		smallCanvas.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				resizeOption++;
				resizeOption %= 5;
			}
		});
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public void draw(BufferedImage image) {
		Canvas canvas;
		canvas = bigCanvas;
		if (!VideoPanels.instance.focusSingle)
			canvas = smallCanvas;
		if (canvas.getBufferStrategy() == null)
			canvas.createBufferStrategy(2);
		Graphics2D g = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		//System.out.println(image);
		if (image != null) {
			double wc = canvas.getWidth();
			double hc = canvas.getHeight();
			double wi = image.getWidth();
			double hi = image.getHeight();
			double rw = wc / wi;
			double rh = hc / hi;
			double r = 1;
			switch (resizeOption) {
				case SIZE_FIT -> {
					r = Math.min(rw, rh);
					rw = r;
					rh = r;
				}
				case SIZE_ZOOM -> {
					r = Math.max(rw, rh);
					rw = r;
					rh = r;
				}
				case SIZE_MEAN -> {
					r = (rw + rh) / 2;
					rw = r;
					rh = r;
				}
				case SIZE_ORIGINAL -> {
					rw = r;
					rh = r;
				}
			}
			int w = (int) (wi * rw);
			int h = (int) (hi * rh);
			int x = canvas.getWidth() / 2 - w / 2;
			int y = canvas.getHeight() / 2 - h / 2;
			//System.out.printf("c(%.2f, %.2f)\ti(%.2f, %.2f)\n", wc, hc, wi, hi);
			//System.out.printf("r: %.2f\tr(%.2f, %.2f)\ts(%d, %d)\tp(%d, %d)\n", r, rw, rh, w, h, x, y);
			g.drawImage(image, x, y, w, h, null);
		}
		
		CallUser user = CallClient.getInstance().users.get(uuid);
		g.setColor(new Color(0, 0, 0, 159));
		g.fillRect(0, canvas.getHeight() - 30, canvas.getWidth(), 30);
		g.setColor(Color.WHITE);
		String resizeString = switch (resizeOption) {
			case SIZE_FIT -> "fit";
			case SIZE_ZOOM -> "zoom";
			case SIZE_MEAN -> "mean";
			case SIZE_ORIGINAL -> "original";
			case SIZE_STRETCH -> "stretch";
			default -> "";
		};
		g.drawString(user.firstname + " " + user.lastname + " ".repeat(10) + "(" + resizeString + ")", 5,
				canvas.getHeight() - 5);
		
		g.dispose();
		canvas.getBufferStrategy().show();
	}
	
}
