package ru.vlad805.mapssharedpoints;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import java.util.HashMap;

import mjson.Json;

public class AboutActivity extends ExtendedActivity {

	final public static String URL_SITE ="http://places.vlad805.ru/android-app";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

	public void checkUpdates (View v) {
		new Checker(this);
	}

	private class Checker {

		private int versionCode;
		private Context context;

		Checker (Context context) {
			this.context = context;
			try {
				versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			setProgressDialog(R.string.about_checking_updates);
			request();
		}

		private void request () {
			HashMap<String, String> params = new HashMap<>();
			params.put(Const.APPLICATION_ID, "7");
			params.put(Const.BUILD_ID, String.valueOf(versionCode));
			API.invokeAPIMethod(context, Const.API.checkUpdates, params, new APICallback() {
				@Override
				public void onResult(Json result) {
					showResult(new CheckerResult(result));
				}

				@Override
				public void onError(APIError e) {
					showAPIError(e);
				}
			});

		}
		private void showResult (final CheckerResult result) {
			closeProgressDialog();
			if (!result.isOld) {
				Utils.toast(context, String.format(getString(R.string.about_update_not_found), result.version, result.buildId));
			} else {
				AlertDialog.Builder a = Utils.alert(context, R.string.about_update_title, String.format(getString(R.string.about_update_description), result.version, result.buildId, result.updateDescription));
				a.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_SITE));
						startActivity(intent);
					}
				}).create().show();
			}
		}
	}

	private class CheckerResult {
		public boolean isOld;
		public int buildId;
		public String version;
		public String url;
		public int size;
		public String updateDescription;
		public boolean canDownload = false;

		CheckerResult (Json e)  {
			isOld = e.at(Const.IS_OLD).asBoolean();
			buildId = e.at(Const.BUILD_ID).asInteger();
			version = e.at(Const.VERSION).asString();
			if (e.has(Const.DOWNLOAD) && !e.at(Const.DOWNLOAD).isNull()) {
				Json d = e.at(Const.DOWNLOAD);
				url = d.at(Const.LINK).asString();
				size = d.at(Const.SIZE).asInteger();
				canDownload = true;
				if (d.has("updateDescription"))
					updateDescription = d.at("updateDescription").asString();
			}
		}
	}
}
