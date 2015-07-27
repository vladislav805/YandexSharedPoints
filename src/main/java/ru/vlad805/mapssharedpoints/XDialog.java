package ru.vlad805.mapssharedpoints;

import android.app.AlertDialog;
import android.content.Context;

public class XDialog extends AlertDialog.Builder {
	public XDialog (Context context) {
		super(context);
	}

	public XDialog (Context context, String title) {
		super(context);
		setTitle(title);
	}

	public XDialog (Context context, String title, String text) {
		super(context);
		setTitle(title);
		setMessage(text);
	}

	public XDialog (Context context, int title) {
		super(context);
		setTitle(title);
	}

	public XDialog (Context context, int title, int text) {
		super(context);
		setTitle(title);
		setMessage(text);
	}

	public XDialog open () {
		create();
		show();
		return this;
	}
}
