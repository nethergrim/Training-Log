package com.nethergrim.combogymdiary.model;


import com.nethergrim.combogymdiary.tools.BaseDbObjectInterface;

import java.io.Serializable;

public class Exercise extends BaseDbObject implements Serializable {

    private String name;
    private String timer;
    private String partOfBody;
    private Boolean checked = false;

    public interface Columns extends BaseDbObjectInterface{
        public static final String TABLE = "exe_tab";

        public static final String FIELD_EXERCISE_NAME = "exercise_name";
        public static final String FIELD_TIMER_VALUE = "timer_value";
        public static final String FIELD_PART_OF_BODY = "part_of_body";
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getPartOfBody() {
        return partOfBody;
    }

    public void setPartOfBody(String partOfBody) {
        this.partOfBody = partOfBody;
    }


}
