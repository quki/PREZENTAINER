package com.puregodic.android.prezentainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
	// participate
	Button homeStartBtn, homeLoadBtn;
	TextView mEmailView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mEmailView = (TextView)findViewById(R.id.mEmailView);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
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
	
	// Click Event Handler Call Back
	public void myOnClick(View v) {
		switch (v.getId()) {
		case R.id.homeStartBtn: {
			Intent i = new Intent(HomeActivity.this, SettingActivity.class);
			startActivity(i);
			break;
		}
		case R.id.homeLoadBtn: {
			break;
		}

		}

	}

}
