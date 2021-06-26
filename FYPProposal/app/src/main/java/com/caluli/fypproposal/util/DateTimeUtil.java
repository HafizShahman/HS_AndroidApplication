package com.caluli.fypproposal.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {

    public String getCurrentTimeStamp() {
        long tsLong = System.currentTimeMillis();
        final String ts = tsLong.toString();
        return ts;
    }

    public String getDate(String timeStamp, String pattern) {

        Long ts = Long.parseLong(timeStamp);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date netDate = (new Date(ts));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "Invalid Date";
        }
    }

    public String getLastActive(String timeStamp) {

        if (!timeStamp.equals("")) {
            String current_timestamp = getCurrentTimeStamp();

            //convert to long
            long current_ts = Long.parseLong(current_timestamp);
            long passed_ts = Long.parseLong(timeStamp);
            long difference = current_ts - passed_ts;

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = difference / daysInMilli;
            difference = difference % daysInMilli;

            long elapsedHours = difference / hoursInMilli;
            difference = difference % hoursInMilli;


            long elapsedMinutes = difference / minutesInMilli;
            difference = difference % hoursInMilli;

            String positive;

            if (elapsedDays > 0) {
                positive = String.format("%d days", elapsedDays) + "ago";

            } else if (elapsedHours > 0) {
                positive = String.format("%d hours", elapsedHours) + "ago";

            } else if (elapsedMinutes > 0) {
                positive = String.format("%d minutes", elapsedMinutes) + "ago";
            } else {
                positive = " now";
            }

            return positive;
        } else {
            return "-";
        }
    }

    public String getJoinDuration(String timestamp) {

        if (!timestamp.equals("")) {
            //get current user timestamp
            String current_timestamp = getCurrentTimeStamp();

            //convert to long
            long current_ts = Long.parseLong(current_timestamp);
            long passed_ts = Long.parseLong(timestamp);
            long difference = current_ts - passed_ts;

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;
            long yearsInMilli = daysInMilli * 365;

            long elapsedYears = difference / yearsInMilli;
            difference = difference % yearsInMilli;

            long elapsedDays = difference / daysInMilli;
            difference = difference % daysInMilli;

            long elapsedHours = difference / hoursInMilli;
            difference = difference % hoursInMilli;


            long elapsedMinutes = difference / minutesInMilli;
            difference = difference % hoursInMilli;

            String positive;


            if (elapsedYears < 1) {
                positive = "less than year on JustShop";

            } else if (elapsedYears == 1) {
                positive = "One year on JustShop";

            } else {
                positive = String.format("%d years", elapsedYears) + "on JustShop";
            }

            return positive;
        } else {
            return "-";
        }
    }

    public String getDateDifference(String str_date1, String str_date2) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
            Date date1 = sdf.parse(str_date1);
            Date date2 = sdf.parse(str_date2);

            long diff = date1.getTime() - date2.getTime();
            return String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "-";
    }
}





