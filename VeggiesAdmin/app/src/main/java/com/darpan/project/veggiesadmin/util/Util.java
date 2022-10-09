package com.darpan.project.veggiesadmin.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;



import com.darpan.project.veggiesadmin.activity.admin.AdminActivity;
import com.darpan.project.veggiesadmin.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.darpan.project.veggiesadmin.constant.Constants.ConfirmNotification;

public  class Util {


    private static final String TAG = "Util: ";


    @NonNull
    public static String getCurrentTime() {
        /*get time in 24hrs fpr and store in database*/
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date d = new Date();

        System.out.println("date object=" + d);
        System.out.println("date=" + new SimpleDateFormat("dd/MMM/yyyy",
                Locale.getDefault()).format(d));
        return sdf.format(d);
    }


    public static String getDate(long dt) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy",Locale.getDefault());
        Date date = new Date(dt);
        return dateFormat.format(date);
    }

    public static String getNextDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        return dateFormat.format(tomorrow);
    }

    static String IMEI = "";

    public static String getIMEI(Context context) {

        String unique_id = Settings.
                Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("unique_id", "-->" + unique_id);
        return unique_id;
    }


    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }

    public static Snackbar showSnackBar(String msg, View v) {
        Snackbar snackbar = Snackbar
                .make(v.getRootView(), msg, Snackbar.LENGTH_LONG);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        /* snackbar.show();*/
        return snackbar;
    }

    public static Toast showToast(String message, Context context) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);

    }


    public static String getFBUrl(String url) {
        if (url.contains("facebook"))
            return url + "?type=large";
        else return url;
    }


    private static boolean isTime() {
        //API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime now = LocalTime.now();
            LocalTime limit = LocalTime.parse("15:30");
            return now.isAfter(limit);
        }
        return false;
    }

    public static Notification buildNotification(Context context,
                                                 String title,
                                                 String Shortmessage, String bigMessage){
        Intent activityIntent = new Intent(context.getApplicationContext(), AdminActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.logo);

        return  new NotificationCompat.Builder(context, ConfirmNotification)
                .setSmallIcon(R.drawable.ic_shop)
                .setContentTitle(title)
                .setLargeIcon(largeIcon)
                .setContentText(Shortmessage)
                .setSound(Settings.System.DEFAULT_RINGTONE_URI)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigMessage)
                        .setBigContentTitle(title)
                        .setSummaryText(context.getResources().
                                getString(R.string.order_summry)))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true)
                .build();
    }

    public static long getToday() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * @return milliseconds since 1.1.1970 for tomorrow 0:00:01 local timezone
     */
    public static long getTomorrow() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 1);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 1);
        return c.getTimeInMillis();
    }



    public static long getDays(long startDate, long endDate) {


        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Date start = new Date(startDate);
        Date end = new Date(endDate);
        System.out.println("start=" + dateFormat.format(start));
        System.out.println("end=" + dateFormat.format(end));
        if (endDate > startDate) {
            long diffInMillies = Math.abs(end.getTime() - start.getTime());
            return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }

        return 0;
    }

    public static String convertIn12Hrs(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
            SimpleDateFormat format12hrs = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date dateObj = sdf.parse(time);
            Log.d(TAG, "convertIn12Hrs: " + dateObj);
            return format12hrs.format(dateObj);
        } catch (Exception e) {
            Log.d(TAG, "convertIn12Hrs: e=" + e.getMessage());
            e.getStackTrace();
        }
        return "Nan";
    }





    /*try (OutputStream fileOut = new FileOutputStream("Javatpoint.xls")) {
            Workbook wb = new HSSFWorkbook();
            Sheet sheet = wb.createSheet("Sheet");
            Row row = sheet.createRow(1);
            Cell cell = row.createCell(1);
            cell.setCellValue("Two cells have merged");
              //Merging cells by providing cell index
            sheet.addMergedRegion(new CellRangeAddress(1,1,1,2));
            wb.write(fileOut);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }  */
}
