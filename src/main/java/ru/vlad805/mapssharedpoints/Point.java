package ru.vlad805.mapssharedpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mjson.Json;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.balloon.OnBalloonListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

public class Point {
	public int ownerId;
	public int pointId;
	public double latitude;
	public double longitude;
	public String title;
	public String description;
	public long created;
	public long updated;
	public long visited;
	public boolean isPublic;
	public boolean isVisited;
	public int colorId;

	public Point (Map<String, Json> j) {
		pointId = j.get(Const.POINT_ID).asInteger();
		latitude = j.get(Const.LATITUDE).asDouble();
		longitude = j.get(Const.LONGITUDE).asDouble();
		title = j.get(Const.TITLE).asString();
		description = j.get(Const.DESCRIPTION).asString();
		created = j.get(Const.DATE_CREATED).asInteger();
		updated = j.get(Const.DATE_UPDATED).asLong();
		visited = j.get(Const.DATE_VISIT).asLong();
		isPublic = j.get(Const.IS_PUBLIC).asBoolean();
		isVisited = j.get(Const.IS_VISITED).asBoolean();
		colorId = j.get(Const.COLOR_ID).asInteger();
	}

	public OverlayItem getOverlay (final Context ctx, Drawable drawable) {
		OverlayItem item = new OverlayItem(getPoint(), drawable);
		BalloonItem balloon = new BalloonItem(ctx, item.getGeoPoint());
		balloon.setText(this.title);
		balloon.setOnBalloonListener(new OnBalloonListener() {
			@Override
			public void onBalloonViewClick(BalloonItem b, View v) {
				Intent intent = new Intent(ctx, PointActivity.class);
				intent.putExtra(Const.POINT_ID, pointId);
				ctx.startActivity(intent);
			}
			@Override
			public void onBalloonShow(BalloonItem arg0) {}
			@Override
			public void onBalloonHide(BalloonItem arg0) {}
			@Override
			public void onBalloonAnimationStart(BalloonItem arg0) {}
			@Override
			public void onBalloonAnimationEnd(BalloonItem arg0) {}
		});
		item.setBalloonItem(balloon);
		item.setOffsetX(-7);
		item.setOffsetY(20);
		return item;
	}

	public GeoPoint getPoint () {
		return new GeoPoint(latitude, longitude);
	}

	final static int[] icons = new int[] {
			R.drawable.point_cyan,
			R.drawable.point_blue,
			R.drawable.point_gray,
			R.drawable.point_green,
			R.drawable.point_red,
			R.drawable.point_black,
			R.drawable.point_pink,
			R.drawable.point_yellow,
			R.drawable.point_purple,
			R.drawable.point_orange
	};

	public int getDrawableId () {
		return icons[colorId < 0 || colorId > icons.length ? 0 : colorId];
	}

	static ArrayList<Point> parse (List<Json> a) {
		ArrayList<Point> d = new ArrayList<>();
		for (int i = 0, l = a.size(); i < l; ++i)
			d.add(new Point(a.get(i).asJsonMap()));
		return d;
	}

	static Point getById (Context context, int pointId) {
		List<Json> json = Json.read(Utils.getString(context, Const.SHARED_DATA)).asJsonList();
		ArrayList<Point> data = Point.parse(json);
		for (int i = 0, l = data.size(); i < l; ++i) {
			if (data.get(i).pointId == pointId) {
				return data.get(i);
			}
		}
		return null;
	}

	static void create (Context context, APICallback callback, double latitude, double longitude, String title, String description, boolean isPublic) {
		HashMap<String, String> params = new HashMap<>();
		params.put(Const.LATITUDE, String.valueOf(latitude));
		params.put(Const.LONGITUDE, String.valueOf(longitude));
		params.put(Const.TITLE, title);
		params.put(Const.DESCRIPTION, description);
		params.put(Const.IS_PUBLIC, String.valueOf(isPublic ? 1 : 0));
		API.invokeAPIMethod(context, Const.API.addPoint, params, callback);
	}

	public void share (Context context, APICallback callback) {
		HashMap<String, String> params = new HashMap<>();
		params.put(Const.OWNER_ID, String.valueOf(ownerId));
		params.put(Const.POINT_ID, String.valueOf(pointId));
		API.invokeAPIMethod(context, Const.API.sharePoint, params, callback);
	}

	public void copy (Context context, APICallback callback) {
		HashMap<String, String> params = new HashMap<>();
		params.put(Const.OWNER_ID, String.valueOf(ownerId));
		params.put(Const.POINT_ID, String.valueOf(pointId));
		API.invokeAPIMethod(context, Const.API.copyPoint, params, callback);
	}

	public void edit (Context context, APICallback callback, String title, String description, boolean isPublic) {
		HashMap<String, String> params = new HashMap<>();
		params.put(Const.OWNER_ID, String.valueOf(ownerId));
		params.put(Const.POINT_ID, String.valueOf(pointId));
		params.put(Const.TITLE, title);
		params.put(Const.DESCRIPTION, description);
		params.put(Const.IS_PUBLIC, isPublic ? "1" : "0");
		params.put(Const.COLOR_ID, String.valueOf(colorId));
		API.invokeAPIMethod(context, Const.API.editPoint, params, callback);
	}

	public void visit (Context context, APICallback callback) {
		HashMap<String, String> params = new HashMap<>();
		params.put(Const.OWNER_ID, String.valueOf(ownerId));
		params.put(Const.POINT_ID, String.valueOf(pointId));
		params.put(Const.IS_VISIT, "1");
		API.invokeAPIMethod(context, Const.API.setVisitPoint, params, callback);
	}

	public void delete (Context context, APICallback callback) {
		HashMap<String, String> params = new HashMap<>();
		params.put(Const.OWNER_ID, String.valueOf(ownerId));
		params.put(Const.POINT_ID, String.valueOf(pointId));
		API.invokeAPIMethod(context, Const.API.deletePoint, params, callback);
	}


}
