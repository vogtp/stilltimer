package ch.almana.android.stillmeter.view.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ExpandableListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.SimpleCursorTreeAdapter.ViewBinder;
import android.widget.TextView;
import ch.almana.android.stillmeter.helper.Formater;
import ch.almana.android.stillmeter.provider.db.DB;
import ch.almana.android.stillmeter.provider.db.DB.Day;
import ch.almana.android.stillmeter.provider.db.DB.Session;
import ch.almana.android.stillmeter.provider.db.DB.StillTime;
import ch.almana.android.stilltimer.R;

public class DaysExpandList extends ExpandableListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Cursor c = managedQuery(DB.Day.CONTENT_URI, DB.Day.PROJECTION_DEFAULT, null, null, DB.Day.SORTORDER_DEFAULT);
		SimpleCursorTreeAdapter adapter = new SimpleCursorTreeAdapter(this,
				c,
				R.layout.day_list_item_main,
				new String[] { Day.NAME_DAY },
				new int[] { R.id.tvDay },
				android.R.layout.two_line_list_item,
				new String[] { DB.Session.NAME_TIME_START, DB.Session.NAME_TOTAL_TIME },
				new int[] { android.R.id.text1, android.R.id.text2 }) {

			@Override
			protected Cursor getChildrenCursor(Cursor groupCursor) {
				long id = groupCursor.getLong(DB.INDEX_ID);
				return managedQuery(Session.CONTENT_URI, Session.PROJECTION_DEFAULT, Session.SELECTION_BY_DAY, new String[] { Long.toString(id) }, Session.SORTORDER_DEFAULT);
			}
		};

		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (columnIndex == Session.INDEX_TIME_START) {
					((TextView) view).setText(Formater.sessionTime(cursor));
					return true;
				} else if (columnIndex == Session.INDEX_TOTAL_TIME) {
					StringBuilder sb = new StringBuilder();
					sb.append(getString(R.string.labelTotal)).append(": ").append(Formater.timeElapsed(cursor.getLong(Session.INDEX_TOTAL_TIME)));
					sb.append("; ").append(getString(R.string.left)).append(": ").append(Formater.timeElapsed(cursor.getLong(Session.INDEX_BREAST_LEFT_TIME)));
					sb.append("; ").append(getString(R.string.right)).append(": ").append(Formater.timeElapsed(cursor.getLong(Session.INDEX_BREAST_RIGHT_TIME)));

					((TextView) view).setText(sb.toString());
					return true;
				}
				return false;
			}
		});

		getExpandableListView().setAdapter(adapter);
		getExpandableListView().setOnCreateContextMenuListener(this);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		super.onChildClick(parent, v, groupPosition, childPosition, id);
		Uri uri = ContentUris.withAppendedId(DB.Session.CONTENT_URI, id);
		startActivity(new Intent(Intent.ACTION_EDIT, uri));
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		int type = ExpandableListView.getPackedPositionType(((ExpandableListContextMenuInfo) menuInfo).packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			getMenuInflater().inflate(R.menu.list_context, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		final Uri uri = ContentUris.withAppendedId(Session.CONTENT_URI, info.id);

		switch (item.getItemId()) {
		case R.id.itemInsert:
			Intent intent = new Intent(Intent.ACTION_INSERT, Session.CONTENT_URI);
			Cursor c = null;
			try {
				c = getContentResolver().query(uri, Session.PROJECTION_DEFAULT, null, null, Session.SORTORDER_DEFAULT);
				if (c.moveToFirst()) {
					intent.putExtra(SessionEditor.EXTRA_DAY, c.getLong(Session.INDEX_TIME_START));
				}
			} finally {
				if (c != null) {
					c.close();
				}
			}
			startActivity(intent);
			break;
		case R.id.itemEdit:
			startActivity(new Intent(Intent.ACTION_EDIT, uri));
			break;

		case R.id.itemDelete:
			Builder alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle(R.string.title_delete_session);
			alertBuilder.setMessage(R.string.msg_delete_session);
			alertBuilder.setNegativeButton(R.string.no, null);
			alertBuilder.setPositiveButton(R.string.yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					long sessId = ContentUris.parseId(uri);
					getContentResolver().delete(uri, null, null);
					getContentResolver().delete(StillTime.CONTENT_URI, StillTime.SELECTION_BY_SESSION_ID, new String[] { Long.toString(sessId) });
				}
			});
			AlertDialog alert = alertBuilder.create();
			alert.show();
			break;
		}
		return true;
	}

}
