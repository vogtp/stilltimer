package ch.almana.android.stillmeter.view.activity;

import java.util.Date;

import android.app.ExpandableListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.SimpleCursorTreeAdapter.ViewBinder;
import android.widget.TextView;
import ch.almana.android.stillmeter.helper.Formater;
import ch.almana.android.stillmeter.provider.db.DB;
import ch.almana.android.stillmeter.provider.db.DB.Day;
import ch.almana.android.stillmeter.provider.db.DB.Session;
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
					StringBuilder sb = new StringBuilder();
					Date time = new Date(cursor.getLong(Session.INDEX_TIME_START));
					sb.append(Formater.formatTime(time));
					time.setTime(cursor.getLong(Session.INDEX_TIME_END));
					sb.append(" - ").append(Formater.formatTime(time));
					((TextView) view).setText(sb.toString());
					return true;
				} else if (columnIndex == Session.INDEX_TOTAL_TIME) {
					StringBuilder sb = new StringBuilder();
					sb.append("Total: ").append(Formater.timeElapsed(cursor.getLong(Session.INDEX_TOTAL_TIME)));
					sb.append("; Left: ").append(Formater.timeElapsed(cursor.getLong(Session.INDEX_BREAST_LEFT_TIME)));
					sb.append("; Right: ").append(Formater.timeElapsed(cursor.getLong(Session.INDEX_BREAST_RIGHT_TIME)));

					((TextView) view).setText(sb.toString());
					return true;
				}
				return false;
			}
		});

		getExpandableListView().setAdapter(adapter);
	}
	
}
