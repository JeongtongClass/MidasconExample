package com.jinasoft.midasconexample;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hanvitsi.midascon.Beacon;

public class MainActivity extends Activity {

	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
	private BroadcastReceiver receiver;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//findViewById(R.id.start).setOnClickListener(this);
		//findViewById(R.id.stop).setOnClickListener(this);

		EmergencyService.run=true;

		startService(new Intent(getApplicationContext(), EmergencyService.class));
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			receiver = null;
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			}
		} else {
			//checkButton();

			if (receiver == null)
				receiver = new BroadcastReceiver() {

					@Override
					public void onReceive(Context context, Intent intent) {
						//checkButton();
					}
				};

			registerReceiver(receiver, new IntentFilter(EmergencyService.ACTION_STATUS));
		}

	}

	@Override
	protected void onPause() {
		super.onPause();

		startService(new Intent(getApplicationContext(), EmergencyService.class));


		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			receiver = null;
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			}
		} else {
			//checkButton();

			if (receiver == null)
				receiver = new BroadcastReceiver() {

					@Override
					public void onReceive(Context context, Intent intent) {
						//checkButton();
					}
				};

			registerReceiver(receiver, new IntentFilter(EmergencyService.ACTION_STATUS));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		startService(new Intent(getApplicationContext(), EmergencyService.class));

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			receiver = null;
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			}
		} else {
			//checkButton();

			if (receiver == null)
				receiver = new BroadcastReceiver() {

					@Override
					public void onReceive(Context context, Intent intent) {
						//checkButton();
					}
				};

			registerReceiver(receiver, new IntentFilter(EmergencyService.ACTION_STATUS));
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		startService(new Intent(getApplicationContext(), EmergencyService.class));

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			receiver = null;
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			}
		} else {
			//checkButton();

			if (receiver == null)
				receiver = new BroadcastReceiver() {

					@Override
					public void onReceive(Context context, Intent intent) {
						//checkButton();
					}
				};

			registerReceiver(receiver, new IntentFilter(EmergencyService.ACTION_STATUS));
		}
	}

	/*private void checkButton() {
		if (EmergencyService.run) {
			findViewById(R.id.start).setEnabled(true);
			findViewById(R.id.stop).setEnabled(false);
		} else {
			findViewById(R.id.start).setEnabled(false);
			findViewById(R.id.stop).setEnabled(true);
		}
	}/*

	/*@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			if (BluetoothAdapter.getDefaultAdapter().isEnabled() == false) {
				//블루투스 꺼져있으면
				Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
				startActivity(settingsIntent);
				//설정-블루투스로 들어감
				return;
			}

			startService(new Intent(getApplicationContext(), EmergencyService.class));
			break;

		case R.id.stop:
			stopService(new Intent(getApplicationContext(), EmergencyService.class));
			break;

		default:
			break;
		}
		checkButton();
	}*/

}
