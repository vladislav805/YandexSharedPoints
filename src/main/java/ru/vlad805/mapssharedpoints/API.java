package ru.vlad805.mapssharedpoints;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import mjson.Json;
import java.util.HashMap;

public class API {
	final public static String API_DOMAIN = "api.vlad805.ru";

	private Context context;
	private String method;
	private HashMap<String, String> params;
	private Json result;
	private APICallback callback = null;

	public API (Context ctx, String method, HashMap<String, String> params, APICallback callback) {
		this.context = ctx;
		this.method = method;
		this.params = params == null ? new HashMap<String, String>() : params;
		this.callback = callback;
	}

	public void send () throws NotAvailableInternetException {
		if (Utils.isAuthorized(context))
			params.put(Const.USER_ACCESS_TOKEN, Utils.getAccessToken(context));
		params.put(Const.V, "2.0");

		StringBuilder p = new StringBuilder();

		for (String key: params.keySet())
			p.append(key).append("=").append(params.get(key)).append("&");
		Log.i("APIRequestParams<" + method + ">", p.toString());
		new AsyncLoadData().execute("http://" + API_DOMAIN + "/" + method, p.toString());
	}

	public Json getResult () {
		return result;
	}

	private class AsyncLoadData extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... url) {
			Internet request = new Internet(context);
			String buffer = null;
			try {
				buffer = request.send(url[0], url[1]);
			} catch (NotAvailableInternetException e) {
				e.printStackTrace();
				publishProgress(-1);
			}
			return buffer;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
//			if (progress[0] == -1)

		}

		@Override
		protected void onPostExecute (String r) {
			Log.i("APIRequestResult<" + method + ">", r);
			result = Json.read(r);
			result = result.at(Const.RESPONSE);
			if (callback != null) {
				if (result.isObject() && result.has(Const.ERROR_ID))
					callback.onError(new APIError(result));
				else
					callback.onResult(result);
			}
		}
	}

	static void invokeAPIMethod (Context context, String method, HashMap<String, String> params, APICallback callback) {
		try {
			new API(context, method, params, callback).send();
		} catch (NotAvailableInternetException e) {
			e.printStackTrace();
		}
	}
}
