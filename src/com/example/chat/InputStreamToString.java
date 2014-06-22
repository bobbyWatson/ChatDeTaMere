package com.example.chat;

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
			    builder.append(line); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return builder.toString();
	}
}