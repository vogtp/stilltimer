package ch.almana.android.stillmeter.view.activity;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import ch.almana.android.stillmeter.helper.Formater;
import ch.almana.android.stillmeter.model.BreastModel.Position;
import ch.almana.android.stillmeter.model.SessionModel;
import ch.almana.android.stillmeter.model.StillTimeModel;
import ch.almana.android.stillmeter.provider.db.DB;
import ch.almana.android.stillmeter.provider.db.DB.Session;
import ch.almana.android.stillmeter.provider.db.DB.StillTime;
import ch.almana.android.stillmeter.view.adapter.BreastSpinnerAdapter;
import ch.almana.android.stillmeter.view.adapter.TimeSpinnerAdapter;
import ch.almana.android.stilltimer.R;

public class SessionEditor extends Activity implements OnDateSetListener {

	public static final String EXTRA_DAY = "EXTRA_DAY";
	private Calendar currentStartTime;
	private Button buDay;
	private long dbId = -1;
	private Spinner spBreast1;
	private Spinner spBreast2;
	private Spinner spTime1;
	private Spinner spTime2;
	private TimePicker tpStartTime;
	private long time1;
	private long time2;
	private Position pos1;
	private Position pos2;
	private long origStartTime;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_editor);

		currentStartTime = Calendar.getInstance();

		String action = getIntent().getAction();
		long leftTime = 0;
		long rightTime = 0;
		if (Intent.ACTION_EDIT.equals(action)) {
			Uri uri = getIntent().getData();
			Cursor cursor = getContentResolver().query(uri, Session.PROJECTION_DEFAULT, null, null, Session.SORTORDER_DEFAULT);
			if (cursor.moveToFirst()) {
				dbId = cursor.getLong(DB.INDEX_ID);
				origStartTime = cursor.getLong(Session.INDEX_TIME_START);
				currentStartTime.setTimeInMillis(origStartTime);
				leftTime = cursor.getLong(Session.INDEX_BREAST_LEFT_TIME);
				rightTime = cursor.getLong(Session.INDEX_BREAST_RIGHT_TIME);
			}
		} else if (Intent.ACTION_INSERT.equals(action)) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				currentStartTime.setTimeInMillis(extras.getLong(EXTRA_DAY));
			}
		}

		buDay = (Button) findViewById(R.id.buDay);
		tpStartTime = (TimePicker) findViewById(R.id.tpStartTime);
		spBreast1 = (Spinner) findViewById(R.id.spBreast1);
		spBreast2 = (Spinner) findViewById(R.id.spBreast2);
		spTime1 = (Spinner) findViewById(R.id.spTime1);
		spTime2 = (Spinner) findViewById(R.id.spTime2);

		time1 = 0;
		time2 = 0;
		pos1 = Position.none;
		pos2 = Position.none;

		Cursor c = null;
		try {
			c = getContentResolver().query(StillTime.CONTENT_URI, StillTime.PROJECTION_DEFAULT, StillTime.SELECTION_BY_SESSION_ID, new String[] { Long.toString(dbId) },
					StillTime.SORTORDER_REVERSE);
			if (c.moveToFirst()) {
				Position breast = Position.valueOf(c.getString(StillTime.INDEX_BREAST));
				switch (breast) {
				case left:
					time1 = leftTime;
					time2 = rightTime;
					pos1 = Position.left;
					pos2 = Position.right;
					break;
				case right:
					time1 = rightTime;
					time2 = leftTime;
					pos1 = Position.right;
					pos2 = Position.left;
					break;

				default:
					break;
				}
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}

		spTime1.setAdapter(new TimeSpinnerAdapter(this, time1));
		spTime2.setAdapter(new TimeSpinnerAdapter(this, time2));
		BreastSpinnerAdapter breastAdapter1 = new BreastSpinnerAdapter(this);
		BreastSpinnerAdapter breastAdapter2 = new BreastSpinnerAdapter(this);
		spBreast1.setAdapter(breastAdapter1);
		spBreast2.setAdapter(breastAdapter2);
		spBreast1.setSelection(breastAdapter1.getPositionId(pos1));
		spBreast2.setSelection(breastAdapter2.getPositionId(pos2));

		buDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int year = currentStartTime.get(Calendar.YEAR);
				int monthOfYear = currentStartTime.get(Calendar.MONTH);
				int dayOfMonth = currentStartTime.get(Calendar.DAY_OF_MONTH);

				DatePickerDialog dpd = new DatePickerDialog(SessionEditor.this, SessionEditor.this, year, monthOfYear, dayOfMonth);
				dpd.show();
			}
		});

		final int hourOfDay = currentStartTime.get(Calendar.HOUR_OF_DAY);
		final int minute = currentStartTime.get(Calendar.MINUTE);

		tpStartTime.setIs24HourView(true);
		tpStartTime.setCurrentHour(hourOfDay);
		tpStartTime.setCurrentMinute(minute);

		updateView();
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		currentStartTime.set(Calendar.YEAR, year);
		currentStartTime.set(Calendar.MONTH, monthOfYear);
		currentStartTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		updateView();
	}


	private void updateView() {
		Date startDate = currentStartTime.getTime();
		buDay.setText(Formater.formatDate(startDate));
	}

	@Override
	protected void onPause() {
		if (hasChanges()) {
			Toast.makeText(this, "Saving changes", Toast.LENGTH_LONG).show();
			// FIXME ask
			save();
		}
		super.onPause();
	}

	private void save() {
		updateModel();
		long start = currentStartTime.getTimeInMillis();
		long dayDbId = SessionModel.getAndEnsureDay(this, currentStartTime.getTime());
		long leftTime = 0;
		long rightTime = 0;
		long leftStartTime = 0;
		long rightStartTime = 0;
		switch ((Position) spBreast1.getSelectedItem()) {
		case left:
			leftTime += (Long) spTime1.getSelectedItem();
			leftStartTime = start;
			break;
		case right:
			rightTime += (Long) spTime1.getSelectedItem();
			rightStartTime = start;
			break;
		}
		switch ((Position) spBreast2.getSelectedItem()) {
		case left:
			leftTime += (Long) spTime2.getSelectedItem();
			leftStartTime = start + rightTime;
			break;
		case right:
			rightTime += (Long) spTime2.getSelectedItem();
			rightStartTime = start + leftTime;
			break;
		}
		long totalTime = leftTime + rightTime;
		ContentValues sessionValues = SessionModel.getValues(dayDbId, start, start + totalTime, leftTime, rightTime, totalTime);
		ContentResolver resolver = getContentResolver();
		if (dbId > -1) {
			// update
			String[] selSessionId = new String[] { Long.toString(dbId) };
			resolver.update(Session.CONTENT_URI, sessionValues, DB.SELECTION_BY_ID, selSessionId);
			resolver.delete(StillTime.CONTENT_URI, StillTime.SELECTION_BY_SESSION_ID, selSessionId);
		} else {
			// insert
			dbId = ContentUris.parseId(resolver.insert(Session.CONTENT_URI, sessionValues));
		}
		if (leftTime > 0) {
			resolver.insert(StillTime.CONTENT_URI, StillTimeModel.getValues(Position.left, dbId, leftStartTime, leftStartTime + leftTime));
		}
		if (rightTime > 0) {
			resolver.insert(StillTime.CONTENT_URI, StillTimeModel.getValues(Position.right, dbId, rightStartTime, rightStartTime + rightTime));
		}
	}

	private boolean hasChanges() {
		updateModel();
		if (origStartTime != currentStartTime.getTimeInMillis()) {
			return true;
		}
		if (time1 != (Long) spTime1.getSelectedItem()) {
			return true;
		}
		if (time2 != (Long) spTime2.getSelectedItem()) {
			return true;
		}
		if (pos1 != (Position) spBreast1.getSelectedItem()) {
			return true;
		}
		if (pos2 != (Position) spBreast2.getSelectedItem()) {
			return true;
		}
		return false;
	}

	private void updateModel() {
		currentStartTime.set(Calendar.HOUR_OF_DAY, tpStartTime.getCurrentHour());
		currentStartTime.set(Calendar.MINUTE, tpStartTime.getCurrentMinute());
	}

}
