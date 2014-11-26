package com.example.medmemory.model;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Locale;

import com.example.medmemory.db.Database;

import android.graphics.Bitmap;

/**
 * Implementation of a simple medication. Corresponds with the medication table in the SQLite DB.
 * New instances are <strong>not</strong> written to the DB via the constructor - they only exist in memory.
 *
 */
public class Medication {
	// Fields:
	private int id;
	private String name;
	private Bitmap image;
	private String dosage;
	private String notes;
	private int currentPillCount;
	private int maximumPillCount;
	private Date refillDate;
	private Date reminderDate;
	
	// Static ID to keep track of autoinc PK:
	private static int currentId = 1;
	
	/**
	 * Gets the current largest ID across the entire medication table. Guaranteed to be unique.
	 * @return A unique ID number.
	 */
	public static int getCurrentId()
	{
		return currentId;
	}
	
	/**
	 * Creates a new, empty instance of Medication. It will have an
	 * empty name, image, dosage, and notes. The current pill count and maximum
	 * pill count will be zero. The refill and reminder dates will be the Unix epoch.
	 */
	public Medication() {
		this.id = currentId++;
		this.name = "";
		this.image = null;
		this.dosage = "";
		this.notes = "";
		this.currentPillCount = 0;
		this.maximumPillCount = 0;
		this.refillDate = new Date(0);
		this.reminderDate = new Date(0);
	}
	
	/**
	 * Creates a new instance of Medication with the specified parameters.
	 * @param name					The name of the medication.
	 * @param image					The image associated with the medication.
	 * @param doage					The dosage instructions for the medication.
	 * @param notes					Special notes for taking the medication.
	 * @param currentPillCount		The current remaining count of pills available for consumption.
	 * @param maximumPillCount		The maximum amount of pills available.
	 * @param refillDate			The estimated refill date of the medication.
	 * @param reminderDate			The reminder date of the medication.
	 */
	public Medication(String name, Bitmap image, String dosage, String notes, int currentPillCount,
			int maximumPillCount, Date refillDate, Date reminderDate) {
		this.id = currentId++;
		this.name = name;
		this.image = image;
		this.dosage = dosage;
		this.notes = notes;
		this.currentPillCount = currentPillCount;
		this.maximumPillCount = maximumPillCount;
		this.refillDate = refillDate;
		this.reminderDate = reminderDate;
	}
	
	/**
	 * Gets this Medication's internal ID number.
	 * @return The Medication's ID number.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Gets this Medication's name.
	 * @return The Medication's name.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets this Medication's image as an instance of <code>android.graphics.Bitmap</code>.
	 * @return The Medication's image as a Bitmap.
	 */
	public Bitmap getImage() {
		return this.image;
	}
	
	/**
	 * Gets this Medication's image as a <code>byte[]</code>.
	 * @return The Medication's image as a byte array.
	 */
	public byte[] getImageAsByteArray() {
		if (this.image != null) {
			int size = this.image.getByteCount();
			
			if (size > 0) {
				ByteBuffer buffer = ByteBuffer.allocate(size);
				image.copyPixelsFromBuffer(buffer);
				
				return buffer.array();
			}
		}
		
		return new byte[] { };
	}
	
	/**
	 * Gets this Medication's dosage string.
	 * @return The Medication's dosage string.
	 */
	public String getDosage() {
		return this.dosage;
	}
	
	/**
	 * Gets this Medication's special notes.
	 * @return The Medication's special notes.
	 */
	public String getNotes() {
		return this.notes;
	}

	/**
	 * Gets this Medication's notes.
	 * @return The Medication's notes.
	 */
	public String MaximumPillCount() {
		return this.notes;
	}

	/**
	 * Gets this Medication's current pill count.
	 * @return The Medication's current pill count.
	 */
	public int getCurrentPillCount() {
		return this.currentPillCount;
	}

	/**
	 * Gets this Medication's maximum pill count.
	 * @return The Medication's maximum pill count.
	 */
	public int getMaximumPillCount() {
		return this.maximumPillCount;
	}
	
	/**
	 * Gets this Medication's estimated refill date.
	 * @return The Medication's estimated refill date.
	 */
	public Date getRefillDate() {
		return this.refillDate;
	}
	
	/**
	 * Gets this Medication's reminder date.
	 * @return The Medication's reminder date.
	 */
	public Date getReminderDate() {
		return this.reminderDate;
	}
	
	/**
	 * <span style="color: red;"><em>Internal-use only.</em></span>
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets this Medication's name. An <code>IllegalArgumentException</code> is thrown when attempting to set <code>name</code>
	 * to a null or empty string, as enforced by the DB schema.
	 * @param name The new name of the medication.
	 */
	public void setName(String name) {
		if (name == null || name.length() <= 0) {
			throw new IllegalArgumentException("'name' cannot be null or empty.");
		}
		
		this.name = name;
	}
	
	/**
	 * Set this Medication's image.
	 * @param image The new image for the medication.
	 */
	public void setImage(Bitmap image) {
		this.image = image;
	}

	/**
	 * Sets this Medication's dosage string.
	 * @param dosage The new dosage string of the medication.
	 */
	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	/**
	 * Sets this Medication's notes.
	 * @param notes The new notes for the medication.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * Sets this Medication's refill date.
	 * @param date The new refill date for the medication.
	 */
	public void setRefillDate(Date date) {
		this.refillDate = date;
	}

	/**
	 * Sets this Medication's reminder date.
	 * @param date The new reminder date for the medication.
	 */
	public void setReminderDate(Date date) {
		this.reminderDate = date;
	}

	/**
	 * Sets this Medication's current pill count. An <code>IllegalArgumentException</code> is thrown when attempting to set this
	 * value to an integer smaller than zero or larger than <code>maximumPillCount</code>.
	 * @param count The new current pill count.
	 */
	public void setCurrentPillCount(int count) throws IllegalArgumentException {
		if (count < 0) {
			throw new IllegalArgumentException("'count' must be greater than 0.");
		}

		if (count > this.maximumPillCount) {
			throw new IllegalArgumentException("'count' must be less than or equal to maximumPillCount.");
		}
		
		this.currentPillCount = count;
	}

	/**
	 * Sets this Medication's maximum pill count. An <code>IllegalArgumentException</code> is thrown when attempting to set this
	 * value to an integer smaller than zero or <code>currentPillCount</code>.
	 * @param count The new maximum pill count.
	 */
	public void setMaximumPillCount(int count) throws IllegalArgumentException {
		if (count < 0) {
			throw new IllegalArgumentException("'count' must be greater than 0.");
		}

		if (count < this.currentPillCount) {
			throw new IllegalArgumentException("'count' must be greater than or equal to currentPillCount.");
		}
		
		this.maximumPillCount = count;
	}
	
	/**
	 * Indicates the patient has taken a dosage of this Medication. Defaults to one pill.
	 */
	public int takeDosage() {
		return Database.takeDosage(this);
	}
	
	/**
	 * Indicates the patient has taken a dosage of this Medication.
	 * @param amount	The number of pills taken for this dosage.
	 */
	public int takeDosage(int amount) {
		return Database.takeDosage(this, amount);
		
		// TODO: recalculate refillDate
	}
	
	/**
	 * Indicates this Medication has been refilled.
	 */
	public void refill() {
		this.setCurrentPillCount(this.getMaximumPillCount());

		// TODO: recalculate refillDate
	}
	
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "Medication %d: %s, %d/%d pills remaining", this.id, this.name, this.currentPillCount, this.maximumPillCount);
	}
}
