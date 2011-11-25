package ch.almana.android.stillmeter.model;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import ch.almana.android.stillmeter.model.BreastModel.Position;
import ch.almana.android.stillmeter.provider.db.DB;
import ch.almana.android.stillmeter.provider.db.DB.StillTime;

public class StillTimeModel {

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
		ContentValues values = new ContentValues();
		values.put(StillTime.NAME_BREAST, position.toString());
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
