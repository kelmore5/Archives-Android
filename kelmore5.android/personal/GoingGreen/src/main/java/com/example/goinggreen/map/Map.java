package com.example.goinggreen.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.PopupMenu;

import com.example.goinggreen.ADK;
import com.example.goinggreen.R;

public class Map extends Activity {
	private Location bestLoc;
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		
		Log.w(ADK.TAG, "Test");

		bestLoc = null;
		/*if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			map.getUiSettings().setMyLocationButtonEnabled(true);
		}*/
		
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

				Log.w(ADK.TAG, "Lat: " + bestLoc.getLatitude() + " Long: " + bestLoc.getLongitude());
			}
		};
		
		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
	}
	
	@Override
	public void onPause() {
		locationManager.removeUpdates(locationListener);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
		super.onResume();
	}

	public void showPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.activity_main, popup.getMenu());
		popup.getMenu().getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem m) {
				if(bestLoc != null) {
					Editor edit = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
					edit.putLong("locTime", bestLoc.getTime());
					edit.putFloat("locAccuracy", bestLoc.getAccuracy());
					edit.putString("locProvider", bestLoc.getProvider());
					edit.putString("locLat", "" + bestLoc.getLatitude());
					edit.putString("locLong", "" + bestLoc.getLongitude());
					edit.apply();
				}
				startActivity(new Intent(Map.this, MapSettings.class));
				return true;
			}

		});
		popup.show();
	}
}
