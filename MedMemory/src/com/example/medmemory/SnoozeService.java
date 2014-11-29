package com.example.medmemory;

import android.app.NotificationManager;
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
		// Get info from the intent.
		Bundle extras = intent.getExtras();
		int medId = extras.getInt("medId");
		String name = extras.getString("name");
		Bitmap image = (Bitmap) extras.getParcelable("image");
		String dosage = extras.getString("dosage");
		String notes = extras.getString("notes");
		
		// Cancel the notification.
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(medId);
		
		// Tell the user that we will get back to them.
		Context context = getApplicationContext();
		String text = "We'll remind you in a few minutes.";
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
