package com.example.goinggreen;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Settings extends Activity {
	private static Settings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = this;

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
	}

	public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
		private NotificationManager notificationManager;
		private AlarmManager alarms;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);

			alarms = (AlarmManager) settings.getSystemService(Context.ALARM_SERVICE);
			notificationManager =(NotificationManager) settings.getSystemService(Context.NOTIFICATION_SERVICE);
		}

		@Override
		public void onResume() {
			super.onResume();
			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
			super.onPause();
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
			if(key.equals("pref_notifyOnOff") || key.equals("pref_alwaysNotify") || key.equals("pref_notifyFrequency")) {
				Notification notification = TempNotification.createNotification(settings.getBaseContext());

				// Prepare the intent which should be launched at the date
				Intent intent = new Intent(settings, TempNotification.class);

				// Prepare the pending intent
				PendingIntent notificationIntent = PendingIntent.getBroadcast(settings, 0, intent, 0);

				alarms.cancel(notificationIntent);
				notificationManager.cancel(0);
				if(pref.getBoolean("pref_notifyOnOff", false)) {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.SECOND, 1);
					long firstTime = c.getTimeInMillis();

					alarms.setInexactRepeating(AlarmManager.RTC, firstTime, Long.parseLong(pref.getString("pref_notifyFrequency", "21700000")), notificationIntent);
					notificationManager.notify(0, notification);
				}
			}
		}


	}
}
