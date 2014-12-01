package com.example.medmemory;

import java.util.Calendar;

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

public class SnoozeService extends Service {
	
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

		
		// Cancel the notification.
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(medId);
		
		// Tell the user that we will get back to them.
		Context context = getApplicationContext();
		String text = "We'll remind you in ten minutes.";
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		renewNotification(medId, cal, name, dosage, notes);

		stopSelf();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * Reschedules a new notification for 10 minutes later.
	 */
	private void renewNotification(int medId, Calendar cal, String name, String dosage, String notes) {
		// Create the intent that will display the notification.
		Intent intent = new Intent(this , NotifyService.class);
		
		// Increase the time for the notification by a day.
		System.out.println(cal.getTimeInMillis());
		cal.add(Calendar.MINUTE, 10);
		System.out.println(cal.getTimeInMillis());
		
		
		// Add the data needed for the notification.
		intent.putExtra("medId", medId);
		intent.putExtra("cal", cal.getTimeInMillis());
		intent.putExtra("name", name);
//		intent.putExtra("image", image);
		intent.putExtra("dosage", dosage);
		intent.putExtra("notes", notes);
		
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), medId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	}

}
