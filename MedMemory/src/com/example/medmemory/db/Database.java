package com.example.medmemory.db;

import java.io.IOException;
import java.text.*;
import java.util.*;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.example.medmemory.model.Medication;

public class Database {
	private static DatabaseHelper databaseHelper = null;
	
	public static Context context;
	
	/**
	 * </strong><em>Internal-use only.</em></strong> Opens a connection to the database suitable for standard CRUD operations.
	 */
	private static void openDatabase() {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(context);
		}
		
		try {
			databaseHelper.createEmptyDatabase();
		}
		catch (IOException ex) {
			throw new Error("FATAL: Unable to create database");
		}
		
		databaseHelper.openDatabase();
	}
	
	/**
	 * Retrieves all existing Medication records from the database.
	 * @return		An <code>ArrayList<Medication></code> containing all of the Medication records, or null if
	 * 				the database contains no Medication records.
	 */
	public static ArrayList<Medication> getAllMedications() {
		ArrayList<Medication> list = null;
		
		openDatabase();
		Cursor cursor = databaseHelper.selectAll();
		

		if (cursor.moveToFirst()) {
			list = new ArrayList<Medication>();
			
			while (cursor.isAfterLast() == false) {
				Date refillDate = new Date(0);
				Date reminderDate = new Date(0);
				
				try {
					String refillDateString = cursor.getString(cursor.getColumnIndex("refillDate"));
					String reminderDateString = cursor.getString(cursor.getColumnIndex("reminderDate"));
					
					if (refillDateString != null && refillDateString.length() > 0) {
						refillDate = DatabaseHelper.dateFormat.parse(refillDateString);
					}

					if (reminderDateString != null && reminderDateString.length()  > 0) {
						reminderDate = DatabaseHelper.dateFormat.parse(reminderDateString);
					}
				}
				catch (ParseException ex) {
					System.err.printf("Error converting date from database to Date object; PK = %d.", cursor.getInt(0));
				}
				
				byte[] blob = cursor.getBlob(2);
				Bitmap image = DatabaseHelper.convertByteArrayToBitmap(blob);
				
				Medication medication = new Medication(cursor.getString(1), image, cursor.getString(3), cursor.getString(4),
						cursor.getInt(5), cursor.getInt(6), refillDate, reminderDate);
				medication.setId(cursor.getInt(0));
				
				list.add(medication);
				cursor.moveToNext();
			}
		}
		
		return list;
	}
	
	/**
	 * Retrieves a Medication record by its ID.
	 * @param id	The ID of the medication to retrieve. Must be greater than or equal to zero.
	 * @return		An instance of <code>Medication</code> with values corresponding to the record in the <code>medication</code> table,
	 * 				or <code>null</code> if one such record does not exist.
	 */
	public static Medication getMedicationById(int id) {
		if (id < 0) {
			System.err.printf("[DB] 'id' must be >= 0.\n");
			return null;
		}
		
		openDatabase();
		
		Medication medication = null;
		Cursor cursor = databaseHelper.select(id);
		
		if (cursor.moveToFirst()) {
			Date refillDate = new Date(0);
			Date reminderDate = new Date(0);
			
			try {
				String refillDateString = cursor.getString(cursor.getColumnIndex("refillDate"));
				String reminderDateString = cursor.getString(cursor.getColumnIndex("reminderDate"));
				
				if (refillDateString != null && refillDateString.length() > 0) {
					refillDate = DatabaseHelper.dateFormat.parse(refillDateString);
				}

				if (reminderDateString != null && reminderDateString.length()  > 0) {
					reminderDate = DatabaseHelper.dateFormat.parse(reminderDateString);
				}
			}
			catch (ParseException ex) {
				System.err.printf("Error converting date from database to Date object; PK = %d.", cursor.getInt(0));
			}
			
			byte[] blob = cursor.getBlob(2);
			Bitmap image = DatabaseHelper.convertByteArrayToBitmap(blob);
			
			medication = new Medication(cursor.getString(1), image, cursor.getString(3), cursor.getString(4),
					cursor.getInt(5), cursor.getInt(6), refillDate, reminderDate);
			medication.setId(cursor.getInt(0));
		}

		databaseHelper.close();
		return medication;
	}
	
	/**
	 * Adds a new <code>Medication</code> object to the database.
	 * @param medication	An instance of <code>Medication</code> to commit to the database.
	 * @return				True if the record was successfully added; false otherwise.
	 */
	public static boolean addMedication(Medication medication) {
		if (medication == null) {
			System.err.printf("[DB] 'medication' cannot be null.\n");
			return false;
		}
		
		openDatabase();
		
		ContentValues values = new ContentValues();
		values.put("name", medication.getName());
		values.put("image", medication.getImageAsByteArray());
		values.put("dosage", medication.getDosage());
		values.put("notes", medication.getNotes());
		values.put("currentPillCount", medication.getCurrentPillCount());
		values.put("maximumPillCount", medication.getMaximumPillCount());
		values.put("refillDate", DatabaseHelper.dateFormat.format(medication.getRefillDate()));
		values.put("reminderDate", DatabaseHelper.dateFormat.format(medication.getReminderDate()));
		
		boolean success = databaseHelper.insert(values);
		
		databaseHelper.close();
		return success;
	}

	
	/**
	 * Updates an existing <code>Medication</code> record in the database.
	 * @param id			The ID of the record to update. Must be greater than or equal to zero.
	 * @param values		An instance of <code>ContentValues</code> containing the updated values.
	 * 						Valid keys include name, image, dosage, notes, currentPillCount, maximumPillCount,
	 * 						refillDate, and reminderDate.
	 * @return				True if the record was successfully updated; false otherwise.
	 */
	public static boolean updateMedicationById(int id, ContentValues values) {
		if (getMedicationById(id) == null) {
			System.err.printf("[DB] Couldn't find medication with id=%d.\n", id);
			return false;
		}
		
		if (values == null || values.size() == 0) {
			System.err.printf("[DB] 'values' provided is null or empty.\n");
			return false;
		}
		
		if (values.containsKey("id") || values.containsKey("_id")) {
			System.err.printf("[DB] Don't attempt to update record's primary key!\n");
			return false;
		}
		
		openDatabase();
		boolean success = databaseHelper.update(id, values);
		
		databaseHelper.close();
		return success;
	}
	
	/**
	 * Updates an existing <code>Medication</code> record in the database.
	 * @param medication		The Medication object representing the record to update with its values.
	 * @return					True if the record was successfully updated; false otherwise
	 */
	public static boolean updateMedication(Medication medication)
	{
		ContentValues values = new ContentValues();
		values.put("name", medication.getName());
		values.put("image", medication.getImageAsByteArray());
		values.put("dosage", medication.getDosage());
		values.put("notes", medication.getNotes());
		values.put("currentPillCount", medication.getCurrentPillCount());
		values.put("maximumPillCount", medication.getMaximumPillCount());
		values.put("refillDate", DatabaseHelper.dateFormat.format(medication.getRefillDate()));
		values.put("reminderDate", DatabaseHelper.dateFormat.format(medication.getReminderDate()));
		
		return updateMedicationById(medication.getId(), values);
	}

	/**
	 * Deletes an existing <code>Medication</code> record in the database.
	 * @param id			The ID of the record to delete. Must be greater than or equal to zero.
	 * @param values		An instance of <code>ContentValues</code> containing the updated values.
	 * 						Valid keys include name, image, dosage, notes, currentPillCount, maximumPillCount, refillDate, and reminderDate.
	 * @return				True if the record was successfully updated; false otherwise.
	 */
	public static boolean deleteMedicationById(int id) {
		if (getMedicationById(id) == null) {
			System.err.printf("[DB] Couldn't find medication with id=%d.\n", id);
			return false;
		}
		
		openDatabase();
		boolean success = databaseHelper.delete(id);
		
		databaseHelper.close();
		return success;
	}
	
	/**
	 * Indicates that the patient has taken a single pill of a medication.
	 * @param id		The id of the medication.
	 * @return			The remaining number of pills.
	 */
	public static int takeDosage(int id) {
		return takeDosage(id, 1);
	}
	
	/**
	 * Indicates that the patient has taken a single pill of a medication.
	 * @param id		The Medication.
	 * @param amount	The amount of pills taken in this dosage.
	 * @return			The remaining number of pills.
	 */
	public static int takeDosage(Medication medication) {
		return takeDosage(medication.getId(), 1);
	}
	
	/**
	 * Indicates that the patient has taken a dosage of a medication.
	 * @param id		The Medication.
	 * @param amount	The amount of pills taken in this dosage.
	 * @return			The remaining number of pills.
	 */
	public static int takeDosage(Medication medication, int amount) {
		return takeDosage(medication.getId(), amount);
	}
	
	/**
	 * Indicates that the patient has taken a dosage of a medication.
	 * @param id		The id of the medication.
	 * @param amount	The amount of pills taken in this dosage.
	 * @return			The remaining number of pills.
	 */
	public static int takeDosage(int id, int amount) {
		if (getMedicationById(id) == null) {
			throw new IllegalArgumentException("Medication with id " + id + " does not exist.");
		}
		
		Medication medication = getMedicationById(id);
		
		if (amount <= 0 || amount > medication.getCurrentPillCount()) {
			throw new IllegalArgumentException("'amount' must be between 0 and the remaining number of pills.");
		}
		
		if (amount > medication.getMaximumPillCount()) {
			throw new IllegalArgumentException("'amount' cannot be greater than the maximum number of pills available.");
		}
		
		// Update pill count:
		int newPillCount = medication.getCurrentPillCount() - amount;
		ContentValues values = new ContentValues();
		values.put("currentPillCount", newPillCount);
		boolean success = updateMedicationById(id, values);
		
		databaseHelper.close();

		// Only update in-memory instance if the DB write was successful:
		if (success) {
			medication.setCurrentPillCount(newPillCount);
		}
		
		return medication.getCurrentPillCount();
	}
	
	public static int getLastInsertId() {
		openDatabase();
		int id = databaseHelper.getLastInsertId();
		databaseHelper.close();
		return id;
	}
}
