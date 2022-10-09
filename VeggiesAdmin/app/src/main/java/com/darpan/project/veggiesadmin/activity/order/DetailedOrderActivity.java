package com.darpan.project.veggiesadmin.activity.order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.firebaseModal.UserModal;
import com.darpan.project.veggiesadmin.modal.OrderDataModal;
import com.darpan.project.veggiesadmin.util.Util;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.darpan.project.veggiesadmin.constant.Constants.DELIVERED;
import static com.darpan.project.veggiesadmin.constant.Constants.ORDER_PLACED_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.PENDING;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_COLLECTION;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_ORDER_COLLEC;

public class DetailedOrderActivity extends AppCompatActivity {
    private TextView orderName, orderDesc, custAddress, orderPrice, paymentMode,
            custId, custName, orderQty, orderTime, orderDate,
            orderStatus, orderId, deliveryCharge;
    private ImageView orderImage;
    private TextView UniquePid,optionQty;

    private OrderDataModal ODM;
    private static final String TAG = "DetailedOrderActivity: ";
    private Spinner statusSpinner;
    ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_order);
        orderName = findViewById(R.id.name);
        orderQty = findViewById(R.id.order_qty);
        orderPrice = findViewById(R.id.order_price);
        orderDate = findViewById(R.id.order_date);
        deliveryCharge = findViewById(R.id.order_del_charge);
        paymentMode = findViewById(R.id.order_payment);

        orderId = findViewById(R.id.oId);
        orderStatus = findViewById(R.id.order_status);
        orderTime = findViewById(R.id.order_time);
        custAddress = findViewById(R.id.cust_address);
        custId = findViewById(R.id.cust_id);
        orderImage = findViewById(R.id.order_img);
        statusSpinner = findViewById(R.id.status_spinner);

        UniquePid=findViewById(R.id.unique_pid);
        optionQty=findViewById(R.id.txt_option_qty);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{PENDING,
                        DELIVERED});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);


        Intent intent = getIntent();
        ODM = intent.getParcelableExtra("ORDER_DATA_MODAL");

        if (ODM != null) {

            setData();
            Log.d(TAG, "onCreate: ODM ID=" + ODM.getOrderId());
        }

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) parent.getSelectedItem();
                orderStatus.setText(s);
                changeStatus(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        listenerRegistration = FirebaseFirestore.getInstance().collection(
                ORDER_PLACED_COLLEC
        ).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "onEvent: Exception=" + e.getMessage());
                    return;
                }
                if (queryDocumentSnapshots != null)
                    for (QueryDocumentSnapshot Qs : queryDocumentSnapshots) {

                        orderStatus.setText(String.format("Order Status: %s",
                                Qs.getString("orderStatus")));
                    }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
    }

    private void changeStatus(String s) {
        FirebaseFirestore.getInstance().collection(
                ORDER_PLACED_COLLEC
        ).document(ODM.getOrderId())
                .update("orderStatus", s.trim())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseFirestore.getInstance().collection(
                                USER_COLLECTION
                        ).document(ODM.getCustomerId()).collection(USER_ORDER_COLLEC).
                                document(ODM.getOrderId())
                                .update("orderStatus", s.trim());
                    }
                });

    }

    private void setData() {
        orderName.setText(String.format("Name: %s", ODM.getOrderName()));
        orderQty.setText(String.format("Description: %s", ODM.getOrderQuantity()));
        orderPrice.setText(String.format("Total Amount Rs: %s", ODM.getTotalPrice()));

        UniquePid.setText(String.format("Item Unique Id: %s",ODM.getUniquePid()));
        optionQty.setText(String.format("OptioNal Qty: %s",ODM.getOptionQty()));

        orderTime.setText(String.format("Order Time: %s",
                convertIn12Hrs(ODM.getOrderTiming().trim())));

        deliveryCharge.setText(String.format("Delivery Charge Rs: %s",
                ODM.getDeliveryCharge()));

        paymentMode.setText(String.format("Payment Mode: %s",
                ODM.getModeOfPayment()));

        orderId.setText(String.format("Order Id: %s",
                ODM.getOrderId()));

        orderStatus.setText(String.format("Order Status: %s",
                ODM.getOrderStatus()));

        orderDate.setText(String.format("Order Date: %s",
                Util.getDate(Long.parseLong(ODM.getDateOfOrder())))
        );


        custId.setText(String.format("Customer Id: %s",
                ODM.getCustomerId()));

        getAddress(ODM.getCustomerId());

        Glide.with(this)
                .load(ODM.getOrderImage())
                .error(R.drawable.empty)
                .into(orderImage);


    }

    private void getAddress(String customerId) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.show();
        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                .document(customerId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserModal UM = documentSnapshot.toObject(UserModal.class);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(UM.getFullAddress());
                        String lndMrk = UM.getLandmark();
                        String blockName = UM.getBlockName();
                        String blockNO = UM.getBlockNo();
                        String Mobile = UM.getMobile();
                        String fullAddress = "Name: " + UM.getName() + "\n" +
                                "Block Name: " + blockName + "\n" +
                                "Block No: " + blockNO + "\n" +
                                "Full Address: " + UM.getFullAddress() + "\n" +
                                "LandMark: " + lndMrk + "\n" +
                                "Mobile: " + Mobile + "\n" +
                                "PIN :" + UM.getPin();

                        custAddress.setText(fullAddress);
                        pd.dismiss();

                    }
                });
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
}