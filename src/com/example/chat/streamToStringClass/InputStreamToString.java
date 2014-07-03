package com.example.chat.streamToStringClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class InputStreamToString {
	
	public static String convert(InputStream is) {
	    String line = "";
	    StringBuilder builder = new StringBuilder();
	    
	    BufferedReader rd=new BufferedReader(new InputStreamReader(is));
 
	    try {
			while ((line = rd.readLine()) != null) { 
			    Log.i("READ LINE CLASS STREAM",line);
				builder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return builder.toString();
	}
}
