package com.nethergrim.combogymdiary.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
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

    @Override
    public int getSize() {
        return SIZE_NORMAL;
    }

    @Override
    public void initBackground() {
        final int backgroundId;

        backgroundId = R.drawable.fab_background;


        Drawable background = getResources().getDrawable(backgroundId);

        if (background instanceof LayerDrawable) {
            LayerDrawable layers = (LayerDrawable) background;
            if (layers.getNumberOfLayers() == 2) {
                Drawable shadow = layers.getDrawable(0);
                Drawable circle = layers.getDrawable(1);

                if (shadow instanceof GradientDrawable) {
                    ((GradientDrawable) shadow.mutate()).setGradientRadius(getShadowRadius(shadow, circle));
                }

                if (circle instanceof GradientDrawable) {
                    ((GradientDrawable) circle.mutate()).setColor(getColor());
                }
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(background);
        } else {
            setBackground(background);
        }
    }
}
