package ch.almana.android.stillmeter.view.activity;

import android.app.Activity;
import android.os.Bundle;
import ch.almana.android.stillmeter.view.widget.TimeGraphView;
import ch.almana.android.stilltimer.R;

public class GraphActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);

		TimeGraphView timeGraphView = (TimeGraphView) findViewById(R.id.timeGraphView);

	}
}
