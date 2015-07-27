package ru.vlad805.mapssharedpoints;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import java.util.HashMap;
import mjson.Json;

public class SettingsActivity extends ExtendedActivity implements View.OnClickListener, OnCheckedChangeListener {
	
	final public static int iNight = R.id.settings_night;
	final public static int iHD = R.id.settings_hd;
	final public static int iTraffic = R.id.settings_traffic;
	final public static int iLocation = R.id.settings_location;

	private EditText editViewFirstName;
	private EditText editViewLastName;
	private EditText editViewLogin;
	private RadioGroup rgSex;

	private Settings settings;
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hideActionBar();

		setContentView(R.layout.activity_settings);

		settings = new Settings(this);
		user = getUser();

		CheckBox settingNight = (CheckBox) findViewById(iNight);
		CheckBox settingHD = (CheckBox) findViewById(iHD);
		CheckBox settingTraffic = (CheckBox) findViewById(iTraffic);
		CheckBox settingLocation = (CheckBox) findViewById(iLocation);

		editViewFirstName = (EditText) findViewById(R.id.profile_firstName);
		editViewLastName = (EditText) findViewById(R.id.profile_lastName);
		editViewLogin = (EditText) findViewById(R.id.profile_login);
		rgSex = (RadioGroup) findViewById(R.id.profile_sex);
		RadioButton rbSexFemale = (RadioButton) findViewById(R.id.profile_sex_female);
		RadioButton rbSexMale = (RadioButton) findViewById(R.id.profile_sex_male);
		Button profileSubmit = (Button) findViewById(R.id.profile_submit);

		settingNight.setOnCheckedChangeListener(this);
		settingHD.setOnCheckedChangeListener(this);
		settingTraffic.setOnCheckedChangeListener(this);
		settingLocation.setOnCheckedChangeListener(this);
		
		settingNight.setChecked(settings.getIsNight());
		settingHD.setChecked(settings.getIsHD());
		settingTraffic.setChecked(settings.getIsTraffic());
		settingLocation.setChecked(settings.getIsMyLocation());

		editViewLogin.setText(user.login);
		editViewFirstName.setText(user.firstName);
		editViewLastName.setText(user.lastName);
		switch (user.sex) {
			case 1: rbSexFemale.setChecked(true); break;
			case 2: rbSexMale.setChecked(true); break;
		}
		profileSubmit.setOnClickListener(this);

		TabHost tabs = (TabHost) findViewById(R.id.settings_tabHost);
		tabs.setup();

		TabHost.TabSpec spec;

		spec = tabs.newTabSpec("settingsApplication");
		spec.setContent(R.id.settings_application);
		spec.setIndicator(getString(R.string.settings_application));
		tabs.addTab(spec);

		spec = tabs.newTabSpec("settingsProfile");
		spec.setContent(R.id.settings_profile);
		spec.setIndicator(getString(R.string.settings_profile));
		tabs.addTab(spec);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey(Const.Settings.TAB_PROFILE)) {
				tabs.setCurrentTab(1);
			}
		}
	}
	@Override
	public void onClick (View v) {
		switch (v.getId()) {
			case R.id.profile_submit:
				saveProfileInfo();
				break;
		}
	}

	public HashMap<String, String> getDataFields () {
		HashMap<String, String> d = new HashMap<>();
		d.put(Const.FIRST_NAME, editViewFirstName.getText().toString().trim());
		d.put(Const.LAST_NAME, editViewLastName.getText().toString().trim());
		d.put(Const.LOGIN, editViewLogin.getText().toString().trim());
		int sex = 0;
		switch (rgSex.getCheckedRadioButtonId()) {
			case R.id.profile_sex_female: sex = 1; break;
			case R.id.profile_sex_male: sex = 2; break;
		}
		d.put(Const.SEX, String.valueOf(sex));
		return d;
	}

	public void saveProfileInfo () {
		final HashMap<String, String> d = getDataFields();
		if (d.get(Const.FIRST_NAME).isEmpty() || d.get(Const.LAST_NAME).isEmpty() || d.get(Const.LOGIN).isEmpty()) {
			Utils.toast(this, R.string.profile_error_empty);
			return;
		}
		setProgressDialog(R.string.profile_saving);
		sendProfileInfo (d);
	}

	private void sendProfileInfo (final HashMap<String, String> info) {
		user.editInfo(getActivity(), new APICallback() {
			@Override
			public void onResult (Json result) {
				closeProgressDialog();
				Utils.toast(getActivity(), R.string.profile_settings_saved);
				Json user = Json.read(Utils.getString(getActivity(), Const.SHARED_USER));
				user.set(Const.FIRST_NAME, info.get(Const.FIRST_NAME));
				user.set(Const.LAST_NAME, info.get(Const.LAST_NAME));
				user.set(Const.SEX, info.get(Const.SEX));
				user.set(Const.LOGIN, info.get(Const.LOGIN));
				Utils.setString(getActivity(), Const.SHARED_USER, user.toString());
			}

			@Override
			public void onError(APIError e) {
				closeProgressDialog();
				showAPIError(e);
			}
		}, info);
	}

	@Override
	public void onCheckedChanged (CompoundButton v, boolean state) {
		switch (v.getId()) {
			case iNight: settings.setNight(state); break;
			case iHD: settings.setHD(state); break;
			case iTraffic: settings.setTraffic(state); break;
			case iLocation: settings.setMyLocation(state); break;
		}
	}
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
