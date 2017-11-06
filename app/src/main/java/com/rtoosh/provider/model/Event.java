package com.rtoosh.provider.model;

/*
 * Created by rishav on 9/12/2017.
 */

public class Event {
    private int key;
    private String value;

    public Event(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
