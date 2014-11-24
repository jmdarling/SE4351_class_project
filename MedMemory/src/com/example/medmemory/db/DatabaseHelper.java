package com.example.medmemory.db;

import java.io.*;
import java.sql.SQLException;
import java.text.*;
import java.util.Date;
import java.util.Locale;
import com.example.medmemory.model.*;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.graphics.*;

/**
 * 
 * Helper class for copying a SQLite database existing as a project asset to a 
 * physical file suitable for reading and writing.
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	// DB path and name:
	private static String DB_PATH = "data/data/com.example.memorymed/databases/";
	private static String DB_NAME = "memorymed";
	
	// Static DateFormat for SQLite conversion:
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	
	private SQLiteDatabase database;
	private final Context context;
	
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		DB_PATH = context.getApplicationInfo().dataDir + "/databases/"; 
		this.context = context;
	}
	
	/**
	 * Attempts to copy the DB from the project's assets to a physical file.
	 * @throws IOException
	 */
	private void createDatabase() throws IOException {
		boolean databaseExists = checkDatabaseExists();
		
		if (!databaseExists) {
			this.getReadableDatabase();
			
			try {
				copyDatabase();
			}
			catch (IOException ex) {
				throw ex;
			}
		}
	}
	
	/**
	 * Checks whether the database specified by DB_PATH and DB_NAME exists and is readable.
	 * @return True if the database exists and is readable; false otherwise.
	 */
	private boolean checkDatabaseExists() {
		SQLiteDatabase database = null;
		
		try {
			String path = DB_PATH + DB_NAME;
			database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLiteException ex) {
			// Database doesn't exist yet or is unable to be opened
		}
		
		if (database != null) {
			database.close();
		}
		
		return database != null ? true : false;
	}
	
	/**
	 * Copies the project's database from assets to a physical file.
	 * @throws IOException
	 */
	private void copyDatabase() throws IOException {
		InputStream inputStream = this.context.getAssets().open(DB_NAME);
		String fileName = DB_PATH + DB_NAME;
		OutputStream outputStream = new FileOutputStream(fileName);
		
		byte[] buffer  = new byte[1024];
		int length = 0;
		
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		
		outputStream.flush();
		outputStream.close();
		inputStream.close();
	}
	
	/**
	 * Opens the database in read-only mode for copying.
	 * @throws SQLException
	 */
	private void openDatabase() throws SQLException {
		String path = DB_PATH + DB_NAME;
		database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	}
	
	/**
	 * Closes the DB if it has been previously opened.
	 */
	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		
		super.close();
	}

	/**
	 * Called when the database is created for the first time.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Not currently necessary, but SQLiteOpenHelper requires override.
	}

	/**
	 * Called when the database needs to be upgraded. 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Not currently necessary, but SQLiteOpenHelper requires override.
	}
	
	
	// Public methods for reading and writing to the database:
	
	/**
	 * Gets a medication ID which is guaranteed to be unique.
	 * @return An integer greater than 0.
	 */
	public int getId() throws SQLException {
		// TODO
		// Also, may not be necessary.
		return -1;
	}
	
	/**
	 * Converts a <code>byte[]</code> to an instance of <code>android.graphics.Bitmap</code>.
	 * @param blob	The byte array containing pixel data.
	 * @return		A <code>Bitmap</code> representation of the blob.
	 */
	public static Bitmap convertByteArrayToBitmap(byte[] blob) {
		return BitmapFactory.decodeByteArray(blob, 0, blob.length);
	}
	
	/**
	 * Attempts to get an existing <code>medication</code> record by its ID.
	 * @param id	The ID of the existing record to retrieve from the database.
	 * @return		An instance of <code>com.example.medmemory.model.Medication</code> with parameters 
	 * from the <code>medication</code> table record, or <code>null</code> if the record was not found.
	 * @throws		SQLException
	 */
	public Medication getMedicationFromId(int id) throws SQLException {
		SQLiteDatabase readableDatabase = null;
		int existingMedicationId = -1;
		
		// Attempt to get a readable database:
		try {
			readableDatabase = getReadableDatabase();
		}
		catch (SQLiteException ex) {
			throw new SQLException("Unable to read from database.");
		}

		// Query the DB for the existing record:
		Cursor result = readableDatabase.rawQuery("SELECT * FROM medication WHERE _id = ?", new String[] { Integer.toString(id) });
		
		if (result.getCount() <= 0) {
			return null;
		}
		
		existingMedicationId = result.getInt(0);
		result.close();
		readableDatabase.close();
		
		if (existingMedicationId <= 0) {
			return null;
		}
		else {
			Date refillDate = new Date(0);
			
			try {
				refillDate = dateFormat.parse(result.getString(7));
			}
			catch (ParseException ex) {
				// TODO
			}
			
			return new Medication(result.getString(1), convertByteArrayToBitmap(result.getBlob(2)), result.getString(3),
					result.getString(4), result.getInt(5), result.getInt(6), refillDate);
		}
	}
	
	/**
	 * Inserts an instance of <code>com.example.medmemory.model.Medication</code> to the
	 * <code>medication</code> table of the database as a new record.
	 * @param medication	The Medication object to write to the database.
	 * @return				True if the write was successful; false otherwise.
	 * @throws				SQLException
	 */
	public boolean insertMedicationRecord(Medication medication) throws SQLException {
		SQLiteDatabase writableDatabase = null;
		long newMedicationId = -1;
		
		try {
			writableDatabase = getWritableDatabase();
		}
		catch (SQLiteException ex) {
			throw new SQLException("Unable to write to database.");
		}
		
		ContentValues values = new ContentValues();
		values.put("name", medication.getName());
		values.put("image", medication.getImageAsByteArray());
		values.put("dosage", medication.getDosage());
		values.put("notes", medication.getNotes());
		values.put("currentPillCount", medication.getCurrentPillCount());
		values.put("maximumPillCount", medication.getMaximumPillCount());
		values.put("refillDate", dateFormat.format(medication.getRefillDate()));
		
		newMedicationId = writableDatabase.insert("medication", null, values);
		
		writableDatabase.close();
		return newMedicationId == -1 ? false : true;
	}

	/**
	 * Updates an existing <code>medication</code> record with the specified parameters.
	 * Throws an <code>SQLException</code> if the specified <code>id</code> was not found in the database.
	 * @param id			The ID of the existing record to update.
	 * @param medication	An instance of Medication containing new information.
	 * @return				True if the update was successful; false otherwise.
	 * @throws				SQLException
	 */
	public boolean updateMedicationRecord(int id, Medication medication) throws SQLException {
		SQLiteDatabase readableDatabase = null;
		SQLiteDatabase writableDatabase = null;
		int existingMedicationId = -1;
		int rowsAffected = -1;
		
		// Attempt to get a readable database:
		try {
			readableDatabase = getReadableDatabase();
		}
		catch (SQLiteException ex) {
			throw new SQLException("Unable to read from database.");
		}

		// Query the DB for the existing record:
		Cursor result = readableDatabase.rawQuery("SELECT * FROM medication WHERE _id = ?", new String[] { Integer.toString(id) });
		
		if (result.getCount() <= 0) {
			throw new SQLException("Unable to find medication record with id = " + id);
		}
		
		existingMedicationId = result.getInt(0);
		result.close();
		readableDatabase.close();
		
		if (existingMedicationId <= 0) {
			throw new SQLException("Unable to find medication record with id = " + id);
		}
		
		// Attempt to get a writable database:
		try {
			writableDatabase = getWritableDatabase();
		}
		catch (SQLiteException ex) {
			throw new SQLException("Unable to write to database.");
		}
		
		// Write the changes back to the DB:
		ContentValues values = new ContentValues();
		values.put("name", medication.getName());
		values.put("image", medication.getImageAsByteArray());
		values.put("dosage", medication.getDosage());
		values.put("notes", medication.getNotes());
		values.put("currentPillCount", medication.getCurrentPillCount());
		values.put("maximumPillCount", medication.getMaximumPillCount());
		values.put("refillDate", medication.getRefillDate().toString());
		
		rowsAffected = writableDatabase.update("medication", values, "_id = ?", new String[] { Integer.toString(id) });
		writableDatabase.close();
		
		return rowsAffected <= 0 ? false : true;
	}
	
	/**
	 * Deletes an existing <code>medication</code> record by its ID.
	 * @param id	The ID of the record to delete.
	 * @return		True if the delete was successful; false otherwise.
	 * @throws		SQLException
	 */
	public boolean deleteMedicationRecord(int id) throws SQLException {
		SQLiteDatabase writableDatabase = null;
		int rowsAffected = -1;
		
		// Attempt to get a readable database:
		try {
			writableDatabase = getWritableDatabase();
		}
		catch (SQLiteException ex) {
			throw new SQLException("Unable to read from database.");
		}

		// Delete the existing record by id:
		rowsAffected = writableDatabase.delete("medication", "_id = ?", new String[] { Integer.toString(id) });
		writableDatabase.close();
		
		return rowsAffected <= 0 ? false : true;
	}
}
