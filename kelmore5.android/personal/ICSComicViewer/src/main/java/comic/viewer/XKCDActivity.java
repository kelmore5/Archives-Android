package comic.viewer;

import android.app.Activity;
import android.graphics.Color;
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

public class XKCDActivity extends Activity
{
	WebView view;
	WebSettings settings;
	TextView test;
	TextView titleBar;
	EditText inputNumber;

	String currentURL;
	XKCDGenerator generator;
	int currentNumber;
	int newestComicNumber;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comic);
		titleBar = (TextView) findViewById(R.id.title);
		view = (WebView) findViewById(R.id.webView);
		settings = view.getSettings();
		settings.setBuiltInZoomControls(true);
		generator = new XKCDGenerator();
		currentURL = generator.getComicSite();
		if(generator.isNumbered())
		{
			newestComicNumber = generator.currentComicNumber();
			currentNumber = newestComicNumber;
		}
		else
		{
			newestComicNumber = 0;
			currentNumber = 0;
		}
		view.loadUrl(generator.getImageURL());
		titleBar.setText(generator.getTitle());
		TableRow tr = (TableRow) findViewById(R.id.table);
		tr.setBackgroundColor(Color.WHITE);
		makeButtons();
		inputNumber = (EditText) findViewById(R.id.number);
		if(generator.isNumbered())
		{
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
					String numberString = generator.getNumberedComic(number);
					if(numberString.equals("Comic does not exist"))
					{
						toast(numberString);
					}
					else
					{
						currentURL = numberString;
						changeCurrentURL();
					}
					return true;
				}
			});
		}
		else
		{
			inputNumber.setVisibility(TextView.GONE);
		}
		test = (TextView) findViewById(R.id.textWidget);
	}

	private void makeButtons()
	{     
		final Button random = (Button) findViewById(R.id.random);
		if(generator.isNumbered())
		{
			random.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					currentURL = generator.randomComic();
					currentNumber = generator.currentComicNumber();
					changeCurrentURL();
				}
			});
		}
		else
		{
			random.setVisibility(Button.GONE);
		}

		final Button next = (Button) findViewById(R.id.forward);
		next.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				currentURL = generator.nextComic();
				currentNumber += 1;
				changeCurrentURL();
			}
		});

		final Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				currentURL = generator.previousComic();
				currentNumber -= 1;
				changeCurrentURL();
			}
		});

		final Button first = (Button) findViewById(R.id.first);
		first.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				currentURL = generator.firstComic();
				currentNumber = 1;
				changeCurrentURL();
			}
		});
		final Button last = (Button) findViewById(R.id.last);
		last.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				currentURL = generator.getComicSite();
				currentNumber = newestComicNumber;
				changeCurrentURL();
			}
		});
	}

	private void changeCurrentURL()
	{
		generator = new XKCDGenerator(currentURL);
		view.loadUrl(generator.getImageURL());
		titleBar.setText(generator.getTitle());
		if(generator.isNumbered())
		{
			inputNumber.setText("" + currentNumber);
		}
	}

	private void toast(String message)
	{
		Toast.makeText(this, message, 0).show();
	}
}
