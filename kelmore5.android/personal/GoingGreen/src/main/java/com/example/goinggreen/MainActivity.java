package com.example.goinggreen;

//Google grain

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.goinggreen.map.Map;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/cooper_becker_poster_black.ttf");
		TextView title = (TextView) findViewById(R.id.title);
		//title.setTypeface(tf);


		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.id.title);

		//Set default preferences
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		Button location = (Button) findViewById(R.id.locationButton);

		location.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, Map.class));
				//Connection.dumpSensors();
			}
		});

		Button temp = (Button) findViewById(R.id.temp);

		temp.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, Temperature.class));
			}
		});
	}

	public void showPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.activity_main, popup.getMenu()); //
		popup.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem m) {
				startActivity(new Intent(MainActivity.this, Settings.class));
				return true;
			}

		});
		popup.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		/*getMenuInflater().inflate(R.menu.activity_main, menu);

		menu.getItem(0).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem m) {
				startActivity(new Intent(MainActivity.this, Settings.class));
				return true;
			}

		});*/
		return true;
	}

}