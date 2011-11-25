package ch.almana.android.stillmeter.provider.db;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import ch.almana.android.stillmeter.provider.StillProvider;
import ch.almana.android.stillmeter.provider.db.DB.OpenHelper;
import ch.almana.android.stillmeter.provider.db.DB.Session;

public class DBBackendSession {

	private static HashMap<String, String> projectionMap;

	private static final int ENTITY = 1;
	private static final int ENTITY_ID = 2;

	private static final UriMatcher sUriMatcher;

	public static int delete(OpenHelper openHelper, Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case ENTITY:
			count = db.delete(Session.TABLE_NAME, selection, selectionArgs);
			break;

		case ENTITY_ID:
			String id = uri.getPathSegments().get(1);
			count = db.delete(Session.TABLE_NAME, DB.NAME_ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return count;
	}

	public static String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case ENTITY:
			return Session.CONTENT_TYPE;

		case ENTITY_ID:
			return Session.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	public static Cursor query(OpenHelper openHelper, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(Session.TABLE_NAME);
		qb.setProjectionMap(projectionMap);
		switch (sUriMatcher.match(uri)) {
		case ENTITY:
			break;

		case ENTITY_ID:
			qb.appendWhere(DB.NAME_ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = Session.SORTORDER_DEFAULT;
		} else {
			orderBy = sortOrder;
		}
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		return c;
	}

	public static int update(OpenHelper openHelper, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case ENTITY:
			count = db.update(Session.TABLE_NAME, values, selection, selectionArgs);
			break;

		case ENTITY_ID:
			String id = uri.getPathSegments().get(1);
			count = db.update(Session.TABLE_NAME, values, DB.NAME_ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return count;
	}

	public static Uri insert(OpenHelper openHelper, Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != ENTITY) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = db.insert(Session.TABLE_NAME, null, values);
		if (rowId > 0) {
			Uri retUri = ContentUris.withAppendedId(Session.CONTENT_URI, rowId);
			return retUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(StillProvider.AUTHORITY, Session.CONTENT_ITEM_NAME, ENTITY);
		sUriMatcher.addURI(StillProvider.AUTHORITY, Session.CONTENT_ITEM_NAME + "/#", ENTITY_ID);

		projectionMap = new HashMap<String, String>();
		for (String col : Session.colNames) {
			projectionMap.put(col, col);
		}
	}
}
