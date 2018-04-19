package org.liberty.android.fantastischmemo.ui;

import org.junit.Test;
import org.junit.Before;

import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.BuildConfig;
import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.ui.WorkoutSchedulingScreen;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static org.junit.Assert.*;

import static org.junit.Assert.assertTrue;

import org.robolectric.RobolectricGradleTestRunner;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml",packageName = "org.liberty.android.fantastischmemo")
public class WorkoutReschedulingScreenTest {

    WorkoutSchedulingScreen activity;
    TextInputLayout numDaysInputWrapper;
    TextInputLayout numCardsInputWrapper;
    String numDaysInput;
    String numCardsInput;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(WorkoutSchedulingScreen.class)
                .create()
                .get();
        numDaysInputWrapper = (TextInputLayout) activity.findViewById(R.id
       .num_days_input_wrapper);
        numDaysInputWrapper.getEditText().setText("123");
        numCardsInputWrapper = (TextInputLayout) activity.findViewById(R.id
                .num_cards_input_wrapper);
        numDaysInput = numDaysInputWrapper.getEditText().getText()
                .toString();
       /* secondNumber = (EditText)activity.findViewById(R.id.txtNumber2);
        addResult = (TextView)activity.findViewById(R.id.txtResult);
        btnAdd = (Button) activity.findViewById(R.id.btnAdd);*/
    }

    @Test
    public void testMainActivityAddition() {
      /*  //setup

        firstNumber.setText("12.2");
        secondNumber.setText("13.3");

        //test
        btnAdd.performClick();

        //verify
        assertEquals(25.5, Double.parseDouble(addResult.getText().toString()), 0.0);*/

    }
}