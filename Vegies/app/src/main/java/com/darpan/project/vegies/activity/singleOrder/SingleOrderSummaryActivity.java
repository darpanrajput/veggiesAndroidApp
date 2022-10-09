package com.darpan.project.vegies.activity.singleOrder;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.Utils.Utiles;
import com.darpan.project.vegies.activity.profile.ProfileActivity;
import com.darpan.project.vegies.fcm.APIService;
import com.darpan.project.vegies.fcm.Client;
import com.darpan.project.vegies.fcm.Data;
import com.darpan.project.vegies.fcm.MyResponse;
import com.darpan.project.vegies.fcm.NotificationSender;
import com.darpan.project.vegies.fcm.Token;
import com.darpan.project.vegies.firebaseModal.OrderPlacedModal;
import com.darpan.project.vegies.firebaseModal.UserModal;
import com.darpan.project.vegies.projectModal.OrderModal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.darpan.project.vegies.constant.Constants.AREA_COLLEC;
import static com.darpan.project.vegies.constant.Constants.AREA_NAME;
import static com.darpan.project.vegies.constant.Constants.DEFAULT;
import static com.darpan.project.vegies.constant.Constants.DELIVERY_CHARGE;
import static com.darpan.project.vegies.constant.Constants.ORDER_PLACED_COLLEC;
import static com.darpan.project.vegies.constant.Constants.O_F_S;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_CLASS;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_LIST_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_NAME;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_QUANTITY;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_STOCK;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_UNIT;
import static com.darpan.project.vegies.constant.Constants.SP_AREA;
import static com.darpan.project.vegies.constant.Constants.TOKEN_DOC;
import static com.darpan.project.vegies.constant.Constants.TOKEN_KEY;
import static com.darpan.project.vegies.constant.Constants.UNIQUE_PID;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.USER_ORDER_COLLEC;
import static com.darpan.project.vegies.constant.Constants.USER_TOKEN_COLLEC;
import static com.darpan.project.vegies.constant.Constants.u_fullAddress;

