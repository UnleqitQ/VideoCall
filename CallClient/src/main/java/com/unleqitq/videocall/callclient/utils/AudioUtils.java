package com.unleqitq.videocall.callclient.utils;

import com.unleqitq.videocall.callclient.CallClient;
import com.unleqitq.videocall.transferclasses.call.AudioData;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.*;

public class AudioUtils {
	
	TargetDataLine targetDataLine;
	
	Mixer.Info microphoneInfo;
	
	Mixer.Info speakersInfo;
	
	Mixer microphone;
	
	Mixer speakers;
	
	Map<UUID, Clip> clips = new HashMap<>();
	
	AudioInputStream ais;
	
	public static final AudioFormat FORMAT = new AudioFormat(44100, 16, 1, true, false);
	
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
			targetDataLine = AudioSystem.getTargetDataLine(FORMAT);
			//targetDataLine = (TargetDataLine) microphone.getLine(microphone.getTargetLineInfo());
			microphoneInfo = mic;
			targetDataLine.open(FORMAT, 1 << 16);
			targetDataLine.start();
			ais = new AudioInputStream(targetDataLine);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void setSpeakersInfo(Mixer.Info speakersInfo) {
		this.speakersInfo = speakersInfo;
		Map<UUID, Clip> clips0 = new HashMap<>(clips);
		clips.clear();
		for (Map.Entry<UUID, Clip> entry : clips0.entrySet()) {
			if (entry.getValue().isRunning()) {
				entry.getValue().stop();
			}
			if (entry.getValue().isOpen()) {
				entry.getValue().close();
			}
		}
	}
	
	public Clip getClip(UUID uuid) {
		if (!clips.containsKey(uuid)) {
			try {
				clips.put(uuid, AudioSystem.getClip(speakersInfo));
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				return null;
			}
		}
		return clips.get(uuid);
	}
	
	/**
	 * Open Clip
	 */
	@NotNull
	public Clip openClip(@NotNull Clip clip, byte[] data, int offset, int bufferSize) {
		try {
			clip.open(FORMAT, data, offset, bufferSize);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		return clip;
	}
	
	@NotNull
	public Clip closeClip(@NotNull Clip clip) {
		if (clip.isOpen()) {
			clip.close();
		}
		return clip;
	}
	
	/**
	 * Start Clip
	 */
	@NotNull
	public Clip startClip(@NotNull Clip clip) {
		if (!clip.isRunning() && clip.isOpen()) {
			clip.start();
		}
		return clip;
	}
	
	@NotNull
	public Clip stopClip(@NotNull Clip clip) {
		if (clip.isRunning()) {
			clip.stop();
		}
		return clip;
	}
	
	public List<Mixer.Info> getMicrophones() {
		List<Mixer.Info> microphones = new ArrayList<>();
		for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			if (mixer.getTargetLineInfo().length > 0)
				microphones.add(mixerInfo);
		}
		return microphones;
	}
	
	public List<Mixer.Info> getSpeakersList() {
		List<Mixer.Info> speakers = new ArrayList<>();
		for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			if (mixer.getSourceLineInfo().length > 0)
				speakers.add(mixerInfo);
		}
		return speakers;
	}
	
	public AudioData read() throws IOException {
		byte[] buffer = new byte[(int) (FORMAT.getFrameSize() * CallClient.Config.audioDuration)];
		int bytesRead = ais.read(buffer);
		for (int i = 0; i < buffer.length; i++) {
			if (Math.abs(buffer[i]) < 5) {
				buffer[i] = 0;
			}
		}
		return new AudioData(buffer, 0, bytesRead, null);
	}
	
}
