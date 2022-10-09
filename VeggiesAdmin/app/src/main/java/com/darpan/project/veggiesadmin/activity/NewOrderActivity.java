package com.darpan.project.veggiesadmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.darpan.project.veggiesadmin.fcm.APIService;
import com.darpan.project.veggiesadmin.fcm.Client;
import com.darpan.project.veggiesadmin.fcm.Data;
import com.darpan.project.veggiesadmin.fcm.MyResponse;
import com.darpan.project.veggiesadmin.fcm.NotificationSender;
import com.darpan.project.veggiesadmin.firebaseModal.OrderPlacedModal;
import com.darpan.project.veggiesadmin.util.Util;
import com.darpan.project.veggiesadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.darpan.project.veggiesadmin.constant.Constants.ACCEPTED;
import static com.darpan.project.veggiesadmin.constant.Constants.ORDER_PLACED_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.PENDING;
import static com.darpan.project.veggiesadmin.constant.Constants.TOKEN_DOC;
import static com.darpan.project.veggiesadmin.constant.Constants.TOKEN_KEY;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_TOKEN_COLLEC;

public class NewOrderActivity extends AppCompatActivity {
    private ProgressDialog pd;
    private static final String TAG = "NewOrderActivity:";

    private APIService apiService;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_activity);
        pd = new ProgressDialog(this);

        notificationManager = NotificationManagerCompat.from(this);
        Button button = findViewById(R.id.start_time_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrderAckToClient();
            }
        });


    }


    private void sendOrderAckToClient() {
        Log.d(TAG, "sendOrderAckToClient: called");
        FirebaseFirestore.getInstance().collection(ORDER_PLACED_COLLEC)
                .get().addOnCompleteListener(this, task -> {
            Log.d(TAG, "onComplete: Caled");
            if (task.isSuccessful() && task.getResult() != null) {
                Log.d(TAG, "onComplete: Success ds called");
                for (DocumentSnapshot document : task.getResult()) {
                    if (document != null) {
                        sendACK(document);
                    }

                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.getMessage());
            }
        });

    }

    private void sendACK(DocumentSnapshot ds) {
        //document snapshot of the orderPlaced
        Log.d(TAG, "sendACK: called");
        showPd("Loading");
        Log.d(TAG, "sendACK: doc name " + ds.get("orderName"));
        if (ds.get("orderStatus") != null && ds.get("orderStatus").toString().trim().contains(PENDING)) {

            OrderPlacedModal OPM = ds.toObject(OrderPlacedModal.class);
            if (OPM != null) {
                OPM.setOrderStatus(ACCEPTED);
                FirebaseFirestore.getInstance().collection(ORDER_PLACED_COLLEC)
                        .document(ds.getId())
                        .set(OPM, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            showToast("order Accepted");
                            hidePd();
                            /*now we need the customer id to go into
                             * the customer token inside userOrder collection*/
                            String userMsg = "order has been Accepted it will be deliver in 1 hour";
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    NotifyClientAboutOrderStatus(OPM, userMsg);
                                }
                            }, 3000);

                          /*  *//*change the order id field when admin accept it as
                            * it is currently the same as the user id of that user*//*
                            current iam not changing it because i can get
                            order id through loop by using user id
                            each user may have multiple order and hence order id*/
                        })
                        .addOnFailureListener(e -> {
                            showToast(e.getMessage());
                            hidePd();

                        });
            }

        }


    }

    private void NotifyClientAboutOrderStatus(OrderPlacedModal OPM, String orderStatus) {
        apiService = Client.getClient("https://fcm.googleapis.com/").
                create(APIService.class);
        Data data = new Data("Order Status", orderStatus);
        FirebaseFirestore.getInstance().collection(USER_COLLEC)
                .document(OPM.getCustomerId()).collection(USER_TOKEN_COLLEC)
                .document(TOKEN_DOC).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    Map<String, Object> tokenMap = documentSnapshot.getData();
                    String userTokenKey = tokenMap.get(TOKEN_KEY).toString();
                    Log.d(TAG, "onSuccess: NotifyClientAboutOrderStatus:userToken=" + userTokenKey);
                    NotificationSender sender =
                            new NotificationSender(data, userTokenKey);

                    apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MyResponse> call,
                                               @NonNull Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body() != null && response.body().success != 1) {

                                    Log.d(TAG, "onResponse: error=" + response.code());
                                    Log.d(TAG, "onResponse: response.body().success =" + response.body().success);
                                } else {
                                    Log.d(TAG, "onResponse: Null");
                                }
                            } else {
                                Log.d(TAG, "onResponse: failed code=" + response.code());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<MyResponse> call, Throwable t) {
                            t.getStackTrace();
                            Notification nt = Util.buildNotification(
                                    NewOrderActivity.this,
                                    "failed",
                                    "failed To inform User About the Order Status",
                                    "failed To inform User About the Order Status");

                            notificationManager.notify(2, nt);//it will not override the
                            //same notification with same Channel id
                            Log.d(TAG, "onFailure: " + t.getMessage());

                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.getStackTrace();
                Notification nt = Util.buildNotification(
                        NewOrderActivity.this,
                        "exception",
                        "failed To inform User About the Order Status",
                        "firebase exception occur " + e.getMessage());

                notificationManager.notify(2, nt);//it will not override the
                //same notification with same Channel id
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });


    }

    private void showToast(String MSG) {
        Toast.makeText(this, MSG, Toast.LENGTH_SHORT).show();
    }


    private void showPd(String title) {
        pd.setTitle(title);
        pd.show();
    }

    private void hidePd() {
        pd.cancel();
    }
}