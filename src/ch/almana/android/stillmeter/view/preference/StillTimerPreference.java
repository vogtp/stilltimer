package ch.almana.android.stillmeter.view.preference;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import ch.almana.android.stillmeter.helper.Settings;
import ch.almana.android.stilltimer.R;

public class StillTimerPreference extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);

		handleColor(Settings.PREF_KEY_BREAST_COLOR_LEFT);
		handleColor(Settings.PREF_KEY_BREAST_COLOR_RIGHT);

	}

	private void handleColor(final String prefKey) {
		Preference preference = findPreference(prefKey);

		preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Context ctx = StillTimerPreference.this;
				int initialColor = Settings.getInstance(ctx).getColor(prefKey);
				AmbilWarnaDialog dialog = new AmbilWarnaDialog(ctx, initialColor, new OnAmbilWarnaListener() {
					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
					}
					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						Settings.getInstance(getApplicationContext()).setColor(prefKey, color);
					}

				});

				dialog.show();
				return true;
			}
		});

	}

}
