package org.liberty.android.fantastischmemo.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
    private TextView incompleteCardsText;
    private WorkoutDialogUtil workoutDialogUtil;
    List<Card> incompleteCards;
    final String TAG = getClass().getSimpleName();

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

        incompleteCardsText = (TextView) v.findViewById(R.id.incomplete_cards);

        startQuizButton = (Button) v.findViewById(R.id.start_quiz_button);
        startQuizButton.setOnClickListener(startQuizButtonOnClickListener);

        incompleteCards = new ArrayList<>();

        /* verify each card in the current date and find all incomplete cards from the any past
        date and assign to array incompleteCards */
        for (Card card : AnyMemoDBOpenHelperManager.getHelper(dbPath).getCardDao().getAllCards(null)) {
            if (card.getLearningDate() != null) {
                if (!card.getLearningDate().equals(DateUtil.getDate(1, 1, 1)) &&
                        card.getLearningDate().before(DateUtil.today()) ) {
                    incompleteCards.add(card);
                }
            }
        }
        /*display those incomplete cards questions and answers in the TextView */
        displayCards(incompleteCards, incompleteCardsText);

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
                                     boolean isChecked){
            View settingsView = radioButtonSettingsMapping.get(buttonView);
            if (isChecked) {
                if (settingsView.getId() == R.id.reschedule_radio) {
                    dateText.setVisibility(View.VISIBLE);
                    //allow user to pick a new date from the calendar
                    PickStartDateFragment pickStartDateFragment = new PickStartDateFragment();
                    pickStartDateFragment.show(mActivity.getSupportFragmentManager(), "DatePicker");
                    pickStartDateFragment.setText(dateText);

                } else if (settingsView.getId() == R.id.skip_forever_radio) {
                    dateText.setVisibility(View.GONE);
                    //set card's date to 1/1/1 flagging it as done
                    workoutDialogUtil.setCardsDates(AnyMemoDBOpenHelperManager.getHelper(dbPath), DateUtil.getDate(1, 1, 1), incompleteCards);
                } else if (settingsView.getId() == R.id.ask_me_tomorrow_radio) {
                    dateText.setVisibility(View.GONE);
                    Date yesterday = DateUtil.addDays(DateUtil.today(), -1);
                    //set card's date to yesterday so that the card does not show up in today's
                    // workout but will be asked again tomorrow
                    workoutDialogUtil.setCardsDates(AnyMemoDBOpenHelperManager.getHelper(dbPath), yesterday, incompleteCards);
                }
            }
        }
    };

    private View.OnClickListener startQuizButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (rescheduleRadio.isChecked()) {
                String dateAsString = dateText.getText().toString();
                String[] dateAsArray;
                Log.d(TAG, "In method onClick of startQuiz button. Date as string is " + dateAsString);

                //parses the EditText containing the date, and puts each integer value in an array
                dateAsArray = dateAsString.split("/");
                Date date = DateUtil.getDate(
                        Integer.parseInt(dateAsArray[0]),
                        Integer.parseInt(dateAsArray[1]),
                        Integer.parseInt(dateAsArray[2]));
                //set chosen date for all incomplete cards
                workoutDialogUtil.setCardsDates(AnyMemoDBOpenHelperManager.getHelper(dbPath), date, incompleteCards);
            }
            Intent intent = new Intent(mActivity, StudyActivity.class);
            intent.putExtra(StudyActivity.EXTRA_DBPATH, dbPath);
            startActivity(intent);
        }
    };

    /** Populates the EditText in the dialog box by listing all wanted cards's
     * question and answer
     * @param cards the current set of cards for which we want to display
     * @param view the EditText we want to populate with the question and corresponding answer
     *             for the card set
     * @return the string that is used to populate the EditText
     * **/
    public String displayCards(List<Card> cards, TextView
            view) {

        StringBuilder listOfCardsText = new StringBuilder();

        //put all incomplete cards in an arraylist
        for (Card card : cards) {
                listOfCardsText.append("question: ")
                        .append(card.getQuestion())
                        .append(" ")
                        .append("answer: ")
                        .append(card.getAnswer())
                        .append("\n");
        }
        view.setText(listOfCardsText.toString());

        return listOfCardsText.toString();

    }
}

