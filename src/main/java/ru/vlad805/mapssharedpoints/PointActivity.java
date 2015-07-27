package ru.vlad805.mapssharedpoints;

import java.util.Date;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mjson.Json;

public class PointActivity extends ExtendedActivity {

	private TextView tvDescription;
	private TextView tvCreated;
	private TextView tvUpdated;
	private TextView tvVisited;
	private Button bVisit;
	private Point point;
	private int pointId;

	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_point);

		tvDescription = (TextView) findViewById(R.id.point_description);
		tvCreated = (TextView) findViewById(R.id.point_created);
		tvUpdated = (TextView) findViewById(R.id.point_updated);
		tvVisited = (TextView) findViewById(R.id.point_visited);
		bVisit = (Button) findViewById(R.id.point_visit);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			pointId = extras.getInt(Const.POINT_ID);
			updateInfo();
		}
	}

	@Override
	public void onResume () {
		super.onResume();
		updateInfo();
	}

	private void updateInfo () {
		Point s = Point.getById(this, pointId);
		if (s == null) {
			Utils.toast(this, "Что-то пошло не так...");
			finish();
			return;
		}
		showInfo(point = s);
	}

	@SuppressWarnings("deprecation")
	public void showInfo (Point p) {
		setTitle(p.title);
		if (!p.description.isEmpty()) {
			tvDescription.setText(p.description);
			tvDescription.setVisibility(View.VISIBLE);
		} else {
			tvDescription.setVisibility(View.GONE);
		}
		tvCreated.setText(String.format("Создано: %s%s", new Date(p.created * 1000).toLocaleString(), (p.isPublic ? "" : " (приват.)")));
		if (p.updated > 0) {
			tvUpdated.setText(String.format("Изменено: %s", new Date(p.updated * 1000).toLocaleString()));
			tvUpdated.setVisibility(View.VISIBLE);
		} else {
			tvUpdated.setVisibility(View.GONE);
		}
		if (p.isVisited) {
			tvVisited.setText(String.format("Посещено: %s", new Date(p.visited * 1000).toLocaleString()));
			tvVisited.setVisibility(View.VISIBLE);
			bVisit.setVisibility(View.GONE);
		} else {
			tvVisited.setVisibility(View.GONE);
			bVisit.setVisibility(View.VISIBLE);
		}
	}

	public void visitPoint (View v) {
		visit();
	}

	public void editPoint (View v) {
		edit(point);
	}
	
	public void deletePoint (View v) {
		XDialog a = new XDialog(this, R.string.confirm, R.string.confirm_delete_point);
		a.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				delete();
			}
		});
		a.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		a.open();
	}

	public void edit (Point point) {
		Intent i = new Intent(this, EditPointActivity.class);
		i.putExtra(Const.POINT_ID, point.pointId);
		startActivity(i);
	}

	public void visit () {
		setProgressDialog(R.string.sync);

		point.visit(getActivity(), new APICallback() {
			@Override
			public void onResult(Json result) {
				List<Json> local = Json.read(Utils.getString(getActivity(), Const.SHARED_DATA)).asJsonList();
				long visited = new Date().getTime() / 1000;
				for (int i = 0, l = local.size(); i < l; ++i) {
					if (local.get(i).at(Const.POINT_ID).asInteger() == pointId) {
						local.get(i).set(Const.IS_VISITED, true);
						local.get(i).set(Const.DATE_VISIT, visited);
						point.isVisited = true;
						point.visited = visited;
					}
				}
				Utils.setString(getActivity(), Const.SHARED_DATA, local.toString());
			}

			@Override
			public void onError(APIError e) {
				showAPIError(e);
			}
		});
	}

	public void delete () {
		setProgressDialog(R.string.sync);

		point.delete(getActivity(), new APICallback() {
			@Override
			public void onResult(Json result) {
				List<Json> local = Json.read(Utils.getString(getActivity(), Const.SHARED_DATA)).asJsonList();
				Json fresh = Json.array();
				for (int i = 0, l = local.size(); i < l; ++i)
					if (local.get(i).at(Const.POINT_ID).asInteger() != point.pointId)
						fresh.add(local.get(i));
				Utils.setString(getActivity(), Const.SHARED_DATA, fresh.toString());
				Utils.toast(getActivity(), "Точка удалена");
				finish();
			}

			@Override
			public void onError(APIError e) {

			}
		});
	}
}
