package ru.vlad805.mapssharedpoints;

import mjson.Json;

public interface APICallback {
	void onResult (Json result);
	void onError (APIError e);
}
