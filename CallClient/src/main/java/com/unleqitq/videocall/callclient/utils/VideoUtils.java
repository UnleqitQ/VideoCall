package com.unleqitq.videocall.callclient.utils;


import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.log.WebcamLogConfigurator;
import com.unleqitq.videocall.callclient.gui.settings.SettingsPanel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;

public class VideoUtils implements WebcamDiscoveryListener {
	
	Webcam webcam;
	
	private boolean connected;
	
	public VideoUtils() {
		String config = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><configuration></configuration>";
		WebcamLogConfigurator.configure(new ByteArrayInputStream(config.getBytes()));
		Webcam.addDiscoveryListener(this);
		webcam = null;
		Webcam wc = Webcam.getDefault();
		if (wc != null)
			connect(wc);
	}
	
	public void connect(Webcam webcam) {
		try {
			if (connected)
				disconnect();
			this.webcam = webcam;
			webcam.open();
			connected = true;
		} catch (Exception ignored) {
		}
	}
	
	public void disconnect() {
		webcam.close();
		connected = false;
	}
	
	
	public BufferedImage capture() {
		return webcam.getImage();
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	
	@Override
	public void webcamFound(WebcamDiscoveryEvent event) {
		if (SettingsPanel.instance != null)
			SettingsPanel.instance.devicesPanel.updateCams();
	}
	
	public List<Webcam> getWebcams() {
		return Webcam.getWebcams();
	}
	
	@Override
	public void webcamGone(WebcamDiscoveryEvent event) {
		if (SettingsPanel.instance != null)
			SettingsPanel.instance.devicesPanel.updateCams();
		if (event.getWebcam() == webcam)
			disconnect();
	}
	
}
