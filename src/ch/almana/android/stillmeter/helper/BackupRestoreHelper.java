package ch.almana.android.stillmeter.helper;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import ch.almana.android.importexportdb.BackupRestoreCallback;
import ch.almana.android.importexportdb.exporter.ExportConfig;
import ch.almana.android.importexportdb.exporter.ExportConfig.ExportType;
import ch.almana.android.importexportdb.exporter.ExportDataTask;
import ch.almana.android.importexportdb.importer.ImportConfig;
import ch.almana.android.importexportdb.importer.ImportDataTask;
import ch.almana.android.stillmeter.log.Logger;
import ch.almana.android.stillmeter.provider.StillProvider;
import ch.almana.android.stillmeter.provider.db.DB;

public class BackupRestoreHelper {

	public static final String DIRECTORY_BACKUP = "backup";

	public static final Object MUTEX = new Object();

	private final BackupRestoreCallback cb;


	public BackupRestoreHelper(BackupRestoreCallback cb) {
		super();
		this.cb = cb;
	}

	public static File getStoragePath(Context ctx) {
		File storagePath = new File(Environment.getExternalStorageDirectory(), ctx.getPackageName() + "/" + DIRECTORY_BACKUP);
		if (!storagePath.isDirectory()) {
			if (storagePath.mkdirs()) {
				Logger.i("Created " + storagePath.getAbsolutePath());
			} else {
				Logger.i("Could not create " + storagePath.getAbsolutePath());
			}
		}
		return storagePath;
	}

	public void backup() {
		synchronized (MUTEX) {
			Logger.i("Creating backup");
			File storagePath = getStoragePath(cb.getContext());
			SQLiteDatabase db = new DB.OpenHelper(cb.getContext()).getWritableDatabase();
			ExportConfig config = new ExportConfig(db, DB.DATABASE_NAME, storagePath, ExportType.JSON);
			ExportDataTask exportDataTask = new ExportDataTask(cb);
			exportDataTask.execute(new ExportConfig[] { config });
		}
	}

	public void restore() throws Exception {
		synchronized (MUTEX) {
			Logger.i("Restoring...");
			Context context = cb.getContext();
			try {
				StillProvider.deleteAllTables(context);
				BackupRestoreCallback callback = new BackupRestoreCallback() {

					@Override
					public void hasFinished(boolean success) {
						StillProvider.setNotifyChanges(true);
						cb.hasFinished(success);
					}

					@Override
					public Context getContext() {
						return cb.getContext();
					}
				};
				ImportDataTask idt = new ImportDataTask(callback);
				ImportConfig config = new ImportConfig(DB.DATABASE_NAME, getStoragePath(context));
				config.addTable(DB.Day.TABLE_NAME, DB.Day.CONTENT_URI);
				config.addTable(DB.Session.TABLE_NAME, DB.Session.CONTENT_URI);
				config.addTable(DB.StillTime.TABLE_NAME, DB.StillTime.CONTENT_URI);
				StillProvider.setNotifyChanges(false);
				idt.execute(new ImportConfig[] { config });
			} catch (Exception e) {
				Logger.e("Cannot restore configuration", e);
				cb.hasFinished(false);
				throw new Exception("Cannot restore configuration", e);
			}
		}
	}

}
