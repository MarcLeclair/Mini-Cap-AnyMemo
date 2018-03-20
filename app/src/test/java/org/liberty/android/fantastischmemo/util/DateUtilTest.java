package org.liberty.android.fantastischmemo.util;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.service.NotificationService;
import org.liberty.android.fantastischmemo.ui.OpenActionsFragment;
import org.liberty.android.fantastischmemo.utils.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RobolectricTestRunner;
import org.testng.annotations.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.liberty.android.fantastischmemo.utils.DateUtil.getDateDifference;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Wei on 3/19/2018.
 */
//@RunWith(RobolectricTestRunner.class)
@PrepareForTest({DateUtil.class})
public class DateUtilTest {
    int numDays;
    Date date;
    Date date2;
    Date date3;
    Calendar cal;
    DateUtil dt;


    public void setUp() throws Exception {
        numDays = 3;
        cal = Calendar.getInstance();
        date = cal.getTime();
        cal.add(Calendar.DATE, 1);
        date2 = cal.getTime();
        cal.add(Calendar.DATE, 1);
        date3 = cal.getTime();
        dt= new DateUtil();
    }


    @Test
    public void getDateDifferenceTest(){
       int delay1 = getDateDifference(new Date());
       int delay2 = getDateDifference(new Date());
        assertEquals(delay1,delay2);
    }
}

