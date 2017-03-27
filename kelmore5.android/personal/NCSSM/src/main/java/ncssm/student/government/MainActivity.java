package ncssm.student.government;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		
		createButton(R.id.parents, new Intent(MainActivity.this, ncssm.parents.MainActivity.class));
		createButton(R.id.curStudents, new Intent(MainActivity.this, ncssm.students.current.MainActivity.class));
		createButton(R.id.prosStudent, new Intent(MainActivity.this, ncssm.students.prospective.MainActivity.class));
		
		//startActivity(new Intent(MainActivity.this, ncssm.parents.MainActivity.class));
	}
	
	private void createButton(int id, final Intent intent) {
		Button button = (Button) findViewById(id);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(intent);
			}
		});
	}

}
