package com.puregodic.android.prezentainer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.puregodic.android.prezentainer.connectpchelper.ConnecToPcHelper;
import com.puregodic.android.prezentainer.service.AccessoryService;

public class SettingActivity extends AppCompatActivity {

	private AccessoryService mAccessoryService = null;
	private Boolean isBound = false;
	ConnecToPcHelper mConnecToPcHelper;
	Button startBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		startBtn = (Button)findViewById(R.id.startBtn);
		startBtn.setEnabled(false);
		// Bind Service
		doBindService();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
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
		// 시작하기
		case R.id.connectToGearBtn: {
			startConnection();
			break;
		}
		case R.id.connectToPcBtn: {

			mConnecToPcHelper = new ConnecToPcHelper();
			mConnecToPcHelper.enabledBluetoothAdapter();
			break;
		}
		case R.id.startBtn: {
			Intent intent = new Intent(SettingActivity.this,
					StartActivity.class);
			startActivity(intent);
			break;
		}

		}

	}

	private void doBindService() {

		Intent intent = new Intent(SettingActivity.this, AccessoryService.class); // Action
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

	}

	private void startConnection() {
		if (isBound == true && mAccessoryService != null) {
			mAccessoryService.findPeers();
		}
	}

	// ServiceConnection Interface
	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mAccessoryService = ((AccessoryService.MyBinder) service)
					.getService();
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mAccessoryService = null;
			isBound = false;
		}
	};
}
