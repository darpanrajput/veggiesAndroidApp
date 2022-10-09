package com.darpan.project.vegies.activity.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.darpan.project.vegies.activity.notification.NotificationActivity;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.Utils.Utiles;
import com.darpan.project.vegies.adapters.category.myorder.MyOrderAdapter;
import com.darpan.project.vegies.fcm.APIService;
import com.darpan.project.vegies.fcm.Client;
import com.darpan.project.vegies.fcm.Data;
import com.darpan.project.vegies.fcm.MyResponse;
import com.darpan.project.vegies.fcm.NotificationSender;
import com.darpan.project.vegies.firebaseModal.OrderPlacedModal;

import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.darpan.project.vegies.constant.Constants.ACCEPTED;
import static com.darpan.project.vegies.constant.Constants.CANCELLED;
import static com.darpan.project.vegies.constant.Constants.DELIVERED;
import static com.darpan.project.vegies.constant.Constants.ORDER_CLASS;
import static com.darpan.project.vegies.constant.Constants.ORDER_PLACED_COLLEC;
import static com.darpan.project.vegies.constant.Constants.TOKEN_DOC;
import static com.darpan.project.vegies.constant.Constants.TOKEN_KEY;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.USER_ORDER_COLLEC;

public class MyOrderActivity extends AppCompatActivity {
    private RecyclerView myOrderRv;
    private TextView txtSubTotal, txtDelivery, txtTotal, txtStatus, txtTime, txtDate, txtMessage;
    private ProgressBar pb;
    private MyOrderAdapter myOrderAdapter;
    private Intent i;
    private static final String TAG = "MyOrderActivity:";
    private OrderPlacedModal OPM;
    private String oId;//actual order id not the same as that of the order id as user id
    private NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        notificationManager = NotificationManagerCompat.from(this);
        i = getIntent();
        OPM = i.getParcelableExtra(ORDER_CLASS);
        oId = i.getStringExtra("orderId");

        Log.d(TAG, "onCreate: Oid=" + oId);

