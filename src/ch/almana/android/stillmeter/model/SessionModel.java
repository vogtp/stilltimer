package ch.almana.android.stillmeter.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import ch.almana.android.stillmeter.helper.Settings;
import ch.almana.android.stillmeter.log.Logger;
import ch.almana.android.stillmeter.model.BreastModel.Position;
import ch.almana.android.stillmeter.provider.db.DB;
import ch.almana.android.stillmeter.provider.db.DB.Day;
import ch.almana.android.stillmeter.provider.db.DB.Session;

public class SessionModel {

	private static final SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.yyyy");

	private final EnumMap<Position, BreastModel> breastModels = new EnumMap<Position, BreastModel>(Position.class);

	private long dbId = -1;
	private final String day;
	private long dayDbId = -1;

	private long start = -1;
	private long end = -1;

	private long lastAction;

	private Position position = Position.none;

	public SessionModel(Context ctx) {
		super();
		day = dayFormat.format(new Date());
		start = System.currentTimeMillis();
		ensureDay(ctx);
		insertOrUpdate(ctx);
		breastModels.put(Position.left, new BreastModel(Position.left, dbId));
		breastModels.put(Position.right, new BreastModel(Position.right, dbId));
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
		breastModels.get(pos).end(ctx);
		end = System.currentTimeMillis();
		position = Position.none;
		insertOrUpdate(ctx);
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(Session.NAME_DAY, dayDbId);
		values.put(Session.NAME_TIME_START, start);
		values.put(Session.NAME_TIME_END, end);
		values.put(Session.NAME_BREAST_LEFT_TIME, getLeftTime());
		values.put(Session.NAME_BREAST_RIGHT_TIME, getRightTime());
		values.put(Session.NAME_TOTAL_TIME, getTotalTime());
		return values;
	}

	private void insertOrUpdate(Context ctx) {
		if (dbId > -1) {
			ctx.getContentResolver().update(Session.CONTENT_URI, getValues(), DB.SELECTION_BY_ID, new String[] { Long.toString(dbId) });
		} else {
			Uri uri = ctx.getContentResolver().insert(Session.CONTENT_URI, getValues());
			dbId = ContentUris.parseId(uri);
		}
	}

	private void ensureDay(Context ctx) {
		Cursor c = null;
		try {
			if (day == null) {
				return;
			}
			c = ctx.getContentResolver().query(Day.CONTENT_URI, Day.PROJECTION_DEFAULT, Day.SELECTION_BY_DAY, new String[] { day }, Day.SORTORDER_DEFAULT);
			if (c.moveToFirst()) {
				dayDbId = c.getLong(DB.INDEX_ID);
			} else {
				ContentValues values = new ContentValues();
				values.put(Day.NAME_DAY, day);
				values.put(Day.NAME_TIME_START, start);
				Uri uri = ctx.getContentResolver().insert(Day.CONTENT_URI, values);
				dayDbId = ContentUris.parseId(uri);
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
		if (System.currentTimeMillis() - lastAction > Settings.getInstance().getMaxSessionAge()) {
			Logger.i("Session too old");
			return true;
		}
		Logger.i("Session not too old");
		return false;
	}

	public Position getPossition() {
		return position;
	}
}
