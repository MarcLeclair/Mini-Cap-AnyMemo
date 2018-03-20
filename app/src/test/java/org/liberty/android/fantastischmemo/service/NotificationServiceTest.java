package org.liberty.android.fantastischmemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Trigger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.ui.OpenActionsFragment;
import org.liberty.android.fantastischmemo.utils.DateUtil;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RuntimeEnvironment;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by Wei on 3/20/2018.
 */
@RunWith(RobolectricTestRunner.class)
//@PowerMockIgnore({"*org.mockito"})
@PrepareForTest({NotificationService.class})
public class NotificationServiceTest {
Context mockContext;
Intent mockIntent;
NotificationService notificationService;
NotificationManager mockNotificationManager;
FirebaseJobDispatcher mockDispatcher;
@Mock
private Context mockApplicationContext;
@Mock
private SharedPreferences mockSharedPreferences;
@Mock
private Resources mockContextResources;
@Before
    public void SetUp(){
    notificationService = new NotificationService();
    mockNotificationManager = PowerMockito.mock(NotificationManager.class);
    mockContext = PowerMockito.mock(Context.class);
    mockIntent = new Intent(RuntimeEnvironment.application, OpenActionsFragment.class);
    // context = mockOpenActionsFragment.getContext();
//        mockGooglePlayDriver = mock(GooglePlayDriver.class);
//        mockDispatcher = mock(FirebaseJobDispatcher.class);
        MockitoAnnotations.initMocks(this);
        when(mockApplicationContext.getResources()).thenReturn(mockContextResources);
        when(mockApplicationContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);

        when(mockContextResources.getString(anyInt())).thenReturn("mocked string");
        when(mockContextResources.getStringArray(anyInt())).thenReturn(new String[]{"mocked string 1", "mocked string 2"});
        when(mockContextResources.getColor(anyInt())).thenReturn(Color.BLACK);
        when(mockContextResources.getBoolean(anyInt())).thenReturn(false);
        when(mockContextResources.getDimension(anyInt())).thenReturn(100f);
        when(mockContextResources.getIntArray(anyInt())).thenReturn(new int[]{1,2,3});
}
    @Test
    public void testSendNotification(){

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String dateString =formatter.format(date );
//
//        int days = DateUtil.getDateDifference(date );
//        int duration=Math.abs(days*24);
//
//        final int periodicity = (int) TimeUnit.HOURS.toSeconds(duration);
//        final int toleranceInterval = (int) TimeUnit.HOURS.toSeconds(1);
//
//
//        bundle.putString("startdate", dateString);
//        bundle.putString("numDays", Integer.toString(numDays));


        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mockApplicationContext));
        int result= dispatcher.schedule(dispatcher.newJobBuilder()
                .setService(NotificationService.class)
                .setTag("First day of your work out")
            //    .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                .setReplaceCurrent(true)
                .setRecurring(false)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
              //  .setExtras(bundle)
                .build()
        );
        Assert.assertEquals(result, 1);
    }
}
