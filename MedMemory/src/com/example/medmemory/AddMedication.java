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
					setMedAlarm(medId, cal);
					
					System.out.println("Added med successfully? "+success);
				}
				else
				{
					Database.context = AddMedication.this;
					Database.deleteMedicationById(editMed.getId());
					
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
					
					boolean success = Database.addMedication(editMed);
					
					// Set the alarm.
					setMedAlarm(editMed.getId(), cal);
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
        
        // Test notification button listener.
        testNotificationBtn = (Button) findViewById(R.id.test_notification_button);
        testNotificationBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// Add notification
				Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AddMedication.this);
				mBuilder.setSmallIcon(R.drawable.noticon);
				mBuilder.setContentTitle("It's time to take your medication!");
				mBuilder.setContentText("Click on this notification to get the details.");
				mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
				mBuilder.setSound(alarmSound);
				mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(1, mBuilder.build());
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
			if(data.getData() != null) // Gallery returned data
			{
				Uri selectedImage = data.getData();
				
				
				ParcelFileDescriptor parcelFD;
				try
				{
					parcelFD = getContentResolver().openFileDescriptor(selectedImage, "r");
					FileDescriptor fd = parcelFD.getFileDescriptor();
					image = BitmapFactory.decodeFileDescriptor(fd);
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			else // Camera returned data
			{
				Bundle extras = data.getExtras();
				image = (Bitmap) extras.get("data");
			}
			
			imageView.setImageBitmap(image);
		}
	    
	}
	
	
	private void setMedAlarm(int medId, Calendar cal) {
		// Set an alarm with the alarm manager.
		Intent myIntent = new Intent(AddMedication.this , NotifyService.class);     
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 24*60*60*1000 , pendingIntent);  //set repeating every 24 hours
	}
	
	
}
