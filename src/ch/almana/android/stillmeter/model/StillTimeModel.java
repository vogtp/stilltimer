package ch.almana.android.stillmeter.model;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import ch.almana.android.stillmeter.model.BreastModel.Position;
import ch.almana.android.stillmeter.provider.db.DB;
import ch.almana.android.stillmeter.provider.db.DB.StillTime;

public class StillTimeModel {

	private static final String BUNDLE_DBID = "BUNDLE_DBID";

	private static final String BUNDLE_SESSION = "BUNDLE_SESSION";

	private static final String BUNDLE_STARTTIME = "BUNDLE_STARTTIME";

	private static final String BUNDLE_ENDTIME = "BUNDLE_ENDTIME";

	private static final String BUNDLE_POSITION = "BUNDLE_POSITION";

	private Position position = Position.none;

	private long dbId = -1;

	private long session = -1;
	
	private long startTime = -1;

	private long endTime = -1;

	private StillTimeModel() {
		super();
	}

	public StillTimeModel(Context ctx, Position position, long session, long startTime, long endTime) {
		this();
		this.session = session;
		this.position = position;
		this.startTime = startTime;
		this.endTime = endTime;
		insertOrUpdate(ctx);
	}

	public StillTimeModel(Context ctx, Position position, long session, long startTime) {
		this(ctx, position, session, startTime, -1);
	}

	public StillTimeModel(Bundle state) {
		this();
		dbId = state.getLong(BUNDLE_DBID);
		session = state.getLong(BUNDLE_SESSION);
		startTime = state.getLong(BUNDLE_STARTTIME);
		endTime = state.getLong(BUNDLE_ENDTIME);
		position = Position.valueOf(state.getString(BUNDLE_POSITION));
	}

	public Bundle getBundle() {
		Bundle state = new Bundle();
		state.putLong(BUNDLE_DBID, dbId);
		state.putLong(BUNDLE_SESSION, session);
		state.putLong(BUNDLE_STARTTIME, startTime);
		state.putLong(BUNDLE_ENDTIME, endTime);
		state.putString(BUNDLE_POSITION, position.toString());
		return state;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(Context ctx, long startTime) {
		this.startTime = startTime;
		insertOrUpdate(ctx);
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(Context ctx, long endTime) {
		this.endTime = endTime;
		insertOrUpdate(ctx);
	}

	public long getDay() {
		return session;
	}

	public long getStillTime() {
		if (endTime > 0) {
			return endTime - startTime;
		}
		return System.currentTimeMillis() - startTime;
	}

	public ContentValues getValues() {
		return getValues(position, session, startTime, endTime);
	}

	public static ContentValues getValues(Position pos, long session, long startTime, long endTime) {
		ContentValues values = new ContentValues();
		values.put(StillTime.NAME_BREAST, pos.toString());
		values.put(StillTime.NAME_SESSION, session);
		values.put(StillTime.NAME_TIME_START, startTime);
		values.put(StillTime.NAME_TIME_END, endTime);
		return values;
	}


	private void insertOrUpdate(Context ctx) {
		if (dbId > -1) {
			ctx.getContentResolver().update(StillTime.CONTENT_URI, getValues(), DB.SELECTION_BY_ID, new String[] { Long.toString(dbId) });
		} else {
			Uri uri = ctx.getContentResolver().insert(StillTime.CONTENT_URI, getValues());
			dbId = ContentUris.parseId(uri);
		}
	}


}
