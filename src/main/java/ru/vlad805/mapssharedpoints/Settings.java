package ru.vlad805.mapssharedpoints;

import android.content.Context;

public class Settings {
	private boolean isNight = false;
	private boolean isTraffic = false;
	private boolean isMyLocation = true;
	private boolean isHD = true;
	private Context context;
	
	public Settings (Context context) {
		this.context = context;
		this.isNight = Utils.getBoolean(context, Const.Settings.IS_NIGHT);
		this.isTraffic = Utils.getBoolean(context, Const.Settings.IS_TRAFFIC);
		this.isMyLocation = Utils.getBoolean(context, Const.Settings.IS_LOCATION);
		this.isHD = Utils.getBoolean(context, Const.Settings.IS_HD);
	}
	public Settings setNight (boolean state) {
		this.isNight = state;
		this.reWrite();
		return this;
	}
	public Settings setTraffic (boolean state) {
		this.isTraffic = state;
		this.reWrite();
		return this;
	}
	public Settings setMyLocation (boolean state) {
		this.isMyLocation = state;
		this.reWrite();
		return this;
	}
	public Settings setHD (boolean state) {
		this.isHD = state;
		this.reWrite();
		return this;
	}
	
	public boolean getIsNight () {return this.isNight;}
	public boolean getIsTraffic () {return this.isTraffic;}
	public boolean getIsMyLocation () {return this.isMyLocation;}
	public boolean getIsHD () {return this.isHD;}

	public Settings reWrite () {
		Utils.setBoolean(context, Const.Settings.IS_NIGHT, this.isNight);
		Utils.setBoolean(context, Const.Settings.IS_TRAFFIC, this.isTraffic);
		Utils.setBoolean(context, Const.Settings.IS_LOCATION, this.isMyLocation);
		Utils.setBoolean(context, Const.Settings.IS_HD, this.isHD);
		return this;
	}
}
