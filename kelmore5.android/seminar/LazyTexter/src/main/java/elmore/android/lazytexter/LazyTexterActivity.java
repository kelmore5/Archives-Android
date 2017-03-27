package elmore.android.lazytexter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class LazyTexterActivity extends Activity implements OnClickListener, OnItemSelectedListener 
{
	EditText phoneField, bodyField;
	Button submitButton;
	String greeting;
	
	CheckBox useSignature;
	RadioButton slogan1, slogan2;
	
	Spinner chooseGreeting;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        greeting = "";
        
        phoneField = (EditText) findViewById(R.id.phoneField);
        bodyField = (EditText) findViewById(R.id.textField);
        
        submitButton = (Button) findViewById(R.id.sendText);
        submitButton.setOnClickListener(this);
        
        useSignature = (CheckBox) findViewById(R.id.checkBox1);
        slogan1 = (RadioButton) findViewById(R.id.radioButton2);
        slogan2 = (RadioButton) findViewById(R.id.radioButton1);
        
        chooseGreeting = (Spinner) findViewById(R.id.chooseSalutation);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
			R.array.greetings, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseGreeting.setAdapter(adapter);
    }
    
	@Override
	public void onClick(View v) 
	{
		String phoneNumber = phoneField.getText().toString();
		String myText = greeting + "\n";
		if(slogan1.isChecked())
		{
			myText += "What is up?\n";
		}
		else
		{
			myText += "Yo dawg.";
		}
		if(useSignature.isChecked())
		{
			myText += "I hate signatures";
		}
		myText += bodyField.getText().toString();
		if(phoneNumber.length() > 0)
		{
			Toast.makeText(this, myText, Toast.LENGTH_LONG).show();
			sendSMS(phoneNumber, myText);
		}
	}

	private void sendSMS(String phoneNumber, String text) 
	{
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, LazyTexterActivity.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, text, pi, null);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) 
	{
		greeting = parent.getItemAtPosition(pos).toString();
		Toast.makeText(parent.getContext(), greeting, Toast.LENGTH_LONG);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
}