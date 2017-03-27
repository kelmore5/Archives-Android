package elmore.magic.eightball;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MagicEightBall extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magic_eight_ball);
		
		final TextView mainText = (TextView) findViewById(R.id.mainText);
		Button mainButton = (Button) findViewById(R.id.mainButton);
		
		final String[] answers = new String[] {
			"Yes",
			"No",
			"Maybe so",
			"Try asking again",
			"Another time perhaps",
			"Not in this lifetime",
			"Just stop asking"
		};
		
		mainButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Random r = new Random();
				mainText.setText(answers[r.nextInt(answers.length)]);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.magic_eight_ball, menu);
		return true;
	}

}
