package ru.vlad805.mapssharedpoints;

import android.app.ProgressDialog;
import android.content.Context;

public class XProgressDialog extends ProgressDialog {

	private void init () {
		setProgressStyle(ProgressDialog.STYLE_SPINNER);
		setCancelable(false);
	}

	public XProgressDialog(Context context) {
		super(context);
		init();
	}

	public XProgressDialog (Context context, String text) {
		super(context);
		init();
		setMessage(text);
	}

	public XProgressDialog (Context context, int text) {
		super(context);
		init();
		setMessage(context.getString(text));
	}
	public XProgressDialog (Context context, String title, String text) {
		super(context);
		init();
		if (title != null)
			setTitle(title);
		setMessage(text);
	}

	public XProgressDialog setCloseable (boolean isCloseable) {
		setCancelable(isCloseable);
		return this;
	}

	public XProgressDialog open () {
		show();
		return this;
	}

	public void close () {
		cancel();
	}
}
