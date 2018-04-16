package org.liberty.android.fantastischmemo.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.toIntExact;

/**
 * Created by melid on 2018-03-16.
 */

public class DateUtil {
//code source : https://stackoverflow.com/questions/428918/how-can-i-increment-a-date-by-one-day-in-java
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); // use -days to get a previous day
        return cal.getTime();
    }

    public static Date getDate(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date today(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getDateDifference(Date date){
        Date now = new Date();
        long diffInMillies = date.getTime() - now.getTime();
        long convertedDifference = TimeUnit.DAYS.convert(diffInMillies,TimeUnit.MILLISECONDS);
        int delayDays = toIntExact(convertedDifference) ;
        return delayDays;
    }

    public static Date getCalenderDays(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
