package ch.almana.android.stillmeter.model;

import android.content.Context;
import ch.almana.android.stillmeter.log.Logger;

public class BreastModel {


	public enum Position {
		none, left, right
	};

	private final Position position;
	private StillTimeModel currentStillTimeModel = null;

	private long totalTime = 0;
	private final long session;

	public BreastModel(Position position, long session) {
		super();
		this.session = session;
		this.position = position;
	}

	public void start(Context ctx) {
		currentStillTimeModel = new StillTimeModel(ctx, position, session, System.currentTimeMillis());
	}

	public void end(Context ctx) {
		if (currentStillTimeModel == null) {
			Logger.w("BreastModel does not have a StillTimeModel");
			return;
		}
		currentStillTimeModel.setEndTime(ctx, System.currentTimeMillis());
		totalTime += currentStillTimeModel.getStillTime();
		currentStillTimeModel = null;
	}

	public long getTime() {
		long t = totalTime;
		if (currentStillTimeModel != null) {
			t += currentStillTimeModel.getStillTime();
		}
		return t;
	}


}
