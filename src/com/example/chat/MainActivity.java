package com.example.chat;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	// Declare every private variables we will need
	public static final String MyPREFERENCES = "chatPrefs";
	
	private EditText loginField;
	private EditText passwordField;
	private Button cancelBtn;
	private Button connectBtn;
	private Button inscriptionBtn;
	private ProgressDialog spin;
	private SharedPreferences prefs;
	
	/**
	 * Init of the current activity and her view
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the view associated to this activity
		setContentView(R.layout.activity_main);
		
		// Get all field and buttons from the view
		prefs = getSharedPreferences(MyPREFERENCES, 0);
		
		loginField = (EditText) findViewById(R.id.login_form);
		passwordField = (EditText) findViewById(R.id.password_form);
		
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		connectBtn = (Button) findViewById(R.id.connect_btn);
		inscriptionBtn = (Button) findViewById(R.id.inscription_btn);
		
		// Add events listeners on every buttons
		cancelBtn.setOnClickListener(cancelHandler);
		connectBtn.setOnClickListener(submitHandler);
		inscriptionBtn.setOnClickListener(inscriptionHandler);
		// Test if we where connected before last exit, and if we were we use those informations to reconnect directly to the chat   
		if (prefs.getString("LOGIN", "") != ""
				&& prefs.getString("PASSWORD", "") != "") {
			AskConnectTask task = new AskConnectTask();
			task.execute(prefs.getString("LOGIN", ""),
					prefs.getString("PASSWORD", ""));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * Cancel button click callback function, empty fields when we click on cancel button
	 */
	private View.OnClickListener cancelHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			loginField.setText("");
			passwordField.setText("");
		}
	};
	/**
	 * Inscription button click callback function , for open new activity for register a new subscriber
	 */
	private View.OnClickListener inscriptionHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), SubscriberPage.class);
			MainActivity.this.startActivity(intent);
		}
	};
	/**
	 * Submit button click callback function, for try connection to the chat with form values
	 */
	private View.OnClickListener submitHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// Test if field are empty 
			if (loginField.getText().toString().equals("")|| passwordField.getText().toString().equals("")) {
				// Animate empty fields
				if(loginField.getText().toString().equals("")){
					Anims.ShakeError(MainActivity.this,loginField);
				}
				if(passwordField.getText().toString().equals("")){
					Anims.ShakeError(MainActivity.this,passwordField);
				}
				// Display toast with error message
				Toast toast = Toast.makeText(MainActivity.this, getResources()
						.getString(R.string.forms_empty), 2);
				toast.show();
				
					
			} else {
				// Start connexion try
				AskConnectTask task = new AskConnectTask();
				task.execute(loginField.getText().toString(), passwordField
						.getText().toString());
			}
		}
	};
	/**
	 * private class for start connection to the chat
	 */
	private class AskConnectTask extends
			android.os.AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Show or set up spinning loader
			if (spin != null) {
				spin.show();
			} else {
				spin = ProgressDialog.show(
						MainActivity.this,
						getResources().getString(
								R.string.connexion_progress_title),
						getResources().getString(
								R.string.connecsion_progress_body));
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String login = params[0];
			String password = params[1];

			InputStream content = null;
			// Try catch for send message to others user on chat api
			try {
				HttpClient httpclient = new DefaultHttpClient();
				// Init url request.
				StringBuilder urlBuilder = new StringBuilder();
				// Setting url request and params.
				urlBuilder
						.append("http://dev.loicortola.com/parlez-vous-android/connect/")
						.append(login).append("/").append(password);
				// Execute http request and stock inside response var the return
				HttpResponse response = httpclient.execute(new HttpGet(
						urlBuilder.toString()));
				// Get data from response !! it's a data stream !!
				content = response.getEntity().getContent();
				// Transforms data stream to string
				String res = InputStreamToString.convert(content);
				// Test if return value of call is boolean, if it's one then we validate the call and return value
				if (Boolean.valueOf(res)) {
					return true;
				}
			} catch (Exception e) {
				Log.i("[GET REQUEST]", "Network exception");
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// Hide spinning loader 
			spin.hide();
			if (result) {
				String login;
				String password;
				// test if login and password fields are empty
				if (!loginField.getText().toString().equals("")
						&& !passwordField.getText().toString().equals("")) {
					login = loginField.getText().toString();
					password = passwordField.getText().toString();
					// Save inside Editor login name and password for use in others activity
					Editor editor = prefs.edit();
					editor.putString("LOGIN", login);
					editor.putString("PASSWORD", password);
					editor.commit();
				} else {
					// Get login and password
					login = prefs.getString("LOGIN", "");
					password = prefs.getString("PASSWORD", "");
				}
				// Start new activity with some values send to her
				Intent intent = new Intent(MainActivity.this, ChatPage.class);
				intent.putExtra("LOGIN", login);
				intent.putExtra("PASSWORD", password);
				startActivity(intent);
			} else {
				// Display error and animations if connexion failed
				Anims.ShakeError(MainActivity.this,loginField);
				Anims.ShakeError(MainActivity.this,passwordField);
				Toast toast = Toast.makeText(MainActivity.this, getResources()
						.getString(R.string.unknown_id), 2);
				toast.show();
			}
		}
	}
}
