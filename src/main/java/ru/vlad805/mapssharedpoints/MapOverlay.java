package ru.vlad805.mapssharedpoints;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;
import android.content.Context;
import android.content.Intent;

public class MapOverlay extends Overlay {
	
	Context context;
	
	public MapOverlay(MapController controller, Context ctx) {
		super(controller);
		this.context = ctx;
	}
	public boolean onLongPress (float x, float y) {
		GeoPoint g = getMapController().getGeoPoint(new ScreenPoint(x, y));
		Intent intent = new Intent(context, NewPointActivity.class);
		intent.putExtra(Const.LATITUDE, g.getLat());
		intent.putExtra(Const.LONGITUDE, g.getLon());
		context.startActivity(intent);
		return true;
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public int compareTo ( Object another) {
		return 0;
	}
}
