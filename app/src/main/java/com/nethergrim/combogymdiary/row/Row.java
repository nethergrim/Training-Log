package com.nethergrim.combogymdiary.row;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Andrey Drobyazko on 22.09.2014.
 */
public interface Row {

    public RowType getType();

    public View getView(View convertView, LayoutInflater inflater);

    public long getId();
}
