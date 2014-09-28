package com.nethergrim.combogymdiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.row.ExpandableRow;
import com.nethergrim.combogymdiary.row.Row;
import com.nhaarman.listviewanimations.util.Insertable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey Drobyazko on 22.09.2014.
 */
public class ListViewAdapter extends BaseAdapter {

    protected List<Row> rows;
    protected LayoutInflater inflater;
    protected Context ctx;
    protected int lastPosition = -1;

    public ListViewAdapter(Context context) {
        this.rows = new ArrayList<Row>();
        this.inflater = LayoutInflater.from(context);
        this.ctx = context;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void addRows(List<Row> rows1){
        this.rows.addAll(rows1);
    }

    public void addRow(Row row) {
        rows.add(row);
    }

    public void removeRow(int i) {
        rows.remove(i);
        notifyDataSetChanged();
    }

    public void clearAdapter(){
        for (int i = rows.size() - 1; i >= 0; i--){
            rows.remove(i);
        }
    }

    public void switchItems(int i1, int i2) {
        Row row = rows.get(i1);
        rows.set(i1, rows.get(i2));
        rows.set(i2, row);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Row getItem(int i) {
        return rows.get(i);
    }

    @Override
    public long getItemId(int i) {
        return rows.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = rows.get(i).getView(view, inflater);
        Animation animation = AnimationUtils.loadAnimation(ctx, (i > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        v.startAnimation(animation);
        lastPosition = i;
        return v;
    }

}
