package com.unleqitq.videocall.callclient.utils;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.callclient.ClientCallUser;
import com.unleqitq.videocall.callclient.gui.settings.SettingsPanel;
import com.unleqitq.videocall.transferclasses.call.AudioData;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AudioUtils {
	
	/**
	 * Speakers
	 */
	//SourceDataLine sourceDataLine;
	ConcurrentMap<UUID, SourceDataLine> speakerLines = new ConcurrentHashMap<>();
	/**
	 * Micro
	 */
	TargetDataLine targetDataLine;
	
	Mixer.Info microphoneInfo;
	
	Mixer.Info speakersInfo;
	
	Mixer microphone;
	
	Mixer speakers;
	
	//Map<UUID, Clip> clips = new HashMap<>();
	
	AudioInputStream ais;
	
	//public CombineInputStream cis = new CombineInputStream();
	
	public static final AudioFormat FORMAT = new AudioFormat(44100, 16, 2, true, false);
	
	public AudioUtils() {
	}
	
	public void setMicrophoneInfo(Mixer.Info mic) {
		if (microphone != null) {
			Arrays.stream(microphone.getSourceLines()).forEach(line -> {
				if (line.isOpen())
					line.close();
			});
			Arrays.stream(microphone.getTargetLines()).forEach(line -> {
				if (line.isOpen())
					line.close();
			});
			microphone.close();
		}
		microphoneInfo = mic;
		System.out.println(mic);
		if (ais != null)
			try {
				ais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if (targetDataLine != null && targetDataLine.isOpen())
			targetDataLine.close();
		try {
			//microphone = AudioSystem.getMixer(mic);
			targetDataLine = AudioSystem.getTargetDataLine(FORMAT, microphoneInfo);
			//targetDataLine = (TargetDataLine) microphone.getLine(microphone.getTargetLineInfo());
			microphoneInfo = mic;
			targetDataLine.open(FORMAT);
			targetDataLine.start();
			ais = new AudioInputStream(targetDataLine);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void setSpeakersInfo(Mixer.Info speakersInfo) {
		this.speakersInfo = speakersInfo;
		/*Map<UUID, Clip> clips0 = new HashMap<>(clips);
		clips.clear();
		for (Map.Entry<UUID, Clip> entry : clips0.entrySet()) {
			if (entry.getValue().isRunning()) {
				entry.getValue().stop();
			}
			if (entry.getValue().isOpen()) {
				entry.getValue().close();
			}
		}*/
		Map<UUID, SourceDataLine> lines = new HashMap<>(speakerLines);
		speakerLines.clear();
		for (Map.Entry<UUID, SourceDataLine> entry : lines.entrySet()) {
			/*if (entry.getValue().isRunning()) {
				entry.getValue().stop();
			}
			if (entry.getValue().isOpen()) {
				entry.getValue().close();
			}*/
			stopLine(entry.getValue());
			closeLine(entry.getValue());
		}
		/*if (sourceDataLine != null) {
			if (sourceDataLine.isRunning())
				sourceDataLine.stop();
			if (sourceDataLine.isOpen())
				sourceDataLine.close();
			sourceDataLine = null;
		}
		
		try {
			sourceDataLine = AudioSystem.getSourceDataLine(FORMAT);
			sourceDataLine.open();
			sourceDataLine.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}*/
		System.out.println(speakersInfo);
	}
	
	/*public Clip getClip(UUID uuid) {
		if (!clips.containsKey(uuid)) {
			try {
				clips.put(uuid, AudioSystem.getClip(speakersInfo));
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				return null;
			}
		}
		return clips.get(uuid);
	}*/
	
	/*
	 * Open Clip
	 */
	/*@NotNull
	public Clip openClip(@NotNull Clip clip, byte[] data, int offset, int bufferSize) {
		try {
			clip.open(FORMAT, data, offset, bufferSize);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		return clip;
	}*/
	
	/*@NotNull
	public Clip closeClip(@NotNull Clip clip) {
		if (clip.isOpen()) {
			clip.close();
		}
		return clip;
	}*/
	
	/*
	 * Start Clip
	 */
	/*@NotNull
	public Clip startClip(@NotNull Clip clip) {
		if (!clip.isRunning() && clip.isOpen()) {
			clip.start();
		}
		return clip;
	}*/
	
	/*@NotNull
	public Clip stopClip(@NotNull Clip clip) {
		if (clip.isRunning()) {
			clip.stop();
		}
		return clip;
	}*/
	public SourceDataLine getLine(UUID user) {
		if (!speakerLines.containsKey(user)) {
			SourceDataLine sourceDataLine;
			try {
				sourceDataLine = AudioSystem.getSourceDataLine(FORMAT);
				sourceDataLine.open();
				sourceDataLine.start();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				return null;
			}
			speakerLines.put(user, sourceDataLine);
			return sourceDataLine;
		}
		return speakerLines.get(user);
	}
	
	public List<Mixer.Info> getMicrophones() {
		List<Mixer.Info> microphones = new ArrayList<>();
		for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
			/*Mixer mixer = AudioSystem.getMixer(mixerInfo);
			
			if (mixer.getTargetLineInfo().length > 0)*/
			if (mixerInfo.getDescription().toLowerCase().contains("capture"))
				microphones.add(mixerInfo);
		}
		return microphones;
	}
	
	public List<Mixer.Info> getSpeakersList() {
		List<Mixer.Info> speakers = new ArrayList<>();
		for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
			/*Mixer mixer = AudioSystem.getMixer(mixerInfo);
			if (mixer.getSourceLineInfo().length > 0)*/
			if (mixerInfo.getDescription().toLowerCase().contains("playback"))
				speakers.add(mixerInfo);
		}
		return speakers;
	}
	
	public AudioData read() throws IOException {
		byte[] buffer = new byte[(int) (FORMAT.getFrameRate() * CallClient.Config.audioDuration * FORMAT.getFrameSize())];
		int bytesRead = ais.read(buffer);
		return new AudioData(buffer, 0, bytesRead, null);
	}
	
	/**
	 * Open Line
	 */
	@NotNull
	public SourceDataLine openLine(@NotNull SourceDataLine line) {
		try {
			line.open(FORMAT);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	/**
	 * First use {@link #openLine}
	 * Then use {@link #startLine}
	 */
	@NotNull
	public SourceDataLine writeLine(@NotNull SourceDataLine line, byte[] data, int offset, int bufferSize) {
		new Thread(() -> line.write(data, offset, bufferSize)).start();
		return line;
	}
	
	@NotNull
	public SourceDataLine closeLine(@NotNull SourceDataLine line) {
		if (line.isOpen()) {
			line.close();
		}
		return line;
	}
	
	/**
	 * Start Line
	 */
	@NotNull
	public SourceDataLine startLine(@NotNull SourceDataLine line) {
		if (!line.isRunning() && line.isOpen()) {
			line.start();
		}
		return line;
	}
	
	@NotNull
	public SourceDataLine stopLine(@NotNull SourceDataLine line) {
		if (line.isRunning()) {
			line.stop();
		}
		return line;
	}
	
	public void updateGain(UUID user) {
		SourceDataLine line = getLine(user);
		FloatControl control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue(CallClient.getInstance().clientCallUsers.get(
				user).gain * SettingsPanel.instance.gainSlider.getValue() * 0.001f * (control.getMaximum() - control.getMinimum()) + control.getMinimum());
	}
	
	public void updateGain() {
		CallClient.getInstance().clientCallUsers.values().stream().filter(ClientCallUser::isConnected).forEach(
				c -> updateGain(c.uuid));
	}
	
	/*public void step() throws IOException {
		byte[] bytes = new byte[FORMAT.getFrameSize()];
		int length = cis.read(bytes);
		sourceDataLine.write(bytes, 0, length);
	}*/
	
}
