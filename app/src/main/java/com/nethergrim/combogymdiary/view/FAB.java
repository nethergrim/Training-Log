package com.nethergrim.combogymdiary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.nethergrim.combogymdiary.R;

public class FAB extends com.shamanland.fab.FloatingActionButton {

    public FAB(Context context) {
        super(context);
    }

    public FAB(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FAB(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
//            setAlpha(1.0f);
            setColor(getContext().getResources().getColor(R.color.material_cyan_a400));
            initBackground();
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            setAlpha(0.6f);
            setColor(getContext().getResources().getColor(R.color.material_light_green_a400));
            initBackground();
        }
        return super.onTouchEvent(event);
    }
}
