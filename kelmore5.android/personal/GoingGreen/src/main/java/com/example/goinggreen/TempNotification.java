package com.example.goinggreen;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class TempNotification extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, createNotification(context));
	}

	public static Notification createNotification(Context context) {
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.sun)
		.setContentTitle("Temperature Update")
		.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.sun));

		String outsideTempString = "";
		
		long update = PreferenceManager.getDefaultSharedPreferences(context).getLong("pref_outsideTempUpdate", 0);

		if(System.currentTimeMillis() - update > 3600000 || update == 0) {
			outsideTempString = Temperature.getOutsideTemperature();
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("pref_outsideTemp", outsideTempString);
			PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("pref_outsideTempUpdate", System.currentTimeMillis());
		}
		else {
			outsideTempString = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_outsideTemp", "0");
		}
		
		mBuilder.setContentText("Inside: " + Temperature.getInsideTemperature() + " Outside: " + outsideTempString);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, Temperature.class);

		Intent test = new Intent(context, MainActivity.class);

		PendingIntent resultPendingIntent = PendingIntent.getActivities(context, 0, new Intent[] { test, resultIntent }, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		// mId allows you to update the notification later on.
		Notification notification = mBuilder.build();

		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_alwaysNotify", false)) {
			notification.flags = Notification.FLAG_ONGOING_EVENT;
		}

		return notification;
	}
}
