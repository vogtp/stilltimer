package ch.almana.android.stillmeter.helper;

import android.graphics.Color;

public class Settings {

	private static final int MIN_IN_MILLIES = 1000 * 60;
	private static Settings instance;

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	public long getMaxSessionAge() {
		return 30 * MIN_IN_MILLIES;
	}

	public int getLeftColor() {
		return Color.RED;
	}

	public int getRightColor() {
		return Color.YELLOW;
	}

}
