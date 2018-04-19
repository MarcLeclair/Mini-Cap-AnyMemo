package org.liberty.android.fantastischmemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Trigger;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.ui.AnyMemo;

import java.util.concurrent.TimeUnit;

/**
 * Created by Wei on 3/16/2018.
 */

public class NotificationService extends JobService {

    //create a static global counter that will increment the id for every NotificationService
    // instance
    private static int globalCounter = 0;
    private int NOTIFICATION_ID = 0;
    private int count = 0;
    private final String TAG = getClass().getSimpleName();

    //Id incrementation for each new NotificationService object
    public NotificationService() {
        NOTIFICATION_ID = globalCounter++;
    }

    @Override
    public boolean onStartJob(JobParameters job) {

        Intent intent = new Intent(this, AnyMemo.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        Notification notification = new android.support.v7.app.NotificationCompat.Builder(this)
                .setTicker("AnyMemo")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.anymemo_notification_icon)
                .setContentTitle(job.getTag())
                .setContentText("You have a workout to do today!")
                .setContentIntent(pendingIntent)
                .build();

        notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(NOTIFICATION_ID, notification);


        try {
            if (count == 0) {
                scheduleFollowingJob(job.getExtras());

            }
            count++;
        } catch (Exception e) {
            Log.e(TAG, "No bundle found");
        }


        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d("SCHEDULER", "onStopJob()");
        return false; // Answers the question: "Should this job be retried?"
    }

    public void scheduleFollowingJob(Bundle bundle) {

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        final int periodicity = (int) TimeUnit.HOURS.toSeconds(24);
        final int toleranceInterval = (int) TimeUnit.HOURS.toSeconds(1);


        dispatcher.schedule(dispatcher.newJobBuilder()
                .setService(NotificationService.class)
                .setTag("Another workout day")
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .build()
        );

        String dayNum = bundle.getString("numDays");
        int days = Integer.parseInt(dayNum);
        if (days == (count + 1)) {
            dispatcher.cancelAll();
        }
    }
}
