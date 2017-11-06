package com.rtoosh.provider.model.POJO.register;

/*
 * Created by win 10 on 11/3/2017.
 */

import com.google.gson.annotations.SerializedName;

public class OpeningHours {
    private String day;
    @SerializedName("time")
    private OpeningTime openingTime;

    public OpeningHours(String day, OpeningTime openingTime) {
        this.day = day;
        this.openingTime = openingTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public OpeningTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(OpeningTime openingTime) {
        this.openingTime = openingTime;
    }
}
