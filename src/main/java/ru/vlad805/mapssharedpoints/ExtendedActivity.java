package ru.vlad805.mapssharedpoints;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import mjson.Json;

public class ExtendedActivity extends AppCompatActivity {

	public  APIError APIError;
	public XProgressDialog progress;

	public int getUserId () {
		return Utils.has(this, Const.USER_ID) ? Utils.getInt(this, Const.USER_ID) : -1;
	}

	public User getUser () {
		String s = Utils.getString(this, Const.SHARED_USER);
		return new User(Json.read(s));
	}

	public Context getActivity () {
		return this;
	}
	public Runnable noInternet = new Runnable () {
		@Override
		public void run () {
			Utils.alert(getActivity(), "Ошибка", "У Вас соединение с Интернетом, случаем, не пропало?").show();
		}
	};

	public Runnable showAPIError = new Runnable() {
		@Override
		public void run() {
			showAPIError(APIError);
		}
	};
	public void showAPIError (APIError e) {
		Utils.alert(getActivity(), "Ошибка #" + e.getErrorId(), e.getErrorInfo()).show();
	}

	public void setProgressDialog (int text) {
		setProgressDialog(getString(text));
	}

	public void setProgressDialog (int title, int text) {
		setProgressDialog(getString(title), getString(text));
	}

	public void setProgressDialog (String text) {
		setProgressDialog(null, text);
	}

	public void setProgressDialog (String title, String text) {
		if (progress != null) {
			closeProgressDialog();
		}
		progress = new XProgressDialog(getActivity(), title, text).open();
	}

	public void closeProgressDialog () {
		progress.close();
		progress = null;
	}

	public boolean onOptionsItemSelected (MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void setStatusBarTranslucent(boolean makeTranslucent) {
		if (Build.VERSION.SDK_INT >= 19) {
			if (makeTranslucent) {
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			} else {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			}
		}
	}

	protected void hideActionBar () {
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.hide();
		}
	}

	public ArrayList<Point> getPoints () {
		return Point.parse(Json.read(Utils.getString(getActivity(), Const.SHARED_DATA)).asJsonList());
	}

	public void addBackButton () {
		ActionBar ab = getSupportActionBar();
		if (ab != null)
			ab.setDisplayHomeAsUpEnabled(true);
	}
}
