package ru.vlad805.mapssharedpoints;

import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import mjson.Json;

public class NewPointActivity extends ExtendedActivity {
	
	private double latitude;
	private double longitude;
	
	private EditText evTitle;
	private EditText evDescription;
	private CheckBox cbIsPublic;
	
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hideActionBar();

		setContentView(R.layout.activity_creating);

		evTitle = (EditText) findViewById(R.id.create_title);
		evDescription = (EditText) findViewById(R.id.create_description);
		cbIsPublic = (CheckBox) findViewById(R.id.create_isPublic);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			latitude = extras.getDouble(Const.LATITUDE);
			longitude = extras.getDouble(Const.LONGITUDE);
		} else finish();
	}

	public void onSubmit (View view) {
		String title = evTitle.getText().toString().trim();
		String description = evDescription.getText().toString().trim();
		boolean isPublic = cbIsPublic.isChecked();
		
		if (title.isEmpty()) {
			Utils.toast(this, R.string.point_create_empty_title);
			return;
		}

		setProgressDialog(R.string.sync);
		create(title, description, isPublic);
	}

	private void create (String title, String description, boolean isPublic) {
		Point.create(getActivity(), new APICallback() {
			@Override
			public void onResult(Json result) {
				closeProgressDialog();

				List<Json> local = Json.read(Utils.getString(getActivity(), Const.SHARED_DATA)).asJsonList();
				local.add(result);
				Utils.setString(getActivity(), Const.SHARED_DATA, local.toString());
				Utils.toast(NewPointActivity.this, R.string.point_created);
				finish();
			}

			@Override
			public void onError(APIError e) {
				showAPIError(e);
			}
		}, latitude, longitude, title, description, isPublic);
	}
}
