package tools.framework.iosnavbar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

/*import learning.clones.bottom_navigator.R;
import learning.clones.simpleinstaclone.fragments.FeedFragment;
import learning.clones.simpleinstaclone.fragments.FollowingFragment;
import learning.clones.simpleinstaclone.fragments.ProfileFragment;
import learning.clones.simpleinstaclone.fragments.SearchFragment;*/

/**
 * This is a framework for an Android application
 * with a bottom navigation drawer (like on iOS)
 * It uses a RadioGroup for the navigation bar and
 * fragments to go between parts of the app
 *
 * @author Kyle Elmore
 * @version 1.0
 * @since 2016-03-26
 */
public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    RadioGroup navbar; //The navigation bar
    Fragment[] fragments; //Different sections of the app

    int overlayAlpha = 66;

    /*final int top_bottom_padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources().getDisplayMetrics());
    final int left_right_padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, this.getResources().getDisplayMetrics());*/


    /**
     * {@inheritDoc}
     *
     * On startup:  Initialize each app fragment and put it in the fragments array
     *              Create navigation bar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Navbar navbar = (Navbar) findViewById(R.id.navbar);

        Drawable iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_camera_alt_white_24dp);

        int top_bottom_padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources().getDisplayMetrics());
        int left_right_padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, this.getResources().getDisplayMetrics());

        //The icon
        InsetDrawable icon = new InsetDrawable(iconDrawable,
                left_right_padding, top_bottom_padding,
                left_right_padding, top_bottom_padding);

        //Icon with dimmed overlay (for when not selected)
        Drawable iconLowAlpha = new InsetDrawable(iconDrawable,
                left_right_padding, top_bottom_padding,
                left_right_padding, top_bottom_padding).mutate();
        iconLowAlpha.setAlpha(this.overlayAlpha);

        StateListDrawable selector = Navbar.createButtonBackground(icon, iconLowAlpha, new ColorDrawable(Color.BLUE),
                new ColorDrawable(Color.BLACK));

        //((RadioButton) navbar.getChildAt(2)).setBackground(selector);

        fragments = new Fragment[] {
               /* new FeedFragment(),
                new SearchFragment(),
                null,
                new FollowingFragment(),
                new ProfileFragment()*/
        };

        //createNavBar();
    }

    /**
     * Adds a listener for each button in the navigation bar
     */
    private void createNavBar() {
        /*navbar = (RadioGroup) findViewById(R.id.navbar);

        RadioButton radioButton;
        radioButton = (RadioButton) findViewById(R.id.btnHome);
        radioButton.setOnCheckedChangeListener(this);
        radioButton.setChecked(true);
        radioButton = (RadioButton) findViewById(R.id.btnSearch);
        radioButton.setOnCheckedChangeListener(this);
        radioButton = (RadioButton) findViewById(R.id.btnGallery);
        radioButton.setOnCheckedChangeListener(this);
        radioButton = (RadioButton) findViewById(R.id.btnFollowing);
        radioButton.setOnCheckedChangeListener(this);
        radioButton = (RadioButton) findViewById(R.id.btnProfile);
        radioButton.setOnCheckedChangeListener(this);*/
    }

    /**
     * {@inheritDoc}
     *
     * Listener for all the radio buttons in the navigation bar
     * When a button is clicked, the correct fragment is shown on the screen
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            int index = navbar.indexOfChild(buttonView);
            if(index != 2) {
              //  getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragments[index]).commit();
                //fragments[index].setArguments(getIntent().getExtras());
            }
        }
    }
}