        findView();
        setData();

    }


    private void findView() {
        myOrderRv = findViewById(R.id.myoder_rv);
        txtSubTotal = findViewById(R.id.od_subtotal);
        txtDelivery = findViewById(R.id.od_delivery_charge);
        txtTotal = findViewById(R.id.od_total);
        txtStatus = findViewById(R.id.od_status);
        txtTime = findViewById(R.id.od_timesloat);
        txtDate = findViewById(R.id.od_date);
        txtMessage = findViewById(R.id.od_message);
        pb = findViewById(R.id.progressBar);
    }

    private void setData() {
        myOrderRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        if (i != null) {
            txtTotal.setText(String.format("Amt: ₹%s", OPM.getTotalPrice()));

            txtDelivery.setText(String.format("Delivery: ₹%s", OPM.getDeliveryCharge()));

            double subTot = Math.abs(Double.parseDouble(OPM.getTotalPrice())
                    - Double.parseDouble(OPM.getDeliveryCharge()));

            txtSubTotal.setText(String.format(Locale.getDefault(),
                    "Total: ₹%.1f", subTot));


            String date = Utiles.getDate(Long.parseLong(OPM.getDateOfOrder().trim()));
            txtDate.setText(String.format("Date :%s", date));

            String OdTime = "Nan";
            Log.d(TAG, "setOrderData: orderTime=" + OPM.getOrderTiming().trim());
            try {
                OdTime = Utiles.convertIn12Hrs(OPM.getOrderTiming().trim());
            } catch (ParseException e) {
                Log.d(TAG, "setOrderData: e=" + e.getMessage());
                e.printStackTrace();
            }

            String status = OPM.getOrderStatus().trim();
            if (status.contains(ACCEPTED)) {
                txtMessage.setVisibility(View.VISIBLE);
                txtMessage.setTextColor(getResources().getColor(R.color.black_color));
                txtMessage.setText("Your Order Will Be delivered Soon");
                showSnackBar("On the way of delivering");
            } else if (status.contains(DELIVERED)) {
                txtMessage.setVisibility(View.VISIBLE);
                txtMessage.setTextColor(getResources().getColor(R.color.black_color));
                txtMessage.setText("Your Order was delivered");
            } else if (status.contains(CANCELLED)) {
                txtMessage.setVisibility(View.VISIBLE);
                txtMessage.setText("Your Order was Cancelled ");
                txtMessage.setTextColor(getResources().getColor(R.color.colorRad));

            } else {
                txtMessage.setVisibility(View.GONE);
            }

            txtStatus.setText(String.format("Status: %s",status));

            txtTime.setText(String.format("Time :%s", OdTime));

            myOrderAdapter = new MyOrderAdapter(OPM, MyOrderActivity.this);

            myOrderRv.setAdapter(myOrderAdapter);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                if (OPM.getOrderStatus().contains(CANCELLED)) {
                    showToast("Already Cancelled");
                    return false;
                } else if (OPM.getOrderStatus().contains(DELIVERED)) {
                    showToast("Order Delivered");
                    return false;
                }
                cancelOrder();
                break;
            case R.id.noti_menu:
                startActivity(new Intent(MyOrderActivity.this,
                        NotificationActivity.class));
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }


    private void cancelOrder() {
        showPb();
        DocumentReference orderRef
                = FirebaseFirestore.getInstance().
                collection(ORDER_PLACED_COLLEC)
                .document(oId);
        Log.d(TAG, "cancelOrder: OId=" + oId);
        OPM.setOrderStatus(CANCELLED);
        orderRef.set(OPM, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    String title = "Order Cancelled By " + OPM.getCustomerName();
                    String msg = "Order of " + txtTotal.getText().toString() +
                            " was Cancelled by\n user Name : " +
                            OPM.getCustomerName() + "\n Address: " +
                            OPM.getCustomerAddress();
                    notifyAdmin(title, msg);
                    notifyUser();
                    hidePb();
                }).addOnFailureListener(e -> {
            hidePb();
            Toast.makeText(MyOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void notifyAdmin(String title, String longMsg) {
        APIService apiService;

        apiService = Client.getClient("https://fcm.googleapis.com/").
                create(APIService.class);
        Data data = new Data(title, longMsg);
        FirebaseFirestore.getInstance().collection("veggiesApp/Veggies/adminToken")
                .document(TOKEN_DOC).get().addOnSuccessListener(
                documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                        Map<String, Object> tokenMap = documentSnapshot.getData();
                        String admintoken = tokenMap.get(TOKEN_KEY).toString();
                        Log.d(TAG, "sendNotificationToAdmin: adMinToken=" + admintoken);
                        NotificationSender sender =
                                new NotificationSender(data, admintoken);

                        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<MyResponse> call,
                                                   @NonNull Response<MyResponse> response) {
                                if (response.code() == 200) {
                                    if (response.body() != null && response.body().success != 1) {
                                        Toast.makeText(MyOrderActivity.this, "Can not Cancel Order", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onResponse: error=" + response.code());
                                        Log.d(TAG, "onResponse: response.body().success =" + response.body().success);
                                    } else if (response.body() != null && response.body().success == 1) {
                                        Toast.makeText(getApplicationContext(), "Order Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "onResponse: failed code=" + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getMessage());
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                }
        );

    }

    private void notifyUser() {
        DocumentReference userDoc
                = FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                .document(OPM.getCustomerId()).collection(USER_ORDER_COLLEC)
                .document(oId);//actual order id
        OPM.setOrderStatus(CANCELLED);
        userDoc.set(OPM, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Notification n = Utiles.buildNotification(MyOrderActivity.this,
                                        "Order Cancelled",
                                        "Your Order Has Been Cancelled",
                                        "Your order has been Cancelled");
                                notificationManager.notify(3, n);
                            }
                        }, 3000);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Notification n = Utiles.buildNotification(MyOrderActivity.this,
                                        "Can not Cancel",
                                        "Your Order can not be  Cancelled due to server error you can manually cancel by contacting the owner",
                                        "Your Order can not be  Cancelled due to server error you can manually cancel by contacting the owner");
                                notificationManager.notify(3, n);
                            }
                        }, 3000);

                    }
                });
    }


    private void showPb() {
        pb.setVisibility(View.VISIBLE);
    }

    private void hidePb() {
        pb.setVisibility(View.GONE);
    }

    private void showToast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(myOrderRv.getRootView(), message,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}