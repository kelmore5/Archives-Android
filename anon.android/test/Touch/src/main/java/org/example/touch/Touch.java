package org.example.touch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Touch extends Activity implements OnTouchListener
{
	ImageView view;
	TextView textWidget;
	//Matrices used to move and zoom the image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	
	Display display;
	
	ImageURLGenerator currentPictureURL;
	String currentURL;
	int currentNumber;
	
	//Can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	
	PointF start;
	float oldDist;
	float newDist;
	
	Bitmap currentImage;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        currentURL = "http://xkcd.com/1022/";
        view = (ImageView) findViewById(R.id.imageView);
        view.setOnTouchListener(this);
    	textWidget = (TextView) findViewById(R.id.textWidget);
    	display = getWindowManager().getDefaultDisplay();
    	currentPictureURL = new ImageURLGenerator(currentURL);
    	currentImage = currentPictureURL.getComic();
    	currentNumber = currentPictureURL.currentXKCDComic();
        
    	reset();
    	
        final Button reset = (Button) findViewById(R.id.selfDestruct);
        reset.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v)
        	{
        		reset();
        	}
        });
        
        final Button random = (Button) findViewById(R.id.random);
        random.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        	        Random r= new Random();
					currentNumber = r.nextInt(1021) + 1;
					changeCurrentURL();
        	}
        });
        
        final Button next = (Button) findViewById(R.id.forward);
        next.setOnClickListener(new View.OnClickListener() 
        {
			public void onClick(View v) 
			{
				if(currentNumber > 1021)
				{
					return;
				}
				else
				{
					currentNumber += 1;
					changeCurrentURL();
				}
			}
		});
        
        final Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() 
        {
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				if(currentNumber < 2)
				{
					return;
				}
				else
				{
					currentNumber -= 1;
					changeCurrentURL();
				}
			}
		});
        
    	start = null;
    	oldDist = 1;
    	newDist = 1;
    }
    
    public boolean onTouch(View v, MotionEvent event)
    {	
    	if(oldDist == 1)
    	{
    		oldDist = spacing(event);
    	}
    	if(start == null)
    	{
    	start = new PointF(event.getX(), event.getY());
    	}
    	
    	//Handle touch events:
    	switch(event.getAction() & MotionEvent.ACTION_MASK)
    	{
    	case MotionEvent.ACTION_DOWN:
    		savedMatrix.set(matrix);
    		//start.set(image.getWidth(), image.getHeight());
    		mode = DRAG;
    		break;
    	case MotionEvent.ACTION_UP:
    	case MotionEvent.ACTION_POINTER_UP:
    		mode = NONE;
    		start = null;
    		oldDist = 1;
    		newDist = 1;
    		break;
    	case MotionEvent.ACTION_POINTER_DOWN:
    		if(oldDist > 100f)
    		{
    			savedMatrix.set(matrix);
    			mode = ZOOM;
    		}
    		break;
    	case MotionEvent.ACTION_MOVE:
    		if(mode == DRAG)
    		{
    			matrix.set(savedMatrix);
    			matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
    		}
    		else if (mode == ZOOM)
    		{
    			newDist = spacing(event);
    			if(newDist > 10f)
    			{
    				matrix.set(savedMatrix);
    				float scale = newDist/oldDist;
    				matrix.postScale(scale, scale);
    			}
    		}
    		break;
    	}
    	
    	//Perform the transformation
    	view.setImageMatrix(matrix);
    	
    	return true; //indicate event was handled
    }
    
    private float spacing(MotionEvent event)
    {
    	float x = event.getX(0) - event.getX(1);
    	float y = event.getY(0) - event.getY(1);
    	return (float) Math.sqrt(x*x + y*y);
    }
    
    private class ImageURLGenerator
    {
        String currentHTML;
    	URL url = null;
    	ImageURLGenerator(String siteURL)
    	{
    		currentURL = siteURL;
            try 
            {
                 url = new URL(siteURL);
            } 
            catch (MalformedURLException e) 
            {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
            }
            try 
            {
				URL url = new URL(siteURL);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setRequestProperty("User-Agent", "");
				connection.setRequestMethod("POST");
				connection.setDoInput(true);
				connection.connect();

				InputStream in = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                 StringBuilder str = new StringBuilder();
                 String line = null;
                 while((line = reader.readLine()) != null)
                 {
              	   str.append(line);
                 }
                 in.close();
                 currentHTML = str.toString();
            } 
            catch (IOException e) 
            {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
            }
    	}
    	
    	private Bitmap getComic()
    	{
    		URL imageURL = null;
    		Bitmap image = null;
    		
            try
            {
            	imageURL = new URL(getXKCDImageURL());
            }
            catch(IOException e)
            {
            	
            }
            
    		try
    		{  			
    			HttpURLConnection conn= (HttpURLConnection) imageURL.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                
                image = BitmapFactory.decodeStream(is);
                image = Bitmap.createScaledBitmap(image, display.getWidth(), (int) (((float)display.getWidth()/(float)image.getWidth())*image.getHeight()) , true);
    		}
            catch (IOException e) 
            {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
            }
    		return image;
    	}
    	
    	private int currentXKCDComic()
    	{
			String numberString = currentURL.substring(16);
			numberString = numberString.substring(0, numberString.indexOf("/"));
			return Integer.parseInt(numberString);
    	}
    	
    	private String getXKCDImageURL()
    	{
    		String newImageURLString = currentHTML.substring(currentHTML.indexOf("Image URL"));
            newImageURLString = newImageURLString.substring((newImageURLString.indexOf("Image URL")+38), newImageURLString.indexOf("</h3>"));
            return newImageURLString;
    	}
    	private String getHTML()
    	{
    		return currentHTML;
    	}
    }
    
    private void changeCurrentURL()
    {
		currentURL = currentURL.substring(0, 16) + currentNumber + "/";
		currentPictureURL = new ImageURLGenerator(currentURL);
		currentImage = currentPictureURL.getComic();
		reset();
    }
    
    private void reset()
    {
		matrix.reset();
		matrix.postTranslate((display.getWidth() - currentImage.getWidth())/2, 0);
		savedMatrix.reset();
		view.setImageBitmap(currentImage);
		view.setImageMatrix(matrix);
		textWidget.setText("" + currentNumber);
    }
}