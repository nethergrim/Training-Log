package com.nethergrim.combogymdiary.model;

import android.content.Context;

import com.nethergrim.combogymdiary.R;

/**
 * Created by Andrey Drobyazko on 22.09.2014.
 */
public enum DayOfWeek {
    MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6), SUNDAY(7);
    private int code;

    DayOfWeek(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static DayOfWeek getByCode(int code){
        switch (code){
            case 1:
                return MONDAY;
            case 2:
                return TUESDAY;
            case 3:
                return WEDNESDAY;
            case 4:
                return THURSDAY;
            case 5:
                return FRIDAY;
            case 6:
                return SATURDAY;
            default:
                return SUNDAY;
        }
    }

    public String getName(Context ctx) {
        switch (code) {
            case 1:
                return ctx.getResources().getString(R.string.monday);
            case 2:
                return ctx.getResources().getString(R.string.tuesday);
            case 3:
                return ctx.getResources().getString(R.string.wednesday);
            case 4:
                return ctx.getResources().getString(R.string.thursday);
            case 5:
                return ctx.getResources().getString(R.string.friday);
            case 6:
                return ctx.getResources().getString(R.string.saturday);
            default:
                return ctx.getResources().getString(R.string.sunday);
        }
    }
}
