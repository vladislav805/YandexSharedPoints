package ru.vlad805.mapssharedpoints;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class TranslationActivity extends ExtendedActivity implements View.OnClickListener {

	private Settings settings;
	private boolean isStarted = false;
	private Button bSwitcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_translation);

		addBackButton();

		settings = new Settings(this);

		bSwitcher = (Button) findViewById(R.id.translation_switcher);
		bSwitcher.setOnClickListener(this);

		Log.e("TRACT", TranslationService.mState.toString());

		if (TranslationService.mState == StateService.RUNNING) {
			isStarted = true;
			updateUi();
		}
	}

	@Override
	public void onClick (View v) {
		switch (v.getId()) {
			case R.id.translation_switcher:
				switchTranslation();
				break;
		}
	}

	private void updateUi () {
		bSwitcher.setText(isStarted ? R.string.translation_stop : R.string.translation_start);
	}

	private void switchTranslation () {
		isStarted = !isStarted;
		updateUi();
		if (isStarted) {
			startService(new Intent(this, TranslationService.class));
		} else {
			stopService(new Intent(this, TranslationService.class));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_translation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
