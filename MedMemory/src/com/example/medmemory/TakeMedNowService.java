package com.example.medmemory;

import com.example.medmemory.db.Database;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class TakeMedNowService extends Service {
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		// Get the ID for the medicine from the intent.
		int medId = intent.getExtras().getInt("medId");
		
		// Cancel the notification.
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(medId);
		
		// Reduce the pill count.
		int remaining = Database.takeDosage(medId);
		
		// Show how many pills are remaining.
		Context context = getApplicationContext();
		String text = "You have " + remaining + " pills left.";
		int duration = Toast.LENGTH_SHORT;

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
