package com.darpan.project.vegies.activity.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.Utils.Utiles;
import com.darpan.project.vegies.activity.multiorder.MultiOrderSummaryActivity;
import com.darpan.project.vegies.activity.singleOrder.SingleOrderSummaryActivity;
import com.darpan.project.vegies.projectModal.OrderModal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.darpan.project.vegies.constant.Constants.DELIVERY_TIME_COLLEC;
import static com.darpan.project.vegies.constant.Constants.DELIVERY_TIME__DOC_NAME;
import static com.darpan.project.vegies.constant.Constants.GLOBAL_TIME_SLOT_KEY;
import static com.darpan.project.vegies.constant.Constants.INTENT_SOURCE;
import static com.darpan.project.vegies.constant.Constants.ITEM_PRICE_LIST;
import static com.darpan.project.vegies.constant.Constants.ITEM_QTY_LIST;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_CLASS;

public class PaymentOrderActivity extends AppCompatActivity
        implements View.OnClickListener {

    private RadioGroup rdgTime;

    private RadioGroup paymentRadioGrp;

    private TextView btnContinueOrder;
    // @BindView(R.id.img_ldate)
    private ImageView imgLdate;
    //  @BindView(R.id.txt_selectdate)
    private TextView txtSelectdate;
    // @BindView(R.id.img_rdate)
    private ImageView imgRdate;
    private int Day = 1;
    private OrderModal orderModal;
    private Intent intent;
    List<Integer> eachItemPriceList = new ArrayList<>();
    List<Integer> eachItemDemandQtyList = new ArrayList<>();
    private TextView deliveryCriteria;
    private static final String TAG = "PaymentOrderActivity:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_order);
        btnContinueOrder = findViewById(R.id.btn_continue_order);
        txtSelectdate = findViewById(R.id.txt_selectdate);
        imgRdate = findViewById(R.id.img_rdate);
        imgLdate = findViewById(R.id.img_ldate);
        paymentRadioGrp = findViewById(R.id.payment_radio_grp);
        deliveryCriteria = findViewById(R.id.txt_delivery_criteria);

        txtSelectdate.setText(String.format("Today is \n%s",
                Utiles.getDate(Utiles.getToday())));

        intent = getIntent();
        orderModal = intent.getParcelableExtra(PRODUCT_CLASS);
        eachItemDemandQtyList = intent.getIntegerArrayListExtra(ITEM_QTY_LIST);
        eachItemPriceList = intent.getIntegerArrayListExtra(ITEM_PRICE_LIST);

        setClicks();
        setPaymentList();
        getDeliveryCriteriaTime();

    }


    private void setClicks() {
        btnContinueOrder.setOnClickListener(this);
        imgRdate.setOnClickListener(this);
        imgLdate.setOnClickListener(this);

    }

    private void setPaymentList() {
//        String[] paymen = {"Pickup myself", "Cash on delivery", "Pay with online"};
        String[] paymen = {getResources().getString(R.string.pic_myslf),
                getResources().getString(R.string.cash_delivery)};
        /* getResources().getString(R.string.pay_online)*/
        RadioButton rdbtn = null;
        for (int i = 0; i < 2; i++) {
            rdbtn = new RadioButton(this);
            rdbtn.setId(View.generateViewId());
            rdbtn.setText(paymen[i]);
            rdbtn.setOnClickListener(this);
            paymentRadioGrp.addView(rdbtn);
        }
        paymentRadioGrp.check(rdbtn.getId());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_ldate:
                minusDate(txtSelectdate.getText().toString());
                break;
            case R.id.img_rdate:
                String Rdate = addDate(txtSelectdate.getText().toString()) + " ";
                txtSelectdate.setText(Rdate);
                break;

            case R.id.btn_continue_order:
                placeOrder();
                break;
        }


    }

    private void placeOrder() {


        /* int selectedId = rdgTime.getCheckedRadioButtonId();
                RadioButton selectTime = rdgTime.findViewById(selectedId);*/
        int selectedId2 = paymentRadioGrp.getCheckedRadioButtonId();
        RadioButton selectedPayment = paymentRadioGrp.findViewById(selectedId2);
        Intent intent1 = new Intent(PaymentOrderActivity.this,
                SingleOrderSummaryActivity.class);
        Intent intent2 = new Intent(PaymentOrderActivity.this,
                MultiOrderSummaryActivity.class);

        if (orderModal != null) {

            if (intent.getStringExtra(INTENT_SOURCE).equals("FROM_CART_ACTIVITY")) {
                //it is multi order
                intent2.putExtra(PRODUCT_CLASS, orderModal);
                intent2.putIntegerArrayListExtra(ITEM_PRICE_LIST, (ArrayList<Integer>) eachItemPriceList);
                intent2.putIntegerArrayListExtra(ITEM_QTY_LIST, (ArrayList<Integer>) eachItemDemandQtyList);
                orderModal.setOrderDate(txtSelectdate.getText().toString());
                orderModal.setOrderMode(selectedPayment.getText().toString());
               /* Log.d(TAG, "placeOrder: eachItemPriceList 0=" + eachItemPriceList.get(0));
                Log.d(TAG, "placeOrder: eachItemPriceList 1=" + eachItemPriceList.get(1));
                Log.d(TAG, "placeOrder: eachItemDemandQtyList 0=" + eachItemDemandQtyList.get(0));
                Log.d(TAG, "placeOrder: eachItemDemandQtyList 1=" + eachItemDemandQtyList.get(1));

                Log.d(TAG, "placeOrder: getProductName 0=" + orderModal.getProductModalForeSales().get(0).getProductName());
                Log.d(TAG, "placeOrder: getProductName 1=" + orderModal.getProductModalForeSales().get(1).getProductName());
*/
                startActivity(intent2);
            } else {
                //it is single order
                intent1.putExtra(PRODUCT_CLASS, orderModal);
                orderModal.setOrderDate(txtSelectdate.getText().toString());
                orderModal.setOrderMode(selectedPayment.getText().toString());
                startActivity(intent1);
            }

        } else {
            Toast.makeText(this, "Null Modal", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "placeOrder: Null MOdal");
        }


    }

    private void getDeliveryCriteriaTime() {
        DocumentReference delTimeRef = FirebaseFirestore.getInstance().
                collection(DELIVERY_TIME_COLLEC).document(DELIVERY_TIME__DOC_NAME);
        delTimeRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ds) {
                String DT = ds.get(GLOBAL_TIME_SLOT_KEY).toString().trim();
                String startTime = DT.split("-")[0].trim();
                String EndTime = DT.split("-")[1].trim();
                try {
                    deliveryCriteria.setText(String.format("Order Will Accepted between \n %s-%s",
                            Utiles.convertIn12Hrs(startTime), Utiles.convertIn12Hrs(EndTime)));
                    if (!Utiles.compare(startTime, EndTime)) {
                        btnContinueOrder.setText(getString(R.string.del_time_exceeds));
                        btnContinueOrder.setEnabled(false);
                        Toast.makeText(PaymentOrderActivity.this,
                                getString(R.string.del_time_exceeds),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private String getCurrentDate() {
        Date d = Calendar.getInstance().getTime();
        System.out.println("Current time => " + d);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(d);
        try {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, Day);  // number of days to add
            formattedDate = df.format(c.getTime());
            c.setTime(df.parse(formattedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    private String addDate(String dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date strDate = null;
        try {
            strDate = sdf.parse(dt);
            if ((System.currentTimeMillis() + 432000000) < strDate.getTime()) {
                Log.e("date change ", "--> 1");
                return dt;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, Day);  // number of days to add
            dt = sdf.format(c.getTime());
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Day++;
        return dt;
    }

    private String minusDate(String dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date strDate = null;
        try {
            strDate = sdf.parse(dt);
            if ((System.currentTimeMillis() + 86400000) > strDate.getTime()) {
                Log.e("date change ", "--> 1");
                return dt;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Day--;
        try {

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, Day);  // number of days to add
            dt = sdf.format(c.getTime());
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtSelectdate.setText("" + dt);
        return dt;
    }
}