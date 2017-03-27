package ncssm.student.government.map;

import ncssm.student.government.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class MapSettings extends Activity{
	private static MapSettings settings;

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
		private int transportOption, navOption, mapTypeOption;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.mapsettings);

			mapTypeOption = 0;
			navOption = 1;
			transportOption = 0;

			Preference myPref = (Preference) findPreference("pref_bugReport");
			myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				public boolean onPreferenceClick(Preference preference) {
					//open browser or intent here
					/* Create the Intent */
					final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

					/* Fill it with Data */
					emailIntent.setType("plain/text");
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"ncssmdirectorofit@gmail.com"});
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Guide to Durham Bug Report");

					String text = "Please describe the issue you are having below. " +
							"We hope will be able to fix it shortly\n\n";
					text += "Message: \n\n\n\n";
					
					text += "***Please ignore this. It will help me fix your issue***\n\n";
					text += "Android OS: " + Build.VERSION.CODENAME + '\n';
					text += "Release #: " + Build.VERSION.RELEASE + '\n';
					
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);

					/* Send it off to the Activity-Chooser */
					settings.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
					return true;
				}
			});
			
			myPref = (Preference) findPreference("pref_suggestion");
			myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				public boolean onPreferenceClick(Preference preference) {
					//open browser or intent here
					final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

					/* Fill it with Data */
					emailIntent.setType("plain/text");
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"ncssmdirectorofit@gmail.com"});
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Guide to Durham Location Suggestion");

					String text = "Please fill in the information below. Hopefully we'll add your place to map soon!\n\n";
					text += "Name of Place: \n\n\n";
					text += "Address: \n\n\n";
					
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);

					/* Send it off to the Activity-Chooser */
					settings.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
					return true;
				}
			});
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
		public void onSharedPreferenceChanged(final SharedPreferences pref, final String key) {
			if(!key.contains("Option")) {
				if(!pref.getBoolean(key, false)) {
					pref.edit().putInt(key + "Option", 0).commit();
				}
				else {
					OnCancelListener cancelListener = new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							pref.edit().putBoolean(key, false).commit();
							dialog.dismiss();
						}
					};

					DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
							pref.edit().putBoolean(key, false).commit();
							dialog.dismiss();
						}
					};

					if(key.equals("pref_defaultApp")) {
						AlertDialog.Builder builder = new AlertDialog.Builder(settings);
						builder.setTitle(R.string.navOptionTitle).setSingleChoiceItems(R.array.navOptions, 
								navOption, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg, int itemPicked) {
								navOption = itemPicked;
							}
						});

						builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User clicked OK button
								pref.edit().putInt(key + "Option", navOption).commit();
								dialog.dismiss();
							}
						});

						builder.setNegativeButton(R.string.cancel, cancelButtonListener);
						builder.setOnCancelListener(cancelListener);

						builder.create().show();
					}
					else if(key.equals("pref_defaultTransport")){
						AlertDialog.Builder builder = new AlertDialog.Builder(settings);
						builder.setTitle(R.string.navTransportation).setSingleChoiceItems(R.array.transportationMethods, 
								transportOption, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg, int itemPicked) {
								transportOption = itemPicked;
							}
						});

						builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								pref.edit().putInt(key + "Option", transportOption).commit();
							}
						});

						builder.setNegativeButton(R.string.cancel, cancelButtonListener);
						builder.setOnCancelListener(cancelListener);

						builder.create().show();
					}
					else if(key.equals("pref_defaultMapType")) {
						AlertDialog.Builder builder = new AlertDialog.Builder(settings);
						builder.setTitle(R.string.mapmenu_mapType).setSingleChoiceItems(R.array.mapTypeOptions, 
								mapTypeOption, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg, int itemPicked) {
								mapTypeOption = itemPicked;
							}
						});

						builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								pref.edit().putInt(key + "Option", mapTypeOption).commit();
								AlertDialog.Builder builder2 = new AlertDialog.Builder(settings);
								builder2.setTitle("Update");
								builder2.setMessage("This will occur next time you load the application");
								builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg, int itemPicked) {
										arg.dismiss();
									}
								});

								dialog.dismiss();
								builder2.create().show();
							}
						});

						builder.setNegativeButton(R.string.cancel, cancelButtonListener);
						builder.setOnCancelListener(cancelListener);

						builder.create().show();
					}
				}
			}
		}
	}
}
