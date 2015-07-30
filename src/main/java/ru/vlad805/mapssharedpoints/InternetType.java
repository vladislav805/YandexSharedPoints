package ru.vlad805.mapssharedpoints;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetType {
	public enum Type {
		TYPE_WIFI,
		TYPE_2G,
		TYPE_3G,
		TYPE_4G,
		TYPE_UNKNOWN
	}

	private Context context;

	public InternetType (Context context) {
		this.context = context;
	}

	private boolean isNetworkAvailable (NetworkInfo network) {
		return network != null && network.isAvailable() && network.isConnected();
	}

	public Type getType () {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Wifi
		NetworkInfo network = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (isNetworkAvailable(network)) {
			return Type.TYPE_WIFI;
		}

		// 2G/3G
		network = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (isNetworkAvailable(network)) {
			String type = network.getTypeName().toLowerCase();
			if (type.equals("gsm") || type.equals("gprs") || type.equals("edge"))
				return Type.TYPE_2G;
			return Type.TYPE_3G;
		}

		// 4G
		network = cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
		if (isNetworkAvailable(network)) {
			return Type.TYPE_4G;
		}
		return Type.TYPE_UNKNOWN;
	}

	public String getTypeName () {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		String type;
		if (networkInfo != null && networkInfo.isConnected()) {
			type = networkInfo.getTypeName().toUpperCase();
			if (type.equals("MOBILE")) {
				return networkInfo.getSubtypeName().toUpperCase();
			}
			return type;
		}
		return "";
	}
}
