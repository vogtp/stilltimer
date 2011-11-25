package ch.almana.android.stillmeter.view.widget;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import ch.almana.android.stillmeter.helper.Settings;
import ch.almana.android.stillmeter.log.Logger;
import ch.almana.android.stillmeter.model.BreastModel.Position;
import ch.almana.android.stillmeter.provider.db.DB.Day;
import ch.almana.android.stillmeter.provider.db.DB.StillTime;
import ch.almana.android.stilltimer.R;

public class TimeGraphView extends View {

	private static final double HOUR_IN_MILLIES = 60d * 60d * 1000d;
	private static final double DAY_IN_MILLIES = 24d * HOUR_IN_MILLIES;
	private Paint dayPaint;
	private Paint timePaint;
	private Paint graphPaint;
	private float lineSep;
	private float strokeWidth;
	private Paint timeLabelPaint;

	public TimeGraphView(Context context) {
		super(context);
		init();
	}

	public TimeGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TimeGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		Resources resources = getContext().getResources();
		dayPaint = new Paint();
		dayPaint.setColor(Color.LTGRAY);
		dayPaint.setStrokeWidth(0);
		dayPaint.setTextSize(resources.getDimension(R.dimen.graphDayTextSize));
		timePaint = new Paint();
		timePaint.setColor(Color.DKGRAY);
		timePaint.setStrokeWidth(0);
		timePaint.setTextSize(resources.getDimension(R.dimen.graphTimeTextSize));
		timeLabelPaint = new Paint();
		timeLabelPaint.setColor(Color.LTGRAY);
		timeLabelPaint.setStrokeWidth(0);
		timeLabelPaint.setTextSize(resources.getDimension(R.dimen.graphTimeTextSize));
		graphPaint = new Paint();
		graphPaint.setColor(Color.RED);
		strokeWidth = resources.getDimension(R.dimen.graphStroke);
		graphPaint.setStrokeWidth(strokeWidth);
		lineSep = resources.getDimension(R.dimen.graphLineSep);
		Cursor dayCursor = null;
		try {
			dayCursor = getContext().getContentResolver().query(Day.CONTENT_URI, Day.PROJECTION_DEFAULT, null, null, Day.SORTORDER_DEFAULT);
			setMinimumHeight(Math.round((dayCursor.getCount() + 2) * lineSep));
		} finally {
			if (dayCursor != null) {
				dayCursor.close();
			}
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		double minInPx = width / DAY_IN_MILLIES;
		ContentResolver resolver = getContext().getContentResolver();
		Cursor dayCursor = null;
		Cursor timeCursor = null;

		for (int i = 0; i < 25; i++) {
			StringBuilder sb = new StringBuilder();
			if (i < 10) {
				sb.append("0");
			}
			sb.append(i);
			double i2 = i;
			float pos = (float) (i2 * HOUR_IN_MILLIES * minInPx);
			canvas.drawText(sb.toString(), pos, lineSep, timeLabelPaint);
			canvas.drawLine(pos, 0, pos, height, timePaint);
		}

		Calendar cal = Calendar.getInstance();
		try {

			Settings settings = Settings.getInstance();
			int leftColor = settings.getLeftColor();
			int rightColor = settings.getRightColor();
			dayCursor = resolver.query(Day.CONTENT_URI, Day.PROJECTION_DEFAULT, null, null, Day.SORTORDER_DEFAULT);
			float day = 2f;
			while (dayCursor.moveToNext()) {
				float yPos = day++ * lineSep;
				canvas.drawLine(0, yPos, width, yPos, dayPaint);
				cal.setTimeInMillis(dayCursor.getLong(Day.INDEX_TIME_START));
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				long dayStart = cal.getTimeInMillis();
				String dayStartStr = Long.toString(dayStart);
				cal.add(Calendar.DAY_OF_YEAR, 1);
				String dayEndStr = Long.toString(cal.getTimeInMillis());
				timeCursor = resolver.query(StillTime.CONTENT_URI, StillTime.PROJECTION_DEFAULT, StillTime.SELECTION_START_END, new String[] { dayStartStr, dayEndStr },
						StillTime.SORTORDER_DEFAULT);
				while (timeCursor.moveToNext()) {
					long startTime = timeCursor.getLong(StillTime.INDEX_TIME_START);
					long endTime = timeCursor.getLong(StillTime.INDEX_TIME_END);
					double t1 = startTime - dayStart;
					double dt1 = endTime - startTime;
					double t = t1 * minInPx;
					double dt = dt1 * minInPx;
					if (t < 0 || dt < 0) {
						continue;
					}
					if (dt < 1) {
						dt = 1;
					}
					Logger.d("Line: " + t1 + "/" + dt1 + " -> " + t + "/" + dt);
					float p = 0;
					switch (Position.valueOf(timeCursor.getString(StillTime.INDEX_BREAST))) {
					case left:
						graphPaint.setColor(leftColor);
						p = yPos + strokeWidth / 2f;
						break;
					case right:
						graphPaint.setColor(rightColor);
						p = yPos - strokeWidth / 2f;
						break;
					case none:
						graphPaint.setColor(Color.LTGRAY);
						p = yPos - strokeWidth / 2f;
						break;
					}

					canvas.drawLine((float) t, p, (float) (t + dt), p, graphPaint);
				}
				if (timeCursor != null) {
					timeCursor.close();
				}
				canvas.drawText(dayCursor.getString(Day.INDEX_DAY), 5, yPos, dayPaint);
			}

		} finally {
			if (dayCursor != null) {
				dayCursor.close();
			}
			if (timeCursor != null) {
				timeCursor.close();
			}
		}
	}

}
