package org.liberty.android.fantastischmemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.liberty.android.fantastischmemo.R;

/**
 * Created by Wei on 3/16/2018.
 */

public class NotificationService extends JobService {
    // Magic id used for Card player's notification
    private static final int NOTIFICATION_ID = 9283372;
    @Override
    public boolean onStartJob(JobParameters job) {
        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        job.getTag();
        Notification notification = new android.support.v7.app.NotificationCompat.Builder(this)
                .setTicker("AnyMemo")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.anymemo_notification_icon)
                .setContentTitle(job.getTag())
                .setContentText(getString(R.string.stat_scheduled))
                .build();

        notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(NOTIFICATION_ID, notification);


//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
//        DateTime notificationDate = formatter.parseDateTime(job.getExtras().getString("startdate"));
//
////.plusHours(21).plusMinutes(5)
//        DateTime tomorrow = notificationDate.plusDays(1).withTimeAtStartOfDay();
//        int windowStart = Hours.hoursBetween(notificationDate, tomorrow).getHours() * 60 * 60;
//
//        dispatcher.schedule(dispatcher.newJobBuilder()
//                .setService(NotificationService.class)
//                .setTag("You successfully create a study plan")
//                .setTrigger(Trigger.executionWindow(windowStart, windowStart + 10))//.setInitialDelay(DAYS.toSeconds(1))
//                .setReplaceCurrent(true)
//                .setRecurring(false)
//                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
//                .build()
//        );



        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d("SCHEDULER", "onStopJob()");
        return false; // Answers the question: "Should this job be retried?"
    }

}
