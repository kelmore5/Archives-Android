package ncssm.student.government.map;

import ncssm.student.government.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class IntroScreen extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro_screen);
		
		new Handler().postDelayed(new Runnable() {
			  @Override
			  public void run() {

			    //Create an intent that will start the main activity.
				  startActivity(new Intent(IntroScreen.this, Map.class));

			    //Finish splash activity so user cant go back to it.
			    IntroScreen.this.finish();

			    //Apply splash exit (fade out) and main entry (fade in) animation transitions.
			    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			  }
			}, 3000);
	}
}
