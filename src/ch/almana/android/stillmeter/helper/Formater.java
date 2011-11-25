package ch.almana.android.stillmeter.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Formater {

	private static final SimpleDateFormat timeDateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	public static String timeElapsed(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		StringBuilder sb = new StringBuilder();
		int min = cal.get(Calendar.MINUTE);
		if (min < 10) {
			sb.append("0");
		}
		sb.append(min).append(":");
		int sec = cal.get(Calendar.SECOND);
		if (sec < 10) {
			sb.append("0");
		}
		sb.append(sec);
		return sb.toString();
	}

	public static String formatTime(Date time) {
		return timeFormat.format(time);
	}

	public static String formatDateTime(Date time) {
		return timeDateFormat.format(time);
	}
}
