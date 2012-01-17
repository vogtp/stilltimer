package ch.almana.android.stillmeter.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import ch.almana.android.stillmeter.log.Logger;
import ch.almana.android.stillmeter.provider.db.DB;
import ch.almana.android.stillmeter.provider.db.DB.Day;
import ch.almana.android.stillmeter.provider.db.DB.OpenHelper;
import ch.almana.android.stillmeter.provider.db.DB.Session;
import ch.almana.android.stillmeter.provider.db.DB.StillTime;
import ch.almana.android.stillmeter.provider.db.DBBackendDay;
import ch.almana.android.stillmeter.provider.db.DBBackendSession;
import ch.almana.android.stillmeter.provider.db.DBBackendStillTime;

public class StillProvider extends ContentProvider {

	public static final String AUTHORITY = "ch.almana.android.stilltimer";

	private static final int STILL_TIME = 1;
	private static final int SESSION = 2;
	private static final int DAY = 3;

	private static final UriMatcher sUriMatcher;

	private static boolean notifyChanges = true;

	private OpenHelper openHelper;

	@Override
	public boolean onCreate() {
		openHelper = new OpenHelper(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Logger.logStacktrace("Deleting entry " + uri);
		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case STILL_TIME:
			count = DBBackendStillTime.delete(openHelper, uri, selection, selectionArgs);
			break;
		case SESSION:
			count = DBBackendSession.delete(openHelper, uri, selection, selectionArgs);
			break;
		case DAY:
			count = DBBackendDay.delete(openHelper, uri, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		notifyChange(uri);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case STILL_TIME:
			return DBBackendStillTime.getType(uri);
		case SESSION:
			return DBBackendSession.getType(uri);
		case DAY:
			return DBBackendDay.getType(uri);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Uri ret;
		switch (sUriMatcher.match(uri)) {
		case STILL_TIME:
			ret = DBBackendStillTime.insert(openHelper, uri, initialValues);
			break;
		case SESSION:
			ret = DBBackendSession.insert(openHelper, uri, initialValues);
			break;
		case DAY:
			ret = DBBackendDay.insert(openHelper, uri, initialValues);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		notifyChange(uri);
		return ret;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor c;
		switch (sUriMatcher.match(uri)) {
		case STILL_TIME:
			c = DBBackendStillTime.query(openHelper, uri, projection, selection, selectionArgs, sortOrder);
			break;
		case SESSION:
			c = DBBackendSession.query(openHelper, uri, projection, selection, selectionArgs, sortOrder);
			break;
		case DAY:
			c = DBBackendDay.query(openHelper, uri, projection, selection, selectionArgs, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case STILL_TIME:
			count = DBBackendStillTime.update(openHelper, uri, values, selection, selectionArgs);
			break;
		case SESSION:
			count = DBBackendSession.update(openHelper, uri, values, selection, selectionArgs);
			break;
		case DAY:
			count = DBBackendDay.update(openHelper, uri, values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		notifyChange(uri);
		return count;
	}

	private void notifyChange(Uri uri) {
		if (notifyChanges) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
	}

	public static void setNotifyChanges(boolean b) {
		notifyChanges = b;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, StillTime.CONTENT_ITEM_NAME, STILL_TIME);
		sUriMatcher.addURI(AUTHORITY, StillTime.CONTENT_ITEM_NAME + "/#", STILL_TIME);
		sUriMatcher.addURI(AUTHORITY, Session.CONTENT_ITEM_NAME, SESSION);
		sUriMatcher.addURI(AUTHORITY, Session.CONTENT_ITEM_NAME + "/#", SESSION);
		sUriMatcher.addURI(AUTHORITY, Day.CONTENT_ITEM_NAME, DAY);
		sUriMatcher.addURI(AUTHORITY, Day.CONTENT_ITEM_NAME + "/#", DAY);
	}

	public static void deleteAllTables(Context ctx) {
		ContentResolver resolver = ctx.getContentResolver();
		resolver.delete(DB.Session.CONTENT_URI, null, null);
		resolver.delete(DB.Day.CONTENT_URI, null, null);
		resolver.delete(DB.StillTime.CONTENT_URI, null, null);
	}

}
	