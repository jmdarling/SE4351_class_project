package com.example.medmemory.db;

import java.io.IOException;
import java.text.*;
import java.util.Date;
import java.util.Locale;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.medmemory.model.Medication;

public class Database {
	private static DatabaseHelper databaseHelper = null;
	
	public static Context context;
	
	private static void openDatabase() {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(context);
		}
		
//		try {
//			databaseHelper.createEmptyDatabase();
//		}
//		catch (IOException ex) {
//			throw new Error("Unable to create database");
//		}
		
		databaseHelper.openDatabase();
	}
	
	public static Medication getMedicationById(int id) throws IllegalArgumentException {
		if (id < 0) {
			throw new IllegalArgumentException("'id' must be an integer between 0 and " + Integer.MAX_VALUE + ", inclusive.");
		}
		
		openDatabase();
		
		Medication medication = null;
		Cursor cursor = databaseHelper.select(id);
		
		if (cursor.moveToFirst()) {
			Date refillDate = new Date(0);
			
			try {
				refillDate = DatabaseHelper.dateFormat.parse(cursor.getString(cursor.getColumnIndex("refillDate")));
			} catch (ParseException e) {
				// TODO
			}
			
			medication = new Medication(cursor.getString(1), DatabaseHelper.convertByteArrayToBitmap(cursor.getBlob(2)),
				cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6), refillDate);
			medication.setId(id);
		}

		databaseHelper.close();
		return medication;
	}
	
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
