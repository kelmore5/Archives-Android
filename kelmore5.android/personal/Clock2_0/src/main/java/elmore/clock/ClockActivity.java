package elmore.clock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ClockActivity extends Activity 
{
	private TextView time, date, battery;
	private ImageView batteryImage;
	private Timer timer;
	private int batteryLevel, batteryIcon, batteryStatus;
	private Drawable[] drawables;
	private ImageView[] imageViews;
	private AudioManager audio;

	private final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss a");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private final SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

	private final Integer[] icons = { R.drawable.icon_light_on, R.drawable.icon_sound_on, R.drawable.icon_alarm, R.drawable.icon_music, R.drawable.icon_light_off, R.drawable.icon_sound_off  };

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		imageViews = new ImageView[4];
		drawables = new Drawable[6];

		time = (TextView) findViewById(R.id.Time);
		date = (TextView) findViewById(R.id.Date);
		battery = (TextView) findViewById(R.id.Battery);
		batteryImage = (ImageView) findViewById(R.id.batteryIcon);

		imageViews[0] = (ImageView) findViewById(R.id.lightBulb);
		imageViews[1] = (ImageView) findViewById(R.id.volume);
		imageViews[2] = (ImageView) findViewById(R.id.alarmClock);
		imageViews[3] = (ImageView) findViewById(R.id.music);

		audio = (AudioManager) this.getSystemService(AUDIO_SERVICE);

		createIcons();

		batteryLevel = -1;
		batteryIcon = -1;
		batteryStatus = -1;

		updateDate();

		BroadcastReceiver batteryReceiver = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

				if(batteryStatus != 0)
				{
					batteryImage.setVisibility(ImageView.VISIBLE);
					batteryIcon = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1);
					batteryImage.setImageResource(batteryIcon);
				}
				else
				{
					batteryImage.setVisibility(ImageView.INVISIBLE);
				}

				batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				battery.setText("" + batteryLevel);
			}
		};
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryReceiver, filter);

		// SMS RECEIVER
		final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
		BroadcastReceiver SMSbr = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				if(intent.getAction().equals(ACTION))
				{
					Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
					SmsMessage[] msgs = null;
					String msg_from;
					if (bundle != null){
						//---retrieve the SMS message received---
						try{
							Object[] pdus = (Object[]) bundle.get("pdus");
							msgs = new SmsMessage[pdus.length];
							for(int i=0; i<msgs.length; i++)
							{
								msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
								msg_from = msgs[i].getOriginatingAddress();
								String msgBody = msgs[i].getMessageBody();
								Toast.makeText(ClockActivity.this, msgBody, Toast.LENGTH_LONG);
							}
						}catch(Exception e){
							//		                            Log.d("Exception caught",e.getMessage());
						}
					}
				}

			}
		};
		// The BroadcastReceiver needs to be registered before use.
		IntentFilter SMSfilter = new IntentFilter(ACTION);
		this.registerReceiver(SMSbr, SMSfilter);
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
		timer = new Timer("DigitalClock");
		Calendar calendar = Calendar.getInstance();

		// Get the Current Time
		final Runnable updateTask = new Runnable() 
		{
			public void run() 
			{
				String currentTime = getCurrentTimeString();
				if(currentTime.equals("12:00:01 AM") || currentTime.equals("12:00:00 AM"))
				{
					updateDate();
				}
				time.setText(currentTime); //shows the current time of the day
				//	                countdown.setText(getReminingTime()); // shows the remaining time of the day
			}
		};

		int msec = 999 - calendar.get(Calendar.MILLISECOND);
		// update the UI
		timer.scheduleAtFixedRate(new TimerTask() 
		{
			@Override
			public void run() 
			{
				runOnUiThread(updateTask);
			}
		}, msec, 1001);
	}

	@Override
	protected void onStop() 
	{
		super.onStop();
		timer.cancel();
		timer.purge();
		timer = null;
	}

	public void updateDate()
	{
		Date currentDate = new Date();
		date.setText(dateFormat.format(currentDate) + "\n" + dayFormat.format(currentDate));
	}

	public void createIcons()
	{
		for(int k = 0; k < 6; k++)
		{
			drawables[k] = getResources().getDrawable(icons[k]);
			drawables[k].setColorFilter(Color.rgb(0, 194, 255), Mode.MULTIPLY);
			if(k < 4)
			{
				imageViews[k].setImageDrawable(drawables[k]);
			}
		}

		imageViews[0].setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				WindowManager.LayoutParams layout = getWindow().getAttributes();
				if(imageViews[0].getDrawable().equals(drawables[0]))
				{
					layout.screenBrightness = 0.004f;
					imageViews[0].setImageDrawable(drawables[4]);
					getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
				else
				{
					imageViews[0].setImageDrawable(drawables[0]);
					layout.screenBrightness = -1;
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
				getWindow().setAttributes(layout);
			}
		});

		imageViews[1].setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if(imageViews[1].getDrawable().equals(drawables[1]))
				{
					imageViews[1].setImageDrawable(drawables[5]);
					audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				}
				else
				{
					imageViews[1].setImageDrawable(drawables[1]);
					audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				}
			}
		});

		imageViews[2].setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				Intent intent = new Intent("android.intent.action.MAIN");
				//intent.setComponent(ComponentName.unflattenFromString("com.google.android.maps.mytracks/com.google.android.apps.mytracks.MyTracks"));
				intent.addCategory("android.intent.category.LAUNCHER");
				startActivity(intent);
			}
		});

		imageViews[3].setOnClickListener(new OnClickListener(){
			public void onClick(View v)
			{
				Intent intent = new Intent("android.intent.action.MAIN");
				intent.addCategory("android.intent.category.LAUNCHER");
				startActivity(intent);
			}
		});
	}

	/*private String getReminingTime() 
	{
		Calendar calendar = Calendar.getInstance();
		int hour = 23 - calendar.get(Calendar.HOUR_OF_DAY);
		int minute = 59 - calendar.get(Calendar.MINUTE);
		int second = 59 - calendar.get(Calendar.SECOND);
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}*/

	private String getCurrentTimeString() 
	{
		Date date = new Date();
		return timeFormat.format(date);
	}
}