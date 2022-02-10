package com.unleqitq.videocall.callclient.utils;


import marvin.image.MarvinImage;
import marvin.video.MarvinJavaCVAdapter;
import marvin.video.MarvinVideoInterface;
import marvin.video.MarvinVideoInterfaceException;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class VideoUtils {
	
	MarvinVideoInterface videoAdapter;
	
	private boolean connected;
	
	public VideoUtils() {
		videoAdapter = new MarvinJavaCVAdapter();
		try {
			videoAdapter.connect(0);
			connected = true;
		} catch (MarvinVideoInterfaceException e) {
			e.printStackTrace();
		}
	}
	
	public void connect(int id) {
		try {
			if (connected)
				videoAdapter.disconnect();
			connected = false;
		} catch (MarvinVideoInterfaceException e) {
			e.printStackTrace();
		}
		try {
			videoAdapter.connect(id);
			connected = true;
		} catch (MarvinVideoInterfaceException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			if (connected)
				videoAdapter.disconnect();
			connected = false;
		} catch (MarvinVideoInterfaceException e) {
			e.printStackTrace();
		}
	}
	
	
	public BufferedImage capture() throws IOException {
		try {
			MarvinImage mImage = videoAdapter.getFrame();
			return mImage.getBufferedImage();
		} catch (MarvinVideoInterfaceException e) {
			throw new IOException(e);
		}
	}
	
	public boolean isConnected() {
		return connected;
	}
	
}
