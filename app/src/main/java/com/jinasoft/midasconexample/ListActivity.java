package com.jinasoft.midasconexample;

import com.hanvitsi.midascon.Beacon;
import com.hanvitsi.midascon.BeaconCallback;
import com.hanvitsi.midascon.MidasApplication;
import com.hanvitsi.midascon.manager.ContextManager;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class ListActivity extends Activity implements BeaconCallback, Runnable {

	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
	private ContextManager contextManager;
	private BeaconListAdapter adapter;

	private String CHANNEL_NAME = "High priority channel";
	private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);


		contextManager = getMidasApplication().getContextManager();
		contextManager.getBeaconSettings().setMidasScanMode(false);

		adapter = new BeaconListAdapter(getBaseContext());

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);

		startService(new Intent(getApplicationContext(), BeaconListAdapter.class));
	}

	@Override
	public void onBeaconCallback(int status, Beacon beacon) {
		switch (status) {
			case STATUS_CODE_ENTER:
			case STATUS_CODE_UPDATE:
				if (adapter != null)
					adapter.addBeacon(beacon);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					showNotification(beacon);
				}

				break;

			case STATUS_CODE_EXIT:
				if (adapter != null)
					adapter.removeBeacon(beacon);
				break;

			default:
				break;
		}

		runOnUiThread(this);

	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void showNotification(Beacon beacon) {
		//오레오 (API26)이상부터 채널을 추가해야 notification 사용 가능
		NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
		notificationChannel.enableLights(true);
		notificationChannel.enableVibration(true);
		notificationChannel.setDescription("this is the description of the channel.");
		notificationChannel.setLightColor(Color.RED);
		notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.createNotificationChannel(notificationChannel);

		if (beacon == null)
			return;
		int notify = beacon.getId().hashCode();
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("title", "비콘 들어옴");
		intent.putExtra("message", beacon.getId());
		intent.putExtra("notify", notify);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID);
		builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), notify, intent, PendingIntent.FLAG_UPDATE_CURRENT));

		builder.setPriority(NotificationCompat.PRIORITY_HIGH);

		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setTicker("비콘 들어옴");
		builder.setContentTitle(beacon.getId());
		builder.setContentText(beacon.getId() + " 비콘 들어옴");

		builder.setAutoCancel(true);
		builder.setDefaults(NotificationCompat.DEFAULT_ALL);

		NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
		style.bigText(beacon.getId() + " 비콘 들어옴");
		style.setBigContentTitle(" 비콘 들어옴");
		style.setSummaryText(getString(R.string.app_name));

		manager.notify(notify, style.build());
	}

	@Override
	public void run() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	// AndroidManifest.xml에 설정된 name 클래스 호출
	public MidasApplication getMidasApplication() {
		return (MidasApplication) getApplication();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			}
		} else {
			if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
				// 콜백 등록
				contextManager.setBeaconCallback(this);
				contextManager.startLeScan();
			} else {
				contextManager.stopLeScan();

				Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
				startActivity(settingsIntent);
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		contextManager.stopLeScan();
	}

}
