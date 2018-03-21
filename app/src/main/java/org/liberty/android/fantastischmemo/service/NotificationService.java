package org.liberty.android.fantastischmemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Trigger;

import org.liberty.android.fantastischmemo.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by Wei on 3/16/2018.
 */

public class NotificationService extends JobService {
    // Magic id used for Card player's notification
    private static final int NOTIFICATION_ID = 9283372;
    private int count=0;
    private final String TAG = getClass().getSimpleName();
    @Override
    public boolean onStartJob(JobParameters job) {

            NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
            job.getTag();

            Notification notification = new android.support.v7.app.NotificationCompat.Builder(this)
                    .setTicker("AnyMemo")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.anymemo_notification_icon)
                    .setContentTitle(job.getTag())
                    .setContentText("You have a workout to do today!")
                    .build();

            notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(NOTIFICATION_ID, notification);

try{
    if(count==0){
        scheduleFollowingJob(job.getExtras());

    }
    count++;
}catch(Exception e){
            Log.e(TAG,"No bundle found");
        }


        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d("SCHEDULER", "onStopJob()");
        return false; // Answers the question: "Should this job be retried?"
    }

    public void  scheduleFollowingJob(Bundle bundle){

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
        if(days==(count+1)){
            dispatcher.cancelAll();
        }
    }
}
