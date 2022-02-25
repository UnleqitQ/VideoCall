package com.unleqitq.videocall.callclient.gui.video;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.sharedclasses.user.CallUser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class BigVideoPanel {
	
	public JPanel panel;
	public Canvas canvas;
	public int resizeOption = SIZE_FIT;
	public BufferedImage lastImage = null;
	public long lastImageTime = 0;
	
	public static final int SIZE_FIT = 0;
	public static final int SIZE_ZOOM = 1;
	public static final int SIZE_MEAN = 2;
	public static final int SIZE_ORIGINAL = 3;
	public static final int SIZE_STRETCH = 4;
	
	public BigVideoPanel() {
		panel = new JPanel();
		canvas = new Canvas();
		canvas.setBackground(Color.BLACK);
		canvas.setSize(1400, 700);
		panel.add(canvas);
		canvas.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					resizeOption++;
					resizeOption %= 5;
				}
			}
		});
	}
	
	public void draw(BufferedImage image) {
		lastImage = image;
		lastImageTime = System.currentTimeMillis();
		draw();
	}
	
	public void draw() {
		BufferedImage image;
		if (System.currentTimeMillis() - lastImageTime < 1000) {
			image = lastImage;
		}
		else {
			image = new BufferedImage(300, 300, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g0 = image.createGraphics();
			g0.setFont(new Font("", Font.PLAIN, 40));
			g0.setColor(Color.BLACK);
			g0.fillRect(0, 0, 300, 300);
			g0.setColor(Color.WHITE);
			g0.fillOval(75, 75, 150, 150);
			g0.setColor(Color.BLACK);
			String s = (CallClient.getInstance().users.get(VideoPanels.instance.focusedUser).getFirstname().charAt(
					0) + " " + CallClient.getInstance().users.get(VideoPanels.instance.focusedUser).getLastname().charAt(0)).toUpperCase();
			g0.drawString(s, 150 - 30, 150 + 18);
			g0.dispose();
		}
		if (canvas.getBufferStrategy() == null)
			canvas.createBufferStrategy(2);
		Graphics2D g = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
		
		CallUser user = CallClient.getInstance().users.get(VideoPanels.instance.focusedUser);
		g.setColor(new Color(0, 0, 0, 159));
		g.fillRect(0, canvas.getHeight() - 60, canvas.getWidth(), 60);
		g.setFont(new Font("", Font.PLAIN, 15));
		String resizeString = switch (resizeOption) {
			case SIZE_FIT -> "fit";
			case SIZE_ZOOM -> "zoom";
			case SIZE_MEAN -> "mean";
			case SIZE_ORIGINAL -> "original";
			case SIZE_STRETCH -> "stretch";
			default -> "";
		};
		g.setColor(Color.WHITE);
		g.drawString(user.firstname + " " + user.lastname + " ".repeat(15) + "(" + resizeString + ")", 20,
				canvas.getHeight() - 20);
		
		g.dispose();
		canvas.getBufferStrategy().show();
	}
	
	
}
