package com.example.medmemory.db;

import java.io.IOException;
import java.text.*;
import java.util.*;
import android.content.*;
import android.database.Cursor;

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
			throw new Error("Unable to create database");
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
					refillDate = DatabaseHelper.dateFormat.parse(cursor.getString(cursor.getColumnIndex("refillDate")));
					reminderDate = DatabaseHelper.dateFormat.parse(cursor.getString(cursor.getColumnIndex("reminderDate")));
				}
				catch (ParseException ex) {
					System.err.printf("Error converting date \"%s\" from database to Date object; PK = %d.",
						DatabaseHelper.dateFormat.format(refillDate), cursor.getInt(0));
				}
				
				Medication medication = new Medication(cursor.getString(1), DatabaseHelper.convertByteArrayToBitmap(cursor.getBlob(2)),
					cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6), refillDate, reminderDate);
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
	 * @throws IllegalArgumentException
	 */
	public static Medication getMedicationById(int id) throws IllegalArgumentException {
		if (id < 0) {
			throw new IllegalArgumentException("'id' must be an integer between 0 and " + Integer.MAX_VALUE + ", inclusive.");
		}
		
		openDatabase();
		
		Medication medication = null;
		Cursor cursor = databaseHelper.select(id);
		
		if (cursor.moveToFirst()) {
			Date refillDate = new Date(0);
			Date reminderDate = new Date(0);
			
			try {
				refillDate = DatabaseHelper.dateFormat.parse(cursor.getString(cursor.getColumnIndex("refillDate")));
				reminderDate = DatabaseHelper.dateFormat.parse(cursor.getString(cursor.getColumnIndex("reminderDate")));
			}
			catch (ParseException ex) {
				System.err.printf("Error converting date \"%s\" from database to Date object; PK = %d.",
					DatabaseHelper.dateFormat.format(refillDate), cursor.getInt(0));
			}
			
			medication = new Medication(cursor.getString(1), DatabaseHelper.convertByteArrayToBitmap(cursor.getBlob(2)),
				cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6), refillDate, reminderDate);
			medication.setId(id);
		}

		databaseHelper.close();
		return medication;
	}
	
	/**
	 * Adds a new <code>Medication</code> object to the database.
	 * @param medication	An instance of <code>Medication</code> to commit to the database.
	 * @return				True if the record was successfully added; false otherwise.
	 * @throws IllegalArgumentException
	 */
	public static boolean addMedication(Medication medication) throws IllegalArgumentException {
		if (medication == null) {
			throw new IllegalArgumentException("'medication' cannot be null.");
		}
		
		openDatabase();
		
		ContentValues values = new ContentValues();
		values.putNull("_id");
		values.put("name", medication.getName());
		values.put("image", medication.getImageAsByteArray());
		values.put("dosage", medication.getDosage());
		values.put("notes", medication.getNotes());
		values.put("currentPillCount", medication.getCurrentPillCount());
		values.put("maximumPillCount", medication.getMaximumPillCount());
		values.put("refillDate", DatabaseHelper.dateFormat.format(medication.getRefillDate()));
		
		
		boolean success = databaseHelper.insert(values);
		
		databaseHelper.close();
		return success;
	}

	
	/**
	 * Updates an existing <code>Medication</code> record in the database.
	 * @param id			The ID of the record to update. Must be greater than or equal to zero.
	 * @param values		An instance of <code>ContentValues</code> containing the updated values.
	 * 						Valid keys include name, image, dosage, notes, currentPillCount, maximumPillCount, refillDate, and reminderDate.
	 * @return				True if the record was successfully updated; false otherwise.
	 * @throws IllegalArgumentException
	 */
	public static boolean updateMedicationById(int id, ContentValues values) throws IllegalArgumentException {
		if (getMedicationById(id) == null) {
			throw new IllegalArgumentException("Medication with id " + id + " does not exist.");
		}
		
		if (values == null || values.size() == 0) {
			throw new IllegalArgumentException("'values' must not be null and must contain at least one key-value pair.");
		}
		
		if (values.containsKey("id") || values.containsKey("_id")) {
			throw new IllegalArgumentException("You cannot update the table's primary key.");
		}
		
		openDatabase();
		boolean success = databaseHelper.update(id, values);
		
		databaseHelper.close();
		return success;
	}

	/**
	 * Deletes an existing <code>Medication</code> record in the database.
	 * @param id			The ID of the record to delete. Must be greater than or equal to zero.
	 * @param values		An instance of <code>ContentValues</code> containing the updated values.
	 * 						Valid keys include name, image, dosage, notes, currentPillCount, maximumPillCount, refillDate, and reminderDate.
	 * @return				True if the record was successfully updated; false otherwise.
	 * @throws IllegalArgumentException
	 */
	public static boolean deleteMedicationById(int id) throws IllegalArgumentException {
		if (getMedicationById(id) == null) {
			throw new IllegalArgumentException("Medication with id " + id + " does not exist.");
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
}
