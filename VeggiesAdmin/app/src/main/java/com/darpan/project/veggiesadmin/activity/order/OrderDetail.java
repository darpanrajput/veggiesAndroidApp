package com.darpan.project.veggiesadmin.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.adapter.order.MyOrderAdapter;
import com.darpan.project.veggiesadmin.firebaseModal.OrderPlacedModal2;
import com.darpan.project.veggiesadmin.firebaseModal.UserModal;
import com.darpan.project.veggiesadmin.util.Util;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import static com.darpan.project.veggiesadmin.constant.Constants.USER_COLLEC;

public class OrderDetail extends AppCompatActivity {
    private static final String TAG = "OrderDetail:";
    private RecyclerView myOrderRv;
    private OrderPlacedModal2 OPM;
    private Intent i;
    private String oId;//actual order id not the same as that of the order id as user id
    private TextView txtSubTotal, txtDelivery,
            txtTotal, txtStatus, txtTime, txtDate, txtAddress;
    private ProgressBar pb;
    private MyOrderAdapter myOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        i = getIntent();
        OPM = i.getParcelableExtra("ORDER_PLACED_MODAL");
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
        txtAddress = findViewById(R.id.od_message);
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


            String date = Util.getDate(Long.parseLong(OPM.getDateOfOrder().trim()));
            txtDate.setText(String.format("Date :%s", date));

            String OdTime = "Nan";
            Log.d(TAG, "setOrderData: orderTime=" + OPM.getOrderTiming().trim());
            try {
                OdTime = Util.convertIn12Hrs(OPM.getOrderTiming().trim());
            } catch (Exception e) {
                Log.d(TAG, "setOrderData: e=" + e.getMessage());
                e.printStackTrace();
            }

            String status = OPM.getOrderStatus().trim();

            txtStatus.setText(String.format("Status: %s",status));

            txtTime.setText(String.format("Time :%s", OdTime));

            myOrderAdapter = new MyOrderAdapter(OPM, OrderDetail.this);

            myOrderRv.setAdapter(myOrderAdapter);

            setAddress(OPM.getCustomerId().trim());
        }

    }

    private void setAddress(String cusId){
        showPb();
        DocumentReference user
                = FirebaseFirestore
                .getInstance()
                .collection(USER_COLLEC)
                .document(cusId);

        user.get().addOnSuccessListener(documentSnapshot -> {
            String address;
            if (documentSnapshot.exists()){
                UserModal userModal=documentSnapshot.toObject(UserModal.class);
                address="Address: "+userModal.getFullAddress()+"\n"+
                        "Email: "+userModal.getEmail()+"\n"+
                        "LandMark: "+userModal.getLandmark()+"\n"+
                        "Mobile: "+userModal.getMobile()+"\n"+
                        "Block Name: "+userModal.getBlockName()+"\n"+
                        "Block Number: "+userModal.getBlockNo()+"\n"+
                        "Name: "+userModal.getName();

                txtAddress.setText(address);
                hidePb();
            }

        }).addOnFailureListener(e -> {
            hidePb();
            e.getStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showPb(){
     pb.setVisibility(View.VISIBLE);
    }
    private void hidePb(){
        pb.setVisibility(View.GONE);
    }
}