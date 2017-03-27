package ncssm.student.government.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ncssm.student.government.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends Activity{
	private Location bestLoc;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private GoogleMap map;
	private ViewGroup infoWindow;
	private TextView infoTitle;
	private ImageButton infoButton, directionsButton;
	private OnInfoWindowElemTouchListener infoButtonListener, directionsButtonListener;
	private final ArrayList<ArrayList<Marker>> markers = new ArrayList<ArrayList<Marker>>();
	private boolean[] locationChecks = new boolean[] {false, true, false, true, false, true,
			false, true, false, true, false, true};
	private boolean[] locationChecksOld = new boolean[] {false, true, false, true, false, true,
			false, true, false, true, false, true};
	private int navOption, transportOption, mapOption;
	final int[] mapTypes = new int [] {GoogleMap.MAP_TYPE_NORMAL, GoogleMap.MAP_TYPE_HYBRID,
			GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_TERRAIN };
	private Marker ncssm;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);

		/*if(!PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("pref_introScreen", false)) {
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        	startActivity(new Intent(MainActivity.this, IntroScreen.class));
		}
		else {
			startActivity(new Intent(MainActivity.this, Map.class));
		}*/

		final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);

		for(int i = 0; i < 6; i++) {
			markers.add(new ArrayList<Marker>());
		}

		bestLoc = null;
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			// MapWrapperLayout initialization
			// 39 - default marker height
			// 20 - offset between the default InfoWindow bottom edge and it's content bottom edge 
			mapWrapperLayout.init(map, getPixelsFromDp(this, 39 + 20)); 

			map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(36.017265,-78.921596), 16, 0, 0)));

			updateMapOption();
			map.setMapType(mapTypes[mapOption]);
			map.setMyLocationEnabled(true);
			map.getUiSettings().setMyLocationButtonEnabled(true);
		}

		// We want to reuse the info window for all the markers, 
		// so let's create only one class member instance
		this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_window, null);
		this.infoTitle = (TextView) infoWindow.findViewById(R.id.title);
		this.infoButton = (ImageButton)infoWindow.findViewById(R.id.callButton);
		this.directionsButton = (ImageButton) infoWindow.findViewById(R.id.directionsButton);

		// Setting custom OnTouchListener which deals with the pressed state
		// so it shows up 
		this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
				getResources().getDrawable(android.R.color.transparent),
				getResources().getDrawable(R.drawable.btn_default_pressed_holo_light)) {

			@Override
			protected void onClickConfirmed(View v, Marker marker) {
				// Here we can perform some action triggered after clicking the button
				String number = marker.getSnippet().split(";")[1];
				number = number.replaceAll("-", "");
				call(number);
			}
		}; 
		this.infoButton.setOnTouchListener(infoButtonListener);

		this.directionsButtonListener = new OnInfoWindowElemTouchListener(directionsButton,
				getResources().getDrawable(android.R.color.transparent),
				getResources().getDrawable(R.drawable.btn_default_pressed_holo_light)) {

			@Override
			protected void onClickConfirmed(View v, final Marker marker) {
				// Here we can perform some action triggered after clicking the button

				navOption = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getInt("pref_defaultAppOption", 1);
				transportOption = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getInt("pref_defaultTransportOption", 0);
				final boolean navOptionPicked = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("pref_defaultApp", false);
				final boolean transportOptionPicked = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("pref_defaultTransport", false);

				if(navOptionPicked) {		
					if(transportOptionPicked || navOption == 0) {
						launchNavigation(marker);
					}
					else {
						buildTransportDialog(marker);
					}
				}
				else {
					buildTransportApplicationDialog(marker, transportOptionPicked);
				}
			}
		};

		this.directionsButton.setOnTouchListener(directionsButtonListener);

		map.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				// Setting up the infoWindow with current's marker info
				infoTitle.setText(marker.getTitle());
				infoButtonListener.setMarker(marker);
				directionsButtonListener.setMarker(marker);

				// We must call this to set the current marker and infoWindow references
				// to the MapWrapperLayout
				mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
				return infoWindow;
			}
		});

		ncssm = map.addMarker(new MarkerOptions().position(new LatLng(36.017265,-78.921596)).title("NCSSM").snippet("1219 Broad Street, Durham, NC 27705;919-416-2825"));

		try {
			processLocations();
		}
		catch(IOException ex) {

		}

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}

			@Override
			public void onProviderEnabled(String provider) {}

			@Override
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
		inflater.inflate(R.menu.mapmenu, popup.getMenu());
		popup.getMenu().getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem m) {
				boolean[] locationChecksTrue = new boolean[6];
				for(int i = 1; i < locationChecks.length; i+=2) {
					locationChecksTrue[i/2] = locationChecks[i];
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);
				builder.setTitle(R.string.map_locations).setMultiChoiceItems(R.array.locationsArray, 
						locationChecksTrue, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						locationChecks[which*2] = !locationChecks[which*2];
						locationChecks[(which*2)+1] = isChecked;
					}
				});

				builder.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface di) {
						locationChecks = locationChecksOld.clone();
					}
				});

				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
						locationChecksOld = locationChecks.clone();
						String[] groups = getResources().getStringArray(R.array.locationsArray);
						for(int i = 0; i < locationChecks.length; i+=2) {
							if(locationChecks[i]) {
								changeMarkerView(groups[(i/2)], locationChecks[i+1]);
							}
							locationChecks[i] = false;
						}
						dialog.dismiss();
					}
				});

				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
						locationChecks = locationChecksOld.clone();
						dialog.dismiss();
					}
				});

				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
		});

		popup.getMenu().getItem(1).setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem m) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);

				builder.setTitle("Select Category");
				builder.setItems(R.array.goToArray, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == 1) {
							moveMap(ncssm);
						}
						else {
							AlertDialog.Builder builder2 = new AlertDialog.Builder(Map.this);

							final ArrayList<Marker> locations = new ArrayList<Marker>();
							CharSequence[] titles = null;

							if(which == 0) {
								for(ArrayList<Marker> list: markers) {
									for(Marker m: list) {
										locations.add(m);
									}
								}
								Collections.sort(locations, new Comparator<Marker>() {
									public int compare(Marker a, Marker b) {
										return a.getTitle().compareTo(b.getTitle());
									}
								});
							}
							else {
								for(Marker m: markers.get(which-2)) {
									locations.add(m);
								}
							}

							titles = new CharSequence[locations.size()];
							for(int i = 0; i < locations.size(); i++) {
								titles[i] = locations.get(i).getTitle();
							}

							builder2.setItems(titles, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									moveMap(locations.get(which));
								}
							});

							builder2.setNegativeButton(R.string.cancel, null);

							dialog.dismiss();						
							builder2.create().show();
						}
					}
				});

				builder.setNegativeButton(R.string.cancel, null);

				builder.create().show();

				return true;
			}
		});

		popup.getMenu().getItem(2).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem m) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);

				builder.setTitle(R.string.mapmenu_mapType);

				final View view = getLayoutInflater().inflate(R.layout.map_type_dialog, null);

				final RadioButton[] buttons = new RadioButton[] {
						(RadioButton) view.findViewById(R.id.normalMap),
						(RadioButton) view.findViewById(R.id.hybridMap),
						(RadioButton) view.findViewById(R.id.satelliteMap),
						(RadioButton) view.findViewById(R.id.terrainMap)
				};

				buttons[0].setOnClickListener(
						changeMapType(0, new RadioButton[] {buttons[1], buttons[2], buttons[3]}));
				buttons[1].setOnClickListener(
						changeMapType(1, new RadioButton[] {buttons[0], buttons[2], buttons[3]}));
				buttons[2].setOnClickListener(
						changeMapType(2, new RadioButton[] {buttons[0], buttons[1], buttons[3]}));
				buttons[3].setOnClickListener(
						changeMapType(3, new RadioButton[] {buttons[0], buttons[1], buttons[2]}));

				updateMapOption();

				buttons[mapOption].setChecked(true);

				builder.setView(view);

				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
						map.setMapType(mapTypes[mapOption]);
						if(((CheckBox)view.findViewById(R.id.appAskAgain3)).isChecked()) {
							PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putBoolean("pref_defaultMapType", true).commit();
							PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putInt("pref_defaultMapTypeOption", mapOption).commit();
						}
						dialog.dismiss();
					}
				});

				builder.setNegativeButton(R.string.cancel, null);

				builder.create().show();
				return true;
			}
		});

		popup.getMenu().getItem(3).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem m) {
				startActivity(new Intent(Map.this, MapSettings.class));
				return true;
			}

		});
		popup.show();
	}

	public void processLocations() throws IOException {
		BufferedReader bs = new BufferedReader(new InputStreamReader(getAssets().open("locations.csv")));
		String line = bs.readLine();
		while(line != null) {
			String[] split = line.split(";");
			addToLocationLists(map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(split[2]),
					Double.parseDouble(split[3]))).title(split[0]).snippet(split[4] + ";" + split[5])), split[1]);
			line = bs.readLine();
		}
	}

	public static int getPixelsFromDp(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dp * scale + 0.5f);
	}

	private void call(String number) {
		try {
			startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
		} catch (ActivityNotFoundException e) {
			Log.e("helloandroid dialing example", "Call failed", e);
		}
	}

	private void addToLocationLists(Marker marker, String group) {
		if(group.equals("Food")) {
			markers.get(0).add(marker);
		}
		else if(group.equals("Music")) {
			markers.get(1).add(marker);
		}
		else if(group.equals("Other")) {
			markers.get(2).add(marker);
		}
		else if(group.equals("Restaurant")) {
			markers.get(3).add(marker);
		}
		else if(group.equals("Sports")) {
			markers.get(4).add(marker);
		}
		else {
			markers.get(5).add(marker);
		}
	}

	private void changeMarkerView(String group, boolean isChecked) {
		if(group.equals("Food")) {
			changeMarkerViewHelper(markers.get(0), isChecked);
		}
		else if(group.equals("Music")) {
			changeMarkerViewHelper(markers.get(1), isChecked);
		}
		else if(group.equals("Other")) {
			changeMarkerViewHelper(markers.get(2), isChecked);
		}
		else if(group.equals("Restaurant")) {
			changeMarkerViewHelper(markers.get(3), isChecked);
		}
		else if(group.equals("Sports")) {
			changeMarkerViewHelper(markers.get(4), isChecked);
		}
		else {
			changeMarkerViewHelper(markers.get(5), isChecked);
		}
	}

	private void changeMarkerViewHelper(ArrayList<Marker> markers, boolean isChecked) {
		for(Marker m: markers) {
			m.setVisible(isChecked);
		}
	}

	/**
	 * Launches a navigation window based on user-selected navigation application and transportation method
	 * options defined in arrays
	 * 
	 * @param marker
	 * The marker being selected for the location the user is calling navigation on
	 */
	private void launchNavigation(Marker marker) {
		Intent intent = null;
		if(navOption == 0) {
			intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?daddr=" + marker.getTitle() + ", " + 
							marker.getSnippet().split(";")[0]));
		}
		else {
			String mode = "&mode=";
			if(transportOption == 0) {
				mode += "walking";
			}
			else if(transportOption == 1) {
				mode +="bicycling";
			}
			else if(transportOption == 2) {
				mode += "driving";
			}
			else {
				mode += "transit";
			}

			intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("google.navigation:q=" + marker.getTitle() + ", " + 
							marker.getSnippet().split(";")[0] + mode));
		}
		startActivity(intent);
	}

	/**
	 * Creates the transportation method option dialog
	 * 
	 * @param marker
	 * The marker the user is transportation to
	 */
	private void buildTransportDialog(final Marker marker) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);
		builder.setTitle(R.string.navTransportation);

		final View view = getLayoutInflater().inflate(R.layout.transport_nav, null);

		final RadioButton walkingButton = (RadioButton) view.findViewById(R.id.walkingButton);
		final RadioButton bicycleButton = (RadioButton) view.findViewById(R.id.bicycleButton);
		final RadioButton drivingButton = (RadioButton) view.findViewById(R.id.drivingButton);
		final RadioButton transitButton = (RadioButton) view.findViewById(R.id.transitButton);

		walkingButton.setOnClickListener(checkButtonsListener(false, 0, 
				new RadioButton[] {bicycleButton, drivingButton, transitButton}));
		bicycleButton.setOnClickListener(checkButtonsListener(false, 1, 
				new RadioButton[] {walkingButton, drivingButton, transitButton}));
		drivingButton.setOnClickListener(checkButtonsListener(false, 2, 
				new RadioButton[] {walkingButton, bicycleButton, transitButton}));
		transitButton.setOnClickListener(checkButtonsListener(false, 3, 
				new RadioButton[] {walkingButton, bicycleButton, drivingButton}));

		builder.setView(view);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(((CheckBox)view.findViewById(R.id.appAskAgain2)).isChecked()) {
					PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putBoolean("pref_defaultTransport", true).commit();
					PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putInt("pref_defaultTransportOption", transportOption).commit();
				}
				launchNavigation(marker);
			}
		});

		builder.setNegativeButton(R.string.cancel, null);

		builder.create().show();
	}

	/**
	 * Creates the transportation application option dialog
	 * 
	 * @param marker
	 * The marker the user is transportation to
	 */
	private void buildTransportApplicationDialog(final Marker marker, final boolean transportOptionPicked) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);
		builder.setTitle(R.string.navOptionTitle);

		final View view = getLayoutInflater().inflate(R.layout.transport_app, null);

		final RadioButton mapsButton = (RadioButton) view.findViewById(R.id.googleMapsButton);
		final RadioButton navButton = (RadioButton) view.findViewById(R.id.navigationButton);

		mapsButton.setOnClickListener(checkButtonsListener(true, 0, new RadioButton[] {navButton}));
		navButton.setOnClickListener(checkButtonsListener(true, 1, new RadioButton[] {mapsButton}));

		builder.setView(view);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(((CheckBox)view.findViewById(R.id.appAskAgain)).isChecked()) {
					PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putBoolean("pref_defaultApp", true).commit();
					PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putInt("pref_defaultAppOption", navOption).commit();
				}
				if(navOption == 1 && !transportOptionPicked) {
					buildTransportDialog(marker);
				}
				else {
					launchNavigation(marker);
				}
			}
		});

		builder.setNegativeButton(R.string.cancel, null);

		builder.create().show();
	}

	/**
	 * Creates the listener for the directions option view
	 * 
	 * @param state
	 * Whether the option vew is for transportation method or application 
	 * 
	 * @param number
	 * Which option number was chosen
	 * 
	 * @param buttons
	 * The buttons to uncheck from the view
	 * 
	 * @return
	 * returns the listener
	 *
	 */
	private OnClickListener checkButtonsListener(final boolean state, final int number, final RadioButton[] buttons) {
		return new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(state) {
					navOption = number;
				}
				else {
					transportOption = number;
				}

				for(RadioButton b: buttons) {
					b.setChecked(false);
				}

			}
		};
	}

	/**
	 * Creates the listener for the map type dialog box that will change the map type
	 * 
	 * @param mapType
	 * Which map type to change to
	 * 
	 * @param number
	 * The number in the dialog
	 * 
	 * @param buttons
	 * The other buttons in the display to uncheck
	 * 
	 * @return
	 * returns the listener
	 */
	private OnClickListener changeMapType(final int number, final RadioButton[] buttons) {
		return new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mapOption = number;

				for(RadioButton b: buttons) {
					b.setChecked(false);
				}
			}
		};
	}

	/**
	 * Calls Preference Manager to set the map option variable to the default map option type if one is set
	 */
	private void updateMapOption() {
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_defaultMapType", false)) {
			mapOption = PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_defaultMapTypeOption", 0);
		}
		else {
			mapOption = 0;
		}
	}

	/**
	 * Moves the map to the specified marker and opens the marker's info window
	 * 
	 * @param marker
	 * Marker to move the map to
	 */
	private void moveMap(Marker marker) {
		map.moveCamera(CameraUpdateFactory.newCameraPosition(
				new CameraPosition(marker.getPosition(), 16, 0, 0)));
		marker.showInfoWindow();
	}
}
