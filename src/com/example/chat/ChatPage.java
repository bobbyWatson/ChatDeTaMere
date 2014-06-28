package com.example.chat;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.R.string;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class ChatPage extends ActionBarActivity {

	private SharedPreferences prefs;
	private String login;
	private String password;
	private TextView welcomeName;
	private ProgressDialog spin;
	private EditText messageZone;
	private Button sendButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_page);

		messageZone = (EditText) findViewById(R.id.message_zone);
		sendButton = (Button) findViewById(R.id.send_message);
		sendButton.setOnClickListener(sendMessageHandler);

		Intent intent = getIntent();
		welcomeName = (TextView) findViewById(R.id.welcome_name);
		if (intent != null) {
			login = intent.getStringExtra("LOGIN");
			password = intent.getStringExtra("PASSWORD");
			welcomeName.setText(welcomeName.getText().toString() + " " + login);
		}
		
		GetMessagesOperation task = new GetMessagesOperation();
		task.execute();
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_page, menu);
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
		if (id == R.id.unlog) {

			prefs = getSharedPreferences(MainActivity.MyPREFERENCES, 0);
			Editor editor = prefs.edit();
			editor.putString("LOGIN", "");
			editor.putString("PASSWORD", "");
			editor.commit();
			Intent intent = new Intent(ChatPage.this, MainActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private View.OnClickListener sendMessageHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (messageZone.getText().toString().equals("")) {
				Toast toast = Toast.makeText(ChatPage.this, getResources()
						.getString(R.string.message_empty), 2);
				toast.show();
			} else {
				SendMessageOperation task = new SendMessageOperation();
				task.execute(messageZone.getText().toString());
			}
		}
	};

	private class SendMessageOperation extends
			android.os.AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (spin != null) {
				spin.show();
			} else {
				spin = ProgressDialog.show(
						ChatPage.this,
						getResources().getString(
								R.string.connexion_progress_title),
						getResources().getString(
								R.string.connecsion_progress_body));
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String message = params[0];

			InputStream content = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				StringBuilder urlBuilder = new StringBuilder();
				urlBuilder
						.append("http://dev.loicortola.com/parlez-vous-android/message/")
						.append(login).append("/").append(password).append("/")
						.append(message);	
				HttpResponse response = httpclient.execute(new HttpGet(
						urlBuilder.toString()));
				content = response.getEntity().getContent();
				String res = InputStreamToString.convert(content);
				Log.i("TOTO", "coucou = " + res);
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
			spin.hide();
			if (result) {
				GetMessagesOperation task = new GetMessagesOperation();
				task.execute();
			} else {
				Toast toast = Toast.makeText(ChatPage.this, getResources()
						.getString(R.string.send_failed), 2);
				toast.show();
			}
		}
	}

	private class GetMessagesOperation extends
			android.os.AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			InputStream content = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				StringBuilder urlBuilder = new StringBuilder();
				urlBuilder
						.append("http://dev.loicortola.com/parlez-vous-android/messages/")
						.append(login).append("/").append(password);
				HttpResponse response = httpclient.execute(new HttpGet(
						urlBuilder.toString()));
				Log.i("TOTO", "titi = "+ urlBuilder.toString());
				content = response.getEntity().getContent();
				String res = InputStreamToString.convert(content);
				Log.i("TOTO", "messages = "+res);
				return res;
			} catch (Exception e) {
				Log.i("[GET REQUEST]", "Network exception");
				return null;
			}
		}
	}
}
