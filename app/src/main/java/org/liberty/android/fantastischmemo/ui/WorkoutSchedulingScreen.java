package org.liberty.android.fantastischmemo.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Trigger;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelperManager;
import org.liberty.android.fantastischmemo.common.BaseActivity;
import org.liberty.android.fantastischmemo.dao.CardDao;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.service.NotificationService;
import org.liberty.android.fantastischmemo.utils.DateUtil;
import org.liberty.android.fantastischmemo.utils.WorkOutListUtil;
import org.liberty.android.fantastischmemo.utils.WorkoutDialogUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class WorkoutSchedulingScreen extends BaseActivity {

    public static final String EXTRA_DBPATH = "dbpath";
    public static String MAX_NUM_CARDS = "maxNumCards";


    private String dbPath;
    private int maxNumCards;
    private Button startDateButton;
    private Button positiveButton;
    private Button negativeButton;
    private RadioButton numDaysRadioButton;
    private RadioButton numCardsRadioButton;
    private TextInputLayout numDaysInputWrapper;
    private TextInputLayout numCardsInputWrapper;
    private TextView startDateMessage;
    private CheckBox notificationCheckbox;
    final String TAG = getClass().getSimpleName();
    private Map<CompoundButton, View> radioButtonSettingsMapping;
    String numDaysInput;
    String numCardsInput;
    AnyMemoDBOpenHelper helper;
    @Inject
    WorkOutListUtil workoutListUtil;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activityComponents().inject(this);
        setContentView(R.layout.workout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dbPath = extras.getString(EXTRA_DBPATH);
            maxNumCards = extras.getInt(MAX_NUM_CARDS);
        }
        helper = AnyMemoDBOpenHelperManager.getHelper(WorkoutSchedulingScreen.this, dbPath);

        assert dbPath != null : "dbPath shouldn't be null";
        startDateMessage = (TextView) findViewById(R.id.start_date_message);

        startDateButton = (Button) findViewById(R.id.start_date_button);
        startDateButton.setOnClickListener(startDateOnClickButtonListener);

        positiveButton = (Button) findViewById(R.id.button_ok);
        positiveButton.setOnClickListener(positiveOnClickButtonListener);

        negativeButton = (Button) findViewById(R.id.button_cancel);
        negativeButton.setOnClickListener(negativeOnClickButtonListener);

        numDaysRadioButton = (RadioButton) findViewById(R.id.num_days_button);
        numDaysRadioButton.setOnCheckedChangeListener(onCheckedChangeListener);

        numCardsRadioButton = (RadioButton) findViewById(R.id.num_cards_button);
        numCardsRadioButton.setOnCheckedChangeListener(onCheckedChangeListener);

        numDaysInputWrapper = (TextInputLayout) findViewById
                (R.id.num_days_input_wrapper);
        numCardsInputWrapper = (TextInputLayout) findViewById
                (R.id.num_cards_input_wrapper);

        notificationCheckbox = (CheckBox) findViewById(R.id.notification);

        radioButtonSettingsMapping = new HashMap<>(2);
        radioButtonSettingsMapping.put(numDaysRadioButton, findViewById(R.id
                .num_days_settings));
        radioButtonSettingsMapping.put(numCardsRadioButton, findViewById(R.id
                .num_cards_settings));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AnyMemoDBOpenHelperManager.releaseHelper(helper);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private View.OnClickListener startDateOnClickButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PickStartDateFragment pickStartDateFragment = new PickStartDateFragment();
            pickStartDateFragment.setText(startDateMessage);
            pickStartDateFragment.show(getSupportFragmentManager(), "DatePicker");

        }
    };

    public boolean validDate(TextView startDateMessage) {
        if (startDateMessage.getText().toString().equals("") || startDateMessage.getText().toString()
                .equals
                        ("Must choose a start date")) {
            startDateMessage.setText("Must choose a start date");
            return false;
        } else return true;
    }

    public boolean validNumCardsOrDays(TextInputLayout numCardsOrDaysInputWrapper, int max) {

        String numCardsOrDaysInput = numCardsOrDaysInputWrapper.getEditText().getText().toString();
        int numCards;
        boolean correct = true;
        if (numCardsOrDaysInput.equals("")) {
            numCardsOrDaysInputWrapper.setErrorEnabled(true);
            numCardsOrDaysInputWrapper.setError("Must enter a number between " + 1 + " and " + max);
            correct = false;
        } else {
            numCards = Integer.parseInt(numCardsOrDaysInput);
            if (numCards > max || numCards == 0) {
                numCardsOrDaysInputWrapper.setErrorEnabled(true);
                numCardsOrDaysInputWrapper.setError("Must enter a number between " + 1 + " and " + max);
                correct = false;
            }
        }
        return correct;
    }

    private View.OnClickListener positiveOnClickButtonListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            //if the the user chose a start date, the number of days is checked, and the user entered a valid number of days
            if (validDate(startDateMessage) && numDaysRadioButton.isChecked() && validNumCardsOrDays(numDaysInputWrapper,
                    maxNumCards)) {
                Log.d(TAG, "Date as string is " + startDateMessage.getText().toString());

                //parse the date the user has chosen
                String[] dateAsArray = startDateMessage.getText().toString().split("/");
                Date startDate = DateUtil.getDate(
                        Integer.parseInt(dateAsArray[0]),
                        Integer.parseInt(dateAsArray[1]),
                        Integer.parseInt(dateAsArray[2]));

                //parse the number of days the user gave
                numDaysInput = numDaysInputWrapper.getEditText().getText()
                        .toString();

                //convert it to an integer
                int numDays = Integer.parseInt(numDaysInput);

                //set the workout dates for the cards in the deck according to the number of
                // days and the start date
                setWorkoutModeDates(helper, numDays, startDate, true);

                //add the db to the workoutListUtil so it can be displayed in the workout tab
                workoutListUtil.addToRecentList(dbPath);

                //if the notifications are checked, add a notification for the current deck
                // of cards for the next workout date
                try {
                    if (notificationCheckbox.isChecked()) {
                        addNotificationScheduler
                                (startDate, numDays, WorkoutSchedulingScreen.this);
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Parse exception from notification checkbox");
                }
                Toast.makeText(WorkoutSchedulingScreen.this, "Successfully added deck to " +
                        "workout" +
                        " " +
                        "mode!", Toast
                        .LENGTH_LONG).show();

                //destroy the activity after the user presses on "Add to workout mode"
                finish();

                // if the date is chosen, the number of cards is checked, and the user entered a
                // valid number of cards
            } else if (validDate(startDateMessage) && numCardsRadioButton.isChecked() && validNumCardsOrDays(numCardsInputWrapper, maxNumCards)) {

                Log.d(TAG, "Date as string is " + startDateMessage.getText().toString());

                //parse the date chosen by the user
                String[] dateAsArray = startDateMessage.getText().toString().split("/");
                Date startDate = DateUtil.getDate(
                        Integer.parseInt(dateAsArray[0]),
                        Integer.parseInt(dateAsArray[1]),
                        Integer.parseInt(dateAsArray[2]));

                //parse the number of cards
                numCardsInput = numCardsInputWrapper.getEditText().getText()
                        .toString();

                //convert string to int for the number of cards
                int numCards = Integer.parseInt(numCardsInput);

                //set the workout dates for the cards in the current deck
                setWorkoutModeDates(helper, numCards, startDate, false);

                //add the current deck to the workoutlist so it can be shown in the workout tab
                workoutListUtil.addToRecentList(dbPath);
                try {
                    //if the user chose to get notified, the notification will be triggered the
                    // day of a workout
                    if (notificationCheckbox.isChecked()) {
                        addNotificationScheduler
                                (startDate, numCards, WorkoutSchedulingScreen.this);
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Parse exception from notification checkbox");
                }
                Toast.makeText(WorkoutSchedulingScreen.this, "Successfully added deck to " +
                        "workout" +
                        " " +
                        "mode!", Toast
                        .LENGTH_LONG).show();

                //destroy the activity once the user pressed on "Add to workout mode"
                finish();
            }

        } //if date is valid
    };


    private View.OnClickListener negativeOnClickButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    /*if the radio button is not checked, dont show the Input text that is associated with it
    otherwise display it*/
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener
            = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            View settingsView = radioButtonSettingsMapping.get(buttonView);
            if (isChecked) {
                settingsView.setVisibility(View.VISIBLE);
            } else {
                settingsView.setVisibility(View.GONE);
            }
        }
    };

    /*
    this method already exists in WorkoutDialogUtil. But because there was a null pointer when
    passing the helper from WorkoutSchedulingScreen to that class's method, it had to be copied here. The methods are
    identical. Therefore the test for this method resides in WorkoutDialogUtilTest in the "test" folder
     */
    public List<Card> setWorkoutModeDates(AnyMemoDBOpenHelper helper, int numDaysOrCards, Date
            startDate, boolean daysMode) {
        int nbCardsPerWorkout = 0;
        CardDao cardDao = helper.getCardDao();
        List<Card> cards = cardDao.getAllCards(null);
        Log.d(TAG, "card numbers " + cards.size());
        if (daysMode) {
            nbCardsPerWorkout = (cards.size() + numDaysOrCards - 1) / numDaysOrCards;
            Log.d(TAG, "card numbers " + nbCardsPerWorkout);
        } else {
            nbCardsPerWorkout = numDaysOrCards;
            Log.d(TAG, "card numbers " + nbCardsPerWorkout);
        }
        int count = 0;
        int addDays = 0;

        //Log.d(TAG, "before setting the date");
        Date learningDate;

        for (Card card : cards) {

            if (count == nbCardsPerWorkout) {
                count = 0;
                addDays++;
            }
            learningDate = DateUtil.addDays(startDate,
                    addDays);
            card.setLearningDate(learningDate);
            cardDao.update(card);
            Log.d(TAG, "date is set to : " + card.getLearningDate());
            count++;
        }
        AnyMemoDBOpenHelperManager.releaseHelper(helper);
        return cards;
    }

    //schedule notifications
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean addNotificationScheduler(Date startDate, int numDays, Activity mActivity) throws ParseException {

        int days = DateUtil.getDateDifference(startDate);
        int duration = Math.abs(days * 24);

        final int periodicity = (int) TimeUnit.HOURS.toSeconds(duration);
        final int toleranceInterval = (int) TimeUnit.MINUTES.toSeconds(1);

        Bundle bundle = new Bundle();
        bundle.putString("numDays", Integer.toString(numDays));

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mActivity));
        int result =
                dispatcher.schedule(dispatcher.newJobBuilder()
                        .setService(NotificationService.class)
                        .setTag("First day of your work out")
                        .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                        .setReplaceCurrent(true)
                        .setRecurring(false)
                        .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                        .setExtras(bundle)
                        .build()
                );
        if (result == FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
            return true;
        } else {
            Log.d(TAG, "Job not scheduled");
            return false;
        }
    }

    /*Hard codes the date of the first card. This is done for testing purposes to trigger the
    reschedule pop up
      just set the testing flag to true, if you want to run this piece of
      code, otherwise set it to false*/
    private void setIncompleteDates(boolean testing) {
        if (testing) {

            List<Card> temp = AnyMemoDBOpenHelperManager.getHelper(dbPath)
                    .getCardDao()
                    .getAllCards(null);
            if (temp != null) {
                temp.get(0).setLearningDate(DateUtil.getDate(12, 4, 2000));
                AnyMemoDBOpenHelperManager.getHelper(dbPath).getCardDao().update(temp
                        .get(0));
            }
        }
    }
}

