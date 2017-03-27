package com.example.goinggreen.map;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.goinggreen.ADK;
import com.example.goinggreen.R;

public class MapSettings extends Activity {
	private static MapSettings settings;
	private static Location bestLoc;
	private static LocationManager locationManager;
	private static LocationListener locationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = this;

		if(!PreferenceManager.getDefaultSharedPreferences(this).getString("locProvider", "").equals("")) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			bestLoc = new Location(prefs.getString("locProvider", ""));
			bestLoc.setAccuracy(prefs.getFloat("locAccuracy", 0));
			bestLoc.setTime(prefs.getLong("locTime", 0));
			bestLoc.setLatitude(Double.parseDouble(prefs.getString("locLat", "0")));
			bestLoc.setLongitude(Double.parseDouble(prefs.getString("locLong", "0")));
		}
		else {
			bestLoc = null;
		}

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}

			@Override
			public void onLocationChanged(Location loc) {
				if(Utilities.isBetterLocation(loc, bestLoc)) {
					bestLoc = loc;
				}
			}
		};

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
		
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
	}
	
	@Override
	public void onPause() {
		locationManager.removeUpdates(locationListener);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		super.onResume();
	}

	public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.mapsettings);
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

			Log.w(ADK.TAG, "Lat: " + pref.getString("pref_homeLat", "0") + " Long: " + pref.getString("pref_homeLong", "0"));

			if(key.equals("pref_homeAddress")) {
				String search = pref.getString(key, "");
				//Log.w(ADK.TAG, "Address: " + address);
				if(!search.equals("")) {
					Geocoder coder = new Geocoder(settings);
					List<Address> addresses = null;
					try {
						addresses = coder.getFromLocationName(search, 10);
					} catch (IOException e) {
						e.printStackTrace();
					}

					if(addresses != null) {
						if(addresses.size() == 1) {
							String address = "";
							int index = 0;
							while(addresses.get(0).getAddressLine(index) != null) {
								address += addresses.get(0).getAddressLine(index) + ", ";
								index++;
							}

							address = address.substring(0, address.lastIndexOf(","));

							Editor editor = pref.edit();

							editor.putString("pref_homeLat", "" +  addresses.get(0).getLatitude());
							editor.putString("pref_homeLong", "" + addresses.get(0).getLongitude());
							editor.putString(key, address);
							editor.apply();
						}
					}
				}
			}

			else if(key.equals("pref_homeLoc")) {
				if(pref.getBoolean(key, false)) {
					if(bestLoc != null) {
						Editor editor = pref.edit(); //

						editor.putString("pref_homeLat", "" +  bestLoc.getLatitude());
						editor.putString("pref_homeLong", "" +  bestLoc.getLongitude());
						editor.apply();
						
						Log.w(ADK.TAG, "Lat: " + bestLoc.getLatitude() + " Long: " + bestLoc.getLongitude());
					}
				}
			}
			
			else if(key.equals("pref_homeNotify")) {
				Log.w(ADK.TAG, "Home notify");
				if(pref.getBoolean(key, false)) {
					locationManager.removeUpdates(locationListener);
					settings.startService(new Intent(settings, MapNotification.class));
				}
			}

		}

	}
}
