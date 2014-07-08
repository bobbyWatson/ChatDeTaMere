package com.example.chat.tools.anims;

import com.example.chat.R;
import com.example.chat.R.anim;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

public class Anims {

	public static void ShakeError(Context currentCtx,EditText current) {
		Animation shake = AnimationUtils.loadAnimation(currentCtx, R.anim.shake); 
		current.startAnimation(shake);
	}
		
//	public static void flip(Context currentCtx,View currentV,EditText current) {
//		ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(currentCtx, R.animator.flip); 
//		anim.setTarget(currentV);
//		anim.start();
//	}
	
public  static void wizz(Context ctx) {
	Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
	long[] pattern = {0,50,100,50,100,50,100,400,100,300,100,350,50,200,100,100,50,600};
	v.vibrate(pattern, 2);
}
}
