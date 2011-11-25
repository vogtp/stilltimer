package ch.almana.android.stillmeter.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import ch.almana.android.stillmeter.helper.Formater;
import ch.almana.android.stillmeter.helper.Settings;
import ch.almana.android.stillmeter.model.BreastModel.Position;
import ch.almana.android.stillmeter.model.SessionModel;
import ch.almana.android.stilltimer.R;

public class TimerActivity extends Activity {

	private Button buLeft;
	private Button buRight;
	private TextView tvLeft;
	private TextView tvRight;
	private TextView tvTotal;

	private static SessionModel sessionModel;
	private static WakeLock wakeLock = null;

	private Handler handler;

	private final Runnable updateView = new Runnable() {
		@Override
		public void run() {
			updateView();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timer);

		if (sessionModel == null) {
			sessionModel = new SessionModel(this);
		}
		handler = new Handler();

		buLeft = (Button) findViewById(R.id.buLeft);
		buRight = (Button) findViewById(R.id.buRight);
		tvLeft = (TextView) findViewById(R.id.tvLeft);
		tvRight = (TextView) findViewById(R.id.tvRight);
		tvTotal = (TextView) findViewById(R.id.tvTotal);

		Settings settings = Settings.getInstance();

		buLeft.setHeight(buLeft.getWidth());
		buLeft.setTextColor(settings.getLeftColor());
		buRight.setHeight(buRight.getWidth());
		buRight.setTextColor(settings.getRightColor());

		buLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonClick(Position.left);
			}
		});

		buRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonClick(Position.right);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (sessionModel.isTooOld()) {
			sessionModel = new SessionModel(this);
		}
		updateView();
		runTimerHandler();
	}

	@Override
	protected void onPause() {
		handler.removeCallbacks(updateView);
		super.onPause();
	}

	protected void updateView() {
		buRight.setText(R.string.right);
		buLeft.setText(R.string.left);
		switch (sessionModel.getPossition()) {
		case left:
			buLeft.setText(R.string.buLeftOn);
			break;
		case right:
			buRight.setText(R.string.buRightOn);
			break;
		}
		long l = sessionModel.getLeftTime();
		long r = sessionModel.getRightTime();
		long t = sessionModel.getTotalTime();
		updateTextView(tvLeft, l);
		updateTextView(tvRight, r);
		updateTextView(tvTotal, t);
		runTimerHandler();
	}

	protected void updateTextView(TextView tv, long time) {
		if (time < 500) {
			return;
		}
		tv.setText(Formater.timeElapsed(time));
	}

	protected void buttonClick(Position pos) {
		if (sessionModel.isTooOld()) {
			sessionModel = new SessionModel(this);
		}
		if (pos != sessionModel.getPossition()) {
			sessionModel.startFeeding(this, pos);
		} else {
			sessionModel.endFeeding(this, pos);
		}
		if (sessionModel.getPossition() == Position.none) {
			releaseWakelock();
		} else {
			acquireWakelock();
		}
		updateView();
	}

	private void runTimerHandler() {
		handler.postDelayed(updateView, 1000);
	}

	private void acquireWakelock() {
		PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "stilltimer");
		wakeLock.acquire();
	}

	private void releaseWakelock() {
		if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}