package com.nethergrim.combogymdiary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.nethergrim.combogymdiary.R;

/**
 * Created by Andrey Drobyazko on 28.09.2014.
 */
public class HeaderFooterListVIew extends ListView {


    public HeaderFooterListVIew(Context context) {
        super(context);
        addHeaderAndFooter(context);
    }

    public HeaderFooterListVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
        addHeaderAndFooter(context);
    }

    public HeaderFooterListVIew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addHeaderAndFooter(context);
    }

    public void addHeaderAndFooter(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        addHeaderView(inflater.inflate(R.layout.header_footer_view, null));
        addFooterView(inflater.inflate(R.layout.header_footer_view, null));
    }
}
