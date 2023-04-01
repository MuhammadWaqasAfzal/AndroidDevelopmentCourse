package com.example.myapplication.common;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService {

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            showNotification(
                    remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("body"), remoteMessage);
        } else if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(), remoteMessage);
        }
    }


    // Method to display the notifications
    public void showNotification(String title,
                                 String message, RemoteMessage remoteMessage) {
        // Pass the intent to switch to the MainActivity

        Intent intent;
        intent = new Intent(this, MainActivity.class);


        // Assign channel ID
        String channel_id = "notification_channel";

        intent.putExtra("MODE", "FIRE_NOTIFICATION");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.app_icon)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);


        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.JELLY_BEAN) {


            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.app_icon);

        } else {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.app_icon);
        }


        int notificationImportance = NotificationManager.IMPORTANCE_HIGH;


        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    notificationImportance);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }


        notificationManager.notify(0, builder.build());
    }
}