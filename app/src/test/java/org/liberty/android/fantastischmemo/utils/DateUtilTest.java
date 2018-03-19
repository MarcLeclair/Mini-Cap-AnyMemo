package org.liberty.android.fantastischmemo.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by melid on 2018-03-18.
 */

public class DateUtilTest {
    Calendar cal;

    @Before
    public void setUp() throws Exception {
        cal = Calendar.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        cal = null;
    }

    @Test
    public void addDaysTest() {
        //these tests are used to verify that the current date + x days, equals to the
        // corresponding future date.
        //today
        Date date = cal.getTime();
        assertTrue(DateUtil.addDays(date, 0).equals(date));

        //tomorrow
        cal.add(Calendar.DATE, 1);
        Date date2 = cal.getTime();
        assertTrue(DateUtil.addDays(date, 1).equals(date2));

        //yesterday
        cal.add(Calendar.DATE, -2);
        Date date3 = cal.getTime();
        assertTrue(DateUtil.addDays(date, -1).equals(date3));
    }

    @Test
    public void getDateTest() {
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.MONTH, 2);
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        assertEquals(date, DateUtil.getDate(12, 3, 2018));
    }
}
