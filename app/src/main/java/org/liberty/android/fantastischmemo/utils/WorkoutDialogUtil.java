package org.liberty.android.fantastischmemo.utils;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Trigger;

import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelperManager;
import org.liberty.android.fantastischmemo.dao.CardDao;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.service.NotificationService;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by melid on 2018-04-12.
 */

public class WorkoutDialogUtil {
    private final String TAG = getClass().getSimpleName();

    /**
     * Set all cards in current deck to today or future chosen workout date
     * If the user chooses the number of days of workout, the result of nbCardsPerWorkout is done
     * by  rounding up
     * the result of an integer division for example if the person chooses 2 days to study a deck
     * of 3 cards, 3 / 2 = 2 there will be 2 cards to study at first then 1 card, we need to round
     * up with integer division. Otherwise 3/2 = 1 which would mean 1 card per day, for 3 days,
     * which is not what we want.
     * If the user chooses the number of cards per day, you simply pass that parameter in to
     * nbCardsPerWorkout,
     * without calcuation necessary
     *
     * @param helper         helper used to access the cardDao
     * @param numDaysOrCards holds either the number of workout days or number of cards per day
     *                       of workout
     * @param daysMode       flags whether numDaysOrCards is the number of days or cards. if daysMode
     *                       is set to true, then numDaysOrCards represents the number of days, else it
     *                       represents the number of cards
     * @return list of all cards within deck with new learningDate value
     **/
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

    /**
     * Reschedules cards' learningDate
     *
     * @param helper          helper used to access the cardDao
     * @param date            date chosen to reschedule the list of incompleteCards
     * @param incompleteCards all cards that need to be rescheduled
     * @return list of now rescheduled cards
     **/
    public List<Card> setCardsDates(AnyMemoDBOpenHelper helper, Date date, List<Card>
            incompleteCards) {
        CardDao cardDao = helper.getCardDao();
        for (Card card : incompleteCards) {
            card.setLearningDate(date);
            cardDao.update(card);
        }
        return incompleteCards;
    }

    //schedule notifications
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean addNotificationScheduler(Date startDate, int numDays, Activity mActivity) throws ParseException {

        DateUtil dt = new DateUtil();

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
}
