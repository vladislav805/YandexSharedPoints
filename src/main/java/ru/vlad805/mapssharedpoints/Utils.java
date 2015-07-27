package ru.vlad805.mapssharedpoints;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.Toast;

public class Utils {

	public static SharedPreferences getSettings (Context ctx) {
		return ctx.getSharedPreferences(Const.SHARED_PREFERENCES, Context.MODE_PRIVATE);
	}

	public static String getString (Context ctx, String key) {
		return getSettings(ctx).getString(key, "");
	}

	public static boolean has (Context ctx, String key) {
		return getSettings(ctx).contains(key);
	}

	public static boolean getBoolean (Context ctx, String name) {
		return getSettings(ctx).getBoolean(name, false);
	}

	public static void setString (Context ctx, String key, String value) {
		getSettings(ctx).edit().putString(key, value).apply();
	}

	public static void setInt (Context ctx, String key, int value) {
		getSettings(ctx).edit().putInt(key, value).apply();
	}

	public static void removeData (Context ctx, String key) {
		getSettings(ctx).edit().remove(key).apply();
	}

	public static int getInt (Context ctx, String key) {
		return getSettings(ctx).getInt(key, 0);
	}

	public static void setBoolean (Context ctx, String name, boolean value) {
		getSettings(ctx).edit().putBoolean(name, value).apply();
	}

	public static Toast toast (Context context, int text) {
		return toast(context, context.getString(text));
	}

	public static Toast toast (Context ctx, String text) {
		Toast toast = Toast.makeText(ctx, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 75);
		toast.show();
		return toast;
	}

	public static AlertDialog.Builder alert (Context context, int title, String text) {
		return alert(context, title != 0 ? context.getString(title) : null, text);
	}

	public static AlertDialog.Builder alert (Context context, int title, int text) {
		return alert(context, title != 0 ? context.getString(title) : null, context.getString(text));
	}

	public static AlertDialog.Builder alert (Context ctx, String title, String text) {
		AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
		if (title != null)
			alert.setTitle(title);
		if (text != null)
			alert.setMessage(text);
		return alert;
	}

	public static ProgressDialog progress (Context ctx, String title, String text) {
		ProgressDialog dialog = new ProgressDialog(ctx);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if (title != null)
			dialog.setTitle(title);
		dialog.setMessage(text);
		dialog.show();
		return dialog;
	}

	static String getAccessToken (Context context) {
		return has(context, Const.USER_ACCESS_TOKEN) ? getString(context, Const.USER_ACCESS_TOKEN) : "";
	}

	static boolean isAuthorized (Context ctx) {
		return !getAccessToken(ctx).isEmpty();
	}

	static String textCase (String[] t, int i) {
		return t[((i % 100 > 4 && i % 100 < 20) ? 2 : new int[]{2, 0, 1, 1, 1, 2}[(i % 10 < 5) ? i % 10 : 5])];
	}
}
