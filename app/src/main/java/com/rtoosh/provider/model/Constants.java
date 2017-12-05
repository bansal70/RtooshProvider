package com.rtoosh.provider.model;

/*
 * Created by rishav on 10/26/2017.
 */

public class Constants {

    /* Account Status Keys */
    public static final String ACCOUNT_ACTIVE = "1";
    public static final String ACCOUNT_INACTIVE = "2";
    public static final String ACCOUNT_REVIEWING = "3";
    public static final String ACCOUNT_SUSPENDED = "4";

    /* Preferences Keys */
    public static final String USER_STATUS_KEY = "user_status";
    public static final String VACATION_MODE_KEY = "vacation_mode";
    public static final String WORK_ONLINE_KEY = "work_online";
    public static final String WORK_SCHEDULE_KEY = "work_schedule";
    public static final String SCHEDULE_HOURS_KEY = "job_hours";
    public static final String LANGUAGE_KEY = "lang";
    public static final String USER_ID_KEY = "user_id";
    public static final String EMAIL_KEY = "email";
    public static final String PHONE_KEY = "phone";
    public static final String ID_NUMBER_KEY = "id_number";
    public static final String PROFILE_PIC_KEY = "profile_pic";
    public static final String REGISTERED_KEY = "registered";
    public static final String COUNTRY_CODE_KEY = "country_code";
    public static final String FULL_NAME_KEY = "full_name";
    public static final String ACCOUNT_STATUS_KEY = "active";
    public static final String REQUEST_ID_KEY = "request_id";
    public static final String TERMS_ACCEPTED_KEY = "terms_accepted";

    public static final String LANGUAGE_EN = "en";

    public static final String DEVICE_TYPE = "Android";
    public static final String USER_TYPE = "2";
    public static final String CURRENCY = "SAR";
    public static final String ORDER_ONLINE = "1";
    public static final String ORDER_SCHEDULE = "2";

    public static final String ORDER_PENDING = "1";
    public static final String ORDER_ACCEPTED = "2";
    public static final String ORDER_REJECTED = "3";
    public static final String ORDER_STARTED = "4";
    public static final String ORDER_COMPLETED = "5";
    public static final String ORDER_INITIATED = "6";
    public static final String ORDER_CANCELED = "7";
    public static final String ORDER_REVIEWED = "8";
    public static final String ORDER_TIMEOUT = "9";

    public static final String NOTIFY_ACCOUNT_STATUS = "1";
    public static final String NOTIFY_ORDER = "2";
    public static final String NOTIFY_REMINDER = "3";
    public static final String NOTIFY_SERVICE_STARTED = "4";

    public static final String VACATION_MODE_OFF = "0";
    public static final String VACATION_MODE_ON = "1";

    public static final String STATUS_ONLINE = "1";
    public static final String STATUS_OFFLINE = "0";

    public static final String WORK_TYPE_ONLINE = "1";
    public static final String WORK_TYPE_SCHEDULE = "1";

    public static final String WORK_TYPE_OFFLINE = "0";

    public static final String TIMEOUT_MINUTES = "09";

    public static final long COUNTDOWN_TIME = 5*60*1000;

    public static final int NO_RESPONSE = 1000;

    public static final int OTP_SUCCESS = 1001;
    public static final int OTP_ERROR = 1002;

    public static final int REGISTER_SUCCESS = 1003;
    public static final int REGISTER_ERROR = 1004;

    public static final long FASTEST_TIME_INTERVAL = 10000; // 10 seconds
    public static final long TIME_INTERVAL = 60 * 1000; // 1 minutes
}
