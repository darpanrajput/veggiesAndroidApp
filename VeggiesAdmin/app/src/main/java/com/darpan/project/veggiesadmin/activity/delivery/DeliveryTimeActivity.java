package com.darpan.project.veggiesadmin.activity.delivery;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.darpan.project.veggiesadmin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.darpan.project.veggiesadmin.constant.Constants.DELIVERY_TIME_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.DELIVERY_TIME__DOC_NAME;
import static com.darpan.project.veggiesadmin.constant.Constants.GLOBAL_TIME_SLOT_KEY;

public class DeliveryTimeActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "DeliveryTimeActivity:";
    private DocumentReference timeDoc = FirebaseFirestore.getInstance().
            collection(DELIVERY_TIME_COLLEC)
            .document(DELIVERY_TIME__DOC_NAME);
    private TextView txtShowTime;
    private ListenerRegistration listenerRegistration;
    private String startHrs1, endMin1;
    private String startHrs2, endMin2;
    private String completeDeliveryTime;
    private int counter = 1;
    Button endTimeBtn;
    private Button resetBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_time);
        Button startTimeBtn = (Button) findViewById(R.id.start_time_btn);
        endTimeBtn = (Button) findViewById(R.id.end_time_btn);
        Button updateTimeBtn = (Button) findViewById(R.id.update_time_btn);
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        txtShowTime = (TextView) findViewById(R.id.show_time_txt);
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),
                        "time picker");
            }
        });

        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        DeliveryTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                //initialise time and hours
                                counter++;
                                if (counter >= 2) {
                                    updateTimeBtn.setEnabled(true);
                                    updateTimeBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                }
                                startHrs2 = String.valueOf(hourOfDay);
                                endMin2 = String.valueOf(minute);
                                Log.d(TAG, "endTimeBtn:=" + startHrs2 + ":" + endMin2);
                                String res = startHrs2.trim() + ":" + endMin1.trim();

                                completeDeliveryTime = completeDeliveryTime + "-" + res;
                                Log.d(TAG, "endTimeBtn:=" + completeDeliveryTime);
                               /* try {
                                    convertIn24Hrs(startHrs2 + ":" + endMin2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }*/
                            }
                        }

                        , hour, minute, false);

                timePickerDialog.show();
            }


        });

        updateTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String startTime = completeDeliveryTime.split("-")[0].trim();
                String EndTime = completeDeliveryTime.split("-")[1].trim();
                String t1 = convertIn12Hrs(startTime);
                String t2 = convertIn12Hrs(EndTime);
                String t1t2 = t1.trim() + "-" + t2.trim();

                new AlertDialog.Builder(DeliveryTimeActivity.this)
                        .setTitle("Update Time ? This will Affect Delivery Time")
                        .setMessage("Confirm time\n" + t1t2)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                saveTime();


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        }).show();


            }
        });

        resetBtn = findViewById(R.id.reset_time_btn);
        String reset = "10:00 AM-06:00 PM";
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryTimeActivity.this)
                        .setTitle("Reset To default")
                        .setMessage("It will Reset the Time To\n " +
                                reset)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ResetTime();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        listenerRegistration = timeDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(DeliveryTimeActivity.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: Exeception=" + e.getMessage());
                    e.getStackTrace();
                    return;
                }
                if (ds != null) {
                    String DT = ds.getString(GLOBAL_TIME_SLOT_KEY);
                   /* String startTime = DT.split("-")[0].trim();
                    String EndTime = DT.split("-")[1].trim();*/
                    txtShowTime.setText(DT);

                    /*try {
                        textView.setText(String.format("%s:%s",convertIn12Hrs(startTime),convertIn12Hrs(EndTime)));
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }*/
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        counter++;
        if (counter >= 1) {
            endTimeBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            endTimeBtn.setEnabled(true);

        }
        Log.d(TAG, "onTimeSet: " + "Hour: " + hourOfDay + " Minute: " + minute);
        startHrs1 = String.valueOf(hourOfDay);
        endMin1 = String.valueOf(minute);

        String res = startHrs1.trim() + ":" + endMin1.trim();
       /* try {
            convertIn24Hrs(startHrs1 + ":" + endMin1);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        completeDeliveryTime = res;
        Log.d(TAG, "onTimeSet: start time in complete time sting= " + completeDeliveryTime);


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
        return " ";
    }

    public static String convertIn24Hrs(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
        SimpleDateFormat format24hrs = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date dateObj = sdf.parse(time);
        Log.d(TAG, "convertIn24Hrs: " + dateObj);
        return format24hrs.format(dateObj);
    }


    private void saveTime() {
        //update the time on database
        Map<String, Object> time = new HashMap<>();
        Log.d(TAG, "onClick: Update Btn =" + completeDeliveryTime.trim());
        time.put("globalDeliveryTime", completeDeliveryTime.trim());

        timeDoc.update(time)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DeliveryTimeActivity.this,
                                "Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(
                        DeliveryTimeActivity.this,
                        "Can Not Update Time Now",
                        Toast.LENGTH_SHORT).show());

    }

    public void ResetTime() {
        //Reset the time on database
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Resetting..");
        String RESET = "10:00-18:00";
        Map<String, Object> time = new HashMap<>();
        Log.d(TAG, "onClick: RESET Btn =" + RESET);
        time.put("globalDeliveryTime", RESET);

        timeDoc.update(time)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DeliveryTimeActivity.this,
                                "Time Reset", Toast.LENGTH_SHORT).show();

                        pd.dismiss();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(
                        DeliveryTimeActivity.this,
                        "Can Not Update Time Now",
                        Toast.LENGTH_SHORT).show());
        pd.dismiss();

    }
}