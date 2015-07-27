package ru.vlad805.mapssharedpoints;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Date;
import java.util.List;

import mjson.Json;

public class EditPointActivity extends ExtendedActivity {

	private Point point = null;
	private int pointId;

	private EditText evTitle;
	private EditText evDescription;
	private CheckBox cbIsPublic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hideActionBar();
		setContentView(R.layout.activity_editing);

		evTitle = (EditText) findViewById(R.id.edit_title);
		evDescription = (EditText) findViewById(R.id.edit_description);
		cbIsPublic = (CheckBox) findViewById(R.id.edit_isPublic);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			pointId = extras.getInt(Const.POINT_ID);
			point = Point.getById(this, pointId);
		}
		if (point == null)
			finish();

		evTitle.setText(point.title);
		evDescription.setText(point.description);
		cbIsPublic.setChecked(point.isPublic);
	}


	public void onSubmit (View view) {
		final String title = evTitle.getText().toString().trim();
		final String description = evDescription.getText().toString().trim();
		final boolean isPublic = cbIsPublic.isChecked();

		if (title.isEmpty()) {
			Utils.toast(this, "Введите название!");
			return;
		}

		setProgressDialog(R.string.sync);
		point.edit(getActivity(), new APICallback() {
			@Override
			public void onResult(Json result) {
				closeProgressDialog();
				List<Json> local = Json.read(Utils.getString(getActivity(), Const.SHARED_DATA)).asJsonList();
				long updated = new Date().getTime() / 1000;
				for (int i = 0, l = local.size(); i < l; ++i) {
					if (local.get(i).at(Const.POINT_ID).asInteger() == pointId) {
						local.get(i).set(Const.TITLE, title);
						local.get(i).set(Const.DESCRIPTION, description);
						local.get(i).set(Const.IS_PUBLIC, isPublic);
						local.get(i).set(Const.DATE_UPDATED, updated);

						point.title = title;
						point.description = description;
						point.isPublic = isPublic;
						point.updated = updated;
					}
				}
				Utils.setString(getActivity(), Const.SHARED_DATA, local.toString());
				Utils.toast(getActivity(), R.string.point_saved);
				finish();
			}

			@Override
			public void onError(APIError e) {
				showAPIError(e);
			}
		}, title, description, isPublic);
	}
}
