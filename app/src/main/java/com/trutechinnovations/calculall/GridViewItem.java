package com.trutechinnovations.calculall;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * A square item on the GridView.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class GridViewItem extends ImageView {

    public GridViewItem(Context context) {
        super(context);
    }

    public GridViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }
}