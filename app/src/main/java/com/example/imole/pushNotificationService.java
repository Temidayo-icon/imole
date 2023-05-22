package com.example.imole;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class pushNotificationService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingService";


        @Override
        public void onMessageReceived( RemoteMessage remoteMessage) {
            // Handle the incoming message here
            if (remoteMessage.getNotification() != null) {
                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                // Handle the notification as desired (e.g., show a system notification)
                showNotification(title, body);
            }
        }

        private void showNotification(String title, String body) {
            // Create a notification channel (required for Android 8.0 and above)
            createNotificationChannel();
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
            // Create the notification channel (for Android Oreo and above)

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Daily_Power_Consumption_ID")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // Show the notification
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }

        private void createNotificationChannel() {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "Daily_Power_consumption_ID";
                CharSequence channelName = "Daily power consumption";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }


}
