package com.rtoosh.provider.views;

import android.os.CountDownTimer;
import android.widget.TextView;

/*
 * Created by rishav on 11/21/2017.
 */

public class MyCountDownTimer extends CountDownTimer {

    private int countSec, countMin, countDay, countHour;
    private TextView tvTime;

    public MyCountDownTimer(long millisInFuture, long countDownInterval, int countDay, int countHour,
                            int countMin, int countSec, TextView tvTime) {
        super(millisInFuture, countDownInterval);
        this.countSec = countSec;
        this.countMin = countMin;
        this.countHour = countHour;
        this.countDay = countDay;
        this.tvTime = tvTime;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        String sec = String.valueOf(--countSec);

        if (countSec < 10)
            sec = "0" + sec;
        tvTime.setText(String.format("%s:%s:%s:%s", String.valueOf(countDay), String.valueOf(countHour),
                String.valueOf(countMin), sec));

        if (countSec < 1) {
            countSec = 60;

            if (countMin > 0)
                countMin--;

            if (countMin < 1 & countHour > 0)
                countHour--;

            if (countHour < 1 && countDay > 0)
                countDay--;

            tvTime.setText(String.format("%s:%s:%s:%s", String.valueOf(countDay), String.valueOf(countHour),
                    String.valueOf(countMin), sec));

        }
        //int progress = (int) (millisUntilFinished/1000);
    }

    @Override
    public void onFinish() {

    }
}
