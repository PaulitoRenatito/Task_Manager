package com.example.taskmanager.Configurations;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.taskmanager.MainActivity;
import com.example.taskmanager.Model.ModelTask;
import com.example.taskmanager.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PrefConfig {

    public static final String DEBUG_TAG = "PrefConfig";


    public static final String NORMAL_PRIORITY_CHANNEL_ID =
            "normal_priority_notification_channel";
    public static final String HIGH_PRIORITY_CHANNEL_ID =
            "high_priority_notification_channel";
    public static final String VERY_HIGH_PRIORITY_CHANNEL_ID =
            "very_high_priority_notification_channel";

    public static boolean aux = true;

    public static ArrayList<String> day_key_list =
            new ArrayList<>(Arrays.asList(
                    MainActivity.SUNDAY_KEY,
                    MainActivity.TUESDAY_KEY,
                    MainActivity.WEDNESDAY_KEY,
                    MainActivity.THURSDAY_KEY,
                    MainActivity.FRIDAY_KEY,
                    MainActivity.SATURDAY_KEY,
                    MainActivity.SUNDAY_KEY));

    /**
     * Save a ArrayList in SharedPreferences with a key word
     * @param context context.
     * @param list the ArrayList<ModelTask></> that will be saved.
     * @param key_day the key word for the list (key_day must be the day of the week of the Tasks in the list).
     */
    public static void writeListInPref(Context context, ArrayList<ModelTask> list, String key_day) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key_day, jsonString);
        editor.apply();
    }

    /**
     * Get a saved ArrayList in SharedPreferences with a key word
     * @param context context.
     * @param key_day the key word for the list (key_day must be the day of the week of the Tasks in the list).
     * @return the list saved in SharedPreferences.
     */
    public static ArrayList<ModelTask> readListFromPref(Context context, String key_day) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = preferences.getString(key_day, "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ModelTask>>() {}.getType();
        ArrayList<ModelTask> list = gson.fromJson(jsonString, type);

        return list;
    }

    /**
     * Check if exist a task in the list with the same time
     * @param context context.
     * @param key_day the key word for the list (key_day must be the day of the week of the Tasks in the list).
     * @param time the time to check
     * @return true if have a task with the time and false if not.
     */
    public static boolean haveTaskWithSameHour(Context context, String key_day, String time) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        String key_day_Array = preferences.getString(key_day, "default");

        return key_day_Array.contains(time);
    }

    /**
     * Change the time convention (12h convention or 24h convention)
     * @param context context
     */
    public static void changeTimeMode(Context context) {
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean timeMode12h = sharedPreferences
                .getBoolean(SettingsActivity.TimeFragment.KEY_PREF_TIME_MODE_SWITCH, false);

        if (timeMode12h) {
            for (int i = 0; i < day_key_list.size(); i++) {
                ArrayList<ModelTask> list = readListFromPref(context, day_key_list.get(i));
                changeTimeFromModelTaskItemTo24h(list);
                writeListInPref(context, list, day_key_list.get(i));
            }
        }
        else {
            for (int i = 0; i < day_key_list.size(); i++) {
                ArrayList<ModelTask> list = readListFromPref(context, day_key_list.get(i));
                changeTimeFromModelTaskItemTo12h(list);
                writeListInPref(context, list, day_key_list.get(i));
            }
        }
    }

    /**
     * Change the time of each object(Task) of the list to 12h convention
     * @param list list of objects(Task)
     */
    private static void changeTimeFromModelTaskItemTo12h(ArrayList<ModelTask> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String oldTime = list.get(i).getTime();
                int index = oldTime.indexOf(":");
                int oldHour = Integer.parseInt(oldTime.substring(0, index));
                String oldMinute = oldTime.substring((index + 1));

                if (oldHour > 12) {
                    int newHour = oldHour - 12;
                    list.get(i).setTime(newHour + ":" + oldMinute + " pm");
                }
                else {
                    list.get(i).setTime(oldHour + ":" + oldMinute + " am");
                }
            }
        }
    }

    /**
     * Change the time of each object(Task) of the list to 24h convention
     * @param list list of objects(Task)
     */
    private static void changeTimeFromModelTaskItemTo24h(ArrayList<ModelTask> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String oldTime = list.get(i).getTime();
                int index = oldTime.indexOf(":");
                int index2 = oldTime.indexOf(" ");

                int oldHour = Integer.parseInt(oldTime.substring(0, index));
                String oldMinute = oldTime.substring(index+1, index2);

                if (oldTime.contains(" pm")) {
                    int newHour = oldHour + 12;
                    list.get(i).setTime(newHour + ":" + oldMinute);
                }
                else {
                    list.get(i).setTime(oldHour + ":" + oldMinute);
                }
            }
        }
    }

    /**
     * Convert the string time of 12h to 24h convention.
     * The 24h is the one used in calculations (e.g of the Task ID).
     * @param time time to be converted.
     * @return return the time in the 24h convention.
     */
    public static String convertStringTime12hToTime24h(String time) {
        int index = time.indexOf(":");
        int index2 = time.indexOf(" ");
        if (time.contains(" pm")) {
            int hour = Integer.parseInt(time.substring(0, index)) + 12;
            String minute = time.substring(index+1, index2);
            return hour + ":" + minute;
        }
        else {
            String hour = time.substring(0, index);
            String minute = time.substring(index+1, index2);
            return hour + ":" + minute;
        }
    }

    /**
     * Create the 3 Notification Channels.
     * @param context context
     */
    public static void createNotificationChannelNHVh(Context context) {

        // Create a notification manager object.
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (PrefConfig.isNotificationsEnabled()) {
            // Notification channels are only available in OREO and higher.
            // So, add a check on SDK version.
            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.O) {
                if (mNotificationManager.getNotificationChannels().isEmpty()) {
                    createNotificationChannelNormal(mNotificationManager);
                    createNotificationChannelHigh(mNotificationManager);
                    createNotificationChannelVeryHigh(mNotificationManager);
                }
            }
        }
    }

    /**
     * Create the priority Normal Notification Channel.
     * @param mNotificationManager the Notification Manager
     */
    private static void createNotificationChannelNormal(NotificationManager mNotificationManager) {
        NotificationChannel notificationChannelNormal = new NotificationChannel
                (NORMAL_PRIORITY_CHANNEL_ID,
                        "Normal Priority Channel",
                        NotificationManager.IMPORTANCE_HIGH);
        notificationChannelNormal
                .enableLights(true);
        notificationChannelNormal
                .setLightColor(R.color.default_priority);
        notificationChannelNormal
                .enableVibration(false);
        mNotificationManager.createNotificationChannel(notificationChannelNormal);
    }

    /**
     * Create the priority High Notification Channel.
     * @param mNotificationManager the Notification Manager
     */
    private static void createNotificationChannelHigh(NotificationManager mNotificationManager) {
        NotificationChannel notificationChannelHigh = new NotificationChannel
                (HIGH_PRIORITY_CHANNEL_ID,
                        "High Priority Channel",
                        NotificationManager.IMPORTANCE_HIGH);
        notificationChannelHigh
                .enableLights(true);
        notificationChannelHigh
                .setLightColor(R.color.high_priority);
        notificationChannelHigh
                .enableVibration(true);
        notificationChannelHigh
                .setVibrationPattern(new long[] {500, 500, 500, 500});
        mNotificationManager.createNotificationChannel(notificationChannelHigh);
    }

    /**
     * Create the priority Very High Notification Channel.
     * @param mNotificationManager the Notification Manager
     */
    private static void createNotificationChannelVeryHigh(NotificationManager mNotificationManager) {
        NotificationChannel notificationChannelVeryHigh = new NotificationChannel
                (VERY_HIGH_PRIORITY_CHANNEL_ID,
                        "Very High Priority Channel",
                        NotificationManager.IMPORTANCE_HIGH);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        notificationChannelVeryHigh
                .enableLights(true);
        notificationChannelVeryHigh
                .setLightColor(R.color.very_high_priority);
        notificationChannelVeryHigh
                .enableVibration(true);
        notificationChannelVeryHigh
                .setVibrationPattern(new long[] {500, 1000, 500, 1000});
        notificationChannelVeryHigh
                .setSound(notificationSound, audioAttributes);
        mNotificationManager.createNotificationChannel(notificationChannelVeryHigh);
    }

    /**
     * Schedule all the existing task
     * @param context context
     */
    public static void scheduleAllTasks(Context context) {
        for (int i = 0; i < day_key_list.size(); i++) {
            ArrayList<ModelTask> list = readListFromPref(context, day_key_list.get(i));
            for (int j = 0; j < list.size(); j++) {
                list.get(j).schedule(context);
            }
        }
    }

    public static void checkTaskList(Context context, String day_key) {
        ArrayList<ModelTask> list = readListFromPref(context, day_key);
        if (list == null) {
            list = new ArrayList<>();
        }
        if (list.size() > 0) {
            Log.d(DEBUG_TAG, "<Items in " + day_key + " --->");
            for (int j = 0; j < list.size(); j++) {
                Log.d(DEBUG_TAG, "Item " + j + ": " + list.get(j).getTitle() + " | " + list.get(j).getTime());
            }
            Log.d(DEBUG_TAG, "<--- " + "Items in " + day_key + ">");
        }
        else {
            Log.d(DEBUG_TAG, "No Items in " + day_key);
        }
    }

    /**
     * Check if Dark Theme is on.
     * @param context context.
     * @return true if dark theme is on and false if is off.
     */
    public static boolean isThemeDarkOn(Context context) {
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager
                .getDefaultSharedPreferences(context);

        return sharedPreferences
                .getBoolean(SettingsActivity.
                        ThemeFragment.
                        KEY_PREF_THEME_SWITCH, true);
    }

    /**
     * Check if Notifications are Enable.
     * @return true if Notifications are enabled and false if is not.
     */
    public static boolean isNotificationsEnabled() {
        return aux;
    }

    /**
     * Check the time convention.
     * @return true if is in 12h convention and false if is in 24h convention.
     */
    public static boolean is12hConvention(Context context) {
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager
                .getDefaultSharedPreferences(context);

        return sharedPreferences
                .getBoolean(SettingsActivity.
                        TimeFragment.
                        KEY_PREF_TIME_MODE_SWITCH, true);
    }

    /**
     * Set Calendar based on Task information.
     * @param context context.
     * @param time the time of the task.
     * @param dayOfWeek the day of week of the task.
     * @param inOnReceive the task is being called in onReceive?
     * @return calendar
     */
    public static Calendar setCalendar(Context context, String time, String dayOfWeek, Boolean inOnReceive) {

        int index = time.indexOf(":");
        int hour;
        int minute;

        if (PrefConfig.is12hConvention(context)) {
            String timeConverted = PrefConfig.convertStringTime12hToTime24h(time);
            hour = Integer.parseInt(timeConverted.substring(0, timeConverted.indexOf(":")));
            minute = Integer.parseInt(timeConverted.substring((timeConverted.indexOf(":")+1)));
        }
        else {
            hour = Integer.parseInt(time.substring(0, index));
            minute = Integer.parseInt(time.substring((index+1)));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // region Calendar behavior
        // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4655637
        // https://stackoverflow.com/questions/63207858/i-select-random-days-for-notification-but-notification-is-shown-everyday
        // https://stackoverflow.com/questions/35241385/how-to-set-repeated-notification-for-specific-day-of-week
        // endregion
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int daysToAdd = getCalendarDayOfWeek(dayOfWeek) - currentDayOfWeek;

        if (daysToAdd < 0) {
            daysToAdd += 7;
        }

        if (daysToAdd != 0) {
            calendar.add(Calendar.DATE, daysToAdd);
        }
        // region Alarm Trigger Behaviour


        // https://stackoverflow.com/questions/34643937/alarmmanager-fires-alarms-in-the-past-immediately-before-broadcastreceiver-can-r
        // https://developer.android.com/training/scheduling/alarms.html#set
        // endregion
        if(daysToAdd == 0 && calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 7);
        }

        if (inOnReceive){
            calendar.add(Calendar.DATE, 7);
        }

        Log.d(DEBUG_TAG, "inOnReceive: " + inOnReceive);
        Log.d(DEBUG_TAG, "DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
        Log.d(DEBUG_TAG, "DAY_OF_WEEK_IN_MONTH: " + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
        Log.d(DEBUG_TAG, "DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
        Log.d(DEBUG_TAG, "HOUR: " + calendar.get(Calendar.HOUR));
        Log.d(DEBUG_TAG, "MINUTE: " + calendar.get(Calendar.MINUTE));

        return calendar;
    }

    /**
     * Get the calendar day of week
     * @param dayOfWeek the task day of week
     * @return the calendar day of week
     */
    private static int getCalendarDayOfWeek(String dayOfWeek) {
        switch (dayOfWeek) {
            case MainActivity.SUNDAY_KEY:
                return Calendar.SUNDAY;
            case MainActivity.MONDAY_KEY:
                return Calendar.MONDAY;
            case MainActivity.TUESDAY_KEY:
                return Calendar.TUESDAY;
            case MainActivity.WEDNESDAY_KEY:
                return Calendar.WEDNESDAY;
            case MainActivity.THURSDAY_KEY:
                return Calendar.THURSDAY;
            case MainActivity.FRIDAY_KEY:
                return Calendar.FRIDAY;
            case MainActivity.SATURDAY_KEY:
                return Calendar.SATURDAY;
            default:
                return 0;
        }
    }

    /**
     * Get the notification ID based on the Task time with a one at the beginning.
     * e.g
     * (If task time = 10:35 |
     * Then task ID = 11035)
     * @param context context
     * @param time task time
     * @return notification ID
     */
    public static int getNotificationID(Context context, String time) {

        int index = time.indexOf(":");

        if (PrefConfig.is12hConvention(context)) {

            int index2 = time.indexOf(" ");

            if (time.contains(" pm")){
                int hour = Integer.parseInt(time.substring(0, index)) + 12;
                String minute = time.substring(index+1, index2);

                String string_id = "1" + hour + minute;
                return Integer.parseInt(string_id);
            }
            else {
                int hour = Integer.parseInt(time.substring(0, index));
                String minute = time.substring(index+1, index2);

                String string_id = "1" + hour + minute;
                return Integer.parseInt(string_id);
            }
        }
        else {
            String string_id = "1" + time.substring(0, index) + time.substring((index+1));
            return Integer.parseInt(string_id);
        }
    }

    public static void printMessage(String filterName, String message) {
        Log.d(filterName, message);
    }

}

