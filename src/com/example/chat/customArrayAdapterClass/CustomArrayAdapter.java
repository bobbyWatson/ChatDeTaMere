package com.example.chat.customArrayAdapterClass;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chat.R;
/**
 * Create a radom color to each creation of the list ... WONDERFUL !
 * @author ben
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {
	
	private int colorA;
	private int colorB;
	
	private TextView fieldText;
	private LinearLayout wrapper;
	private String login;
	
	public CustomArrayAdapter(Context context, int resource, ArrayList<String> arrayList,String myLogin) {
		super(context, resource,arrayList);
		// TODO Auto-generated constructor stub
		login = myLogin;
		colorA = colorGenerator(Color.rgb(205,150,150));
		colorB = colorGenerator(Color.rgb(205,150,150));
	}
	// Function for generate Random Color
	private int colorGenerator(int i){
		Random random = new Random();
	    int red = random.nextInt(256);
	    int green = random.nextInt(256);
	    int blue = random.nextInt(256);
	    // mix the color

	    int color = Color.rgb(red, green, blue)*i;
	    return color;
		
	}
	// Override of getView to modify the background color of the current currentV
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View currentV = convertView;
		if (currentV == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			currentV = inflater.inflate(R.layout.messageother, parent, false);
		}
		
		wrapper = (LinearLayout) currentV.findViewById(R.id.wrapper);
		
		String coment = getItem(position);
		
		fieldText = (TextView) currentV.findViewById(R.id.comment);
		
		fieldText.setText(coment);
		
//		if(position%2==0)
//		{
//	     // even position color
//		  currentV.setBackgroundColor(colorA);
//		}
//		else
//		{
//	    // odd position color
//		  currentV.setBackgroundColor(colorB);
//		}
		
		
		fieldText.setBackgroundResource(isMyMessage(coment) ? R.drawable.bubble_yellow : R.drawable.bubble_green);
		wrapper.setGravity(isMyMessage(coment) ? Gravity.LEFT : Gravity.RIGHT);
		return currentV;
	}
	
	private Boolean isMyMessage(String message){
		String[] currentMessages = message.split(" said :");
		
		return currentMessages[0].contentEquals(login);
	}
}
