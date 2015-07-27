package ru.vlad805.mapssharedpoints;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import java.util.List;

public class IntentResolver extends ExtendedActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intentresolver);

		String i = getIntent().getAction();

		if (i == null) {
			finish();
		} else {
			resolve(getIntent().getData());
		}
	}

	public void resolve (Uri u) {
		Intent intent;
		List<String> path = u.getPathSegments();
		if (u.getHost().equals("online.vlad805.ru")) {
//			if (path.get(0).equals("register"))
//				startActivity(new Intent(this, RegisterActivity.class));

			intent = new Intent(this, ProfileActivity.class);
			intent.putExtra(Const.LOGIN, path.get(0));
			startActivity(intent);
		} else if (u.getHost().equals("places.vlad805.ru")) {
			if (path.get(0).equals("settings")) {
				startActivity(new Intent(this, SettingsActivity.class));
			} else {
				double latitude = Double.valueOf(path.get(1));
				double longitude = Double.valueOf(path.get(2));
				int zoom = Integer.valueOf(path.get(3));
				int ownerId = Integer.parseInt(path.get(4));
				int pointId = path.size() > 5 ? Integer.parseInt(path.get(5)) : 0;
				String accessCode = path.size() > 6 ? path.get(6) : "";

				intent = new Intent(this, MapActivity.class);
				intent.putExtra(Const.LATITUDE, latitude);
				intent.putExtra(Const.LONGITUDE, longitude);
				intent.putExtra(Const.ZOOM, zoom);
				intent.putExtra(Const.OWNER_ID, ownerId);
				if (pointId > 0)
					intent.putExtra(Const.POINT_ID, pointId);
				if (!accessCode.isEmpty())
					intent.putExtra(Const.ACCESS_CODE, accessCode);
				if (ownerId != getUserId() || pointId > 0)
					intent.putExtra(Const.IS_NOT_OWNER, true);

				startActivity(intent);
			}
		} else {
			unsupported();
		}
		finish();
	}

	public void unsupported () {
		Utils.toast(this, R.string.resolver_not_supported);
	}
}
