package com.example.medmemory.db;

import java.io.*;
import java.text.*;
import java.util.Locale;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;

public class DatabaseHelper extends SQLiteOpenHelper {
	// Database info:
	private static String DB_PATH = null;
	private static final String DB_NAME = "db";
	private static final String MEDICATION_TABLE = "medication";
	
	private SQLiteDatabase database; 
	private final Context context;

	// DateFormat for parsing strings from the DB:
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
		DB_PATH = this.context.getDatabasePath(DB_NAME).getPath();
	}
	
	/**
	 * Creates a empty database on the device and overwrites it with our premade database.
	 * @throws IOException
	 */
	public void createEmptyDatabase() throws IOException {
		// Create the empty database:
		this.getReadableDatabase();
		
		// Attempt to copy our premade database to local storage:
		try {
			copyPremadeDatabase();
		}
		catch (IOException ex) {
			throw new IOException("Unable to copy database to data directory.");
		}
	}
	
	/**
	 * Checks to see if the premade database already exists in local storage to avoid
	 * unnecessarily overwriting it.
	 * @return True if the premade database already exists; false otherwise.
	 */
	private boolean doesDatabaseExist() {
		SQLiteDatabase database = null;
		
		try {
			database = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLiteException ex) {
			// Database doesn't exist; will be created and copied.
		}
		
		if (database != null) {
			database.close();
		}
		
		return database != null ? true : false;
	}
	
	/**
	 * Copies the premade database to local storage.
	 * @throws IOException
	 */
	private void copyPremadeDatabase() throws IOException {
		// Open premade database as an InputStream:
		InputStream inputStream = context.getAssets().open(DB_NAME);
		
		// Create path for destination database in local storage:
		String destinationDatabaseFileName = DB_PATH;
		
		// Open the destination database for writing as an OutputStream:
		OutputStream outputStream = new FileOutputStream(destinationDatabaseFileName);
		
		// Copy the premade database to the destination database:
		int length;
		byte[] buffer = new byte[1024];
		
		while ((length = inputStream.read(buffer)) > 0){
			outputStream.write(buffer, 0, length);
		}
		
		// Clean up:
		outputStream.flush();
		outputStream.close();
		inputStream.close();
	}
	
	public void openDatabase() throws SQLException {
		// Open the database:
		String databaseFileName = DB_PATH;
		database = SQLiteDatabase.openDatabase(databaseFileName, null, SQLiteDatabase.OPEN_READWRITE);
	}
	
	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		
		super.close();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Required override; not needed.
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Required override; not needed.
	}

	/**
	 * Selects a medication record.
	 * @param id		The ID of the record to select.
	 * @return			A <code>Cursor</code> pointing to the entire record.
	 */
	public Cursor select(int id) {
		return database.query(MEDICATION_TABLE, null, "_id = ?", new String[] { Integer.toString(id) }, null, null, null);
	}

	/**
	 * Selects all medication records.
	 * @return			A <code>Cursor</code> pointing to the records.
	 */
	public Cursor selectAll() {
		return database.rawQuery("SELECT * FROM " + MEDICATION_TABLE, null);
	}
	
	/**
	 * Inserts a new medication record with the specified paramters.
	 * @param values	The key-value pair collection of values to insert.
	 * @return			True if the insert was successful; false otherwise.
	 */
	public boolean insert(ContentValues values) {
		long count = database.insert(MEDICATION_TABLE, null, values);
		return count <= 0 ? false : true;
	}

	/**
	 * Updates a medication record.
	 * @param id		The ID of the record to update.
	 * @param values	The key-value pair collection of values to update.
	 * @return			True if the update was successful; false otherwise.
	 */
	public boolean update(int id, ContentValues values) {
		int count = database.update(MEDICATION_TABLE, values, "_id = ?", new String[] { Integer.toString(id) });
		return count <= 0 ? false : true;
	}
	
	/**
	 * Deletes a medication record.
	 * @param id	The ID of the record to delete.
	 * @return		True if the delete was successful; false otherwise.
	 */
	public boolean delete(int id) {
		int count = database.delete(MEDICATION_TABLE, "_id = ?", new String[] { Integer.toString(id) });
		return count <= 0 ? false : true;
	}
	
	/**
	 * Converts a <code>byte[]</code> to an instance of <code>android.graphics.Bitmap</code>.
	 * @param blob	The byte array containing pixel data.
	 * @return		A <code>Bitmap</code> representation of the blob.
	 */
	public static Bitmap convertByteArrayToBitmap(byte[] blob) {
		if (blob != null) {
			return BitmapFactory.decodeByteArray(blob, 0, blob.length);
		}
		
		return null;
	}
}
