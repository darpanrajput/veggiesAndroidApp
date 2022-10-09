package com.darpan.project.vegies.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.activity.order.AllOrderActivity;
import com.darpan.project.vegies.activity.splash.SplashActivity;

import static com.darpan.project.vegies.constant.Constants.ADMIN_NOTI;
import static com.darpan.project.vegies.constant.Constants.NOTI_COUNT;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;

public class firebaseCloudMessaging
        extends FirebaseMessagingService {
    private static final String TAG = "firebaseCloudMessaging:";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: called");
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getData().get("Title");
        String message = remoteMessage.getData().get("Message");
        SharedPreferences sp =this.getApplicationContext().
                getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int count = sp.getInt(NOTI_COUNT, 0);
        count += 1;

        sp.edit().putInt(NOTI_COUNT, count).apply();

        Log.d(TAG, "onMessageReceived: title=" + title);
        Log.d(TAG, "onMessageReceived: MSG=" + message);

        Intent activityIntent;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            activityIntent = new Intent(getApplicationContext(), AllOrderActivity.class);
        } else {
            activityIntent = new Intent(getApplicationContext(), SplashActivity.class);
        }
       /*
       not working due to- Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE be specified when creating a PendingIntent
       PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                0, activityIntent, 0);*/

        PendingIntent contentIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), 0, activityIntent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            contentIntent = PendingIntent.getActivity
                    (getApplicationContext(), 0, activityIntent, 0);
        }


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo);

        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationChannel channel;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            channel = new NotificationChannel(ADMIN_NOTI, "Admin Request Channel",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI,att);

            manager.createNotificationChannel(channel);
        }
        /* Bitmap bigPic= null;
        try {
            bigPic = Glide.with(this)
                    .asBitmap()
                    .load(image)
                    .submit()
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }*/


        NotificationCompat.Builder n = new NotificationCompat.Builder(
                getApplicationContext(), ADMIN_NOTI)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setLargeIcon(largeIcon)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message)
                        .setBigContentTitle(title)
                        .setSummaryText(getResources().getString(R.string.order_summry)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true);


        manager.notify(0, n.build());

    }


}
