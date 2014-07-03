package com.example.chat.chatActivity;

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
import biz.source_code.base64Coder.Base64Coder;

import com.example.chat.R;
import com.example.chat.anims.Anims;
import com.example.chat.customArrayAdapterClass.CustomArrayAdapter;
import com.example.chat.mainActivity.MainActivity;
import com.example.chat.soundClass.SoundManager;
import com.example.chat.streamToStringClass.InputStreamToString;

public class ChatPage extends ActionBarActivity {
	// Declare every private variables we will need
	private SharedPreferences prefs;
	private String login;
	private String password;
	private TextView welcomeName;
	private ProgressDialog spin;
	private EditText messageZone;
	private ListView list;
	private Button sendButton;
	private Button logoutBtn;
	private Boolean isAnimationRun;
	private ArrayAdapter<String> adapter;
	private SoundManager songs;
	/**
	 * Init of the current activity and her view
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the view associated to this activity
		setContentView(R.layout.activity_chat_page);
		// Get all field and buttons from the view
		isAnimationRun = false;
		list = (ListView) findViewById(R.id.list);
		messageZone = (EditText) findViewById(R.id.message_zone);
		
		songs = new SoundManager(getApplicationContext());
		songs.tableCompare.put("bigfart",1);
		songs.tableCompare.put("wetfart",2);
		songs.tableCompare.put("autority",3);
		songs.tableCompare.put("thibmom",4);
		
		welcomeName = (TextView) findViewById(R.id.welcome_name);
		
		logoutBtn = (Button) findViewById(R.id.logout_btn);
		sendButton = (Button) findViewById(R.id.send_message);
		// Add events listeners on every buttons
		logoutBtn.setOnClickListener(logoutHandler);
		sendButton.setOnClickListener(sendMessageHandler);
				
		// Init intent saved before and test it for get all data inside of it.
		Intent intent = getIntent();
		if (intent != null) {
			login = intent.getStringExtra("LOGIN");
			password = intent.getStringExtra("PASSWORD");
			// Init header label with our login field value
			welcomeName.setText(welcomeName.getText().toString() + " " + login);
		}
		
		// adapter = new ChatInfoAdapter(this, testStrings, R.id.message);
		adapter = new CustomArrayAdapter(getApplicationContext(), R.layout.message ,new ArrayList<String>());
		list.setAdapter(adapter);
		// Set new interval timer for call every X seconde one specific function
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new GetMessageTask(), 0L, 2000L);
	
	}
	/**
	 *  task wrapper to execute a update of chat messages.
	 */
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
	/**
	 * Log out button listener callback function , for logout from the chat 
	 */
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
	/**
	 * send message button listener callback  
	 */
	private View.OnClickListener sendMessageHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// Test if messageZone field is empty			
			if (messageZone.getText().toString().equals("")) {
				// display toast with error message
				Toast toast = Toast.makeText(ChatPage.this, getResources()
						.getString(R.string.message_empty), 2);
				toast.show();
				// Start animation error (shaking) the field.
				Anims.ShakeError(ChatPage.this, messageZone);
			} else {
				// start send of the current message
				SendMessageOperation task = new SendMessageOperation();
				task.execute(messageZone.getText().toString());
				
			}
		}
	};

	/**
	 * private class for create and send message on chat
	 */
	private class SendMessageOperation extends
			android.os.AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Show or set up spinning loader 
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
			// Transform current message on base64			
			String message =  Base64Coder.encodeString(params[0]);
			
			InputStream content = null;
			// Try catch for send message to others user on chat api			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				// Init url request.
				StringBuilder urlBuilder = new StringBuilder();
				// Setting url request and params.
				urlBuilder
						.append("http://dev.loicortola.com/parlez-vous-android/message/")
						.append(login).append("/").append(password).append("/")
						.append(message);
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
				Log.i("[GET REQUEST]", e.toString());
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// Hide spinning loader 
			spin.hide();
			// Start à update of post messages on chat		
			GetMessagesOperation task = new GetMessagesOperation();
			task.execute();
			// Empty text field for next message
			messageZone.setText("");
		}
	}
	
	/**
	 * Method for match every :text: and test execute them if methods are associate
	 * @param args String To Test
	 * @param index Int current message index from all messages
	 * @param messageslength Int messages Total length
	 * @param from String user who send this message
	 * @return String who was modified for hide every valide patterns
	 */
	private String patternEmoji(String args, int index, int messageslength, String from) {
			
			String txt = null;
			String tempString = args;
			
			String re1=":";	// Any Single Character 1
			int firstPoint = tempString.indexOf(re1);			
			int secondPoint = tempString.indexOf(":",firstPoint+1);
			
			if(firstPoint>-1 && secondPoint>-1  ){
				txt = tempString.substring(firstPoint+1, secondPoint);
				// find every :helpers and remove then;				
				args = args.replace(":thibmom:", "! Song from south park !");
				args = args.replace(":bigfart:", "! Ca sent le gaz !");
				args = args.replace(":wetfart:", "! Change ton caleçon !");
				args = args.replace(":autority:", "! Respect mon autorité !");
				args = args.replace(":wizz:", "! WINK DANS TA FACE !");
				// start song if good helper is written
				if(!txt.contains(" ") && songs.tableCompare.containsKey(txt) && messageslength-1==index && !from.contentEquals(login) ){	
					songs.playSound(songs.tableCompare.get(txt));
				}
				// start vibration if good helper is written , wizz
				if(!txt.contains(" ") && txt.contentEquals("wizz")&& messageslength-1==index&& !from.contentEquals(login) ){	
						Anims.wizz(getApplicationContext());
				}
				
				return args;
			}
			else{
				return args;
			}
		    
	}
	
	/**
	 * class for get message from api on dev.loicortola.com server
	 */
	class GetMessagesOperation extends
			android.os.AsyncTask<Void, Void, String> {
		
		/**
		 * that function executed asynchronously make call and get data from message api on dev.loicortola.com server
		 */
		@Override
		protected String doInBackground(Void... params) {
			InputStream content = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				// Init url request.
				StringBuilder urlBuilder = new StringBuilder();
				// Setting url request and params.
				urlBuilder
						.append("http://dev.loicortola.com/parlez-vous-android/messages/")
						.append(login).append("/").append(password);
				// Execute http request and stock inside response var the return
				HttpResponse response = httpclient.execute(new HttpGet(
						urlBuilder.toString()));
				// Get data from response !! it's a data stream !!
				content = response.getEntity().getContent();
				// Transforms data stream to string
				String res = InputStreamToString.convert(content);
				return res;
			} catch (Exception e) {
				Log.i("[GET REQUEST]", "Network exception");
				return null;
			}
		}
		/**
		 * Function called at the end of the request, actually, she get all messages from chat 
		 * and parse them from base64 to string and display them.
		 */
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			String[] messages = result.split(";");
			// Loop on every messages
			for(int i = adapter.getCount(); i < messages.length; i++){
			
				// Split the current message to get message username and body message under base64
				String[] messageCuted= messages[i].split(":");
				// Test if array of strings containing both username and message.
				if(messageCuted.length==2){
					
					StringBuilder builder = new StringBuilder();
					// Try catch to decode message from base64.
					try {
						// Base64 message decoding
						String currentMessage=Base64Coder.decodeString(messageCuted[1]);
						
						// Pass our message inside our emoji pattern tester for transform string :wizz: and add interactions.						
						currentMessage=patternEmoji(currentMessage,i,messages.length,messageCuted[0]);
						
						// Append current decoded message to the list
						builder.append(messageCuted[0]).append(" said : ").append(currentMessage);
						// 
						adapter.add(builder.toString());
						// Set list scroll to the last message posted
						list.setSelection(adapter.getCount()-1);
					} catch (Exception e) {
						Log.d("BASE64 DECODE FAIL",messageCuted[1]+" "+e.toString());
					}
				}
			}
		}
	}
}

