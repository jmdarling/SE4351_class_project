package com.example.medmemory;

import java.util.Calendar;

import com.example.medmemory.db.Database;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class TakeMedNowService extends Service {
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		// **DEBUG**
		System.out.println("Entered TakeMedNowService -> onStartCommand.");
		
		// Get info from the intent.
		Bundle extras = intent.getExtras();
		int medId = extras.getInt("medId");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(extras.getLong("cal"));
		String name = extras.getString("name");
//		Bitmap image = (Bitmap) extras.getParcelable("image");
		String dosage = extras.getString("dosage");
		String notes = extras.getString("notes");
		
		// **DEBUG**
		System.out.println("TakeMedNowService -> onStartCommand: medId=" + medId);
		
		// Close the current notification.
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(medId);
				
		// Reduce the pill count.
		int remaining = Database.takeDosage(medId);
		
		// Show how many pills are remaining.
		Context context = getApplicationContext();
		String text = "You have " + remaining + " pills left.";
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		stopSelf();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
