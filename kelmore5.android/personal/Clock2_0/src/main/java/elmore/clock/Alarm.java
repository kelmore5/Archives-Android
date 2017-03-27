package elmore.clock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Alarm extends Activity
{
	AlarmManager alarmManager;
	
	public void onCreate(Bundle savedInstanceBundle)
	{
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		/*Intent intent = new Intent(this, OnetimeAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 1000), sender);
		Toast.makeText(this, "Alarm set", Toast.LENGTH_LONG).show();*/
	}
}
