/*
Copyright (C) 2013 Haowen Ning

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

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelperManager;
import org.liberty.android.fantastischmemo.common.BaseActivity;
import org.liberty.android.fantastischmemo.common.BaseDialogFragment;
import org.liberty.android.fantastischmemo.dao.CardDao;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.utils.AMPrefUtil;
import org.liberty.android.fantastischmemo.utils.DateUtil;
import org.liberty.android.fantastischmemo.utils.WorkoutDialogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class WorkoutIncompleteLauncherDialogFragment extends BaseDialogFragment {

    public static final String EXTRA_DBPATH = "dbpath";
    private AnyMemoDBOpenHelper dbOpenHelper;
    private CardDao cardDao;
    private String dbPath = null;
    private BaseActivity mActivity;
    private Button startQuizButton;
    private RadioButton yesRadio;
    private RadioButton skipForeverRadio;
    private RadioButton askMeTomorrowRadio;
    private RadioButton rescheduleRadio;
    private TextView dateText;
    private WorkoutDialogUtil workoutDialogUtil;

    private Map<CompoundButton, View> radioButtonSettingsMapping;

    @Inject
    AMPrefUtil amPrefUtil;

    public WorkoutIncompleteLauncherDialogFragment() {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        fragmentComponents().inject(this);
        workoutDialogUtil = new WorkoutDialogUtil();

        Bundle extras = getArguments();
        if (extras != null) {
            dbPath = extras.getString(EXTRA_DBPATH);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AnyMemoDBOpenHelperManager.releaseHelper(dbOpenHelper);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.workout_incomplete_dialog, null, false);

        skipForeverRadio = (RadioButton) v.findViewById(R.id.skip_forever_radio);
        skipForeverRadio.setOnCheckedChangeListener(onCheckedChangeListener);

        askMeTomorrowRadio = (RadioButton) v.findViewById(R.id.ask_me_tomorrow_radio);
        askMeTomorrowRadio.setOnCheckedChangeListener(onCheckedChangeListener);

        rescheduleRadio = (RadioButton) v.findViewById(R.id.reschedule_radio);
        rescheduleRadio.setOnCheckedChangeListener(onCheckedChangeListener);

        dateText = (TextView) v.findViewById(R.id.date);

        startQuizButton = (Button) v.findViewById(R.id.start_quiz_button);
        startQuizButton.setOnClickListener(startQuizButtonOnClickListener);


        radioButtonSettingsMapping = new HashMap<>(4);
        radioButtonSettingsMapping.put(skipForeverRadio, v.findViewById(R.id.skip_forever_radio));
        radioButtonSettingsMapping.put(askMeTomorrowRadio, v.findViewById(R.id.ask_me_tomorrow_radio));
        radioButtonSettingsMapping.put(rescheduleRadio, v.findViewById(R.id.reschedule_radio));

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.incomplete_workout_title)
                .setView(v)
                .create();

    }

    @Override
    public void onStart() {
        super.onStart();
        dbOpenHelper = AnyMemoDBOpenHelperManager.getHelper(mActivity, dbPath);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener
            = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            View settingsView = radioButtonSettingsMapping.get(buttonView);
            if (isChecked) {
                List<Card> cards = cardDao.getAllCards(null);
                List<Card> incompleteCards = new ArrayList<>();

                //put all incomplete cards in an arraylist
                for (Card card : cards) {
                    //if it is a pass date, but not flagged as done, a card that is done is
                    // flagged as 1/1/1
                    if (card.getLearningDate().before(DateUtil.today()) && !card.getLearningDate()
                            .equals(DateUtil.getDate(1, 1, 1))) {
                        incompleteCards.add(card);
                    }
                }
                if (settingsView.getId() == R.id.reschedule_radio) {
                    PickStartDateFragment pickStartDateFragment = new PickStartDateFragment();
                    pickStartDateFragment.show(mActivity.getSupportFragmentManager(), "DatePicker");
                    pickStartDateFragment.setText(dateText);
                    String[] dateAsArray = dateText.getText().toString().split("/");
                    Date date = DateUtil.getDate(
                            Integer.parseInt(dateAsArray[0]),
                            Integer.parseInt(dateAsArray[1]),
                            Integer.parseInt(dateAsArray[2]));
                    workoutDialogUtil.setIncompleteCardsDates(date, incompleteCards);
                    dateText.setVisibility(View.VISIBLE);
                } else if (settingsView.getId() == R.id.skip_forever_radio) {
                    dateText.setVisibility(View.GONE);
                    workoutDialogUtil.setIncompleteCardsDates(DateUtil.getDate(1, 1, 1), incompleteCards);
                } else if (settingsView.getId() == R.id.ask_me_tomorrow_radio) {
                    dateText.setVisibility(View.GONE);
                    Date yesterday = DateUtil.addDays(DateUtil.today(), -1);
                    workoutDialogUtil.setIncompleteCardsDates(yesterday, incompleteCards);
                }
            }
        }
    };

    private View.OnClickListener startQuizButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mActivity, StudyActivity.class);
            intent.putExtra(QuizActivity.EXTRA_DBPATH, dbPath);
            startActivity(intent);
        }
    };
}

