package com.hello.gridview;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import java.util.ArrayList;


public class HelloGridViewActivity extends Activity 
{
	private ArrayList<Integer> sounds = new ArrayList<Integer>();
	private SoundPool soundPool;
	boolean loaded = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
        
    this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    
    GridView gridview = (GridView) findViewById(R.id.gridview);
    gridview.setAdapter(new ImageAdapter(this));

	soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
	soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
		public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
		{
			loaded = true;
		}
	});
	
	sounds.add(soundPool.load(this, R.raw.work_it, 1));
	sounds.add(soundPool.load(this, R.raw.make_it, 1));
	sounds.add(soundPool.load(this, R.raw.do_it, 1));
	sounds.add(soundPool.load(this, R.raw.makes_us, 1));
	sounds.add(soundPool.load(this, R.raw.harder, 1));
	sounds.add(soundPool.load(this, R.raw.better, 1));
	sounds.add(soundPool.load(this, R.raw.faster, 1));
	sounds.add(soundPool.load(this, R.raw.stronger, 1));
	sounds.add(soundPool.load(this, R.raw.more_than, 1));
	sounds.add(soundPool.load(this, R.raw.hour, 1));
	sounds.add(soundPool.load(this, R.raw.our, 1));
	sounds.add(soundPool.load(this, R.raw.never, 1));
	sounds.add(soundPool.load(this, R.raw.ever, 1));
	sounds.add(soundPool.load(this, R.raw.after, 1));
	sounds.add(soundPool.load(this, R.raw.work_is, 1));
	sounds.add(soundPool.load(this, R.raw.over, 1));
    
    gridview.setOnItemClickListener(new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            //Toast.makeText(HelloGridViewActivity.this, "" + (position + 1), Toast.LENGTH_SHORT).show();
        	
        	AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
			// Is the sound loaded already?
			if (loaded) 
			{
				soundPool.play(sounds.get(position), 
						((float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))/((float)audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 
						((float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))/((float)audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 
						1, 0, 1f);
			}
        }
    });
    }
    }
    