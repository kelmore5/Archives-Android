package com.example.goinggreen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

public class Temperature extends Activity implements OnSharedPreferenceChangeListener{
	private final static int skip = 16;
	private static TextView outsideTemp, energyWaste, insideTemp;
	private Timer timer;
	private static String outsideTempString = "";
	private static Temperature activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temp);

		activity = this;

		//Set default preferences
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		insideTemp = (TextView) findViewById(R.id.insideTempText);
		outsideTemp = (TextView) findViewById(R.id.outsideTempText);
		energyWaste = (TextView) findViewById(R.id.energyWasteText);

		getOutsideTemperature();

		/*if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("pref_outsideTemp", "null").equals("null")
				|| System.currentTimeMillis() - PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getLong("pref_outsideTempUpdate", 0) > 3600000) {
			PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("pref_outsideTemp", getOutsideTemperature());
			PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putLong("pref_outsideTempUpdate", System.currentTimeMillis());
		}

		outsideTemp.setText(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("pref_outsideTemp", "0"));*/

		timer = new Timer();
		timer.scheduleAtFixedRate(new Task(), 0, 3600000); //

	}

	public static String getInsideTemperature() {
		BufferedInputStream adk = Connection.getSocket(skip);
		String temp = process(Connection.getSensor(adk));
		try {
			adk.close();

			if(temp.equals("")) {
				return "";
			}

			String tempText = "" + outsideTemp.getText();
			if(!tempText.equals("")) {
				String inTempText = "" + insideTemp.getText();
				float tempInt = Float.parseFloat(tempText.substring(0, tempText.indexOf("°")));
				float inTempInt = Float.parseFloat(inTempText.substring(0, inTempText.indexOf("°")));
				Log.w(ADK.TAG, "Temp Out: " + tempInt + " Temp In: " + inTempInt);

				energyWaste.setText("" + (0.12*0.52752793*(inTempInt - (10 + tempInt))/1000));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}

	public static String getOutsideTemperature() {
		String zip = PreferenceManager.getDefaultSharedPreferences(activity).getString("pref_zipCode", "27705");
		String temp = "";
		if(!PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("pref_degrees", false)) {
			temp = "temp_F";
		}
		else {
			temp = "temp_C";
		}
		new HTMLGenerator().execute("http://free.worldweatheronline.com/feed/weather.ashx?q=" + zip + "&fx=no&format=xml&key=5dbfe3de39044746131502", temp);
		return outsideTempString;
	}

	private static String process(int temp) {
		String tempC = "" + temp;
		try {
			tempC = tempC.substring(0, 2) + "." + tempC.substring(2);
		}
		catch(Exception ex) {
			return "";
		}
		if(!PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("pref_degrees", false)) {
			tempC = "" + (Double.parseDouble(tempC)*(9.0/5.0) + 32.0) + "°F";
		}
		else {
			tempC = tempC + "°C";
		}
		return tempC;
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}

	private class Task extends TimerTask {

		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				public void run() {
					insideTemp.setText(getInsideTemperature());
				}
			});
		}
	}

	private static class HTMLGenerator extends AsyncTask<String, Void, String> {
		private String temp;
		@Override
		protected String doInBackground(String... urls) {
			String string = "";
			try
			{


				temp = urls[1];
				URL url = new URL(urls[0]);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setRequestProperty("User-Agent", "");
				connection.setRequestMethod("POST");
				connection.setDoInput(true);
				connection.connect();

				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while((line = reader.readLine()) != null)
				{
					if(line.contains(temp))
					{
						string = line;
						break;
					}
				}
				in.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return string;
		}

		protected void onPostExecute(String line) {
			line = line.substring(line.indexOf(temp)+7);
			line = line.substring(0, line.indexOf(temp)-2);
			float temp = Float.parseFloat(line);

			Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
			if(!PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("pref_degrees", false)) {
				outsideTempString = line + "°F";
				editor.putFloat("pref_degOutF", temp);
				editor.putFloat("pref_degOutC", (float) ((temp-32.0)*(5.0/9.0)));
			}
			else {
				outsideTempString = line + "°C";
				editor.putFloat("pref_degOutC", temp);
				editor.putFloat("pref_degOutF", (float) (temp*(9.0/5.0) + 32.0));
			}

			editor.apply();

			if(outsideTemp != null) {
				outsideTemp.setText(outsideTempString);
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {

	}
}
