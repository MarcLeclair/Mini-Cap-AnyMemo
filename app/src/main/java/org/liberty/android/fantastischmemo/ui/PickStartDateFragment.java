package org.liberty.android.fantastischmemo.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;

/*
This code is developed with the help of the following source:
https://stackoverflow.com/questions/27225815/android-how-to-show-datepicker-in-fragment
 */

public class PickStartDateFragment extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView text;
    String returnedString;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, yy, mm, dd);
        //user can only choose a date from today forwards, never a past date for the workout mode
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePickerDialog;
    }

    public void setText(TextView text){
        this.text = text;
    }

    public static Date getDate(int year, int month, int day) {
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

    public void setReturnedString(String input){
        returnedString = input;
    }

    public void onDateSet(DatePicker view, int yyyy, int mm, int dd) {
        populateSetDate(yyyy, mm+1, dd);
    }

    public void populateSetDate(int year, int month, int day) {
        setReturnedString(day+"/"+month+"/"+year);
        text.setText(returnedString);
    }
}