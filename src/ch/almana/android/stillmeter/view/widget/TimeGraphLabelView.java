package ch.almana.android.stillmeter.view.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import ch.almana.android.stilltimer.R;

public class TimeGraphLabelView extends View {

	private static final double HOUR_IN_MILLIES = 60d * 60d * 1000d;
	private static final double DAY_IN_MILLIES = 24d * HOUR_IN_MILLIES;
	private Paint timePaint;
	private float lineSep;
	private Paint timeLabelPaint;
	private Bitmap bitmap = null;
	private Canvas canvas = null;

	public TimeGraphLabelView(Context context) {
		super(context);
		init();
	}

	public TimeGraphLabelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TimeGraphLabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		Resources resources = getContext().getResources();
		timePaint = new Paint();
		timePaint.setColor(Color.DKGRAY);
		timePaint.setStrokeWidth(0);
		timePaint.setTextSize(resources.getDimension(R.dimen.graphTimeTextSize));
		timeLabelPaint = new Paint();
		timeLabelPaint.setColor(Color.LTGRAY);
		timeLabelPaint.setStrokeWidth(0);
		timeLabelPaint.setTextSize(resources.getDimension(R.dimen.graphTimeTextSize));
		lineSep = resources.getDimension(R.dimen.graphLineSep);

	}

	@Override
	protected void onDraw(Canvas c) {
		if (bitmap == null) {
			paint(c.getWidth(), c.getHeight());
		}
		c.drawBitmap(bitmap, 0, 0, null);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		bitmap = null;
		canvas = null;
		paint(w, h);
	}

	private void paint(int width, int height) {
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			canvas = new Canvas(bitmap);
		}
		double minInPx = width / DAY_IN_MILLIES;

		for (int i = 0; i < 25; i++) {
			StringBuilder sb = new StringBuilder();
			if (i < 10) {
				sb.append("0");
			}
			sb.append(i);
			double i2 = i;
			float pos = (float) (i2 * HOUR_IN_MILLIES * minInPx);
			canvas.drawText(sb.toString(), pos + 1, lineSep, timeLabelPaint);
			canvas.drawLine(pos, 0, pos, height, timePaint);
		}
	}

}
