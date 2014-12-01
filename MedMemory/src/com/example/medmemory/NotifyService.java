package com.example.medmemory;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class NotifyService extends Service {
	
	public static final int TAKE_MED_ID_OFFSET = 1000000;
	public static final int SNOOZE_ID_OFFSET = 1000000;
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		System.out.println("[DEBUG] NotifyService -> onStartCommand: function entered");
		
		// Get message info from the intent.
		Bundle extras = intent.getExtras();
		int medId = extras.getInt("medId");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(extras.getLong("cal"));
		String name = extras.getString("name");
		Bitmap image = (Bitmap) extras.getParcelable("image");
		String dosage = extras.getString("dosage");
		String notes = extras.getString("notes");
		
		System.out.println("[DEBUG] NotifyService -> onStartCommand: processing notification for medId=" + medId);
		System.out.println("[DEBUG] NotifyService -> onStartCommand: passed in time=" + cal.getTimeInMillis());
				
		// Prereqs to build notification //////////
		// Create items for a "TakeMedNow" action.
		Intent takeMedNowIntent = new Intent(this , TakeMedNowService.class);
		takeMedNowIntent.putExtra("medId", medId);
		takeMedNowIntent.putExtra("cal", cal.getTimeInMillis());
		takeMedNowIntent.putExtra("name", name);
//		takeMedNowIntent.putExtra("image", image);
		takeMedNowIntent.putExtra("dosage", dosage);
		takeMedNowIntent.putExtra("notes", notes);
		PendingIntent takeMedNowPendingIntent = PendingIntent.getService(getApplicationContext(), TAKE_MED_ID_OFFSET+medId, takeMedNowIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		String takeMedNowTitle = "Take Now";
		int takeMedNowIcon = R.drawable.medical87;
		
		// Create items for a "Snooze" action.
		Intent snoozeIntent = new Intent(this , SnoozeService.class);
		snoozeIntent.putExtra("medId", medId);
		snoozeIntent.putExtra("cal", cal.getTimeInMillis());
		snoozeIntent.putExtra("name", name);
//		snoozeIntent.putExtra("image", image);
		snoozeIntent.putExtra("dosage", dosage);
		snoozeIntent.putExtra("notes", notes);
		PendingIntent snoozePendingIntent = PendingIntent.getService(getApplicationContext(), SNOOZE_ID_OFFSET+medId, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		String snoozeTitle = "Snooze";
		int snoozeIcon = R.drawable.man322;
		
		// Set up the alarm sound.
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		// Build notification //////////
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		
		// Add the "TakeMedNow" and "Snooze" actions.
		mBuilder.addAction(takeMedNowIcon, takeMedNowTitle, takeMedNowPendingIntent);
		mBuilder.addAction(snoozeIcon, snoozeTitle, snoozePendingIntent);
		
		mBuilder.setSmallIcon(R.drawable.noticon);
		mBuilder.setLargeIcon(image);
		mBuilder.setContentTitle("Take " + dosage + " of " + name);
		mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notes));
		mBuilder.setTicker("It's time to take your medication");
		mBuilder.setSound(alarmSound);
		mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		mNotificationManager.notify(medId, notification);
		
		renewNotification(medId, cal, name, image, dosage, notes);
		
		stopSelf();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * Reschedules a new notification for 24 hours later.
	 */
	private void renewNotification(int medId, Calendar cal, String name, Bitmap image, String dosage, String notes) {
		System.out.println("[DEBUG] NotifyService -> renewNotification: function entered");
		
		// Create the intent that will display the notification.
		Intent intent = new Intent(this , NotifyService.class);
		
		// Increase the time for the notification by a day.
		System.out.println("[DEBUG] NotifyService -> renewNotification: updating time for notification");
		System.out.println("[DEBUG] NotifyService -> renewNotification:passed in time=" + cal.getTimeInMillis());
		cal.add(Calendar.DATE, 1);
		System.out.println("[DEBUG] NotifyService -> renewNotification:updated time=" + cal.getTimeInMillis());
		
		// Add the data needed for the notification.
		intent.putExtra("medId", medId);
		intent.putExtra("cal", cal.getTimeInMillis());
		intent.putExtra("name", name);
		intent.putExtra("image", image);
		intent.putExtra("dosage", dosage);
		intent.putExtra("notes", notes);
		
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), medId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	}
}
