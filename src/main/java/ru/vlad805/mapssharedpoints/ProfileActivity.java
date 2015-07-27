package ru.vlad805.mapssharedpoints;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;

import mjson.Json;

public class ProfileActivity extends ExtendedActivity {

	private User user;

	private TextView tvLogin;
	private TextView tvName;
	private ImageView ivPhoto;
	private TextView tvOnline;
	private Button btMap;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		Bundle i = getIntent().getExtras();
		String login = i.getString(Const.LOGIN);

		if (login == null) {
			Utils.toast(this, R.string.profile_unsupported_link);
		}
		setProgressDialog(R.string.profile_loading);

		addBackButton();

		load(login);

		tvLogin = (TextView) findViewById(R.id.profile_login);
		tvName = (TextView) findViewById(R.id.profile_name);
		ivPhoto = (ImageView) findViewById(R.id.profile_image);
		tvOnline = (TextView) findViewById(R.id.profile_isOnline);
		btMap = (Button) findViewById(R.id.profile_button_map);
	}


	private void load (String login) {
		HashMap<String, String> params = new HashMap<>();
		params.put(Const.USER_IDS, login);
		try {
			new API(getActivity(), Const.API.getUsers, params, new APICallback() {
				@Override
				public void onResult(Json result) {
					closeProgressDialog();
					List<Json> data = result.asJsonList();
					if (data.size() == 0) {
						finish();
						return;
					}
					Json user = data.get(0);
					showInfo(new User(user));
				}

				@Override
				public void onError(APIError e) {
					showAPIError(e);
				}
			}).send();
		} catch (NotAvailableInternetException e) {
			e.printStackTrace();
		}
	}


	private void showInfo (User u) {
		user = u;

		tvLogin.setText("@" + user.login);
		tvName.setText(user.firstName + " " + user.lastName);
		tvOnline.setText(user.isOnline ? getString(R.string.profile_online) : getLastSeen());
		if (getUserId() == u.id) {
			btMap.setVisibility(View.GONE);
		}


		new InternetImage(this, user.photo.photo200, ivPhoto);
	}

	private String getLastSeen () {
		StringBuilder s = new StringBuilder();
		String space = " ";
		Resources r = getResources();
		IntervalDate d = XDate.interval((int) (long) user.lastSeen);

		s.append(r.getStringArray(R.array.profile_was)[user.sex]);
		s.append(space);
		if (d.getHours() < 20) {
			if (d.getHours() > 0)
				s.append(d.getHours()).append(space).append(Utils.textCase(r.getStringArray(R.array.days), d.getHours()));
			if (d.getHours() > 0 && d.getMinutes() > 0)
				s.append(space);
			if (d.getMinutes() > 0)
				s.append(d.getMinutes()).append(space).append(Utils.textCase(r.getStringArray(R.array.minutes), d.getMinutes()));
			s.append(space);
			s.append(getString(R.string.profile_was_back));

		} else {
			s.append(XDate.get(user.lastSeen));
		}
		return s.toString();
	}

	public void openMap (View v) {
		Intent i = new Intent(this, MapActivity.class);
		i.putExtra(Const.OWNER_ID, user.id);
		i.putExtra(Const.IS_NOT_OWNER, true);
		startActivity(i);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_profile, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case R.id.profile_action_settings:
				intent = new Intent(this, SettingsActivity.class);
				intent.putExtra(Const.Settings.TAB_PROFILE, true);
				startActivity(intent);
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
