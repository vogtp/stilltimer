package ch.almana.android.stillmeter.view.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import ch.almana.android.stillmeter.view.preference.StillTimerPreference;
import ch.almana.android.stilltimer.R;

public class TabMainActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);

		initTabs();
	}

	private void initTabs() {
		final TabHost tabHost = getTabHost();
		// tabHost.setup();

		tabHost.addTab(tabHost.newTabSpec("tabCheckin").setIndicator("Timer", getResources().getDrawable(R.drawable.ic_launcher))
				.setContent(new Intent(this, TimerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		tabHost.addTab(tabHost.newTabSpec("tabDays").setIndicator("Days", getResources().getDrawable(R.drawable.tab_day))
				.setContent(new Intent(this, DaysExpandList.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		tabHost.addTab(tabHost.newTabSpec("tabWekk").setIndicator("Graph",
				getResources().getDrawable(R.drawable.stats))
				.setContent(new Intent(this, GraphActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));


	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.gerneral_options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.itemSettings:
			i = new Intent(getApplicationContext(), StillTimerPreference.class);
			startActivity(i);
			break;

		}
		return super.onOptionsItemSelected(item);
	}
	//
	//	@Override
	//	public boolean onPrepareOptionsMenu(Menu menu) {
	//		super.onPrepareOptionsMenu(menu);
	//		MenuItem moreItems = menu.findItem(R.id.optionMore);
	//
	//		boolean emailExportEnabled = Settings.getInstance().isEmailExportEnabled();
	//		boolean backupEnabled = Settings.getInstance().isBackupEnabled();
	//
	//		moreItems.getSubMenu().findItem(R.id.itemExportTimestamps).setEnabled(emailExportEnabled);
	//		moreItems.getSubMenu().findItem(R.id.itemReadInTimestmaps).setEnabled(backupEnabled);
	//
	//		menu.findItem(R.id.itemHolidayEditor).setVisible(Settings.getInstance().isBetaVersion());
	//
	//		return true;
	//	}
}
