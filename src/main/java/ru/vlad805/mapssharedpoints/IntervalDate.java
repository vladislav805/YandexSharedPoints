package ru.vlad805.mapssharedpoints;

public class IntervalDate {
	private int interval;
	public IntervalDate (int i) {
		interval = i;
	}
	public int getDays () {
		return interval / 60 / 60 / 24;
	}
	public int getHours () {
		return interval / 60 / 60;
	}
	public int getMinutes () {
		return interval / 60 % 60;
	}
}