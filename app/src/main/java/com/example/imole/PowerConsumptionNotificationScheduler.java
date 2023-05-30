package com.example.imole;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class PowerConsumptionNotificationScheduler {

    private static final String TAG = "NotificationScheduler";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "PowerConsumptionChannel";
    private static final String CHANNEL_NAME = "Power Consumption";

    public static void scheduleDailyNotification(Context context) {
        // Calculate the time for the daily notification at 10 PM
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Check if the notification time has already passed for today
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            // If yes, add one day to the notification time
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Create an Intent for the notification receiver
        Intent notificationIntent = new Intent(context, PowerConsumptionNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Schedule the notification using AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        } else {
            Log.e(TAG, "Failed to get AlarmManager");
        }
    }

    public static class PowerConsumptionNotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Fetch the daily power consumption value

            MainActivity mainActivity = MainActivity.getInstance(); // Obtain the instance of MainActivity

            double dailyPowerConsumption = mainActivity.getTextViewValue();







            // Create a notification
            Notification notification = buildNotification(context, dailyPowerConsumption);

            // Display the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                // Create a notification channel for Android Oreo and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(
                            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                // Show the notification
                notificationManager.notify(NOTIFICATION_ID, notification);
            } else {
                Log.e(TAG, "Failed to get NotificationManager");
            }
        }

        private double getDailyPowerConsumption() {
            // Retrieve the daily power consumption value from your data source
            // Modify this method based on your specific implementation
            // You can fetch the value from a database, SharedPreferences, or API
            // Here's a dummy implementation that returns a random value
            return Math.random() * 1000; // Modify this to return the actual daily power consumption
        }

        private Notification buildNotification(Context context, double dailyPowerConsumption) {
            // Create a notification builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Daily Power Consumption")
                    .setContentText("Your daily power consumption is: " + dailyPowerConsumption + " kWh")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            // Create an intent to launch the app when the notification is clicked
            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            // Build the notification
            return builder.build();
        }
    }
}
