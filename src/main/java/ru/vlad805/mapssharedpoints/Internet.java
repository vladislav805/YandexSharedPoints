package ru.vlad805.mapssharedpoints;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import android.content.Context;
import android.net.ConnectivityManager;


public class Internet {
	private boolean state;
	private Context context;
	private HttpURLConnection connection = null;

	public Internet (Context ctx) {
		this.context = ctx;
		this.state = isNetworkAvailable();
	}
	public String send(String url, String body) throws NotAvailableInternetException {
		if (!state)
			throw new NotAvailableInternetException();
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.getOutputStream().write(body.getBytes("UTF-8"));
			connection.getResponseCode();
			InputStream is = new BufferedInputStream(connection.getInputStream(), 8192);
			String enc = connection.getHeaderField("Content-Encoding");
			if (enc != null && enc.equalsIgnoreCase("gzip"))
				is = new GZIPInputStream(is);
			return convertStreamToString(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		return null;
	}
	public static String convertStreamToString(InputStream is) throws IOException {
		InputStreamReader r = new InputStreamReader(is);
		StringWriter sw = new StringWriter();
		char[] buffer = new char[1024];
		try {
			for (int n; (n = r.read(buffer)) != -1;)
				sw.write(buffer, 0, n);
		} finally {
			try {
				is.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return sw.toString();
	}
	public boolean isNetworkAvailable () {
		return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
	}
	/*public static boolean isNetworkAvailable (Context context) {
		return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
	}*/
}