package com.rtoosh.provider.model.custom;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by rishav on 10/31/2017.
 */

public class DateUtils {

    public static Date toLocalTime(String utcDate, SimpleDateFormat sdf) throws Exception {

        // create a new Date object using
        // the timezone of the specified city
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date localDate = sdf.parse(utcDate);

        sdf.setTimeZone(TimeZone.getDefault());
        String dateFormateInUTC = sdf.format(localDate);

        return sdf.parse(dateFormateInUTC);
    }

    public static String[] getAllDays() {
        String[] days = new String[7];
        days[0] = "Monday";
        days[1] = "Tuesday";
        days[2] = "Wednesday";
        days[3] = "Thursday";
        days[4] = "Friday";
        days[5] = "Saturday";
        days[6] = "Sunday";
        return days;
    }

    private String getMonthName(final int index, final Locale locale, final boolean shortName)
    {
        String format = "%tB";

        if (shortName)
            format = "%tb";

        Calendar calendar = Calendar.getInstance(locale);
        calendar.set(Calendar.MONTH, index);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return String.format(locale, format, calendar);
    }

    public static String getDate(String dateTime) {

        StringTokenizer tk = new StringTokenizer(dateTime);
        String date = tk.nextToken();
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm", Locale.getDefault());
        Date dt = null;
        try {
            dt = sdf.parse(date);
            System.out.println("Time Display: " + sdfs.format(dt));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sdf.format(dt);
    }

    public static String getDateFormat(String dateTime) {
        // Get date from string
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormatter.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());

        return dateFormat.format(date);
    }

    public static String getTimeFormat(String dateTime) {
        // Get date from string
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormatter.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

// Get time from date
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormatter.format(date);
    }

