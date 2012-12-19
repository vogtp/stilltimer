package ch.almana.android.stillmeter.view.preference;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import ch.almana.android.db.importexport.BackupRestoreCallback;
import ch.almana.android.stillmeter.helper.BackupRestoreHelper;
import ch.almana.android.stillmeter.helper.Settings;
import ch.almana.android.stillmeter.log.Logger;
import ch.almana.android.stillmeter.provider.StillProvider;
import ch.almana.android.stilltimer.R;

public class StillTimerPreference extends PreferenceActivity implements BackupRestoreCallback {

	//	ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);

		handleColor(Settings.PREF_KEY_BREAST_COLOR_LEFT);
		handleColor(Settings.PREF_KEY_BREAST_COLOR_RIGHT);
		findPreference("prefKeyBackup").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				BackupRestoreHelper brh = new BackupRestoreHelper(StillTimerPreference.this);
				startProgressDialog(getString(R.string.msg_backuping));
				brh.backup();
				return true;
			}
		});
		findPreference("prefKeyRestore").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				BackupRestoreHelper brh = new BackupRestoreHelper(StillTimerPreference.this);
				startProgressDialog(getString(R.string.msg_restoring));
				try {
					brh.restore();
				} catch (Exception e) {
					Logger.e("Could not restore", e);
					Toast.makeText(StillTimerPreference.this, R.string.msg_error_restoring, Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});
		findPreference("prefKeyDeleteEverything").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Builder alertBuilder = new AlertDialog.Builder(StillTimerPreference.this);
				alertBuilder.setTitle(R.string.title_delete_tables);
				alertBuilder.setMessage(R.string.msg_delete_tables);
				alertBuilder.setNegativeButton(R.string.no, null);
				alertBuilder.setPositiveButton(R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StillProvider.deleteAllTables(StillTimerPreference.this);
					}
				});
				AlertDialog alert = alertBuilder.create();
				alert.show();
				return true;
			}
		});
	}

	private void handleColor(final String prefKey) {
		Preference preference = findPreference(prefKey);

		preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Context ctx = StillTimerPreference.this;
				int initialColor = Settings.getInstance(ctx).getColor(prefKey);
				AmbilWarnaDialog dialog = new AmbilWarnaDialog(ctx, initialColor, new OnAmbilWarnaListener() {
					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
					}

					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						Settings.getInstance(getApplicationContext()).setColor(prefKey, color);
					}

				});

				dialog.show();
				return true;
			}
		});

	}

	@Override
	public Context getContext() {
		return this;
	}

	private void startProgressDialog(CharSequence message) {
		//		progressDialog = new ProgressDialog(this);
		//		progressDialog.setMessage(message);
		//		progressDialog.show();
	}

	@Override
	public void hasFinished(boolean success) {
		//		if (progressDialog != null) {
		//			progressDialog.hide();
		//		}
	}

}
