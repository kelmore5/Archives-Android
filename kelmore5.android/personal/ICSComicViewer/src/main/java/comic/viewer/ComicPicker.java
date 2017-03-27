package comic.viewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ComicPicker extends Activity
{
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		makeButtons();
	}
	
	public void makeButtons()
	{
		final Button cad = (Button) findViewById(R.id.CAD);
		cad.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				startActivity(new Intent(ComicPicker.this, CADActivity.class));
			}
		});
		
		final Button xkcd = (Button) findViewById(R.id.xkcd);
		xkcd.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				startActivity(new Intent(ComicPicker.this, XKCDActivity.class));
			}
		});
	}

}
