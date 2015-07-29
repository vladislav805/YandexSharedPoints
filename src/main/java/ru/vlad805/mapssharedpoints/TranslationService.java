package ru.vlad805.mapssharedpoints;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import mjson.Json;

public class TranslationService extends Service implements LocationListener {

	private static final int notificationId = 100;
	private Notification.Builder notification;
	private TranslationItem current;
	private Settings settings;
	static StateService mState = StateService.IDLE;
	private Handler task;

	static StateService getState () {
		return mState;
	}

	public TranslationService getContext () {
		return this;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}


	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mState == StateService.IDLE) {
			mState = StateService.RUNNING;
Log.e("ONSTARTCOMMAND", mState.toString());
			settings = new Settings(this);
			current = new TranslationItem();

			initLocationListener();
			initBatteryListener();
			createNotification();
			startTimer();
		} else {
			stopSelf();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(onBatteryChanged);

		stopForeground(true);
		stopSelf();
		mState = StateService.IDLE;
	}

	private void startTimer () {
		task = new Handler();
		task.postDelayed(taskSend, 0);
	}

	private Runnable taskSend = new Runnable() {
		@Override
		public void run() {
			if (mState == StateService.IDLE) {
				task = null;
				return;
			}
			send();
			task.postDelayed(taskSend, settings.getTrackerInterval() * 1000);
		}
	};

	private void send () {
		HashMap<String, String> params = new HashMap<>();

		params.put(Const.LATITUDE, String.valueOf(current.latitude));
		params.put(Const.LONGITUDE, String.valueOf(current.longitude));
		params.put(Const.ACCURACY, String.valueOf(current.accuracy));
		params.put(Const.ALTITUDE, String.valueOf(current.altitude));
		params.put(Const.SPEED, String.valueOf(current.speed));
		params.put(Const.BATTERY, String.valueOf(current.battery));
		params.put(Const.BEARING, String.valueOf(current.bearing));
		params.put(Const.TEMPERATURE, String.valueOf(current.temperature));
		params.put(Const.PROVIDER, current.provider);
		params.put(Const.TIME, String.valueOf(current.time));
		params.put(Const.TIMEOUT, String.valueOf(current.timeout));

		API.invokeAPIMethod(this, Const.API.setPlace, params, new APICallback() {
			@Override
			public void onResult(Json result) {
				onSent();
			}

			@Override
			public void onError(APIError e) {

			}
		});
	}

	private void onSent () {
		sendNotification();
	}

	private void sendNotification () {
		notification
				.setContentTitle(getString(R.string.translation_notification_title))
				.setContentText(getStatusDate());
		Notification n = notification.build();
		n.flags |= Notification.FLAG_FOREGROUND_SERVICE;
		startForeground(notificationId, n);
	}

	private String getStatusDate () {
		return getString(R.string.translation_notification_last_request) +  " " + XDate.get(System.currentTimeMillis() / 1000, XDate.TIME);
	}

	private void createNotification () {
		Intent notificationIntent = new Intent(this, TranslationActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

		notification = new Notification.Builder(this)
				.setContentIntent(contentIntent)
				.setContentTitle(getString(R.string.app_name))
				.setSmallIcon(R.drawable.point)
				.setWhen(System.currentTimeMillis());

		sendNotification();
	}

	private void initLocationListener () {
		((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	private void initBatteryListener () {
		registerReceiver(onBatteryChanged, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}
	private BroadcastReceiver onBatteryChanged = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			current.battery = i.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			current.temperature = (float) i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10;
		}
	};

	@Override
	public void onLocationChanged(Location location) {
		current.latitude = location.getLatitude();
		current.longitude = location.getLongitude();
		current.accuracy = location.getAccuracy();
		current.altitude = location.getAltitude();
		current.bearing = location.getBearing();
		current.speed = location.getSpeed();
		current.provider = location.getProvider();
		current.time = System.currentTimeMillis() / 1000;
		current.timeout = settings.getTrackerInterval();
		if (settings.getTrackerInterval() == Const.Settings.SEND_AS_LOCATION) {
			send();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		current.provider = provider;
	}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onProviderDisabled(String provider) {}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
