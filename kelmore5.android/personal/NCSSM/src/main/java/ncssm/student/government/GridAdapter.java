package ncssm.student.government;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private Drawable unpressed;
    private String[] buttonText;
    private int height;

    public GridAdapter(Context c, int h, Drawable _unpressed, String[] text) {
    	height = h;
        mContext = c;
        unpressed = _unpressed;
        buttonText = text;
    }

    public int getCount() {
        return buttonText.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) 
        {  // if it's not recycled, initialize some attributes
        	textView = new TextView(mContext);
            textView.setBackgroundDrawable(unpressed);
            textView.setHeight((int)((height-16-(8*(Math.round(buttonText.length/2.0)+4)))/(Math.round(buttonText.length/2.0)+1)));
            textView.setText(buttonText[position]);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
        } 
        else 
        {
        	textView = (TextView) convertView;
        }

        return textView;
    }
}