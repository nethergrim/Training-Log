package com.nethergrim.combogymdiary.model;

import com.nethergrim.combogymdiary.tools.BaseDbObjectInterface;

import java.io.Serializable;


public class TrainingProgram extends BaseDbObject {

    private String name;
    private Boolean isPaid;

    public interface Columns extends BaseDbObjectInterface {
        public static final String TABLE = "training_program";

        public static final String FIELD_NAME = "name";
        public static final String FIELD_PAID = "is_paid";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }
}
