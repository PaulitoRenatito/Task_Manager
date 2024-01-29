package com.example.taskmanager.Model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.example.taskmanager.Configurations.AlarmReceiver;
import com.example.taskmanager.Configurations.PrefConfig;
import static android.content.Context.ALARM_SERVICE;

public class ModelTask implements Parcelable {

    private String dayOfWeek;
    private String time;
    private String priority;
    private String title;
    private String description;

    /**
     * Constructor for the ModelTask data model.
     * @param dayOfWeek The day of the week defined for the task.
     * @param time The time defined for start the task.
     * @param priority The priority of the task.
     * @param title Title of the Task.
     * @param description Description about the task.
     */
    public ModelTask(String dayOfWeek, String time,String priority, String title, String description) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.priority = priority;
        this.title = title;
        this.description = description;
    }

    protected ModelTask(Parcel in) {
        dayOfWeek = in.readString();
        time = in.readString();
        priority = in.readString();
        title = in.readString();
        description = in.readString();
    }

    public static final Creator<ModelTask> CREATOR = new Creator<ModelTask>() {
        @Override
        public ModelTask createFromParcel(Parcel in) {
            return new ModelTask(in);
        }

        @Override
        public ModelTask[] newArray(int size) {
            return new ModelTask[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dayOfWeek);
        parcel.writeString(time);
        parcel.writeString(priority);
        parcel.writeString(title);
        parcel.writeString(description);
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Gets the time of the Task.
     *
     * @return The time of the Task.
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the time of the Task.
     * @param time The time that will be defined for the task.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets the priority of the Task.
     *
     * @return The priority of the Task.
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the Task.
     * @param priority The priority that will be defined for the task.
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Gets the title of the Task.
     *
     * @return The title of the Task.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the priority of the Task.
     * @param title The title that will be defined for the task.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the Task.
     *
     * @return The description of the Task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the priority of the Task.
     * @param description The description that will be defined for the task.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Schedule the task in alarm manager.
     * @param context context.
     */
    public void schedule(Context context) {

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(ALARM_SERVICE);

        int NOTIFICATION_ID = PrefConfig.getNotificationID(context, time);

        Intent notifyIntent = new Intent(context, AlarmReceiver.class);
        notifyIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
        notifyIntent.putExtra("Priority", priority);
        notifyIntent.putExtra("Title", title);
        notifyIntent.putExtra("Description", description);
        notifyIntent.putExtra("Time", time);
        notifyIntent.putExtra("Day_of_Week", dayOfWeek);

        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (context, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                PrefConfig.setCalendar(context, time, dayOfWeek, false).getTimeInMillis(),
                notifyPendingIntent);
    }

    /**
     * Remove a schedule task in alarm manager.
     * @param context context.
     */
    public void removeSchedule(Context context) {
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(ALARM_SERVICE);

        int NOTIFICATION_ID = PrefConfig.getNotificationID(context, time);

        Intent notifyIntent = new Intent(context, AlarmReceiver.class);

        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (context, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(notifyPendingIntent);
    }
}
