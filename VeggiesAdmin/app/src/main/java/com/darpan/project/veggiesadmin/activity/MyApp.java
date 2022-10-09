package com.darpan.project.veggiesadmin.activity;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;

import static com.darpan.project.veggiesadmin.constant.Constants.ConfirmNotification;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        creatNotificationChannel();
    }

    private void creatNotificationChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(
                    ConfirmNotification,
                    "Channel For showing Notification Status confirmation every" +
                            "time the admin change,it reflect the admin chnages",
                    NotificationManager.IMPORTANCE_HIGH


            );

            channel.setDescription("This Notification Inform Admin About the Order Status");
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI,att);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }
}
