package ru.vlad805.mapssharedpoints;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class InternetImage {

	public InternetImage (Context context, String url, ImageView view) {
		ImageLoader imageLoader = ImageLoader.getInstance();

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory()
				.cacheOnDisc()
				.build();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		imageLoader.displayImage(url, view, options);
	}
}
