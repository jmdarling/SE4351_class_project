package com.example.medmemory;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class NotifyService extends Service {
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(R.drawable.noticon);
		mBuilder.setContentTitle("It's time to take your medication!");
		mBuilder.setContentText("Click on this notification to get the details.");
		mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
		stopSelf();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
