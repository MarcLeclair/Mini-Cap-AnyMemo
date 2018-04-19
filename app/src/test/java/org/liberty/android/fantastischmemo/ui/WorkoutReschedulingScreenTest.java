package org.liberty.android.fantastischmemo.ui;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.BuildConfig;
import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.ui.WorkoutSchedulingScreen;
import org.liberty.android.fantastischmemo.utils.DateUtil;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static org.junit.Assert.*;

import static org.junit.Assert.assertTrue;

import org.robolectric.RobolectricGradleTestRunner;

import java.text.ParseException;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml", packageName = "org.liberty.android.fantastischmemo")
public class WorkoutReschedulingScreenTest {

    WorkoutSchedulingScreen activity;
    TextInputLayout numDaysInputWrapper;
    TextInputLayout numCardsInputWrapper;
    TextView startDateMessage;
    String numDaysInput;
    String numCardsInput;
    int max = 20;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(WorkoutSchedulingScreen.class)
                .create()
                .get();
        //this is a wrapper on the number of days input
        numDaysInputWrapper = (TextInputLayout) activity.findViewById(R.id
                .num_days_input_wrapper);
        //this is a wrapper on the number of cards input
        numCardsInputWrapper = (TextInputLayout) activity.findViewById(R.id
                .num_cards_input_wrapper);
        startDateMessage = (TextView) activity.findViewById(R.id.start_date_message);
    }

    @After
    public void tearDown(){
        activity = null;
        numDaysInputWrapper = null;
        numCardsInputWrapper = null;
        startDateMessage = null;
    }

    @LargeTest
    @Test
    public void testSetEmptyDate() {
        startDateMessage.setText("");
        Assert.assertFalse(activity.validDate(startDateMessage));
    }

    @LargeTest
    @Test
    public void testSetMessageDate() {
        startDateMessage.setText("Must choose a start date");
        Assert.assertFalse(activity.validDate(startDateMessage));
    }

    @LargeTest
    @Test
    public void testSetCorrectDate() {
        //the calendar handles the user's date input, therefore the format will always be dd/mm/yyyy
        startDateMessage.setText("12/04/2070");
        Assert.assertTrue(activity.validDate(startDateMessage));
    }

    @LargeTest
    @Test
    public void testSetCorrectFarDate() {
        //the calendar handles the user's date input, therefore the format will always be dd/mm/yyyy
        startDateMessage.setText("12/04/2000000000");
        Assert.assertTrue(activity.validDate(startDateMessage));
    }

    @LargeTest
    @Test
    public void testAddNotificationScheduler() {
        String TAG = getClass().getSimpleName();
        try {
            Assert.assertTrue(activity.addNotificationScheduler(DateUtil.getDate(12, 4, 2015), 4, activity));
        } catch (ParseException e) {
            Log.e(TAG, "testAddNotificationScheduler: ");
        }
    }

    @LargeTest
    @Test
    public void testEqualToZeroNumberOfDays() {
        numDaysInputWrapper.getEditText().setText("0");
        numCardsInputWrapper.getEditText().setText("0");
        Assert.assertFalse(activity.validNumCardsOrDays(numCardsInputWrapper, max));
        Assert.assertFalse(activity.validNumCardsOrDays(numDaysInputWrapper, max));
    }

    @LargeTest
    @Test
    public void testEqualToManyZerosNumberOfDays() {
        numDaysInputWrapper.getEditText().setText("000000000000000000000000000000000000000");
        numCardsInputWrapper.getEditText().setText("0000000000000000000000000000000");
        Assert.assertFalse(activity.validNumCardsOrDays(numCardsInputWrapper, max));
        Assert.assertFalse(activity.validNumCardsOrDays(numDaysInputWrapper, max));
    }

    @LargeTest
    @Test
    public void testLessThanMaxNumberOfDays() {
        numDaysInputWrapper.getEditText().setText("19");
        numCardsInputWrapper.getEditText().setText("19");
        Assert.assertTrue(activity.validNumCardsOrDays(numCardsInputWrapper, max));
        Assert.assertTrue(activity.validNumCardsOrDays(numDaysInputWrapper, max));
    }

    @LargeTest
    @Test
    public void testMoreThanMaxNumberOfDays() {
        numDaysInputWrapper.getEditText().setText("21");
        numCardsInputWrapper.getEditText().setText("21");
        Assert.assertFalse(activity.validNumCardsOrDays(numDaysInputWrapper, max));
        Assert.assertFalse(activity.validNumCardsOrDays(numCardsInputWrapper, max));
    }

    @LargeTest
    @Test
    public void testEmptyNumberOfDays() {
        numDaysInputWrapper.getEditText().setText("");
        numCardsInputWrapper.getEditText().setText("");
        Assert.assertFalse(activity.validNumCardsOrDays(numDaysInputWrapper, max));
        Assert.assertFalse(activity.validNumCardsOrDays(numCardsInputWrapper, max));
    }

    @LargeTest
    @Test
    public void testEqualToMaxNumberOfDays() {
        numDaysInputWrapper.getEditText().setText("20");
        numCardsInputWrapper.getEditText().setText("20");
        Assert.assertTrue(activity.validNumCardsOrDays(numDaysInputWrapper, max));
        Assert.assertTrue(activity.validNumCardsOrDays(numCardsInputWrapper, max));
    }

    @LargeTest
    @Test
    public void testStartingWithZerosAndEqualToMaxNumberOfDays() {
        numDaysInputWrapper.getEditText().setText("00000000000000000020");
        numCardsInputWrapper.getEditText().setText("000000000000000000000000020");
        Assert.assertTrue(activity.validNumCardsOrDays(numDaysInputWrapper, max));
        Assert.assertTrue(activity.validNumCardsOrDays(numCardsInputWrapper, max));
    }

    @LargeTest
    @Test
    public void testStartingWithZerosAndLessToMaxNumberOfDays() {
        numDaysInputWrapper.getEditText().setText("00000000000000000019");
        numCardsInputWrapper.getEditText().setText("000000000000000000000000019");
        Assert.assertTrue(activity.validNumCardsOrDays(numDaysInputWrapper, max));
        Assert.assertTrue(activity.validNumCardsOrDays(numCardsInputWrapper, max));
    }

    @LargeTest
    @Test
    public void testStartingWithZerosAndMoreThanMaxNumberOfDays() {
        numDaysInputWrapper.getEditText().setText("00000000000000000000000000000021");
        numCardsInputWrapper.getEditText().setText("00000000000000000000000000000021");
        Assert.assertFalse(activity.validNumCardsOrDays(numDaysInputWrapper, max));
        Assert.assertFalse(activity.validNumCardsOrDays(numCardsInputWrapper, max));
    }

}