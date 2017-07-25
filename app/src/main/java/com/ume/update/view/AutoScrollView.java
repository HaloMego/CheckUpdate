package com.ume.update.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ScrollView;


public class AutoScrollView extends ScrollView {
    private Context mContext;
    private DisplayMetrics mDisplayMetrics;

    public AutoScrollView(Context context) {
        super(context);
        init(context);
    }

    public AutoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public AutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        mDisplayMetrics = new DisplayMetrics();
        display.getMetrics(mDisplayMetrics);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mDisplayMetrics.heightPixels / 6, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
