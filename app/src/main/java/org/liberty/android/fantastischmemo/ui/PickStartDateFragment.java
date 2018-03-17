package org.liberty.android.fantastischmemo.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
This code is developed with the help of the following source:
https://stackoverflow.com/questions/27225815/android-how-to-show-datepicker-in-fragment
 */

public class PickStartDateFragment extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView text;
    int year;
    int month;
    int day;
    String returnedString;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void setText(TextView text){
        this.text = text;
    }

//    public Date convertStringToDate(){
//        SimpleDateFormat date= new SimpleDateFormat("dd-MM-yyyy");
//        Date date = date.parse(returnedString);
//    }

    public void setYear(int year){
        this.year = year;
    }

    public void setMonth(int month){
        this.month = month;
    }

    public void setDay(int day){
        this.day = day;
    }

    public void setReturnedString(String input){
        returnedString = input;
    }


    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
        setYear(yy);
        setMonth(mm);
        setDay(dd);
        }
    public void populateSetDate(int year, int month, int day) {
        setReturnedString(month+"/"+day+"/"+year);
        text.setText(returnedString);
    }

}