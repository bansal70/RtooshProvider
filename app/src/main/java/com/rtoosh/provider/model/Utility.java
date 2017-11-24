package com.rtoosh.provider.model;

/*
 * Created by rishav on 11/21/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.custom.Utils;

public class Utility {

    public static boolean isOnline(Context mContext) {
        String userStatus = RPPreferences.readString(mContext, Constants.USER_STATUS_KEY);

        return userStatus.equals(Constants.STATUS_ONLINE);
    }

    public static boolean onVacationMode(Context mContext) {
        String vacation = RPPreferences.readString(mContext, Constants.VACATION_MODE_KEY);
        return vacation.equals(Constants.VACATION_MODE_ON);
    }

    public static boolean workOnline(Context mContext) {
        String work = RPPreferences.readString(mContext, Constants.WORK_ONLINE_KEY);
        return work.equals(Constants.WORK_TYPE_ONLINE);
    }

    public static boolean checkStatus(Context mContext) {
        String vacation = RPPreferences.readString(mContext, Constants.VACATION_MODE_KEY);
        String workOnline = RPPreferences.readString(mContext, Constants.WORK_ONLINE_KEY);
        String workSchedule = RPPreferences.readString(mContext, Constants.WORK_SCHEDULE_KEY);

        if (vacation.equals(Constants.VACATION_MODE_OFF)) {
            if (workOnline.equals(Constants.WORK_TYPE_ONLINE)) {
                return true;
            } else {
                Utils.showToast(mContext, mContext.getString(R.string.change_work_type));
                return false;
            }
        } else {
            Utils.showToast(mContext, mContext.getString(R.string.change_vacation_mode));
            return false;
        }
    }
}
