package com.unleqitq.videocall.callclient.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenUtils {
	
	private Robot robot;
	private GraphicsDevice device;
	private boolean connected;
	
	public ScreenUtils() {
	}
	
	public void connect(GraphicsDevice device) {
		try {
			robot = new Robot(device);
			this.device = device;
			connected = true;
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public GraphicsEnvironment getEnvironment() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment();
	}
	
	public GraphicsDevice[] getDevices() {
		return getEnvironment().getScreenDevices();
	}
	
	public GraphicsDevice getDevice() {
		return device;
	}
	
	public Robot getRobot() {
		return robot;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public BufferedImage capture() {
		if (!connected)
			return null;
		return robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}
	
}
