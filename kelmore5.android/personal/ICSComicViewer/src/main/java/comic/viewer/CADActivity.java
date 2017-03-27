package comic.viewer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CADActivity extends Activity 
{
	WebView view;
	WebSettings settings;
	TextView test;
	TextView titleBar;
	EditText inputNumber;

	CADGenerator generator;
	ArrayList<String> archives;
	ArrayList<String> titles;
	int currentNumber;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comic);
		
		archives = new ArrayList<String>();
		titles = new ArrayList<String>();
		generator = new CADGenerator();
		File archiveList = this.getFileStreamPath("archive.list");
		if(archiveList.exists())
		{
			archives = readFile("archive.list");
		}
		else
		{
			archives = getFile(R.raw.archives);
			saveFile(archives, "archive.list");
		}
		
		File titleList = this.getFileStreamPath("titles.list");
		if(titleList.exists())
		{
			titles = readFile("titles.list");
		}
		else
		{
			titles = getFile(R.raw.titles);
			saveFile(titles, "title.list");
		}
		
		generator.getNewestArchives(archives, titles);
		
		titleBar = (TextView) findViewById(R.id.title);
		view = (WebView) findViewById(R.id.webView);
		settings = view.getSettings();
		settings.setBuiltInZoomControls(true);
		view.loadUrl(archives.get(1));
		titleBar.setText(titles.get(0));
		TableRow tr = (TableRow) findViewById(R.id.table);
		tr.setBackgroundColor(Color.WHITE);
		makeButtons();
		
		inputNumber = (EditText) findViewById(R.id.number);
		inputNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() 
		{

			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				Integer number = null;
				try
				{
					number = Integer.parseInt(inputNumber.getText().toString());
				}
				catch(NumberFormatException ex)
				{
					toast("Type a number");
					return true;
				}
				if((number < 1) || (number > archives.size()-1))
				{
					toast("Comic does not exist");
				}
				else
				{
					currentNumber = archives.size() - number;
					changeCurrentURL();
				}
				return true;
			}
		});
		inputNumber.setText("" + (archives.size() - currentNumber - 1));
		
		test = (TextView) findViewById(R.id.textWidget);
		currentNumber = 1;
		
		//test.setText("" + titles.size() + " " + archives.size());
	}

	private void makeButtons()
	{     
		final Button random = (Button) findViewById(R.id.random);
		random.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				Random r = new Random();
				currentNumber = r.nextInt(archives.size()-2) + 1;
				changeCurrentURL();
			}
		});

		final Button next = (Button) findViewById(R.id.forward);
		next.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				if(currentNumber > 1)
				{
					currentNumber -= 1;
				}
				else
				{
					return;
				}
				changeCurrentURL();
			}
		});

		final Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				if(currentNumber < (archives.size()-1))
				{
					currentNumber += 1;
				}
				else
				{
					return;
				}
				changeCurrentURL();
			}
		});

		final Button first = (Button) findViewById(R.id.first);
		first.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				currentNumber = (archives.size()-1);
				changeCurrentURL();
			}
		});
		final Button last = (Button) findViewById(R.id.last);
		last.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				currentNumber = 1;
				changeCurrentURL();
			}
		});
	}

	private void changeCurrentURL()
	{
		view.loadUrl(archives.get(currentNumber));
		titleBar.setText(titles.get(currentNumber-1));
		inputNumber.setText("" + (archives.size() - currentNumber));
	}

	private void saveFile(ArrayList<String> archive, String fileName)
	{
		try 
		{
			FileOutputStream fos = this.openFileOutput(fileName, MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(archive);
			oos.close();
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> readFile(String fileName)
	{
		ArrayList<String> archive = new ArrayList<String>();
		FileInputStream fis;
		try 
		{
			fis = this.openFileInput(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			archive = (ArrayList<String>) ois.readObject();
			ois.close();
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (StreamCorruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return archive;
	}
	
	private ArrayList<String> getFile(int id)
	{
		ArrayList<String> archiveList = new ArrayList<String>();
		BufferedInputStream bis = new BufferedInputStream(getResources().openRawResource(id));
        BufferedReader reader = new BufferedReader(new InputStreamReader(bis));
		StringBuilder str = new StringBuilder();
        String line = null;
        try 
        {
			while((line = reader.readLine()) != null)
			{
			   str.append(line);
			}
		} 
        catch (IOException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String archives = str.toString();
        String[] archiveOriginalList = archives.split("aaaaaaaaaa");
        for(String c: archiveOriginalList)
        {
        	archiveList.add(c);
        }
        return archiveList;
	}

	private void toast(String message)
	{
		Toast.makeText(this, message, 0).show();
	}
	
	private class RetrieveCADComic extends AsyncTask<String, Void, Void>
	{

		@Override
		protected Void doInBackground(String... arg0) 
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}