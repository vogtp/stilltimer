package ch.almana.android.stillmeter.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import ch.almana.android.stillmeter.helper.Settings;
import ch.almana.android.stillmeter.log.Logger;
import ch.almana.android.stillmeter.model.BreastModel.Position;
import ch.almana.android.stillmeter.provider.db.DB;
import ch.almana.android.stillmeter.provider.db.DB.Day;
import ch.almana.android.stillmeter.provider.db.DB.Session;

public class SessionModel {

	private static final SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.yyyy");

	private static final String BUNDLE_LEFT_BREAST = "BUNDLE_LEFT_BREAST";
	private static final String BUNDLE_RIGHT_BREAST = "BUNDLE_RIGHT_BREAST";
	private static final String BUNDLE_DBID = "BUNDLE_DBID";
	private static final String BUNDLE_DAY = "BUNDLE_DAY";
	private static final String BUNDLE_DAY_DBID = "BUNDLE_DAY_DBID";
	private static final String BUNDLE_START = "BUNDLE_START";
	private static final String BUNDLE_END = "BUNDLE_END";
	private static final String BUNDLE_LAST_ACTION = "BUNDLE_LAST_ACTION";
	private static final String BUNDLE_POSITION = "BUNDLE_POSITION";

	private final EnumMap<Position, BreastModel> breastModels = new EnumMap<Position, BreastModel>(Position.class);

	private long dbId = -1;
	//	private final String day;
	private long dayDbId = -1;

	private long start = -1;
	private long end = -1;

	private long lastAction;

	private Position position = Position.none;

	private Context context;

	public SessionModel(Context ctx) {
		super();
		context = ctx.getApplicationContext();
		dayDbId = getAndEnsureDay(context, new Date());
		start = System.currentTimeMillis();
		insertOrUpdate(context);
		breastModels.put(Position.left, new BreastModel(Position.left, dbId));
		breastModels.put(Position.right, new BreastModel(Position.right, dbId));
	}

	public SessionModel(Bundle inState) {
		super();
		dbId = inState.getLong(BUNDLE_DBID);
		dayDbId = inState.getLong(BUNDLE_DAY_DBID);
		start = inState.getLong(BUNDLE_START);
		end = inState.getLong(BUNDLE_END);
		lastAction = inState.getLong(BUNDLE_LAST_ACTION);
		position = Position.valueOf(inState.getString(BUNDLE_POSITION));
		loadBreastState(BUNDLE_LEFT_BREAST, Position.left, inState);
		loadBreastState(BUNDLE_RIGHT_BREAST, Position.right, inState);
	}

	public ContentValues getValues() {
		return getValues(dayDbId, start, end, getLeftTime(), getRightTime(), getTotalTime());
	}

	public static ContentValues getValues(long dayDbId, long start, long end, long leftTime, long rightTime, long totalTime) {
		ContentValues values = new ContentValues();
		values.put(Session.NAME_DAY, dayDbId);
		values.put(Session.NAME_TIME_START, start);
		values.put(Session.NAME_TIME_END, end);
		values.put(Session.NAME_BREAST_LEFT_TIME, leftTime);
		values.put(Session.NAME_BREAST_RIGHT_TIME, rightTime);
		values.put(Session.NAME_TOTAL_TIME, totalTime);
		return values;
	}

	public void saveInstanceState(Bundle outState) {
		outState.putLong(BUNDLE_DBID, dbId);
		outState.putLong(BUNDLE_DAY_DBID, dayDbId);
		outState.putLong(BUNDLE_START, start);
		outState.putLong(BUNDLE_END, end);
		outState.putLong(BUNDLE_LAST_ACTION, lastAction);
		outState.putString(BUNDLE_POSITION, position.toString());
		saveBreastState(BUNDLE_LEFT_BREAST, Position.left, outState);
		saveBreastState(BUNDLE_RIGHT_BREAST, Position.right, outState);
	}

	private void loadBreastState(String key, Position pos, Bundle inState) {
		Bundle state = inState.getBundle(key);
		if (state != null) {
			breastModels.put(pos, new BreastModel(dbId, state));
		}
	}

	private void saveBreastState(String key, Position pos, Bundle outState) {
		BreastModel breastModel = breastModels.get(pos);
		if (breastModel != null) {
			outState.putBundle(key, breastModel.getBundle());
		}
	}

	public long getTotalTime() {
		return (getLeftTime() / 1000 + getRightTime() / 1000) * 1000;
	}

	public long getLeftTime() {
		if (breastModels.get(Position.left) == null) {
			return 0;
		}
		return breastModels.get(Position.left).getTime();
	}

	public long getRightTime() {
		if (breastModels.get(Position.right) == null) {
			return 0;
		}
		return breastModels.get(Position.right).getTime();
	}

	public void startFeeding(Context ctx, Position pos) {
		long now = System.currentTimeMillis();
		lastAction = now;
		if (position != Position.none) {
			breastModels.get(position).end(ctx);
		}
		breastModels.get(pos).start(ctx);
		position = pos;
		insertOrUpdate(ctx);
	}

	public void endFeeding(Context ctx, Position pos) {
		lastAction = System.currentTimeMillis();
		// npe?
		breastModels.get(pos).end(ctx);
		end = System.currentTimeMillis();
		position = Position.none;
		insertOrUpdate(ctx);
	}

	private void insertOrUpdate(Context ctx) {
		if (dbId > -1) {
			ctx.getContentResolver().update(Session.CONTENT_URI, getValues(), DB.SELECTION_BY_ID, new String[] { Long.toString(dbId) });
		} else {
			Uri uri = ctx.getContentResolver().insert(Session.CONTENT_URI, getValues());
			dbId = ContentUris.parseId(uri);
		}
	}

	public static long getAndEnsureDay(Context ctx, Date time) {
		Cursor c = null;
		String day = dayFormat.format(time);
		try {
			c = ctx.getContentResolver().query(Day.CONTENT_URI, Day.PROJECTION_DEFAULT, Day.SELECTION_BY_DAY, new String[] { day }, Day.SORTORDER_DEFAULT);
			if (c.moveToFirst()) {
				return c.getLong(DB.INDEX_ID);
			} else {
				ContentValues values = new ContentValues();
				values.put(Day.NAME_DAY, day);
				values.put(Day.NAME_TIME_START, time.getTime());
				Uri uri = ctx.getContentResolver().insert(Day.CONTENT_URI, values);
				return ContentUris.parseId(uri);
			}
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}
	}

	public boolean isTooOld() {
		if (lastAction < 0) {
			Logger.i("Session not too old since it is never used");
			return false;
		}
		try {
			if (System.currentTimeMillis() - lastAction > Settings.getInstance(context).getMaxSessionAge()) {
				Logger.i("Session too old");
				return true;
			}
		} catch (Exception e) {
			Logger.i("Session age produces error", e);
			return true;
		}
		Logger.i("Session not too old");
		return false;
	}

	public Position getPossition() {
		return position;
	}

	public long getStartTime() {
		return start;
	}
}
