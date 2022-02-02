package com.unleqitq.videocall.callclient.utils;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.transferclasses.call.VideoData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class VideoUtils implements WebcamListener {
	
	public Webcam webcam;
	
	public List<Webcam> getWebcams() {
		return Webcam.getWebcams();
	}
	
	public void setWebcam(Webcam webcam) {
		if (this.webcam != null)
			Arrays.stream(this.webcam.getWebcamListeners()).forEach(
					(listener) -> this.webcam.removeWebcamListener(listener));
		if (this.webcam != null && this.webcam.isOpen())
			this.webcam.close();
		if (this.webcam != null && this.webcam.getDevice().isOpen())
			this.webcam.getDevice().close();
		this.webcam = webcam;
		//webcam.getDevice().open();
		webcam.addWebcamListener(this);
		webcam.open(false);
	}
	
	@Override
	public void webcamOpen(WebcamEvent we) {
	
	}
	
	@Override
	public void webcamClosed(WebcamEvent we) {
	
	}
	
	@Override
	public void webcamDisposed(WebcamEvent we) {
	
	}
	
	@Override
	public void webcamImageObtained(WebcamEvent we) {
		try {
			if (we.getImage() == null || !CallClient.getInstance().video)
				return;
			CallClient.getInstance().connection.send(
					VideoData.create(we.getImage(), CallClient.getInstance().userUuid));
		} catch (IOException | NullPointerException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
}
