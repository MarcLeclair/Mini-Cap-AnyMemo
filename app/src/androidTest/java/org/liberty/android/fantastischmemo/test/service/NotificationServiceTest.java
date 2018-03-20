package org.liberty.android.fantastischmemo.test.service;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.test.espresso.core.deps.guava.io.Resources;
import android.support.test.filters.SmallTest;
import android.test.mock.MockContext;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Trigger;

import org.junit.Assert;
import org.junit.Test;
import org.liberty.android.fantastischmemo.service.NotificationService;
import org.liberty.android.fantastischmemo.test.AbstractExistingDBTest;
import org.liberty.android.fantastischmemo.ui.OpenActionsFragment;
import org.liberty.android.fantastischmemo.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Created by Wei on 3/19/2018.
 */

public class NotificationServiceTest extends AbstractExistingDBTest {
    private OpenActionsFragment mockOpenActionsFragment;
    private DateUtil mockDateUtil;
    private FirebaseJobDispatcher mockDispatcher;
    private FirebaseJobDispatcher dispatcher;
    private GooglePlayDriver mockGooglePlayDriver;
    @Mock
    private Context mockApplicationContext;
    private Date date;
    private Bundle bundle;
    private int numDays;
    @Mock
    private SharedPreferences mockSharedPreferences;
    @Mock
    private Resources mockContextResources;
    MockContext mockContext;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockOpenActionsFragment = mock(OpenActionsFragment.class);
        mockDateUtil = mock(DateUtil.class);
        MockContext context = mock(MockContext.class);
       // context = mockOpenActionsFragment.getContext();
//        mockGooglePlayDriver = mock(GooglePlayDriver.class);
//        mockDispatcher = mock(FirebaseJobDispatcher.class);
       // MockitoAnnotations.initMocks(this);
       dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));;


        date= new Date();
        bundle = new Bundle();
        numDays =2;
//        when(mockApplicationContext.getResources()).thenReturn(mockContextResources);
//        when(mockApplicationContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
//
//        when(mockContextResources.getString(anyInt())).thenReturn("mocked string");
//        when(mockContextResources.getStringArray(anyInt())).thenReturn(new String[]{"mocked string 1", "mocked string 2"});
//        when(mockContextResources.getColor(anyInt())).thenReturn(Color.BLACK);
//        when(mockContextResources.getBoolean(anyInt())).thenReturn(false);
//        when(mockContextResources.getDimension(anyInt())).thenReturn(100f);
//        when(mockContextResources.getIntArray(anyInt())).thenReturn(new int[]{1,2,3});
    }
    @SmallTest
    @Test
    public void testSendNotification(){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString =formatter.format(date );

        int days = DateUtil.getDateDifference(date );
        int duration=Math.abs(days*24);

        final int periodicity = (int) TimeUnit.HOURS.toSeconds(duration);
        final int toleranceInterval = (int) TimeUnit.HOURS.toSeconds(1);


        bundle.putString("startdate", dateString);
        bundle.putString("numDays", Integer.toString(numDays));



        int result= dispatcher.schedule(dispatcher.newJobBuilder()
                .setService(NotificationService.class)
                .setTag("First day of your work out")
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                .setReplaceCurrent(true)
                .setRecurring(false)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .setExtras(bundle)
                .build()
        );
        Assert.assertEquals(result, 1);
    }
}
