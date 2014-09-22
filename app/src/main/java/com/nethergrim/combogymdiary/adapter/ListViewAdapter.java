package com.nethergrim.combogymdiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nethergrim.combogymdiary.row.Row;
import com.nhaarman.listviewanimations.util.Insertable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey Drobyazko on 22.09.2014.
 */
public class ListViewAdapter extends BaseAdapter implements Insertable {

    private List<Row> rows;
    private LayoutInflater inflater;

    public ListViewAdapter(Context context) {
        this.rows = new ArrayList<Row>();
        this.inflater = LayoutInflater.from(context);
    }

    public void addRow(Row row) {
        add(rows.size(), row);
        notifyDataSetChanged();
    }

    public void removeRow(int i) {
        rows.remove(i);
        notifyDataSetChanged();
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
        return rows.get(i).getView(view, inflater);
    }

    @Override
    public void add(int i, Object o) {
        if (o instanceof Row) {
            rows.add(i, (Row) o);
        }
    }
}
