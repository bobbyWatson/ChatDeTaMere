package com.example.chat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatPage extends ActionBarActivity {

	private SharedPreferences prefs;
	private String login;
	private String password;
	private TextView welcomeName;
	private ProgressDialog spin;
	private EditText messageZone;
	private ListView list;
	private Button sendButton;
	private Button logoutBtn;

	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_page);

		list = (ListView) findViewById(R.id.list);
		messageZone = (EditText) findViewById(R.id.message_zone);

		logoutBtn = (Button) findViewById(R.id.logout_btn);
		sendButton = (Button) findViewById(R.id.send_message);
		
		logoutBtn.setOnClickListener(logoutHandler);
		sendButton.setOnClickListener(sendMessageHandler);

		Intent intent = getIntent();
		welcomeName = (TextView) findViewById(R.id.welcome_name);
		if (intent != null) {
			login = intent.getStringExtra("LOGIN");	
			password = intent.getStringExtra("PASSWORD");
			welcomeName.setText(welcomeName.getText().toString() + " " + login);
		}
		
		//adapter = new ChatInfoAdapter(this, testStrings, R.id.message);
		adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.message ,new ArrayList<String>());
		list.setAdapter(adapter);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new GetMessageTask(), 0L, 1000L);
	
	}
	private class GetMessageTask extends TimerTask{
		public void run() {
			GetMessagesOperation getTask = new GetMessagesOperation();
			getTask.execute();
		}
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

	private View.OnClickListener logoutHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			prefs = getSharedPreferences(MainActivity.MyPREFERENCES, 0);
			Editor editor = prefs.edit();
			editor.putString("LOGIN", "");
			editor.putString("PASSWORD", "");
			editor.commit();
			Intent intent = new Intent(ChatPage.this, MainActivity.class);
			startActivity(intent);
		}
	};
	
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
						.append("/")
						.append(login).append("/").append(password).append("/")
						.append(message);
				HttpResponse response = httpclient.execute(new HttpGet(
						urlBuilder.toString()));
				content = response.getEntity().getContent();
				String res = InputStreamToString.convert(content);
				if (Boolean.valueOf(res)) {
					return true;
				}
			} catch (Exception e) {
				Log.i("[GET REQUEST]", e.toString());
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Log.i("TITI", "coucou "+result.toString());
			spin.hide();
			Log.i("POST EXECUTE",result.toString());
			if (result) {
				GetMessagesOperation task = new GetMessagesOperation();
				task.execute();
			}
			GetMessagesOperation task = new GetMessagesOperation();
			task.execute();
			messageZone.setText("");
			
			/*if (result) {
				
			 else {
				Toast toast = Toast.makeText(ChatPage.this, getResources()
						.getString(R.string.send_failed), 2);
				toast.show();
			}*/
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
				content = response.getEntity().getContent();
				String res = InputStreamToString.convert(content);
				return res;
			} catch (Exception e) {
				Log.i("[GET REQUEST]", "Network exception");
				return null;
			}
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			String[] messages = result.split(";");
			for(int i = adapter.getCount(); i < messages.length; i++){
				String[] messageCuted = messages[i].split(":");
				StringBuilder builder = new StringBuilder();
				builder.append(messageCuted[0]).append(" said : ").append(messageCuted[1]);
				adapter. add(builder.toString());
			}
		}
	}
}
