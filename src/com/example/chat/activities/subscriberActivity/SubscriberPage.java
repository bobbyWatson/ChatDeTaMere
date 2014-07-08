package com.example.chat.activities.subscriberActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.example.chat.R;
import com.example.chat.tools.anims.Anims;
import com.example.chat.tools.streamToStringClass.InputStreamToString;

public class SubscriberPage extends ActionBarActivity {

	public static final String MyPREFERENCES = "chatPrefs";

	private EditText loginField;
	private EditText passwordField;
	private Button cancelBtn;
	private Button subscribeBtn;
	private Button retourBtn;
	private ProgressDialog spin;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_subscriber);
		Intent intent = getIntent();
		prefs = getSharedPreferences(MyPREFERENCES, 0);
		loginField = (EditText) findViewById(R.id.login_form);
		passwordField = (EditText) findViewById(R.id.password_form);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		subscribeBtn = (Button) findViewById(R.id.subscribe_btn);
		retourBtn = (Button) findViewById(R.id.retour_btn);
//		
		cancelBtn.setOnClickListener(cancelHandler);
		subscribeBtn.setOnClickListener(submitHandler);
		
		passwordField.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_SEND) {
		        	subscribeBtn.performClick();
		            handled = true;
		        }
		        return handled;
		    }
		});
		
		retourBtn.setOnClickListener(returnHandler);
		
		prefs = getSharedPreferences(MyPREFERENCES, 0);
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

	private View.OnClickListener cancelHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			loginField.setText("");
			passwordField.setText("");
		}
	};
	
	private View.OnClickListener returnHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	private View.OnClickListener submitHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (loginField.getText().toString().equals("")
					|| passwordField.getText().toString().equals("")) {
				if(loginField.getText().toString().equals("")){
					Anims.ShakeError(SubscriberPage.this,loginField);
				}
				if(passwordField.getText().toString().equals("")){
					Anims.ShakeError(SubscriberPage.this,passwordField);
				}
				Toast toast = Toast.makeText(SubscriberPage.this, getResources()
						.getString(R.string.forms_empty), 2);
				toast.show();
			} else {
				AskConnectTask task = new AskConnectTask();
				task.execute(loginField.getText().toString(), passwordField
						.getText().toString());
			}
		}
	};

	private class AskConnectTask extends
			android.os.AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (spin != null) {
				spin.show();
			} else {
				spin = ProgressDialog.show(
						SubscriberPage.this,
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
			try {
				HttpClient httpclient = new DefaultHttpClient();
				StringBuilder urlBuilder = new StringBuilder();
				urlBuilder
						.append("http://dev.loicortola.com/parlez-vous-android/subscribe");
				HttpPost httppost= new HttpPost(urlBuilder.toString());
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("username", login));
		        nameValuePairs.add(new BasicNameValuePair("password", password));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        Log.i("[post REQUEST]",httppost.toString());
		        HttpResponse response = httpclient.execute(httppost);
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
			spin.hide();
			
			Log.i("subscriber", result.toString());
			if (result) {
				
				String login;
				String password;
				if (!loginField.getText().toString().equals("")
						&& !passwordField.getText().toString().equals("")) {
					login = loginField.getText().toString();
					password = passwordField.getText().toString();
					Editor editor = prefs.edit();
					editor.putString("LOGIN", login);
					editor.putString("PASSWORD", password);
					editor.commit();
				} else {
					login = prefs.getString("LOGIN", "");
					password = prefs.getString("PASSWORD", "");
				}
//				Intent intent = new Intent(SubscriberPage.this, ChatPage.class);
//				intent.putExtra("LOGIN", login);
//				intent.putExtra("PASSWORD", password);
//				startActivity(intent);
			} else {
				Toast toast = Toast.makeText(SubscriberPage.this, getResources()
						.getString(R.string.subscriber),10);
				toast.show();
			}
		}
	}
}
