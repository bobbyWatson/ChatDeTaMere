package com.example.chat.customArrayAdapterClass;

import java.util.ArrayList;
import java.util.Random;
import java.util.zip.Inflater;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
/**
 * Create a radom color to each creation of the list ... WONDERFUL !
 * @author ben
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {
	
	private int colorA;
	private int colorB;
	

	public CustomArrayAdapter(Context context, int resource, ArrayList<String> arrayList) {
		super(context, resource,arrayList);
		// TODO Auto-generated constructor stub
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
	// Override of getView to modify the background color of the current row
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View currentV = super.getView(position, convertView, parent);
	
		if(position%2==0)
		{
	     // even position color
		  currentV.setBackgroundColor(colorA);
		}
		else
		{
	    // odd position color
		  currentV.setBackgroundColor(colorB);
		}
		return currentV;
	}
	
}
