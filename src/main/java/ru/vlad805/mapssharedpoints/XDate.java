package ru.vlad805.mapssharedpoints;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class XDate {

	final public static String DATE_TIME = "dd/MM/yyyy HH:mm:ss";
	final public static String DATE = "dd/MM/yyyy";
	final public static String TIME = "HH:mm:ss";
	final public static String TIME_SHORT = "HH:mm";

	static String get (long date) {
		return get(date, DATE_TIME);
	}

	static String get (long date, String template) {
		return new SimpleDateFormat(template, Locale.getDefault()).format(new Date(date * 1000));
	}

	static IntervalDate interval (int date) {
		int now = (int) (long) (System.currentTimeMillis() / 1000);
		int interval = now - date;
		return new IntervalDate(interval);
	}
}
