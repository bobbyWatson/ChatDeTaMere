package com.example.chat;

import android.R.string;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class ChatPage extends ActionBarActivity {

	private SharedPreferences prefs;
	private string login;
	private string password;
	private TextView welcomeName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_page);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_page, menu);

		Intent intent = getIntent();
		welcomeName = (TextView) findViewById(R.id.welcome_name);

		if (intent != null) {
			welcomeName.setText(welcomeName.getText().toString() + " "
					+ intent.getStringExtra("LOGIN"));
		}
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

}
