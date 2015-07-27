package ru.vlad805.mapssharedpoints;

import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mjson.Json;

public class ListActivity extends ExtendedActivity {

	private ListView list;
	private ArrayList<Point> points;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		list = (ListView) findViewById(R.id.list_view);
		points = getPoints();

		addBackButton();

		initList();
	}

	private void initList () {

		ArrayList<HashMap<String, Object>> items = new ArrayList<>();
		HashMap<String, Object> item;
		Point p;
		for (int i = 0, l = points.size(); i < l; ++i) {
			item = new HashMap<>();
			p = points.get(i);

			item.put(Const.POINT_ID, p.pointId);
			item.put(Const.TITLE, p.title);
			item.put(Const.COLOR_ID, p.getDrawableId());
			items.add(item);
		}
		final SimpleAdapter adapter = new SimpleAdapter(
				getApplicationContext(),
				items,
				R.layout.list_item,
				new String[] {
						Const.POINT_ID,
						Const.TITLE,
						Const.COLOR_ID
				},
				new int[] {
						R.id.list_item_id,
						R.id.list_item_title,
						R.id.list_item_icon
				}
		);
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long q) {
				String id = ((TextView) view.findViewById(R.id.list_item_id)).getText().toString();
				Point point = Point.getById(getActivity(), Integer.valueOf(id));
				if (point == null)
					return;
				Utils.setInt(getActivity(), Const.LIST_OPEN_POINT_ID, point.pointId);
				finish();
			}
		});
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				String i = ((TextView) view.findViewById(R.id.list_item_id)).getText().toString();
				int q = Integer.valueOf(i);

				openActions(q);
				return false;
			}
		});
	}

	final public static int ACTION_SHARE = 0;
	final public static int ACTION_EDIT = 1;
	final public static int ACTION_DELETE = 2;

	public void openActions (final int pointId) {
		XDialog d = new XDialog(this, R.string.actions);
		d.setItems(R.array.point_actions, new DialogInterface.OnClickListener () {
			@Override
			public void onClick (DialogInterface dialog, int which) {
				switch (which) {
					case ACTION_SHARE:
						share(pointId);
						break;
					case ACTION_EDIT:
						edit(pointId);
						break;
					case ACTION_DELETE:
						delete(pointId);
						break;
				}
			}
		});
		d.open();
	}

	public void share (final int pointId) {
		final Point point = Point.getById(this, pointId);
		if (point == null)
			return;
		if (point.isPublic) {
			copyLink(point);
		} else {
			progress = new XProgressDialog(this, R.string.action_create_sharing);
			point.share(getActivity(), new APICallback() {
				@Override
				public void onResult(Json result) {
					progress.close();

					String accessCode = result.at(Const.ACCESS_CODE).asString();
					copyLink(point, accessCode);
				}

				@Override
				public void onError(APIError e) {
					showAPIError(e);
				}
			});
		}
	}

	public void copyLink (Point p) {
		copyLink(p, "");
	}

	public void copyLink (Point p, String accessCode) {
		String url = "http://places.vlad805.ru/map/" + p.latitude + "/" + p.longitude + "/14/" + Utils.getInt(this, Const.USER_ID) + "/" + p.pointId + (accessCode.isEmpty() ? "" : "/" + accessCode);
		copyText(url);
		Utils.toast(this, R.string.action_copied);

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
		startActivity(Intent.createChooser(sharingIntent, getString(R.string.action_share_point_link)));

	}

	public void copyText (String text) {
		((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setText(text);
	}

	public void edit (int pointId) {
		Intent i = new Intent(this, EditPointActivity.class);
		i.putExtra(Const.POINT_ID, pointId);
		startActivity(i);
	}

	public void delete (final int pointId) {
		final Point point = Point.getById(this, pointId);
		if (point == null) {
			return;
		}
		XDialog a = new XDialog(this, R.string.confirm, R.string.confirm_delete_point);
		a.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				point.delete(getActivity(), new APICallback() {
					@Override
					public void onResult(Json result) {
						List<Json> local = Json.read(Utils.getString(getActivity(), Const.SHARED_DATA)).asJsonList();
						Json fresh = Json.array();
						for (int i = 0, l = local.size(); i < l; ++i)
							if (local.get(i).at(Const.POINT_ID).asInteger() != point.pointId)
								fresh.add(local.get(i));
						Utils.setString(getActivity(), Const.SHARED_DATA, fresh.toString());
						Utils.toast(getActivity(), R.string.point_deleted);
					}

					@Override
					public void onError(APIError e) {
						showAPIError(e);
					}
				});
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



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.menu_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}
}
