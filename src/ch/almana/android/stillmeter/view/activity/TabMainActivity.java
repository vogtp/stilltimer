package ch.almana.android.stillmeter.view.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
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



	//	@Override
	//	protected void onDestroy() {
	//		instance = null;
	//		super.onDestroy();
	//	}

	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		super.onCreateOptionsMenu(menu);
	//		getMenuInflater().inflate(R.menu.general_option, menu);
	//		return true;
	//	}
	//
	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item) {
	//		Intent i;
	//		switch (item.getItemId()) {
	//		// case R.id.itemDaysList:
	//		// i = new Intent(this, ListDays.class);
	//		// startActivity(i);
	//		// break;
	//		case R.id.itemExportTimestamps:
	//			if (Settings.getInstance().isEmailExportEnabled()) {
	//				i = new Intent(this, ExportTimestamps.class);
	//				startActivity(i);
	//			} else {
	//				DialogHelper.showFreeVersionDialog(this);
	//			}
	//			break;
	//
	//		case R.id.itemReadInTimestmaps:
	//			if (Settings.getInstance().isBackupEnabled()) {
	//				i = new Intent(this, BackupRestoreActivity.class);
	//				startActivity(i);
	//			} else {
	//				DialogHelper.showFreeVersionDialog(this);
	//			}
	//			break;
	//
	//		case R.id.itemPreferences:
	//			i = new Intent(getApplicationContext(), StechkartePreferenceActivity.class);
	//			startActivity(i);
	//			break;
	//
	//		case R.id.itemHolidayEditor:
	//			i = new Intent(this, HolidaysEditor.class);
	//			startActivity(i);
	//			break;
	//
	//		case R.id.itemFAQ:
	//			i = new Intent(Intent.ACTION_DEFAULT, Uri.parse("http://clockcard.sourceforge.net/faq.html"));
	//			startActivity(i);
	//			break;
	//
	//		}
	//		return super.onOptionsItemSelected(item);
	//	}
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