    public static void getTimeDifference(String serverTime, String orderTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date1 = null, date2 = null;
        try {
            date1 = simpleDateFormat.parse(orderTime);
            date2 = simpleDateFormat.parse(serverTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date1 != null && date2 != null) {
            long difference = date2.getTime() - date1.getTime();
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            hours = (hours < 0 ? -hours : hours);
            Timber.e("======= min"+" :: " + min + "\n=== hour" + hours + "\n== days" + days);
        }
    }

    public static String twoDatesBetweenTime(String orderTime, String serverTime) {
        int day = 0;
        int hh = 0;
        int mm = 0;
        int sec = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date oldDate = dateFormat.parse(orderTime);
            Date cDate = dateFormat.parse(serverTime);
            Long timeDiff = cDate.getTime() - oldDate.getTime();
            day = (int) TimeUnit.MILLISECONDS.toDays(timeDiff);
            hh = (int) (TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(day));
            mm = (int) (TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
            sec = (int) (TimeUnit.MILLISECONDS.toSeconds(timeDiff) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff)) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return day + ":" + hh + ":" + mm + ":" + sec;
        /*if (day > 0) {
            return day + ":" + hh + ":" + mm + ":" + sec;
        }
        else if (day == 0) {
            return hh + ":" +mm + ":" + sec;
        } else if (hh == 0) {
            return String.valueOf(mm) + ":" + sec;
        } else {
            return day + ":" + hh + ":" + mm;
        }*/
    }

    public static String getTimeout(String timeDiff) {
        int min = 0, sec = 0;
        if (timeDiff.contains(":")) {
            String[] splitCurrentTime = timeDiff.split(":");
            min = Integer.parseInt(splitCurrentTime[2]);
            sec = Integer.parseInt(splitCurrentTime[3]);

            if (min < 10) {
                min = 10 - min - 1;
            }
            sec = 60 - sec;
        }
        return String.valueOf(min) + ":" + sec;
    }

    public static String getTimeoutSchedule(String timeDiff) {
        int day = 0, hour = 0, min = 0, sec = 0;
        if (timeDiff.contains(":")) {
            String[] splitCurrentTime = timeDiff.split(":");
            day = Integer.parseInt(splitCurrentTime[0]);
            hour = Integer.parseInt(splitCurrentTime[1]);
            min = Integer.parseInt(splitCurrentTime[2]);
            sec = Integer.parseInt(splitCurrentTime[3]);

            if (min < 10) {
                min = 10 - min - 1;
            }
            sec = 60 - sec;
        }
        return day + ":" + hour + ":" + min + ":" + sec;
    }

    public static String printDifference(String orderTime, String serverTime) {
        String time = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date1 = null, date2 = null;

        try {
            date1 = simpleDateFormat.parse(serverTime);
            date2 = simpleDateFormat.parse(orderTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date1 != null && date2 != null) {
            //milliseconds
            long different = date2.getTime() - date1.getTime();

            System.out.println("startDate : " + orderTime);
            System.out.println("endDate : " + serverTime);
            System.out.println("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            time = elapsedDays + ":" + elapsedHours + ":" + elapsedMinutes + ":" +  elapsedSeconds;
            System.out.printf(
                    "%d :, %d :, %d :, %d :",
                    elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        }

        return time;
    }

    /**
     * Returns abbreviated (3 letters) day of the week.
     *
     * @param date
     *            ISO format date
     * @return The name of the day of the week
     */
    public static String getDayOfWeekAbbreviated(String date) {
        Date dateDT = parseDate(date);

        if (dateDT == null) {
            return null;
        }

        // Get current date
        Calendar c = Calendar.getInstance();
        // it is very important to
        // set the date of
        // the calendar.
        c.setTime(dateDT);
        int day = c.get(Calendar.DAY_OF_WEEK);

        String dayStr = null;

        switch (day) {

            case Calendar.SUNDAY:
                dayStr = "Sun";
                break;

            case Calendar.MONDAY:
                dayStr = "Mon";
                break;

            case Calendar.TUESDAY:
                dayStr = "Tue";
                break;

            case Calendar.WEDNESDAY:
                dayStr = "Wed";
                break;

            case Calendar.THURSDAY:
                dayStr = "Thu";
                break;

            case Calendar.FRIDAY:
                dayStr = "Fri";
                break;

            case Calendar.SATURDAY:
                dayStr = "Sat";
                break;
        }

        return dayStr;
    }

    /**
     * Gets the name of the day of the week.
     *
     * @param date ISO format date
     * @return The name of the day of the week
     * **
     */
    public static String getDayOfWeek(String date) {
        // TODO: move to DateUtils
        Date dateDT = parseDate(date);

        if (dateDT == null) {
            return null;
        }

        // Get current date
        Calendar c = Calendar.getInstance();
        // it is very important to
        // set the date of
        // the calendar.
        c.setTime(dateDT);
        int day = c.get(Calendar.DAY_OF_WEEK);

        String dayStr = null;

        switch (day) {

            case Calendar.SUNDAY:
                dayStr = "Sunday";
                break;

            case Calendar.MONDAY:
                dayStr = "Monday";
                break;

            case Calendar.TUESDAY:
                dayStr = "Tuesday";
                break;

            case Calendar.WEDNESDAY:
                dayStr = "Wednesday";
                break;

            case Calendar.THURSDAY:
                dayStr = "Thursday";
                break;

            case Calendar.FRIDAY:
                dayStr = "Friday";
                break;

            case Calendar.SATURDAY:
                dayStr = "Saturday";
                break;
        }

        return dayStr;
    }

    public static Date parseDate(String date) {
        StringBuilder sbDate = new StringBuilder();
        sbDate.append(date);
        String newDate = null;
        Date dateDT = null;

        try {
            newDate = sbDate.substring(0, 19);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String rDate = newDate.replace("T", " ");
        String nDate = rDate.replaceAll("-", "/");

        try {
            dateDT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(nDate);
            // Log.v( TAG, "#parseDate dateDT: " + dateDT );
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateDT;
    }


    /***
     * Gets the name of the month from the given date.
     *
     * @param date
     *            ISO format date
     * @return Returns the name of the month
     * ***/
    public static String getMonth(String date) {
        Date dateDT = parseDate(date);

        if (dateDT == null) {
            return null;
        }

        // Get current date
        Calendar c = Calendar.getInstance();
        // it is very important to
        // set the date of
        // the calendar.
        c.setTime(dateDT);
        int day = c.get(Calendar.MONTH);

        String dayStr = null;

        switch (day) {

            case Calendar.JANUARY:
                dayStr = "January";
                break;

            case Calendar.FEBRUARY:
                dayStr = "February";
                break;

            case Calendar.MARCH:
                dayStr = "March";
                break;

            case Calendar.APRIL:
                dayStr = "April";
                break;

            case Calendar.MAY:
                dayStr = "May";
                break;

            case Calendar.JUNE:
                dayStr = "June";
                break;

            case Calendar.JULY:
                dayStr = "July";
                break;

            case Calendar.AUGUST:
                dayStr = "August";
                break;

            case Calendar.SEPTEMBER:
                dayStr = "September";
                break;

            case Calendar.OCTOBER:
                dayStr = "October";
                break;

            case Calendar.NOVEMBER:
                dayStr = "November";
                break;

            case Calendar.DECEMBER:
                dayStr = "December";
                break;
        }

        return dayStr;
    }


    /**
     * Gets abbreviated name of the month from the given date.
     *
     * @param date
     *            ISO format date
     * @return Returns the name of the month
     */
    public static String getMonthAbbreviated(String date) {
        Date dateDT = parseDate(date);

        if (dateDT == null) {
            return null;
        }

        // Get current date
        Calendar c = Calendar.getInstance();
        // it is very important to
        // set the date of
        // the calendar.
        c.setTime(dateDT);
        int day = c.get(Calendar.MONTH);

        String dayStr = null;

        switch (day) {

            case Calendar.JANUARY:
                dayStr = "Jan";
                break;

            case Calendar.FEBRUARY:
                dayStr = "Feb";
                break;

            case Calendar.MARCH:
                dayStr = "Mar";
                break;

            case Calendar.APRIL:
                dayStr = "Apr";
                break;

            case Calendar.MAY:
                dayStr = "May";
                break;

            case Calendar.JUNE:
                dayStr = "Jun";
                break;

            case Calendar.JULY:
                dayStr = "Jul";
                break;

            case Calendar.AUGUST:
                dayStr = "Aug";
                break;

            case Calendar.SEPTEMBER:
                dayStr = "Sep";
                break;

            case Calendar.OCTOBER:
                dayStr = "Oct";
                break;

            case Calendar.NOVEMBER:
                dayStr = "Nov";
                break;

            case Calendar.DECEMBER:
                dayStr = "Dec";
                break;
        }

        return dayStr;
    }

    public static int getDayFromTimestamp(final long timestamp) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getYear(@Nullable final Date date, @NonNull final Locale locale) {
        if (date == null)
            return 0;

        final Calendar cal = Calendar.getInstance(locale);
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getDay(@Nullable final Date date, @NonNull final Locale locale) {
        if (date == null)
            return 0;

        final Calendar cal = Calendar.getInstance(locale);
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth(@Nullable final Date date, @NonNull final Locale locale) {
        if (date == null)
            return 0;

        final Calendar cal = Calendar.getInstance(locale);
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }
}
