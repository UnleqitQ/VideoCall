package com.unleqitq.videocall.callclient.utils;

import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.FloatControl;

public class ClipHandler {
	
	@NotNull
	public Clip clip;
	
	public FloatControl masterGain;
	public FloatControl auxSend;
	public FloatControl auxReturn;
	public FloatControl reverbSend;
	public FloatControl reverbReturn;
	public FloatControl volume;
	public FloatControl pan;
	public FloatControl balance;
	public FloatControl sampleRate;
	
	public BooleanControl mute;
	public BooleanControl applyReverb;
	
	public EnumControl reverb;
	
	public ClipHandler(@NotNull Clip clip) {
		this.clip = clip;
		
		masterGain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		auxSend = (FloatControl) clip.getControl(FloatControl.Type.AUX_SEND);
		auxReturn = (FloatControl) clip.getControl(FloatControl.Type.AUX_RETURN);
		reverbSend = (FloatControl) clip.getControl(FloatControl.Type.REVERB_SEND);
		reverbReturn = (FloatControl) clip.getControl(FloatControl.Type.REVERB_RETURN);
		volume = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
		pan = (FloatControl) clip.getControl(FloatControl.Type.PAN);
		balance = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
		sampleRate = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
		
		mute = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
		applyReverb = (BooleanControl) clip.getControl(BooleanControl.Type.APPLY_REVERB);
		
		reverb = (EnumControl) clip.getControl(EnumControl.Type.REVERB);
	}
	
}
