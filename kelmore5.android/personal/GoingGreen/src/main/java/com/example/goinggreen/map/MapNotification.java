package com.example.goinggreen.map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.goinggreen.ADK;
import com.example.goinggreen.R;

public class MapNotification extends Service{
	private Location bestLoc;
	private final double delta = 0.00015;

	@Override
	public void onCreate() {
		bestLoc = null;
		
		Log.w(ADK.TAG, "Starting service");

		final double homeLat = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_homeLat", "0"));
		final double homeLong = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_homeLong", "0"));

		// Acquire a reference to the system Location Manager
		final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			private int drops = 0;
			private double lastDif = 0;

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}

			@Override
			public void onLocationChanged(Location loc) {
				Log.w(ADK.TAG, "Test");
				
				if(Utilities.isBetterLocation(loc, bestLoc)) {
					bestLoc = loc;

					double dif = Math.abs(homeLat - bestLoc.getLatitude()) + Math.abs(homeLong - bestLoc.getLongitude());
					if(dif < delta && dif < lastDif) {
						drops++; 
					}
					else {
						drops = 0;
					}

					lastDif = dif;

					//
					//if(drops == 2) {
						NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
						notificationManager.notify(1, createNotification(getBaseContext(), "Difs: " + dif, "Drops: " + drops));

						PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putBoolean("pref_homeNotify", false).apply();

						//locationManager.removeUpdates(this);

						//stopSelf();
					//}
					
				}
			}
		};

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}
	
	private Notification createNotification(Context context, String contextText, String title) {
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.sun)
		.setContentTitle(title)
		.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.sun))
		.setContentText(contextText);

		return mBuilder.build();
	}

	private Notification createNotification(Context context) {
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.sun)
		.setContentTitle("Leaving?")
		.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.sun))
		.setContentText("Did you remember to turn off your lights?");

		return mBuilder.build();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


}
