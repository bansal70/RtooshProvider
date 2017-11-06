package com.rtoosh.provider.model.POJO.register;

/*
 * Created by rishav on 11/6/2017.
 */

import com.google.gson.annotations.SerializedName;

public class OpeningTime {
    @SerializedName("open")
    private String from;
    @SerializedName("close")
    private String to;

    public OpeningTime(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
