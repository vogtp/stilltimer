package ch.almana.android.stillmeter.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import ch.almana.android.stillmeter.log.Logger;

public class Settings {

	public static final int NO_WAKELOCK = -1;

	public static final String PREF_KEY_BREAST_COLOR_LEFT = "prefKeyBreastColorLeft";
	public static final String PREF_KEY_BREAST_COLOR_RIGHT = "prefKeyBreastColorRight";
	public static final int DEFAULT_COLOR_LEFT = Color.RED;
	public static final int DEFAULT_COLOR_RIGHT = Color.YELLOW;

	private static final int MIN_IN_MILLIES = 1000 * 60;
	private static Settings instance;

	private final Context context;

	public static Settings getInstance(Context ctx) {
		if (instance == null) {
			instance = new Settings(ctx.getApplicationContext());
		}
		return instance;
	}

	public Settings(Context ctx) {
		super();
		context = ctx;
	}

	protected SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public long getMaxSessionAge() {
		int sessionAge = 30;
		try {
			sessionAge = Integer.parseInt(getPreferences().getString("prefKeyMaxSessionAge", "30"));
		} catch (Exception e) {
			Logger.w("Cannot read MaxSessionAge from settings", e);
			sessionAge = 30;
		}
		return sessionAge * MIN_IN_MILLIES;
	}

	public int getColor(String prefKey) {
		int defaultColor = DEFAULT_COLOR_LEFT;
		if (PREF_KEY_BREAST_COLOR_RIGHT.equals(prefKey)) {
			defaultColor = DEFAULT_COLOR_RIGHT;
		}
		return getPreferences().getInt(prefKey, defaultColor);
	}

	public void setColor(String prefKey, int color) {
		Editor editor = getPreferences().edit();
		editor.putInt(prefKey, color);
		editor.commit();
	}

	public int getLeftColor() {
		return getColor(PREF_KEY_BREAST_COLOR_LEFT);
	}

	public int getRightColor() {
		return getColor(PREF_KEY_BREAST_COLOR_RIGHT);
	}

	public int getWakelockType() {
		int wakelock = PowerManager.SCREEN_DIM_WAKE_LOCK;
		try {
			wakelock = Integer.parseInt(getPreferences().getString("prefKeyWakelockType", Integer.toString(PowerManager.SCREEN_DIM_WAKE_LOCK)));
		} catch (Exception e) {
			Logger.w("Cannot read wakelock type from settings", e);
			wakelock = PowerManager.SCREEN_DIM_WAKE_LOCK;
		}
		return wakelock;
	}

}
