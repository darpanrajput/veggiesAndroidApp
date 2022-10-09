package com.darpan.project.veggiesadmin.fcm;

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

import com.darpan.project.veggiesadmin.activity.order.OrderListActivity;
import com.darpan.project.veggiesadmin.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.darpan.project.veggiesadmin.constant.Constants.Client_NOTI;
import static com.darpan.project.veggiesadmin.constant.Constants.NOTI_COUNT;
import static com.darpan.project.veggiesadmin.constant.Constants.PREF_NAME;


public class firebaseCloudMessaging
        extends FirebaseMessagingService {
    private static final String TAG = "firebaseCloudMessaging:";
    private String title, message;


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: called");
        super.onMessageReceived(remoteMessage);
        SharedPreferences sp =
                this.getApplicationContext()
                        .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int count=sp.getInt(NOTI_COUNT,0);
        sp.edit().putInt(NOTI_COUNT,count+1).apply();


        title = remoteMessage.getData().get("Title");
        message = remoteMessage.getData().get("Message");
        Log.d(TAG, "onMessageReceived: title=" + title);
        Log.d(TAG, "onMessageReceived: MSG=" + message);

        Intent activityIntent = new Intent(getApplicationContext(), OrderListActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                0, activityIntent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo);

        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            channel = new NotificationChannel(Client_NOTI, "Order Request Channel",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, att);
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
                getApplicationContext(), Client_NOTI)
                .setSmallIcon(R.drawable.ic_shop)
                .setContentTitle(title)
                .setLargeIcon(largeIcon)
                .setContentText(message)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message)
                        .setBigContentTitle(title)
                        .setSummaryText(getResources().getString(R.string.order_summry)))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true);

        manager.notify(0, n.build());
    }


  /*  public static Notification AdminNotification(Context context,
                                                 String title, String
                                                         Shortmessage, String bigMessage){

        Intent activityIntent = new Intent(context.getApplicationContext(),  NewOrderActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.logo);

        return  new NotificationCompat.Builder(context, Client_NOTI)
                .setSmallIcon(R.drawable.ic_shop)
                .setContentTitle(title)
                .setLargeIcon(largeIcon)
                .setContentText(Shortmessage)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigMessage)
                        .setBigContentTitle(title)
                        .setSummaryText(context.getResources().getString(R.string.order_summry)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true)
                .build();
    }
*/
}
