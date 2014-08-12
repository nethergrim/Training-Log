package com.nethergrim.combogymdiary.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nethergrim.combogymdiary.Constants;

public class TextViewLight extends TextView {

    public TextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.TYPEFACE_LIGHT));
    }

    public TextViewLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.TYPEFACE_LIGHT));
    }

    public TextViewLight(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.TYPEFACE_LIGHT));
    }

}
