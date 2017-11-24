package com.rtoosh.provider.views;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/*
 * Created by win 10 on 11/23/2017.
 */

public class MinuteTimer extends CountDownTimer {

    int countSec = 60, countMin = 9;
    String sec, min;
    TextView tvTime;

    public MinuteTimer(long millisInFuture, long countDownInterval, int countMin, int countSec, TextView tvTime) {
        super(millisInFuture, countDownInterval);
        this.countMin = countMin;
        this.countSec = countSec;
        this.tvTime = tvTime;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        String days = TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))+"";
        String hours = (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) -
                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)))+"";
        String minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))+"";
        String seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))+"";

        tvTime.setText(String.format("%s:%s:%s:%s", days, hours, minutes, seconds));

       /* days.setText();

        hours.setText();

        mins.setText(();

        seconds.setText(();
        sec = String.valueOf(--countSec);

        if (countSec < 10)
            sec = "0" + sec;
        tvTime.setText(String.format("%s:%s", String.valueOf(countMin), String.valueOf(countSec)));

        if (countSec < 1) {
            countSec = 60;
            if (countMin > 0) {
                min = String.valueOf(countMin--);
                min = "0" + min;
                tvTime.setText(String.format("%s:%s", String.valueOf(countMin), String.valueOf(countSec)));
            }
        }*/
    }

    @Override
    public void onFinish() {

    }
}
