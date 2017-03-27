package ncssm.students.prospective;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ncssm.student.government.GridAdapter;
import ncssm.student.government.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity extends Activity {
	private final String[] buttonStrings = new String[] {
			"Letter from the Chancellor", "History of NCSSM", "NCSSM Offerings",
			"Admissions Info", "Course Directory", "Student Ambassador Updates",
			"FAQs", "Residential Life", "Week in Photos"};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parents); 

		GridView gv = (GridView) findViewById(R.id.parentView);
		gv.setAdapter(new GridAdapter(this, getWindowManager().getDefaultDisplay().getHeight(), 
				getResources().getDrawable(R.drawable.background_button_gradient), buttonStrings));

		overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		Log.w("Test", getFilesDir().toString());
		
		CopyReadAssets();
		
		Intent catalog = new Intent(Intent.ACTION_VIEW);
		catalog.setDataAndType(
                Uri.parse("file://" + getFilesDir() + "/CourseCatalog.pdf"),
                "application/pdf");
		
		final Intent[] intents = new Intent[] {
				null,
				null,
				null,
				null,
				catalog,
				null,
				null,
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
	
	private void CopyReadAssets()
    {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "CourseCatalog.pdf");
        try
        {
            in = assetManager.open("CourseCatalog.pdf");
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }

        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + getFilesDir() + "/CourseCatalog.pdf"),
                "application/pdf");

        startActivity(intent);*/
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }

}
