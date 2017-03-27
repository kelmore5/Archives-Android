package tools.framework.iosnavbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/**
 * Created by kyle on 3/28/16.
 */
public class Navbar extends RadioGroup {
    //Possible solution for custom colors?
    //xml array of colors
    Drawable backgroundColor;
    Drawable selectedColor;
    int overlayAlpha;

    final int top_bottom_padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    final int left_right_padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());

    //Each radio button needs:
    //Icon, Text, Id, Fragment
    public Navbar(Context context) {
        super(context);
    }

    public Navbar(Context context, AttributeSet attrs) {
        super(context);

        this.setOrientation(HORIZONTAL);

        //Need to get background color for RadioGroup
        //Have to change on individual icons instead of whole bar
        TypedArray args2 = context.obtainStyledAttributes(attrs, new int[] {android.R.attr.background});
        this.backgroundColor = args2.getDrawable(0);

        if(this.backgroundColor == null) {
            this.backgroundColor = new ColorDrawable();
        }

        //Get custom args for navbar
        TypedArray args = context.obtainStyledAttributes(attrs, R.styleable.Navbar);
        int icons = args.getResourceId(R.styleable.Navbar_icons, 0);
        ColorStateList selectedColor = args.getColorStateList(R.styleable.Navbar_selectedColor);
        this.overlayAlpha = args.getInt(R.styleable.Navbar_overlayAlpha, 66);

        if(selectedColor == null) {
            selectedColor = new ColorStateList(new int[][] {new int [] {android.R.attr.colorBackground}}, new int[] {Color.BLACK});
        }

        this.selectedColor = new ColorDrawable(selectedColor.getDefaultColor());

        if(icons != 0) {
            createIcons(context, getResources().obtainTypedArray(icons));

            ((RadioButton) this.getChildAt(0)).setChecked(true);
        }
    }


    private void createIcons(Context context, TypedArray icons) {
        for(int i = 0; i < icons.length(); i++) {
            //The icon
            InsetDrawable icon = new InsetDrawable(icons.getDrawable(i),
                    this.left_right_padding, this.top_bottom_padding,
                    this.left_right_padding, this.top_bottom_padding);

            //Icon with dimmed overlay (for when not selected)
            Drawable iconLowAlpha = new InsetDrawable(icons.getDrawable(i),
                    this.left_right_padding, this.top_bottom_padding,
                    this.left_right_padding, this.top_bottom_padding).mutate();
            iconLowAlpha.setAlpha(this.overlayAlpha);

            StateListDrawable selector = createButtonBackground(icon, iconLowAlpha, this.backgroundColor, this.selectedColor);

            //Create the button and add to RadioGroup
            RadioButton button = new RadioButton(context);
            button.setLayoutParams(new RadioGroup.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
            button.setGravity(Gravity.CENTER_HORIZONTAL);
            button.setButtonDrawable(null);
            button.setBackground(selector);

            this.addView(button);
        }
    }

    public static StateListDrawable createButtonBackground(Drawable icon_selected,
                                                            Drawable icon_not_selected,
                                                            Drawable background,
                                                            Drawable selected_background) {

        //Combines icons and backgrounds
        LayerDrawable backgroundLayer = new LayerDrawable(new Drawable[] {background, icon_not_selected});
        LayerDrawable selectedLayer = new LayerDrawable(new Drawable[] {selected_background, icon_selected});

        //Basically a selector
        StateListDrawable selector = new StateListDrawable();
        selector.addState(new int[]{android.R.attr.state_checked}, selectedLayer);
        selector.addState(new int[]{}, backgroundLayer);

        return selector;
    }
}
