package ru.vlad805.mapssharedpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mjson.Json;
import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MapActivity extends ExtendedActivity {

	private MapView mapView;
	private MapController mapController;
	private OverlayManager overlayManager;
	private Settings settings;
	private ArrayList<Point> points = null;
	private Overlay overlay;
	private int findId = -1;


	private ForeignData foreignData = null;

	public class ForeignData {
		double latitude;
		double longitude;
		int zoom;

		int ownerId;
		int pointId;

		String accessCode;
	}

	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStatusBarTranslucent(true);
		setContentView(R.layout.activity_map);

		settings = new Settings(this);

		mapView = (MapView) findViewById(R.id.map);

		mapView.showBuiltInScreenButtons(true);
		mapController = mapView.getMapController();
		overlayManager = mapController.getOverlayManager();
		
		reDraw();

		Bundle i = getIntent().getExtras();
		if (i == null || !i.containsKey(Const.IS_NOT_OWNER)) {
			if (Utils.has(this, Const.SHARED_DATA)) {
				points = Point.parse(Json.read(Utils.getString(this, Const.SHARED_DATA)).asJsonList());
				show(true);
			} else
				sync();
		} else {
			if (!i.containsKey(Const.OWNER_ID)) {
				new XDialog(this, "Strange... Param `ownerId` is not defined").show();
				finish();
				return;
			}
			ForeignData d = new ForeignData();

			if (i.containsKey(Const.LATITUDE))
				d.latitude = i.getDouble(Const.LATITUDE);
			if (i.containsKey(Const.LONGITUDE))
				d.longitude = i.getDouble(Const.LONGITUDE);
			if (i.containsKey(Const.ZOOM))
				d.zoom = i.getInt(Const.ZOOM);
			if (i.containsKey(Const.POINT_ID))
				d.ownerId = i.getInt(Const.OWNER_ID);
			if (i.containsKey(Const.POINT_ID))
				d.pointId = i.getInt(Const.POINT_ID);
			if (i.containsKey(Const.ACCESS_CODE))
				d.accessCode = i.getString(Const.ACCESS_CODE);
			foreignData = d;
			sync();
		}

		initOnlineTask();
	}

	public void sync () {
		setProgressDialog(R.string.sync);
		load();
	}

	public void reDraw () {
		settings = new Settings(this);
		overlayManager.getMyLocation().setEnabled(settings.getIsMyLocation());
		mapView.showJamsButton(false);
		mapController.setHDMode(settings.getIsHD());
		mapController.setNightMode(settings.getIsNight());
		mapController.setJamsVisible(settings.getIsTraffic());
	}

	public void onResume () {
		super.onResume();
		reDraw();
		findId = -1;
		if (Utils.has(this, Const.LIST_OPEN_POINT_ID)) {
			findId = Utils.getInt(this, Const.LIST_OPEN_POINT_ID);
			Utils.removeData(this, Const.LIST_OPEN_POINT_ID);
		}
		if (findId > 0) {
			show();
		}
	}

	private void load () {
		HashMap<String, String> params = new HashMap<>();
		if (foreignData != null) {
			if (foreignData.ownerId > 0)
				params.put(Const.OWNER_ID, String.valueOf(foreignData.ownerId));
			if (foreignData.accessCode != null && !foreignData.accessCode.isEmpty())
				params.put(Const.ACCESS_CODE, foreignData.accessCode);
		}
		API.invokeAPIMethod(getActivity(), Const.API.getPoints, params, new APICallback() {
			@Override
			public void onResult(Json result) {
				List<Json> items = result.at(Const.ITEMS).asJsonList();
				if (foreignData == null)
					Utils.setString(MapActivity.this, Const.SHARED_DATA, items.toString());
				points = Point.parse(items);
				closeProgressDialog();
				show(true);
			}

			@Override
			public void onError(APIError e) {
				APIError = e;
				runOnUiThread(showAPIError);
			}
		});
	}

	public void show () {
		show(false);
	}

	public void show (boolean needMove) {
		showPoints();
		if (needMove && overlay != null) {
			setZoomSpan(overlay);
		}
	}

	public void showPoints () {
		if (overlay != null) {
			overlayManager.removeOverlay(overlay);
			overlay = null;
		}
		
		overlay = new MapOverlay(mapController, this);

		OverlayItem foreignOverlay = null;

		OverlayItem o;
		for (Point item : points) {
			o = item.getOverlay(this, getIcon(item.colorId));
			if ((foreignData != null && foreignData.pointId == item.pointId) || findId == item.pointId)
				foreignOverlay = o;
			overlay.addOverlayItem(o);
		}

		overlayManager.addOverlay(overlay);
		
		if (foreignData != null && foreignOverlay == null) {
			new XDialog(MapActivity.this, "Ошибка!", "Такой метки нет или у Вас нет к ней доступа.").create().show();
		}
		if (foreignData != null) {
			mapController.setPositionAnimationTo(new GeoPoint(foreignData.latitude, foreignData.longitude));
			mapController.setZoomCurrent(foreignData.zoom);
		}
		if (foreignOverlay != null) {
			mapController.showBalloon(foreignOverlay.getBalloonItem());
			mapController.setPositionAnimationTo(foreignOverlay.getGeoPoint());
		}
	}

	private Drawable getIcon (int colorId) {
		return this.getResources().getDrawable(Point.icons[colorId]);
	}

	private void setZoomSpan (Overlay overlay) {
		@SuppressWarnings("unchecked")
		List<OverlayItem> list = overlay.getOverlayItems();
		double	maxLat,
				minLat,
				maxLon,
				minLon;
		maxLat = maxLon = Double.MIN_VALUE;
		minLat = minLon = Double.MAX_VALUE;
		
		for (int i = 0; i < list.size(); i++) {
			GeoPoint geoPoint = list.get(i).getGeoPoint();
			double lat = geoPoint.getLat();
			double lon = geoPoint.getLon();
			maxLat = Math.max(lat, maxLat);
			minLat = Math.min(lat, minLat);
			maxLon = Math.max(lon, maxLon);
			minLon = Math.min(lon, minLon);
		}
		mapController.setZoomToSpan(maxLat - minLat, maxLon - minLon);
		//mapController.setZoomToSpan(10);
		mapController.setPositionAnimationTo(new GeoPoint((maxLat + minLat) / 2, (maxLon + minLon) / 2));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case R.id.action_show_list:
				intent = new Intent(this, ListActivity.class);
				startActivity(intent);
				break;

			case R.id.action_profile:
				intent = new Intent(this, ProfileActivity.class);
				intent.putExtra(Const.LOGIN, String.valueOf(Utils.getInt(this, Const.USER_ID)));
				startActivity(intent);
				break;

			case R.id.action_sync:
				sync();
				break;

			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;

			case R.id.action_about:
				startActivity(new Intent(this, AboutActivity.class));
				break;

			case R.id.action_logout:
				Utils.removeData(this, Const.USER_ACCESS_TOKEN);
				Utils.removeData(this, Const.USER_ID);
				Utils.removeData(this, Const.SHARED_DATA);
				Utils.removeData(this, Const.SHARED_USER);
				startActivity(new Intent(this, MainActivity.class));
				setProgressDialog(R.string.logouting);
				logout();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private Timer timer;

	private void initOnlineTask () {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				setOnline();
			}
		}, 0L, 60L * 1000);
		setOnline();
	}

	private void setOnline () {
		getUser().setOnline(this);
	}

	private void logout () {
		try {
			new API(getActivity(), Const.API.logout, null, new APICallback() {
				@Override
				public void onResult(Json result) {
					closeProgressDialog();
					finish();
				}

				@Override
				public void onError(APIError e) {
					Utils.toast(getActivity(), R.string.logout_failed);
					closeProgressDialog();
					finish();

				}
			}).send();
		} catch (NotAvailableInternetException e) {
			runOnUiThread(noInternet);
		}
	}

	@Override
	public void onDestroy () {
		super.onDestroy();
		timer.cancel();
		timer = null;
	}
}
