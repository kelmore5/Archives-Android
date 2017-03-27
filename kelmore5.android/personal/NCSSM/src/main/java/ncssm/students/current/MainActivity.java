package ncssm.students.current;

import ncssm.student.government.GridAdapter;
import ncssm.student.government.R;
import ncssm.student.government.map.IntroScreen;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity extends Activity {
	private final String[] buttonStrings = new String[] {
			"Focus Quick Link", "Moodle Quick Link", "Campus Safety/Health Updates",
			"NCSSM SGA", "Student Polls", "Go UNIs",
			"Guide to Durham Mobile", "Bubble Update", "Week in Photos"};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parents); 

		GridView gv = (GridView) findViewById(R.id.parentView);
		gv.setAdapter(new GridAdapter(this, getWindowManager().getDefaultDisplay().getHeight(), 
				getResources().getDrawable(R.drawable.background_button_gradient), buttonStrings));

		overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		final Intent[] intents = new Intent[] {
				new Intent(Intent.ACTION_VIEW, Uri.parse("https://focus.ncssm.edu/focus/")),
				new Intent(Intent.ACTION_VIEW, Uri.parse("http://moodle.ncssm.edu/login/index.php")),
				null,
				null,
				null,
				null,
				new Intent(MainActivity.this, IntroScreen.class),
				null,
				null
		};

		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long arg3) {
				startActivity(intents[position]);
			}

		});
	}

}
