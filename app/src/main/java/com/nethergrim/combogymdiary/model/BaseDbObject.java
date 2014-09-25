package com.nethergrim.combogymdiary.model;

/**
 * Created by Andrey Drobyazko on 25.09.2014.
 */
public class BaseDbObject {

    private Long id;
    private Long createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
