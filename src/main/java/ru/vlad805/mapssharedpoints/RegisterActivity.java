package ru.vlad805.mapssharedpoints;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKScopes;
import java.util.HashMap;
import mjson.Json;

public class RegisterActivity extends ExtendedActivity implements View.OnClickListener {

	private EditText editViewFirstName;
	private EditText editViewLastName;
	private EditText editViewLogin;
	private EditText editViewPassword;
	private RadioGroup rgSex;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		addBackButton();


		editViewFirstName = (EditText) findViewById(R.id.register_firstName);
		editViewLastName = (EditText) findViewById(R.id.register_lastName);
		editViewLogin = (EditText) findViewById(R.id.register_login);
		editViewPassword = (EditText) findViewById(R.id.register_password);
		rgSex = (RadioGroup) findViewById(R.id.register_sex);

		findViewById(R.id.register_submit).setOnClickListener(this);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.getBoolean(Const.IS_VK)) {
				VKSdk.initialize(this);
				VKSdk.login(this, VKScopes.OFFLINE);
			}
		}

	}

	private HashMap<String, String> getDataFields () {
		HashMap<String, String> d = new HashMap<>();
		d.put(Const.FIRST_NAME, editViewFirstName.getText().toString().trim());
		d.put(Const.LAST_NAME, editViewLastName.getText().toString().trim());
		d.put(Const.LOGIN, editViewLogin.getText().toString().trim().toLowerCase());
		d.put(Const.PASSWORD, editViewPassword.getText().toString().trim());
		int sex = 0;
		switch (rgSex.getCheckedRadioButtonId()) {
			case R.id.profile_sex_female: sex = 1; break;
			case R.id.profile_sex_male: sex = 2; break;
		}
		d.put(Const.SEX, String.valueOf(sex));
		return d;
	}

	private void doRegistration () {
		setProgressDialog(R.string.register_request);

		API.invokeAPIMethod(this, Const.API.registerUser, getDataFields(), new APICallback() {
			@Override
			public void onResult(Json result) {
				closeProgressDialog();
				if (result.at(Const.RESULT).asBoolean() && result.at(Const.USER_ID).asInteger() > 0) {
					Utils.toast(getActivity(), R.string.register_success);
					finish();
				}
			}

			@Override
			public void onError(APIError e) {
				closeProgressDialog();
				showAPIError(e);
			}
		});
	}

	@Override
	public void onClick (View v) {
		switch (v.getId()) {
			case R.id.register_submit:
				doRegistration();
				break;
		}
	}

	public int vkUserId = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
			@Override
			public void onResult(VKAccessToken res) {
				vkUserId = Integer.valueOf(res.userId);
				VKApi.users().get(VKParameters.from("fields", "screen_name,sex")).executeWithListener(new VKRequest.VKRequestListener() {
					@Override
					public void onComplete(VKResponse response) {
						Json user = Json.read(response.responseString).at(Const.RESPONSE).asJsonList().get(0);
						setFields(user);
					}
				});
			}
			@Override
			public void onError(VKError error) {
				Utils.toast(getActivity(), "VKAPIError (" + error.errorCode + "):\n" + error.errorMessage);
			}
		})) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void setFields (Json user) {
		editViewFirstName.setText(user.at("first_name").asString());
		editViewLastName.setText(user.at("last_name").asString());
		editViewLogin.setText(user.at("screen_name").asString());
		int sex = user.at("sex").asInteger();
		switch (sex) {
			case 1: ((RadioButton) findViewById(R.id.register_sex_female)).setChecked(true); break;
			case 2: ((RadioButton) findViewById(R.id.register_sex_male)).setChecked(true); break;
		}
	}
}
