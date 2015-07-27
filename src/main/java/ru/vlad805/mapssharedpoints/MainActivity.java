package ru.vlad805.mapssharedpoints;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import mjson.Json;

public class MainActivity extends ExtendedActivity {
	
	private EditText loginView;
	private EditText passwordView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (Utils.isAuthorized(this)) {
			if (!Utils.has(this, Const.SHARED_USER)) {
				setProgressDialog("");
				resolveUser(Utils.getInt(this, Const.USER_ID));
			}

			startActivity(new Intent(this, MapActivity.class));
			finish();
			return;
		}

		loginView = (EditText) findViewById(R.id.login_login_value);
		passwordView = (EditText) findViewById(R.id.login_password_value);
	}

	public void authorize (View v) {
		
		String login = loginView.getText().toString().trim();
		String password = passwordView.getText().toString().trim();
		if (login.length() <= 3 || password.length() < 6) {
			Utils.toast(this, "Неверно введены данные!");
			return;
		}
		
		setProgressDialog(R.string.login_process);

		auth(login, password);
	}

	public void registerVK (View v) {
		Intent i = new Intent(this, RegisterActivity.class);
		i.putExtra(Const.IS_VK, true);
		startActivity(i);
	}

	private void auth (String login, String password) {
		HashMap<String, String> params = new HashMap<>();
		params.put(Const.LOGIN, login);
		params.put(Const.PASSWORD, password);
		try {
			new API(getActivity(), Const.API.login, params, new APICallback() {
				@Override
				public void onResult (Json result) {
					String userAccessToken = result.at(Const.USER_ACCESS_TOKEN).asString();
					int userId = result.at(Const.USER_ID).asInteger();
					Utils.setInt(getActivity(), Const.USER_ID, userId);
					Utils.setString(getActivity(), Const.USER_ACCESS_TOKEN, userAccessToken);
					resolveUser(userId);
				}

				@Override
				public void onError (APIError e) {
					closeProgressDialog();
					showAPIError(e);
				}
			}).send();
		} catch (NotAvailableInternetException e) {
			runOnUiThread(noInternet);
		}
	}

	public void register (View v) {
		startActivity(new Intent(this, RegisterActivity.class));
	}

	private void resolveUser (int userId) {
		HashMap<String, String> params = new HashMap<>();
		params.put(Const.USER_IDS, String.valueOf(userId));
		try {
			new API(getActivity(), Const.API.getUsers, params, new APICallback() {
				@Override
				public void onResult (Json users) {
					closeProgressDialog();
					if (users.asJsonList().size() == 0) {
						finish();
						return;
					}
					Json user = users.asJsonList().get(0);
					String name = user.at(Const.FIRST_NAME).asString();
					Utils.setString(getActivity(), Const.SHARED_USER, user.toString());
					Utils.toast(getActivity(), String.format(getString(R.string.login_welcome), name));
					startActivity(new Intent(getActivity(), MapActivity.class));
					finish();
				}

				@Override
				public void onError (APIError e) {
					showAPIError(e);
				}
			}).send();
		} catch (NotAvailableInternetException e) {
			noInternet.run();
		}
	}
}
