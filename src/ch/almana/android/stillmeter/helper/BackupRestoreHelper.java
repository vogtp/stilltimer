package ch.almana.android.stillmeter.helper;

import java.io.File;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import ch.almana.android.importexportdb.BackupRestoreCallback;
import ch.almana.android.importexportdb.ExportConfig;
import ch.almana.android.importexportdb.ExportConfig.ExportType;
import ch.almana.android.importexportdb.ExportDataTask;
import ch.almana.android.importexportdb.importer.DataJsonImporter;
import ch.almana.android.stillmeter.log.Logger;
import ch.almana.android.stillmeter.provider.StillProvider;
import ch.almana.android.stillmeter.provider.db.DB;

public class BackupRestoreHelper {

	public static final String DIRECTORY_BACKUP = "backup";

	public static final Object MUTEX = new Object();

	private final BackupRestoreCallback cb;

	private final ContentResolver contentResolver;

	public BackupRestoreHelper(BackupRestoreCallback cb) {
		super();
		this.cb = cb;
		this.contentResolver = cb.getContext().getContentResolver();
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
				StillProvider.setNotifyChanges(false);
				DataJsonImporter dje = new DataJsonImporter(DB.DATABASE_NAME, getStoragePath(context));
				StillProvider.deleteAllTables(context);
				dje.restoreTable(contentResolver, DB.Day.CONTENT_URI, DB.Day.TABLE_NAME);
				dje.restoreTable(contentResolver, DB.Session.CONTENT_URI, DB.Session.TABLE_NAME);
				dje.restoreTable(contentResolver, DB.StillTime.CONTENT_URI, DB.StillTime.TABLE_NAME);
				cb.hasFinished(true);
			} catch (Exception e) {
				Logger.e("Cannot restore configuration", e);
				cb.hasFinished(false);
				throw new Exception("Cannot restore configuration", e);
			} finally {
				StillProvider.setNotifyChanges(true);
			}
		}
	}

}
