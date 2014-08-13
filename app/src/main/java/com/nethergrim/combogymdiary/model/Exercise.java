package com.nethergrim.combogymdiary.model;


import java.io.Serializable;

public class Exercise implements Serializable {

    private long id;
    private String name;
    private String timer;
    private String partOfBody;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Exercise() {
    }

    public Exercise(long id, String name, String timer, String partOfBody) {
        this.id = id;
        this.name = name;
        this.timer = timer;
        this.partOfBody = partOfBody;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
