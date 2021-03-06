package org.liberty.android.fantastischmemo.utils;

import android.test.suitebuilder.annotation.SmallTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.liberty.android.fantastischmemo.utils.DateUtil.getDateDifference;

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

    @SmallTest
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

    @SmallTest
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

    @Test
    public void getDateDifferenceTest(){
        int delay1 = getDateDifference(new Date());
        int delay2 = getDateDifference(new Date());
        Assert.assertEquals(delay1,delay2);
    }

    @Test
    public void getCalenderDaysTest(){

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        assertEquals(date, DateUtil.getCalenderDays(date));
    }
}