public class SingleOrderSummaryActivity extends AppCompatActivity
        implements View.OnClickListener {
    private OrderModal orderModal;
    private Intent intent;
    private static final String TAG = "SingleOrderSummary:";
    private TextView changeAddress, txtTitle, txtPriceWithQty, totalPrice, txtGrandTotal,
            txtDeliveryCharge, txtSubTotal, txtPlaceOrderTot, txtSorryOutOfStock, Address;
    private ImageView itemImage;
    private LinearLayout placeOrderLl;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference itemRef = firebaseFirestore.collection(PRODUCT_LIST_COLLECTION);
    private CollectionReference areaRef = firebaseFirestore.collection(AREA_COLLEC);
    private CollectionReference orderPlacedRef = firebaseFirestore.collection(ORDER_PLACED_COLLEC);
    private CollectionReference userRef = firebaseFirestore.collection(USER_COLLECTION);

    private String itemId;
    private ProgressBar pb;
    private SharedPreferences sp;
    private String deliveryCharge = "0";
    private Map<String, Object> itemMap, orderMap;
    private NotificationManagerCompat notificationManager;
    private FirebaseUser firebaseUser;
    private String Uid;
    private int Grandtotal;
    private ProgressDialog pd;
    private APIService apiService;
    private RelativeLayout relativeLayout;
    private LottieAnimationView orderCompletedAnimation, lottiConnectivity;
    private String actualItemQty, RequiredQty, actualUnit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order_summary);
        intent = getIntent();
        notificationManager = NotificationManagerCompat.from(this);
        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        orderModal = intent.getParcelableExtra(PRODUCT_CLASS);
        orderMap = new HashMap<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        pd = new ProgressDialog(this);
        if (firebaseUser != null) {
            Uid = firebaseUser.getUid();
        }
        findViews();
        if (orderModal != null) {
            itemId = orderModal.getProductModalForeSales().get(0).getProductId();
            Log.d(TAG, "Name: " + orderModal.getProductModalForeSales().get(0).getProductName());
            Log.d(TAG, "ID:" + orderModal.getProductModalForeSales().get(0).getProductId());
            Log.d(TAG, "Date: " + orderModal.getOrderDate());
            Log.d(TAG, "TotalAmount: " + orderModal.getTotalAmount());
            Log.d(TAG, "OrderMode: " + orderModal.getOrderMode());
            Log.d(TAG, "TotalItemCount: " + orderModal.getTotalItemCount());

            setOrderModalData();
            checkOTSfromDatabase();

        } else {
            showToast("Null Modal");
            finish();
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "getInstanceId failed", task.getException());
                        return;
                    }
                    if (task.getResult() != null)

                        updateToken(task.getResult().getToken());
                });

    }


    private void updateToken(String refreshToken) {
        Token token1 = new Token(refreshToken);
        Map<String, Object> tokenMAp = new HashMap<>();
        tokenMAp.put(TOKEN_KEY, token1.getToken());
        Log.d(TAG, "updateToken: " + token1.getToken());
        String id = firebaseUser.getUid();
        FirebaseFirestore.getInstance()
                .collection(USER_COLLECTION)
                .document(id).collection(USER_TOKEN_COLLEC)
                .document(TOKEN_DOC).set(tokenMAp);

    }

    private void findViews() {
        relativeLayout = findViewById(R.id.order_completed_lotti_rl);
        relativeLayout.setVisibility(View.GONE);
        orderCompletedAnimation = relativeLayout.findViewById(R.id.animationView);
        /* lottiConnectivity=findViewById(R.id.)*/
        changeAddress = findViewById(R.id.txt_change_address);
        Address = findViewById(R.id.txt_address);
        txtTitle = findViewById(R.id.txt_name);
        itemImage = findViewById(R.id.item_image);
        txtPriceWithQty = findViewById(R.id.txt_price_and_item);
        totalPrice = findViewById(R.id.txt_total_item_price);
        txtDeliveryCharge = findViewById(R.id.txt_delivery_charge);
        txtSubTotal = findViewById(R.id.txt_item_subtotal);
        txtGrandTotal = findViewById(R.id.txt_grand_total);
        placeOrderLl = findViewById(R.id.place_order_ll);
        txtPlaceOrderTot = findViewById(R.id.txt_place_order_total);
        txtSorryOutOfStock = findViewById(R.id.txt_sorry_out_of_stock);
        pb = findViewById(R.id.progress_circular_bar);
        setClicks();
        setUserAddress();

    }

    private void setUserAddress() {
        userRef.document(Uid)
                .get().addOnSuccessListener(documentSnapshot ->
                Address.setText(documentSnapshot.getString(u_fullAddress)));
    }

    private void animate() {
        Button btn = relativeLayout.findViewById(R.id.refresh_btn);
        orderCompletedAnimation.setRepeatCount(3);
        Log.d(TAG, "animate: called");
        if (relativeLayout.getVisibility() == View.GONE)
            relativeLayout.setVisibility(View.VISIBLE);

        /*hiding in this activity no need*/
        if (btn.getVisibility() == View.VISIBLE)
            btn.setVisibility(View.GONE);

    }

    private void unAnimate() {
        Log.d(TAG, "unAnimate: called");
        if (relativeLayout.getVisibility() == View.VISIBLE)
            relativeLayout.setVisibility(View.GONE);
        txtPlaceOrderTot.setEnabled(false);
        placeOrderLl.setEnabled(false);
        txtPlaceOrderTot.setText("Order Completed");
    }

    private void setClicks() {
        changeAddress.setOnClickListener(this);
        placeOrderLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_change_address:
                startActivity(new Intent(SingleOrderSummaryActivity.this,
                        ProfileActivity.class));
                break;

            case R.id.place_order_ll:
                if (sp.getString(SP_AREA,DEFAULT).equals(DEFAULT)) {
                    Toast.makeText(this, "Please select your Profile Area", Toast.LENGTH_SHORT).show();
                    placeOrderLl.setVisibility(View.GONE);
                } else {
                    createOrder();

                }
                break;

        }

    }

    private void setOrderModalData() {

        txtTitle.setText(orderModal.getProductModalForeSales().get(0).getProductName());
        String imgUrl = orderModal.getProductModalForeSales().get(0).getProductImage();
        Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.ezgifresize)
                .error(R.drawable.empty)
                .into(itemImage);

      /*  int disOrCostPrice = (int) getDiscountPrice(
                orderModal.getProductModalForeSales().get(0).getProductDiscount(),
                orderModal.getProductModalForeSales().get(0).getProductPrice());
*/
        int disOrCostPrice = orderModal.getTotalAmount();

        String Price_and_QTY = orderModal.getTotalItemCount() + " item(s) At Cost Of " +
                getString(R.string.currency) +
                disOrCostPrice;


        txtPriceWithQty.setText(Price_and_QTY);
        int productTot = disOrCostPrice * orderModal.getTotalItemCount();
        String productTotStr = getString(R.string.currency) + productTot;

        totalPrice.setText(productTotStr);

        txtSubTotal.setText(productTotStr);
        txtDeliveryCharge.setText(String.format("₹%s", 0));
        Grandtotal = getDeliveryCharge() + orderModal.getTotalAmount();
        txtGrandTotal.setText(getRupee(String.valueOf(Grandtotal)));

        txtPlaceOrderTot.setText(R.string.place_order);
    }

    private void checkOTSfromDatabase() {
        showPB();
        //OTS=Out of Stock:
        Log.d(TAG, "checkOTSfromDatabase: itemId=" + itemId.trim());
        itemRef.document(itemId.trim())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    itemMap = documentSnapshot.getData();


                    if ((long) itemMap.get(PRODUCT_STOCK) < orderModal.getTotalItemCount()
                            || documentSnapshot.getString("productStatus").equalsIgnoreCase(O_F_S)) {
                        /*the product is not available*/
                        Log.d(TAG, "itemMap.get(PRODUCT_STOCK)" + itemMap.get(PRODUCT_STOCK));
                        Log.d(TAG, "TotalItemCount(): " + orderModal.getTotalItemCount());
                        txtSorryOutOfStock.setText(R.string.OFS);//out of Stock
                        placeOrderLl.setBackgroundResource(R.drawable.bg_out_of_stock_btn);
                        placeOrderLl.setEnabled(false);
                        txtPlaceOrderTot.setText(String.format("Only %s Left",
                                itemMap.get(PRODUCT_STOCK)));

                    } else {
                        /*else the product is available*/

                        setDeliveryCharge(sp.getString(SP_AREA, DEFAULT));
                        setStockAvailableData();

                    }
                } else {
                    showToast("No Data Found");
                    placeOrderLl.setBackgroundResource(R.drawable.bg_out_of_stock_btn);
                    placeOrderLl.setEnabled(false);
                    txtPlaceOrderTot.setText("Can Not Order Right Now");
                }
                hidePB();

            }
        }).addOnFailureListener(e -> {
            hidePB();
            showToast(e.getMessage());
        });

    }


    private void createOrder() {

        showPd("Creating Order..");
        userRef.document(Uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModal userModal = documentSnapshot.toObject(UserModal.class);
                        if (userModal != null)
                            // saveUserOrder(userModal);
                            createEmptyCollect(userModal);
                        hidePd();
                    } else {
                        hidePd();
                        showToast("No User Found");

                    }
                }).addOnFailureListener(e -> {
            showToast(e.getMessage());
            hidePd();
        });

    }

    private void setConnectivityLottie() {
        if (relativeLayout.getVisibility() == View.GONE) {
            relativeLayout.setVisibility(View.VISIBLE);

        }
        orderCompletedAnimation.setAnimation(R.raw.lottie_no_internet);
        Button btn = relativeLayout.findViewById(R.id.refresh_btn);
        /*hiding in this activity no need*/
        btn.setVisibility(View.GONE);
    }

    private void createEmptyCollect(UserModal Um) {

        /*creating empty collection to get the id of he collection*/
       String id= userRef.document(Uid)
                .collection(USER_ORDER_COLLEC).document().getId();
        saveUserOrder(Um,id);

        //also Asynchronously decrease the stock qty by 1

        Map<String, Object> updatedStockMap = new HashMap<>();
        updatedStockMap.put("stockQuantity",
                (long) itemMap.get("stockQuantity") - 1);
        itemRef.document(itemMap.get("productId").toString())
                .update(updatedStockMap);


    }


    private void saveUserOrder(UserModal Um, String emptyOrderId) {
        actualItemQty = itemMap.get(PRODUCT_QUANTITY).toString() + "";
        actualUnit = itemMap.get(PRODUCT_UNIT).toString();
        RequiredQty = Integer.toString(orderModal.getTotalItemCount());

        String orderPlaceQty = RequiredQty + " , " + actualItemQty + " " + actualUnit + " of " +
                orderModal.getProductModalForeSales().get(0).getProductName() + " Each";

        showPd("Saving order..");


        OrderPlacedModal o=new OrderPlacedModal();
        String[] uniqueid ={itemMap.get(UNIQUE_PID).toString().trim()};
        o.setUniquePid(Arrays.asList(uniqueid));
        String[]optionqty={orderModal.getTotalItemCount()+ ""};
        o.setOptionQty(Arrays.asList(optionqty));
        o.setCustomerAddress(Um.getFullAddress());
        o.setCustomerId(Uid);
        o.setCustomerName(Um.getName());
        o.setDateOfOrder(Long.toString(Utiles.getToday()));
        o.setDeliverTimeSlot(Long.toString(Utiles.getToday()));
        o.setDeliveryCharge(deliveryCharge);
        o.setModeOfPayment(orderModal.getOrderMode());
        o.setOrderId(emptyOrderId);

        String[] images={orderModal.getProductModalForeSales().get(0).getProductImage()};
        o.setOrderImage(Arrays.asList(images));
        String[] name={itemMap.get(PRODUCT_NAME).toString().trim()};
        o.setOrderName(Arrays.asList(name));

        String[]orderqty={orderPlaceQty};
        o.setOrderQuantity(Arrays.asList(orderqty));
        o.setOrderStatus("Pending");
        o.setOrderTiming(Utiles.getCurrentTime());
        o.setTotalPrice(Integer.toString(Grandtotal));
        o.setTotalItem("1");

        int val=orderModal.getTotalItemCount()
                *orderModal.getProductModalForeSales().get(0).getProductQuantity();
        //1.0 kg of APPLE WASHINGTON with total Rs of ₹50

        String[] orderdesc={+val+" "+
                orderModal.getProductModalForeSales().get(0).getProductUnit()+
        " of "+orderModal.getProductModalForeSales().get(0).getProductName()+
        " with total Rs of ₹"+Grandtotal};
        o.setOrderDescription(Arrays.asList(orderdesc));

         userRef.document(Uid)
                .collection(USER_ORDER_COLLEC)
                .document(emptyOrderId)
                .set(o, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    savedOrderForAdmin(Um, emptyOrderId);
                    hidePd();

                }).addOnFailureListener(e -> showToast("Order Failed : " + e.getMessage()));

    }

    private void savedOrderForAdmin(UserModal Um, String docId) {
        showPd("Ordering pending..");
        actualItemQty = itemMap.get(PRODUCT_QUANTITY).toString() + "";
        actualUnit = itemMap.get(PRODUCT_UNIT).toString();
        RequiredQty = Integer.toString(orderModal.getTotalItemCount());

        String orderPlaceQty = RequiredQty + " , " + actualItemQty + " " + actualUnit + " of " +
                orderModal.getProductModalForeSales().get(0).getProductName() + " Each";


        OrderPlacedModal o=new OrderPlacedModal();
        String[] uniqueid ={itemMap.get(UNIQUE_PID).toString().trim()};
        o.setUniquePid(Arrays.asList(uniqueid));
        String[]optionqty={orderModal.getTotalItemCount()+""};

        o.setOptionQty(Arrays.asList(optionqty));
        o.setCustomerAddress(Um.getFullAddress());
        o.setCustomerId(Uid);
        o.setCustomerName(Um.getName());
        o.setDateOfOrder(Long.toString(Utiles.getToday()));
        o.setDeliverTimeSlot(Long.toString(Utiles.getToday()));
        o.setDeliveryCharge(deliveryCharge);
        o.setModeOfPayment(orderModal.getOrderMode());
        o.setOrderId(docId);

        String[] images={orderModal.getProductModalForeSales().get(0).getProductImage()};
        o.setOrderImage(Arrays.asList(images));
        String[] name={itemMap.get(PRODUCT_NAME).toString().trim()};
        o.setOrderName(Arrays.asList(name));

        String[]orderqty={orderPlaceQty};
        o.setOrderQuantity(Arrays.asList(orderqty));
        o.setOrderStatus("Pending");
        o.setOrderTiming(Utiles.getCurrentTime());
        o.setTotalPrice(Integer.toString(Grandtotal));
        o.setTotalItem("1");

        int val=orderModal.getTotalItemCount()
                *orderModal.getProductModalForeSales().get(0).getProductQuantity();


        String[] orderdesc={+val+" "+
                orderModal.getProductModalForeSales().get(0).getProductUnit()+
                " of "+orderModal.getProductModalForeSales().get(0).getProductName()+
                " with total Rs of ₹"+Grandtotal};
        o.setOrderDescription(Arrays.asList(orderdesc));


        orderPlacedRef
                .document(docId)
                .set(o, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    hidePd();
                    /*show lottie*/

                    showCustomDialog();

                    txtPlaceOrderTot.setText(String.format("Order Again Rs-%s", Grandtotal));
                    String lonMsg = String.format("Your order of " +
                                    "%s Rs has been created and it will soon be accepted"
                            , Grandtotal);

                    new Handler().postDelayed(() -> {
                        /*hide lottie after 3 sec*/
                        NotificationBuild(getResources().getString(R.string.order_title),
                                getResources().getString(R.string.order_short_meg),
                                lonMsg);
                    }, 3000);


                }).addOnFailureListener(e -> {
            hidePd();
            showToast(e.getMessage());
        });

        /*  *//*change the order id field when admin accept it as
         * it is currently the same as the user id of that user*//*
                            current iam not changing it because i can get
                            order id through loop by using user id
                            each user may have multiple order and hence order id*/

    }


    private void showPB() {
        pb.setVisibility(View.VISIBLE);

    }

    private void hidePB() {
        pb.setVisibility(View.GONE);

    }

    private void showPd(String title) {
        pd.setTitle(title);
        pd.show();
    }

    private void hidePd() {
        pd.cancel();
    }

    private void showToast(String msg) {
        Toast.makeText(SingleOrderSummaryActivity.this, msg, Toast.LENGTH_SHORT).show();

    }


    private void setStockAvailableData() {
        txtSorryOutOfStock.setText("Stock Available");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtSorryOutOfStock.setTextColor(getColor(R.color.black_color));
        } else {
            txtSorryOutOfStock.setTextColor(getResources().getColor(R.color.black_color));
        }

        placeOrderLl.setEnabled(true);
        placeOrderLl.setBackgroundResource(R.drawable.rounded_green2);


        txtSubTotal.setText(getRupee(String.valueOf(orderModal.getTotalAmount())));
        totalPrice.setText(getRupee(String.valueOf(orderModal.getTotalAmount())));
        txtDeliveryCharge.setText(String.format("₹%s", getDeliveryCharge()));

        Grandtotal = orderModal.getTotalAmount() + getDeliveryCharge();
        Log.d(TAG, "setStockAvailableData: grandTotal int=" + Grandtotal);
        txtGrandTotal.setText(getRupee(String.valueOf(Grandtotal)));

        txtPlaceOrderTot.setText(String.format("Place order -Rs %s",
                Grandtotal));


    }

    private double getDiscountPrice(int discount, int actualPrice) {
        if (discount > 0) {
            double result = (discount / 100.0f) * actualPrice;
            Log.d(TAG, "getDiscountPrice: " + (actualPrice - result));
            return actualPrice - result;

        } else {
            return (double) actualPrice;
        }
    }

    private String getRupee(String s) {
        return "₹" + s;
    }


    private void setDeliveryCharge(String areaName) {
        showPB();
        Log.d(TAG, "setDeliveryCharge: area" + areaName);
        if (!areaName.equals(DEFAULT)) {
            //getting into area collection for the delivery price
            areaRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot qs : queryDocumentSnapshots) {
                        if (qs.get(AREA_NAME) != null && qs.get(AREA_NAME).toString().trim().equals(areaName)) {
                            deliveryCharge = qs.get(DELIVERY_CHARGE).toString().trim();
                            Log.d(TAG, "onSuccess: AREA=" + qs.get(AREA_NAME));
                            Log.d(TAG, "onSuccess: delivery=" + qs.get(DELIVERY_CHARGE));
                            Log.d(TAG, "onSuccess: deliveryCharge=" + deliveryCharge);
                        }
                    }

                    txtDeliveryCharge.setText(String.format("₹%s", getDeliveryCharge()));
                    Grandtotal = orderModal.getTotalAmount() + getDeliveryCharge();
                    Log.d(TAG, "setDeliveryCharge: grandTotal int=" + Grandtotal);
                    txtGrandTotal.setText(getRupee(String.valueOf(Grandtotal)));
                    txtPlaceOrderTot.setText(String.format("Place Order - %s",
                            getRupee(String.valueOf(Grandtotal))));

                }
                hidePB();
            }).addOnFailureListener(e -> {
                hidePB();
                showToast(e.getMessage());
                Log.d(TAG, "onFailure: " + e.getMessage());
                e.getMessage();
            });
        } else {
            placeOrderLl.setEnabled(false);
            Toast.makeText(getApplicationContext(),
                    "wrong Area : " + areaName + " Please set Profile Area",
                    Toast.LENGTH_LONG).show();

        }
    }

    private int getDeliveryCharge() {
        Log.d(TAG, "getDeliveryCharge: " + deliveryCharge);
        return Integer.parseInt(deliveryCharge);
    }


    private void NotificationBuild(String title, String msg, String longMsg) {


        Notification n = Utiles.buildNotification(this, title,
                msg, longMsg);

        notificationManager.notify(1, n);

        String adminTitle = "New Request";
        String adminMsg = "New Order Request Of " + orderModal.getTotalItemCount() + " "
                + orderModal.getProductModalForeSales().get(0).getProductName() + " , " +
                orderModal.getProductModalForeSales().get(0).getProductQuantity() + " " +
                orderModal.getProductModalForeSales().get(0).getProductUnit() +
                "  each has been Created";

        //New Order Request Of 1 dairy milk ,250 ml each  has been Created
        //New Order Request Of 2 brinjal  ,1 kg each  has been Created
        //New Order Request Of 3 All Stationary pack ,20 pcs each  has been Created


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                sendNotificationToAdmin(adminTitle, adminMsg);
            }
        }, 2000);


    }

    private void sendNotificationToAdmin(String title, String LongMSG) {
        unAnimate();
        apiService = Client.getClient("https://fcm.googleapis.com/").
                create(APIService.class);
        Data data = new Data(title, LongMSG);
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
                                        Notification nt = Utiles.buildNotification(
                                                SingleOrderSummaryActivity.this,
                                                "Order Not Created",
                                                "Order Has not been Received yet",
                                                "Your Order Has not been Received yet Please Check in order" +
                                                        " list if there is no order then order it Again and if" +
                                                        " it is present then no need to order again");

                                        notificationManager.notify(2, nt);//it will not override the
                                        //same notification with same Channel id
                                        Log.d(TAG, "onResponse: error=" + response.code());
                                        Log.d(TAG, "onResponse: response.body().success =" + response.body().success);
                                    } else {
                                        Log.d(TAG, "onResponse: Null");
                                        Log.d(TAG, "onResponse: response.body().success =" + response.body().success);

                                    }
                                } else {
                                    Log.d(TAG, "onResponse: failed code=" + response.code());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<MyResponse> call, Throwable t) {
                                t.getStackTrace();
                                Notification nt = Utiles.buildNotification(
                                        SingleOrderSummaryActivity.this,
                                        "Order Not Created",
                                        "Order Has not been Received yet",
                                        "Your Order Has not been Received yet Please Check in order" +
                                                "list if there is no order then order it Again");

                                notificationManager.notify(2, nt);//it will not override the
                                //same notification with same Channel id
                                Log.d(TAG, "onFailure: " + t.getMessage());

                            }
                        });
                    }
                }
        );


    }
    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.my_dialog, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        dialogView.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                animate();
            }
        });
    }

}

