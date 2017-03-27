package elmore.android.magiceightball;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MagicEightBallActivity extends Activity implements OnClickListener 
{
	Button myButton;
	TextView myText;
	String[] fortunes;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        myButton = (Button) findViewById(R.id.button1);
        myButton.setOnClickListener(this);
        
        myText = (TextView) findViewById(R.id.textView1);
        
        fortunes = new String[8];
        fortunes[0] = "Yes";
        fortunes[1] = "No";
        fortunes[2] = "Maybe";
        fortunes[3] = "All signs point to no";
        fortunes[4] = "All signs point to yes";
        fortunes[5] = "Ask again later";
        fortunes[6] = "Why are you asking me?";
        fortunes[7] = "I don't know. Fix your own problems";
    }
    
	@Override
	public void onClick(View v) 
	{
		int i = (int) (Math.random()*8);
		myText.setText(fortunes[i]);		
	}
}