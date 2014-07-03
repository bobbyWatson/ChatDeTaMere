package com.example.chat.soundClass;


import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.example.chat.R;

public class SoundManager {
	public final static String TAG = "Log";

	protected SoundPool sp;
	protected Context appContext;

	public final static int SOUND_NB = 4;

	public final int sound1;
	public final int sound2;
	public final int sound3;
	public final int sound4;
	
	public Map<String, Integer> tableCompare=new HashMap<String, Integer>();

//	public final int sound5;
//	public final int sound6;

	public Boolean ready;
	
	public SoundManager(Context ct){
		appContext = ct;
		sp = new SoundPool(SOUND_NB, AudioManager.STREAM_MUSIC, 0);
		Log.v("SOUND MANAGER","sp crée");
		sound1 = sp.load(appContext, R.raw.bigfart, 1);
		sound2 = sp.load(appContext, R.raw.wetfart, 1);
		sound3 = sp.load(appContext, R.raw.cartmanautority, 1);
		sound4 = sp.load(appContext, R.raw.thibsmom, 1);
		Log.v("SOUND MANAGER","sounds crée "+Integer.toString(sound1));
		

		ready=true;
	}

	public void playSound(final int nb){
		boolean mute=false;
		if(mute == false){
			sp.play(nb, 1, 1, 0, 0, 1);
		}
	}
}
