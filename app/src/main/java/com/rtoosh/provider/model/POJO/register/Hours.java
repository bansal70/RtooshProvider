package com.rtoosh.provider.model.POJO.register;

/*
 * Created by win 10 on 11/3/2017.
 */

import com.google.gson.annotations.SerializedName;

public class Hours {
    @SerializedName("time")
    private Time time;
    private String days;

    public static class Time {
        @SerializedName("open")
        private String open;
        @SerializedName("close")
        private String close;

        public Time(String open, String close) {
            this.open = open;
            this.close = close;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }

        public String getClose() {
            return close;
        }

        public void setClose(String close) {
            this.close = close;
        }
    }

    public Hours(Time time, String days) {
        this.time = time;
        this.days = days;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }
}
