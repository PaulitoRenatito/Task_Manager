package com.example.taskmanager.Configurations;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.example.taskmanager.MainActivity;
import com.example.taskmanager.R;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String DEBUG_TAG = "AlarmReceiver";

    private NotificationManager mNotificationManager;

    int NOTIFICATION_ID;
    String time;
    String dayOfWeek;
    String notificationPriority;
    String notificationTitle;
    String notificationDescription;

    @Override
    public void onReceive(Context context, Intent intent) {

        PrefConfig.printMessage(DEBUG_TAG, "Alarm Receiver: On Receive");

        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(ALARM_SERVICE);

        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            PrefConfig.scheduleAllTasks(context);
        }
        else {
            deliverNotification(context, intent);

            NOTIFICATION_ID = intent.getIntExtra("NOTIFICATION_ID", 0);
            time = intent.getStringExtra("Time");
            dayOfWeek = intent.getStringExtra("Day_of_Week");

            Intent notifyIntent = new Intent(context, AlarmReceiver.class);
            notifyIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            notifyIntent.putExtra("Priority", notificationPriority);
            notifyIntent.putExtra("Title", notificationTitle);
            notifyIntent.putExtra("Description", notificationDescription);
            notifyIntent.putExtra("Time", time);
            notifyIntent.putExtra("Day_of_Week", dayOfWeek);

            PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                    (context, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC,
                    PrefConfig.setCalendar(context, time, dayOfWeek, true).getTimeInMillis(),
                    notifyPendingIntent);
        }

    }

    private void deliverNotification(Context context, Intent intent) {

        NOTIFICATION_ID = intent.getIntExtra("NOTIFICATION_ID", 0);
        notificationPriority = intent.getStringExtra("Priority");
        notificationTitle = intent.getStringExtra("Title");
        notificationDescription = intent.getStringExtra("Description");

        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (context
                .getResources()
                .getString(R.string.string_normal)
                .equals(notificationPriority)) {
            NotificationCompat.Builder builderNormal =
                    new NotificationCompat.Builder(context, PrefConfig.NORMAL_PRIORITY_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notifications_black)
                            .setColorized(true)
                            .setColor(context.getColor(R.color.default_priority))
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationDescription)
                            .setContentIntent(contentPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);
            mNotificationManager.notify(NOTIFICATION_ID, builderNormal.build());
        }
        else if (context
                .getResources()
                .getString(R.string.string_high)
                .equals(notificationPriority)) {
            NotificationCompat.Builder builderHigh =
                    new NotificationCompat.Builder(context, PrefConfig.HIGH_PRIORITY_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notifications_black)
                            .setColorized(true)
                            .setColor(context.getColor(R.color.high_priority))
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationDescription)
                            .setContentIntent(contentPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);
            mNotificationManager.notify(NOTIFICATION_ID, builderHigh.build());
        }
        else if (context
                .getResources()
                .getString(R.string.string_very_high)
                .equals(notificationPriority)) {
            NotificationCompat.Builder builderVeryHigh =
                    new NotificationCompat.Builder(context, PrefConfig.VERY_HIGH_PRIORITY_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notifications_black)
                            .setColorized(true)
                            .setColor(context.getColor(R.color.very_high_priority))
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationDescription)
                            .setContentIntent(contentPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);
            mNotificationManager.notify(NOTIFICATION_ID, builderVeryHigh.build());
        }
    }
}
