package org.liberty.android.fantastischmemo.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by melid on 2018-03-16.
 */

//code source : https://stackoverflow.com/questions/428918/how-can-i-increment-a-date-by-one-day-in-java
public class DateUtil
{
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); // use -days to get a previous day
        return cal.getTime();
    }

    public static Date getDate(int day, int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
