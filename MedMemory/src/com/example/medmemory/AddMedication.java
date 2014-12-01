package com.example.medmemory;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.medmemory.db.Database;
import com.example.medmemory.model.Medication;

public class AddMedication extends Activity {

	private static final int SELECT_PICTURE = 1;
	public static final String MEDICATION_ID = "MEDICATION_ID";
	
	TextView medName;
	TextView dosage;
	TextView pillCount;
	TextView notes;
	
	TimePicker timePicker;
	
	ImageView imageView;

	Button saveBtn;
	Button selectImageBtn;
	Button testNotificationBtn;
	
	Date reminderDate;
	
	Bitmap image;
	
	Medication editMed;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmedication);
        setTitle("Add Medication");
        
        medName = (TextView) findViewById(R.id.med_name);
        dosage = (TextView) findViewById(R.id.med_dosage);
        pillCount = (TextView) findViewById(R.id.med_count);
        notes = (TextView) findViewById(R.id.med_notes);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        imageView = (ImageView) findViewById(R.id.med_image);
        
        saveBtn = (Button) findViewById(R.id.save_med_btn);
        saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				// Default data for testing purposes
				if(medName.getText().length() == 0)
					medName.setText("Aspirin");
				if(dosage.getText().length() == 0)
					dosage.setText("100mg");
				if(pillCount.getText().length() == 0)
					pillCount.setText("30");
				if(notes.getText().length() == 0)
					notes.setText("Take with food, do not take with orange juice.");
				
				// Debug log
				System.out.println("====SAVING MED====");
				System.out.println("Name: "+medName.getText());
				System.out.println("Dosage: "+dosage.getText());
				System.out.println("Pill Count: "+pillCount.getText());
				System.out.println("Notes: "+notes.getText());
				System.out.println("Reminder Time: "+timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
				
				if(editMed == null)
				{
					// Create medication
					Medication med = new Medication();
					med.setName(medName.getText().toString());
					med.setImage(image);
					med.setDosage(dosage.getText().toString());
					med.setCurrentPillCount(Integer.parseInt(pillCount.getText().toString()));
					med.setMaximumPillCount(Integer.parseInt(pillCount.getText().toString()));
					med.setNotes(notes.getText().toString());
					
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
					cal.set(Calendar.MINUTE,timePicker.getCurrentMinute());
					cal.set(Calendar.SECOND,0);
					cal.set(Calendar.MILLISECOND,0);
					med.setReminderDate(cal.getTime());
					
					Database.context = AddMedication.this;
					boolean success = Database.addMedication(med);
					
					// Set the alarm.
					int medId = Database.getLastInsertId();
					setMedAlarm(medId, cal, med.getName(), med.getImage(), med.getDosage(), med.getNotes());
					
					System.out.println("Added med successfully? "+success);
				}
				else
				{	
					// Update editMed:
					editMed.setName(medName.getText().toString());
					editMed.setImage(image);
					editMed.setDosage(dosage.getText().toString());
					editMed.setCurrentPillCount(Integer.parseInt(pillCount.getText().toString()));
					editMed.setMaximumPillCount(Integer.parseInt(pillCount.getText().toString()));
					editMed.setNotes(notes.getText().toString());
					
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
					cal.set(Calendar.MINUTE,timePicker.getCurrentMinute());
					cal.set(Calendar.SECOND,0);
					cal.set(Calendar.MILLISECOND,0);
					editMed.setReminderDate(cal.getTime());

					Database.context = AddMedication.this;
					boolean success = Database.updateMedication(editMed);
					
					setMedAlarm(editMed.getId(), cal, editMed.getName(), editMed.getImage(), editMed.getDosage(), editMed.getNotes());
					System.out.println("Added med successfully? "+success);
				}
				
				// Add to DB
				
				
				setResult(RESULT_OK);
				finish();
			}
		});
        
        selectImageBtn = (Button) findViewById(R.id.select_image_btn);
        selectImageBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				Intent pickIntent = new Intent();
				pickIntent.setType("image/*");
				pickIntent.setAction(Intent.ACTION_GET_CONTENT);

				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				String pickTitle = "Select Image";
				Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
				chooserIntent.putExtra
				(
				  Intent.EXTRA_INITIAL_INTENTS, 
				  new Intent[] { takePhotoIntent }
				);

				startActivityForResult(chooserIntent, SELECT_PICTURE);
			}
		});
        
        Intent intent = getIntent();
        int medId = intent.getIntExtra(MEDICATION_ID, -1);
        if(medId != -1)
        {
        	Database.context = this;
        	editMed = Database.getMedicationById(medId);
			medName.setText(editMed.getName());
			dosage.setText(editMed.getDosage());
			pillCount.setText(""+editMed.getCurrentPillCount()); 
			notes.setText(editMed.getNotes());
			image = editMed.getImage();
			imageView.setImageBitmap(editMed.getImage());
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(editMed.getReminderDate());
			timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
        }
        else
        {
        	AssetManager assetManager = getAssets();
        	InputStream in;
        	try {
        		in = assetManager.open("default_med_pic.jpg");
        		image = BitmapFactory.decodeStream(in);
        		imageView.setImageBitmap(image);
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK)
		{
			Bitmap receivedImage = null;
			if(data.getData() != null) // Gallery returned data
			{
				Uri selectedImage = data.getData();
				
				
				ParcelFileDescriptor parcelFD;
				try
				{
					parcelFD = getContentResolver().openFileDescriptor(selectedImage, "r");
					FileDescriptor fd = parcelFD.getFileDescriptor();
					receivedImage = BitmapFactory.decodeFileDescriptor(fd);
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			else // Camera returned data
			{
				Bundle extras = data.getExtras();
				receivedImage = (Bitmap) extras.get("data");
			}
			
			
			if(receivedImage != null && receivedImage.getByteCount() <= Database.IMAGE_SIZE_LIMIT)
			{
				System.out.println("Using user provided image. Size: "+receivedImage.getByteCount());
				image = receivedImage;
				imageView.setImageBitmap(image);
			}
			else
			{
				System.out.println("Image too big. Size: "+receivedImage.getByteCount());
				Toast.makeText(this, "Sorry, that image is too large. This prototype supports images <1MB.", Toast.LENGTH_LONG).show();
				image = receivedImage;
				imageView.setImageBitmap(image);
			}
			
		}
	}
	
	
	private void setMedAlarm(int medId, Calendar cal, String name, Bitmap image, String dosage, String notes) {
		System.out.println("[DEBUG] AddMedication -> setMedAlarm: function entered");
		System.out.println("[DEBUG] AddMedication -> setMedAlarm: creating alarm for medId=" + medId);
		
		// Ensure that the alarm time is greater than the current time. If not,
		// increase it my one day.
		Calendar currentTime = Calendar.getInstance();
		System.out.println("[DEBUG] AddMedication -> setMedAlarm: current time is " + currentTime.getTimeInMillis());
		System.out.println("[DEBUG] AddMedication -> setMedAlarm: selected time is " + cal.getTimeInMillis());
		if (cal.getTimeInMillis() < (currentTime.getTimeInMillis() - 60000)) {
			System.out.println("[DEBUG] AddMedication -> setMedAlarm: selected time is before current time, roll forward 1 day");
			cal.add(Calendar.DATE, 1);
			System.out.println("[DEBUG] AddMedication -> setMedAlarm: rolled forward time is " + cal.getTimeInMillis());
		}
		
		// Create the intent that will display the notification.
		Intent intent = new Intent(AddMedication.this , NotifyService.class);
		
		// Add the data needed for the notification.
		intent.putExtra("medId", medId);
		intent.putExtra("cal", cal.getTimeInMillis());
		intent.putExtra("name", name);
		intent.putExtra("image", image);
		intent.putExtra("dosage", dosage);
		intent.putExtra("notes", notes);
		
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
		System.out.println("[DEBUG] AddMedication -> setMedAlarm: alarm created for medId=" + medId + " to be triggered at time=" + cal.getTimeInMillis());
	}
}
