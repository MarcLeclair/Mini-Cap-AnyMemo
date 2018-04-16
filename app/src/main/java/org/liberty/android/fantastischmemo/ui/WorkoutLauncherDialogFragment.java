package org.liberty.android.fantastischmemo.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class WorkoutLauncherDialogFragment extends BaseDialogFragment {

    public static final String EXTRA_DBPATH = "dbpath";
    private AnyMemoDBOpenHelper dbOpenHelper;
    private String dbPath = null;
    private BaseActivity mActivity;
    private Button startDateButton;
    private Button positiveButton;
    private Button negativeButton;
    private RadioButton numDaysRadioButton;
    private RadioButton numCardsRadioButton;
    private TextInputLayout numDaysInputWrapper;
    private TextInputLayout numCardsInputWrapper;
    private TextView startDateMessage;
    private WorkoutDialogUtil workoutDialogUtil;
    private CheckBox notificationCheckbox;
    final String TAG = getClass().getSimpleName();
    private Map<CompoundButton, View> radioButtonSettingsMapping;

    @Inject
    AMPrefUtil amPrefUtil;

    public WorkoutLauncherDialogFragment() {
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
        View v = getActivity().getLayoutInflater().inflate(R.layout.workout, null, false);

        startDateMessage = (TextView) v.findViewById(R.id.start_date_message);

        startDateButton = (Button) v.findViewById(R.id.start_date_button);
        startDateButton.setOnClickListener(startDateOnClickButtonListener);

        positiveButton = (Button) v.findViewById(R.id.button_ok);
        positiveButton.setOnClickListener(positiveOnClickButtonListener);

        negativeButton = (Button) v.findViewById(R.id.button_cancel);
        negativeButton.setOnClickListener(negativeOnClickButtonListener);

        numDaysRadioButton = (RadioButton) v.findViewById(R.id.num_days_button);
        numDaysRadioButton.setOnCheckedChangeListener(onCheckedChangeListener);

        numCardsRadioButton = (RadioButton) v.findViewById(R.id.Num_cards_button);
        numCardsRadioButton.setOnCheckedChangeListener(onCheckedChangeListener);
        numDaysInputWrapper = (TextInputLayout) v.findViewById
                (R.id.num_days_input_wrapper);
        numCardsInputWrapper = (TextInputLayout) v.findViewById
                (R.id.num_cards_input_wrapper);
        notificationCheckbox = (CheckBox) v.findViewById(R.id.notification);

        radioButtonSettingsMapping = new HashMap<CompoundButton, View>(2);
        radioButtonSettingsMapping.put(numDaysRadioButton, v.findViewById(R.id
                .num_days_settings));
        radioButtonSettingsMapping.put(numCardsRadioButton, v.findViewById(R.id
                .num_cards_settings));

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_to_workout_mode)
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
                settingsView.setVisibility(View.VISIBLE);
            } else {
                settingsView.setVisibility(View.GONE);
            }
  /*          if (isChecked) {
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
            }*/
        }
    };

    private View.OnClickListener startDateOnClickButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PickStartDateFragment pickStartDateFragment = new PickStartDateFragment();
            pickStartDateFragment.setText(startDateMessage);
            pickStartDateFragment.show(mActivity.getSupportFragmentManager(), "DatePicker");
        }
    };

    private View.OnClickListener positiveOnClickButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO
            getView().setVisibility(View.GONE);
//            mActivity.getSupportFragmentManager().popBackStack();
        }
    };

    private View.OnClickListener negativeOnClickButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        //TODO
            mActivity.getSupportFragmentManager().popBackStack();
        }
    };
}

