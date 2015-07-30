package ru.vlad805.mapssharedpoints;

import android.content.Context;

public class Settings {
	private boolean isNight = false;
	private boolean isTraffic = false;
	private boolean isMyLocation = true;
	private boolean isHD = true;
	private int trackerInterval = 10;
	private Context context;
	
	public Settings (Context context) {
		this.context = context;
		isNight = Utils.getBoolean(context, Const.Settings.IS_NIGHT);
		isTraffic = Utils.getBoolean(context, Const.Settings.IS_TRAFFIC);
		isMyLocation = Utils.getBoolean(context, Const.Settings.IS_LOCATION);
		isHD = Utils.getBoolean(context, Const.Settings.IS_HD);
		trackerInterval = Utils.getInt(context, Const.Settings.TRACKER_INTERVAL);
	}

	public Settings setNight (boolean state) {
		isNight = state;
		reWrite();
		return this;
	}

	public Settings setTraffic (boolean state) {
		isTraffic = state;
		reWrite();
		return this;
	}

	public Settings setMyLocation (boolean state) {
		isMyLocation = state;
		reWrite();
		return this;
	}

	public Settings setHD (boolean state) {
		isHD = state;
		reWrite();
		return this;
	}

	public Settings setTrackerInterval (int interval) {
		trackerInterval = interval;
		reWrite();
		return this;
	}
	
	public boolean getIsNight () {return isNight;}
	public boolean getIsTraffic () {return isTraffic;}
	public boolean getIsMyLocation () {return isMyLocation;}
	public boolean getIsHD () {return isHD;}
	public int getTrackerInterval () {return trackerInterval;}

	public Settings reWrite () {
		Utils.setBoolean(context, Const.Settings.IS_NIGHT, isNight);
		Utils.setBoolean(context, Const.Settings.IS_TRAFFIC, isTraffic);
		Utils.setBoolean(context, Const.Settings.IS_LOCATION, isMyLocation);
		Utils.setBoolean(context, Const.Settings.IS_HD, isHD);
		Utils.setInt(context, Const.Settings.TRACKER_INTERVAL, trackerInterval);
		return this;
	}
}
