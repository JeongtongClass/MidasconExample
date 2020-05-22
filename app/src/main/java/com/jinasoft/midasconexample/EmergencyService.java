package com.jinasoft.midasconexample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.hanvitsi.midascon.Beacon;
import com.hanvitsi.midascon.EmergencyCallback;
import com.hanvitsi.midascon.MidasApplication;
import com.hanvitsi.midascon.manager.ContextManager;

public class EmergencyService extends Service implements EmergencyCallback {

	public static final String TAG = EmergencyService.class.getSimpleName();
	public static final String ACTION_STATUS = TAG + ".ACTION_STATUS";

	private String CHANNEL_NAME = "High priority channel";
	private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;

	static boolean run;

	// AndroidManifest.xml에 설정된 name 클래스 호출
	public MidasApplication getMidasApplication() {
		return (MidasApplication) getApplication();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private BluetoothAdapter adapter;
	private ContextManager contextManager;

	@Override
	public void onCreate() {
		super.onCreate();

		run = true;

		adapter = BluetoothAdapter.getDefaultAdapter();
		contextManager = getMidasApplication().getContextManager();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		sendStatus(true);

		// 응급신호 콜백 등록
		contextManager.setEmergencyCallback(this);

		if (adapter.isEnabled()) {
			contextManager.stopLeScan();
			contextManager.startLeScan();

		} else {
			contextManager.stopLeScan();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (contextManager != null)
			contextManager.stopLeScan();
		run = false;

		sendStatus(false);
	}

	@Override
	public void onEmergencyCallback(int status, Beacon beacon) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			showNotification(beacon);
		}
	}

	private void sendStatus(boolean status) {
		Intent intent = new Intent(ACTION_STATUS);
		intent.putExtra("status", status);
		sendBroadcast(intent);
	}

	// 응급신호 알림
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
		intent.putExtra("title", "응급 신호 발생");
		intent.putExtra("message", beacon.getId());
		intent.putExtra("notify", notify);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID);
		builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), notify, intent, PendingIntent.FLAG_UPDATE_CURRENT));

		builder.setPriority(NotificationCompat.PRIORITY_HIGH);

		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setTicker("응급 신호 발생");
		builder.setContentTitle(beacon.getId());
		builder.setContentText(beacon.getId() + " 응급 신호 발생");

		builder.setAutoCancel(true);
		builder.setDefaults(NotificationCompat.DEFAULT_ALL);

		NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
		style.bigText(beacon.getId() + " 응급 신호 발생");
		style.setBigContentTitle(" 응급 신호 발생");
		style.setSummaryText(getString(R.string.app_name));

		manager.notify(notify, style.build());
	}


}
