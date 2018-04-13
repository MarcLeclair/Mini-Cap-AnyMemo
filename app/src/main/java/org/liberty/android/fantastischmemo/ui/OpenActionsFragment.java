/*
Copyright (C) 2012 Haowen Ning

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package org.liberty.android.fantastischmemo.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.AMPrefKeys;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelperManager;
import org.liberty.android.fantastischmemo.common.BaseActivity;
import org.liberty.android.fantastischmemo.common.BaseDialogFragment;
import org.liberty.android.fantastischmemo.utils.AMFileUtil;
import org.liberty.android.fantastischmemo.utils.AMPrefUtil;
import org.liberty.android.fantastischmemo.utils.DatabaseUtil;
import org.liberty.android.fantastischmemo.utils.DateUtil;
import org.liberty.android.fantastischmemo.utils.RecentListUtil;
import org.liberty.android.fantastischmemo.utils.ShareUtil;
import org.liberty.android.fantastischmemo.utils.WorkOutListUtil;
import org.liberty.android.fantastischmemo.utils.WorkoutDialogUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


public class OpenActionsFragment extends BaseDialogFragment {

    @Inject
    DatabaseUtil databaseUtil;

    public static String EXTRA_DBPATH = "dbpath";
    static String TAG = OpenActionsFragment.class.getSimpleName();
    private BaseActivity mActivity;

    private String dbPath;

    private View studyItem;
    private View workoutModeItem;
    private View editItem;
    private View listItem;
    private View quizItem;
    private View cardPlayerItem;
    private View settingsItem;
    private View statisticsItem;
    private View shareItem;
    private View deleteItem;
    Context context;
    private RadioButton numDaysRadioButton;
    private RadioButton numCardsRadioButton;
    private Map<CompoundButton, View> radioButtonSettingsMapping;
    @Inject
    AMFileUtil amFileUtil;

    @Inject
    RecentListUtil recentListUtil;

    @Inject
    ShareUtil shareUtil;

    @Inject
    AMPrefUtil amPrefUtil;


    @Inject
    WorkOutListUtil workoutListUtil;


    AnyMemoDBOpenHelper helper;
    WorkoutDialogUtil workoutDialogBoxUtil;


    public OpenActionsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;

    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        fragmentComponents().inject(this);
        Bundle args = this.getArguments();
        dbPath = args.getString(EXTRA_DBPATH);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        workoutDialogBoxUtil = new WorkoutDialogUtil();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        View v = inflater.inflate(R.layout.open_actions_layout, container, false);
        studyItem = v.findViewById(R.id.study);
        studyItem.setOnClickListener(buttonClickListener);

        workoutModeItem = v.findViewById(R.id.study_mode);
        workoutModeItem.setOnClickListener(buttonClickListener);

        editItem = v.findViewById(R.id.edit);
        editItem.setOnClickListener(buttonClickListener);

        listItem = v.findViewById(R.id.list);
        listItem.setOnClickListener(buttonClickListener);

        quizItem = v.findViewById(R.id.quiz);
        quizItem.setOnClickListener(buttonClickListener);

        settingsItem = v.findViewById(R.id.settings);
        settingsItem.setOnClickListener(buttonClickListener);

        deleteItem = v.findViewById(R.id.delete);
        deleteItem.setOnClickListener(buttonClickListener);

        statisticsItem = v.findViewById(R.id.statistics);
        statisticsItem.setOnClickListener(buttonClickListener);

        shareItem = v.findViewById(R.id.share);
        shareItem.setOnClickListener(buttonClickListener);

        cardPlayerItem = v.findViewById(R.id.card_player);
        cardPlayerItem.setOnClickListener(buttonClickListener);

        return v;
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == studyItem) {
                Intent myIntent = new Intent();
                myIntent.setClass(mActivity, StudyActivity.class);
                myIntent.putExtra(StudyActivity.EXTRA_DBPATH, dbPath);
                startActivity(myIntent);
                recentListUtil.addToRecentList(dbPath);
            }


            if (v == workoutModeItem) {
                int maxNumCards = AnyMemoDBOpenHelperManager.getHelper(mActivity, dbPath)
                        .getCardDao().getAllCards(null).size();
                //if the deck is empty don't pop up the dialogue box for workout mode
                if (maxNumCards == 0) {
                    Toast.makeText(mActivity, "You must have at least 1 card in your deck to add" +
                            " this deck to workout mode!", Toast.LENGTH_LONG).show();
                    return;
                }
                final Dialog dialog = new Dialog(mActivity);


                dialog.setContentView(R.layout.workout);
                dialog.show();

                final TextView startDateMessage = (TextView) dialog.findViewById(R.id.start_date_message);

                final Button startDateButton = (Button) dialog.findViewById(R.id.start_date_button);
                startDateButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        PickStartDateFragment pickStartDateFragment = new PickStartDateFragment();
                        pickStartDateFragment.setText(startDateMessage);
                        pickStartDateFragment.show(mActivity.getSupportFragmentManager(), "DatePicker");

                    }
                });

                Button positiveButton = (Button) dialog.findViewById(R.id.button_ok);
                Button negativeButton = (Button) dialog.findViewById(R.id.button_cancel);


                numDaysRadioButton = (RadioButton) dialog.findViewById(R.id.num_days_button);
                numDaysRadioButton.setOnCheckedChangeListener(onCheckedChangeListener);
                numCardsRadioButton = (RadioButton) dialog.findViewById(R.id.Num_cards_button);
                numCardsRadioButton.setOnCheckedChangeListener(onCheckedChangeListener);

                final TextInputLayout numDaysInputWrapper = (TextInputLayout) dialog.findViewById
                        (R.id.num_days_input_wrapper);
                final TextInputLayout numCardsInputWrapper = (TextInputLayout) dialog.findViewById
                        (R.id.num_cards_input_wrapper);
                final CheckBox notificationCheckbox = (CheckBox) dialog.findViewById(R.id.notification);

                radioButtonSettingsMapping = new HashMap<CompoundButton, View>(2);
                radioButtonSettingsMapping.put(numDaysRadioButton, dialog.findViewById(R.id
                        .num_days_settings));
                radioButtonSettingsMapping.put(numCardsRadioButton, dialog.findViewById(R.id
                        .num_cards_settings));

                // if button is clicked, set the new workout dates for each cards within the deck
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        final String numDaysInput = numDaysInputWrapper.getEditText().getText()
                                .toString();
                        final String numCardsInput = numCardsInputWrapper.getEditText().getText()
                                .toString();
                        int numDays;
                        int numCards;
                        int maxNumCards = AnyMemoDBOpenHelperManager.getHelper(mActivity, dbPath)
                                .getCardDao().getAllCards(null).size();
                        String dateAsString = startDateMessage.getText().toString();
                        try {
                            if (numDaysRadioButton.isChecked() || numCardsRadioButton.isChecked()) {
                                if (dateAsString.equals("")) {
                                    numDaysInputWrapper.setErrorEnabled(true);
                                    numDaysInputWrapper.setError("Must chose a start date ");
                                } else {
                                    numDaysInputWrapper.setErrorEnabled(false);

                                    String[] dateAsArray;
                                    Log.d(TAG, "Date as string is " + dateAsString);
                                    dateAsArray = dateAsString.split("/");
                                    Date startDate = DateUtil.getDate(
                                            Integer.parseInt(dateAsArray[0]),
                                            Integer.parseInt(dateAsArray[1]),
                                            Integer.parseInt(dateAsArray[2]));
//check something
                                    if (!numDaysInput.equals("")) {
                                        numDays = Integer.parseInt(numDaysInput);
                                    } else {
                                        numDays = 0;
                                    }
                                    //check other things
                                    if (numDays > maxNumCards || numDays == 0 || numDaysInput.equals("")) {
                                        numDaysInputWrapper.setErrorEnabled(true);
                                        numDaysInputWrapper.setError("Must enter a number between 1 and " +
                                                maxNumCards);
                                    } else {
                                        numDaysInputWrapper.setErrorEnabled(false);

                                        //retrieving the date as a string, and putting it an an array
                                        // converting that array to a date stored in the variable startDate
                                        workoutDialogBoxUtil.setWorkoutModeDates(AnyMemoDBOpenHelperManager.getHelper
                                                (mActivity, dbPath), numDays, startDate);
                                        dialog.dismiss();
                                        Toast.makeText(mActivity, "Successfully added deck to " +
                                                "workout" +
                                                " " +
                                                "mode!", Toast
                                                .LENGTH_LONG).show();
                                        //schedule notification
                                        if (notificationCheckbox.isChecked()) {
                                            workoutDialogBoxUtil.addNotificationScheduler(startDate, numDays, mActivity);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Workout mode throws an exception ", e);
                        }
                    }
                });

                negativeButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                dialog.show();

                workoutListUtil.addToRecentList(dbPath);
            }

            if (v == editItem) {
                Intent myIntent = new Intent();
                myIntent.setClass(mActivity, PreviewEditActivity.class);
                myIntent.putExtra(PreviewEditActivity.EXTRA_DBPATH, dbPath);
                int startId = amPrefUtil.getSavedInt(AMPrefKeys.PREVIEW_EDIT_START_ID_PREFIX, dbPath, 1);
                myIntent.putExtra(PreviewEditActivity.EXTRA_CARD_ID, startId);
                startActivity(myIntent);
                recentListUtil.addToRecentList(dbPath);
            }

            if (v == listItem) {
                Intent myIntent = new Intent();
                myIntent.setClass(mActivity, CardListActivity.class);
                myIntent.putExtra(StudyActivity.EXTRA_DBPATH, dbPath);
                startActivity(myIntent);
                recentListUtil.addToRecentList(dbPath);
            }

            if (v == quizItem) {
                QuizLauncherDialogFragment df = new QuizLauncherDialogFragment();
                Bundle b = new Bundle();
                b.putString(CategoryEditorFragment.EXTRA_DBPATH, dbPath);
                df.setArguments(b);
                df.show(mActivity.getSupportFragmentManager(), "QuizLauncherDialog");
                recentListUtil.addToRecentList(dbPath);
            }

            if (v == settingsItem) {
                Intent myIntent = new Intent();
                myIntent.setClass(mActivity, SettingsScreen.class);
                myIntent.putExtra(SettingsScreen.EXTRA_DBPATH, dbPath);
                startActivity(myIntent);
            }

            if (v == statisticsItem) {
                Intent myIntent = new Intent();
                myIntent.setClass(mActivity, StatisticsScreen.class);
                myIntent.putExtra(SettingsScreen.EXTRA_DBPATH, dbPath);
                startActivity(myIntent);
            }

            if (v == shareItem) {
                shareUtil.shareDb(dbPath);
            }

            if (v == deleteItem) {
                new AlertDialog.Builder(mActivity)
                        .setTitle(getString(R.string.delete_text))
                        .setMessage(getString(R.string.fb_delete_message))
                        .setPositiveButton(getString(R.string.delete_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                amFileUtil.deleteDbSafe(dbPath);
                                recentListUtil.deleteFromRecentList(dbPath);
                            /* Refresh the list */
                                mActivity.restartActivity();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel_text), null)
                        .create()
                        .show();
            }

            if (v == cardPlayerItem) {
                Intent myIntent = new Intent();
                myIntent.setClass(mActivity, CardPlayerActivity.class);
                myIntent.putExtra(CardPlayerActivity.EXTRA_DBPATH, dbPath);
                startActivity(myIntent);
            }

            dismiss();
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener
            = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            View settingsView = radioButtonSettingsMapping.get(buttonView);
            if (isChecked) {
                settingsView.setVisibility(View.VISIBLE);
            } else {
                settingsView.setVisibility(View.GONE);
            }
        }
    };
}
