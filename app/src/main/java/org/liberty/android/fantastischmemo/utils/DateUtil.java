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
}
