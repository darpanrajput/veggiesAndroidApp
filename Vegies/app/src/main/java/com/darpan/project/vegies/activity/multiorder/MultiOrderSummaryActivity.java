package com.darpan.project.vegies.activity.multiorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import com.google.firebase.iid.FirebaseInstanceId;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.Utils.Utiles;
import com.darpan.project.vegies.activity.MainActivity;
import com.darpan.project.vegies.activity.profile.ProfileActivity;

import com.darpan.project.vegies.adapters.category.multiorder.MultiOrderAdapter2;
import com.darpan.project.vegies.fcm.APIService;
import com.darpan.project.vegies.fcm.Client;
import com.darpan.project.vegies.fcm.Data;
import com.darpan.project.vegies.fcm.MyResponse;
import com.darpan.project.vegies.fcm.NotificationSender;
import com.darpan.project.vegies.fcm.Token;
import com.darpan.project.vegies.firebaseModal.OrderPlacedModal;
import com.darpan.project.vegies.firebaseModal.ProductModalForeSale;
import com.darpan.project.vegies.firebaseModal.UserModal;
import com.darpan.project.vegies.projectModal.MultiOrder;
import com.darpan.project.vegies.projectModal.OrderModal;
import com.darpan.project.vegies.roomdatabase.dao.ProductDao;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;
import com.darpan.project.vegies.roomdatabase.repo.ProductRepo;
import com.darpan.project.vegies.roomdatabase.viewModal.ProductViewModal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.darpan.project.vegies.constant.Constants.AREA_COLLEC;
import static com.darpan.project.vegies.constant.Constants.AREA_NAME;
import static com.darpan.project.vegies.constant.Constants.DEFAULT;
import static com.darpan.project.vegies.constant.Constants.DELIVERY_CHARGE;
import static com.darpan.project.vegies.constant.Constants.ITEM_PRICE_LIST;
import static com.darpan.project.vegies.constant.Constants.ITEM_QTY_LIST;
import static com.darpan.project.vegies.constant.Constants.ORDER_PLACED_COLLEC;
import static com.darpan.project.vegies.constant.Constants.O_F_S;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_CLASS;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_LIST_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.SP_AREA;
import static com.darpan.project.vegies.constant.Constants.TOKEN_DOC;
import static com.darpan.project.vegies.constant.Constants.TOKEN_KEY;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.USER_ORDER_COLLEC;
import static com.darpan.project.vegies.constant.Constants.USER_TOKEN_COLLEC;

public class MultiOrderSummaryActivity extends AppCompatActivity
        implements View.OnClickListener {
    private ProductViewModal productViewModal;
    private ProductRepo productRepo;
    private static final String TAG = "MultiOrderActivity:";

    // private MultiOrderAdapter multiOrderAdapter;
    private MultiOrderAdapter2 multiOrderAdapter2;
    private Intent intent;

    private TextView changeAddress, txtGrandTotal,
            txtDeliveryCharge, txtSubTotal, txtPlaceOrderTot, txtcostToBeDeducted,
            txtAddress;

    private LinearLayout placeOrderLl, deductedLl;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference itemRef = firebaseFirestore.collection(PRODUCT_LIST_COLLECTION);
    private CollectionReference areaRef = firebaseFirestore.collection(AREA_COLLEC);
    private CollectionReference userRef = firebaseFirestore.collection(USER_COLLECTION);
    private CollectionReference orderPlacedRef = firebaseFirestore.collection(ORDER_PLACED_COLLEC);

    private String Uid;
    private ProgressBar pb;
    private SharedPreferences sp;
    private String deliveryCharge = "0";

    private OrderModal orderModal;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private MultiOrder multiOrderClass;
    private double grandTotal, deductedAmount;
    private ProgressDialog pd;
    private RelativeLayout lottieLytRl;
    private LottieAnimationView orderCompletedAnimation;
    private APIService apiService;
    private NotificationManagerCompat notificationManager;
    private List<String> actualItemQty = new ArrayList<>(),
            RequiredQty = new ArrayList<>(),
            actualUnit = new ArrayList<>();

    private List<String> orderIdList;
    private List<ProductTable> productTableList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_order_summary);
        productRepo = new ProductRepo(getApplicationContext());
        productViewModal = ViewModelProviders.of(this)
                .get(ProductViewModal.class);
        intent = getIntent();
        orderModal = intent.getParcelableExtra(PRODUCT_CLASS);
        notificationManager = NotificationManagerCompat.from(this);
        productRepo = new ProductRepo(this);
        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pd = new ProgressDialog(this);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Uid = firebaseUser.getUid();
        }

        try {
            productTableList.addAll(productRepo.getAllProducts());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        findViews();
        /*update token*/
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
        String id = Uid;
        FirebaseFirestore.getInstance()
                .collection(USER_COLLECTION)
                .document(id).collection(USER_TOKEN_COLLEC)
                .document(TOKEN_DOC).set(tokenMAp);

    }

    private void findViews() {
        lottieLytRl = findViewById(R.id.order_completed_lotti_rl);
        lottieLytRl.setVisibility(View.GONE);
        orderCompletedAnimation = lottieLytRl.findViewById(R.id.animationView);
        changeAddress = findViewById(R.id.txt_change_address);
        txtAddress = findViewById(R.id.txt_address);
        txtDeliveryCharge = findViewById(R.id.txt_delivery_charge);
        txtSubTotal = findViewById(R.id.txt_item_subtotal);
        txtGrandTotal = findViewById(R.id.txt_grand_total);
        placeOrderLl = findViewById(R.id.place_order_ll);
        txtPlaceOrderTot = findViewById(R.id.txt_place_order_total);
        pb = findViewById(R.id.pb);
        deductedLl = findViewById(R.id.deducted_ll);
        txtcostToBeDeducted = findViewById(R.id.txt_deducted_charge);
        recyclerView = findViewById(R.id.multi_order_rv);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);


        setClicks();
        setOrderData();
        setAddress();
    }

    private void setAddress() {
        userRef.document(Uid).get()
                .addOnSuccessListener(ds -> {
                    if (ds.exists()) {
                        UserModal o = ds.toObject(UserModal.class);
                        txtAddress.setText(o.getFullAddress());
                    }

                });
    }


    private void setClicks() {
        changeAddress.setOnClickListener(this);
        placeOrderLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_change_address:
                startActivity(new Intent(MultiOrderSummaryActivity.this,
                        ProfileActivity.class));
                break;

            case R.id.place_order_ll:
                if (sp.getString(SP_AREA, DEFAULT).equals(DEFAULT)) {
                    Toast.makeText(this, "Please select your Profile Area", Toast.LENGTH_SHORT).show();
                    placeOrderLl.setVisibility(View.GONE);
                } else {
                    createOrder();

                }

                break;

        }
    }

    private void setOrderData() {
        showPb();
        List<ProductModalForeSale> pms = new ArrayList<>();
        Database d = new Database(productRepo);
        orderIdList = new ArrayList<>();
        txtSubTotal.setText(getRupee(String.valueOf(d.getTotalPrice())));
        if (d.getDocsRef() != null) {
            firebaseFirestore.runTransaction(new Transaction.Function<List<ProductModalForeSale>>() {
                @Override
                public List<ProductModalForeSale> apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    for (DocumentReference doc : d.getDocsRef()) {
                        //checking of out of stock
                        DocumentSnapshot ds = transaction.get(doc);
                        //all reads handling
                        ProductModalForeSale p = ds.toObject(ProductModalForeSale.class);
                        ProductTable pt = d.getAProduct(doc.getId());
                        if (ds.getString("productStatus").equalsIgnoreCase(O_F_S)
                                || ds.getLong("stockQuantity") < 2) {
                            /*transaction.update(doc,"productStatus", O_F_S);*/
                            p.setProductStatus(O_F_S);
                            if (pt != null) {
                                pt.setProductStatus(O_F_S);
                                productRepo.update(pt);
                            }
                        }
                        //updating the local Sqlite data

                        pms.add(p);
                    }

                    return pms;
                }
            }).addOnSuccessListener(productModalForeSales -> {
                hidePb();
                multiOrderAdapter2 =
                        new MultiOrderAdapter2(productTableList,
                                productModalForeSales, MultiOrderSummaryActivity.this);
                recyclerView.setAdapter(multiOrderAdapter2);
                setDeliveryCharge(sp.getString(SP_AREA, DEFAULT));

                if (isAllOFS(productModalForeSales)) {
                    showToast("All Are Out Of Stock");
                    placeOrderLl.setBackgroundResource(R.drawable.bg_out_of_stock_btn);
                    placeOrderLl.setEnabled(false);
                    txtPlaceOrderTot.setText("Can Not Order Right Now");
                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast(e.getMessage());
                    e.getStackTrace();
                    Log.d(TAG, "onFailure: exception=" + e.getMessage());
                }
            });


        }

        /*if (orderModal != null) {
            multiOrderClass = new MultiOrder(intent.getIntegerArrayListExtra(ITEM_QTY_LIST),
                    intent.getIntegerArrayListExtra(ITEM_PRICE_LIST));
            // checkOTSfromDatabase();


        } else {
            Log.d(TAG, "findViews: Order null");
            showToast("Null Order");

        }*/

    }

    private void createOrder() {
        showPd("Creating Order..");

        userRef.document(Uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserModal userModal = documentSnapshot.toObject(UserModal.class);
                if (userModal != null)
                    saveUserOrder(userModal);

            } else {
                hidePd();
                showToast("No User Found");

            }

        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    hidePd();
                    txtPlaceOrderTot.setText("Order Again");
                }

            }
        }).addOnFailureListener(e -> {
            showToast(e.getMessage());
            showPb();
            hidePd();
            e.getStackTrace();
            Log.d(TAG, "onFailure: " + e.getMessage());
        });


    }

    private void saveUserOrder(UserModal userModal) {
        Database d = new Database(productRepo);
        showPd("Saving order");
        String id = userRef.document(Uid)
                .collection(USER_ORDER_COLLEC)
                .document().getId();//newly generated doc id
        OrderPlacedModal OPM = new OrderPlacedModal();
        OPM.setUniquePid(d.getAllUniquePid());
        OPM.setOptionQty(d.getAllOptionQty());
        OPM.setCustomerAddress(userModal.getFullAddress());
        OPM.setCustomerId(Uid);
        OPM.setCustomerName(userModal.getName());
        OPM.setDateOfOrder(Long.toString(Utiles.getToday()));
        OPM.setDeliverTimeSlot(Long.toString(Utiles.getToday()));
        OPM.setDeliveryCharge(deliveryCharge);
        OPM.setModeOfPayment(orderModal.getOrderMode());
        OPM.setOrderId(id);
        OPM.setOrderImage(d.getAllImage());
        OPM.setOrderName(d.getAllNames());
        OPM.setOrderQuantity(d.getAllOptionQty());
        OPM.setOrderStatus("Pending");
        OPM.setOrderTiming(Utiles.getCurrentTime());
        OPM.setTotalPrice(String.format(Locale.getDefault(), "%.2f",
                grandTotal));

        OPM.setOrderDescription(d.getAllDesc());
        OPM.setTotalItem(Integer.toString(d.getActualCount()));

        userRef.document(Uid)
                .collection(USER_ORDER_COLLEC)
                .document(id)
                .set(OPM, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    savedOrderForAdmin(userModal, OPM, id);
                }).
                addOnFailureListener(e ->
                        showToast("Order Failed : "
                                + e.getMessage()));

        String lonMsg = String.format("Your order of " +
                        "%s Rs has been created and it will soon be accepted"
                , grandTotal);

        new Handler().postDelayed(() -> {
            /*hid lottie after 3 sec*/


            NotificationBuild(getResources().getString(R.string.order_title),
                    getResources().getString(R.string.order_short_meg),
                    lonMsg);
        }, 3000);

        hidePd();


    }

    private void savedOrderForAdmin(UserModal userModal,
                                    OrderPlacedModal OPM, String id) {
        showPd("Ordering pending..");
        orderPlacedRef
                .document(id)
                .set(OPM, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    hidePd();
                    /*show lottie*/
                    showCustomDialog();

                    txtPlaceOrderTot.
                            setText(String.format("Order Again Rs-%s", grandTotal));

                    //reducing the amount of stock by previous stock
                    //performing batch write
                    executeBathWrite();


                }).addOnFailureListener(e -> {
            hidePd();
            showToast(e.getMessage());
        });
    }

    private void executeBathWrite() {
        Database d = new Database(productRepo);
        List<ProductModalForeSale> CurrentStocks = new ArrayList<>();
        if (d.getActualDocRef() != null) {
            firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    for (DocumentReference doc : d.getActualDocRef()) {
                        DocumentSnapshot ds = transaction.get(doc);
                        ProductModalForeSale p = ds.toObject(ProductModalForeSale.class);
                        if (p != null)
                            CurrentStocks.add(p);
                    }

                    for (ProductModalForeSale p : CurrentStocks) {
                        double previousStock = d.getSpecificOptionQty(p.getProductId().trim());
                        DocumentReference d = itemRef.document(p.getProductId().trim());
                        transaction.update(d,
                                "stockQuantity",
                                (int) Math.abs(p.getStockQuantity() - previousStock));
                    }

                    return null;
                }
            });

        }
    }


    private boolean isAllOFS(List<ProductModalForeSale> productModalForeSales) {
        for (ProductModalForeSale p : productModalForeSales) {
            if (!p.getProductStatus().trim().equalsIgnoreCase(O_F_S))
                return false;
        }
        return true;
    }

    private void setDeliveryCharge(String areaName) {
        Database d = new Database(productRepo);
        double tot = d.getTotalPrice();
        showPb();
        Log.d(TAG, "setDeliveryCharge: area=" + areaName);
        if (!areaName.equals(DEFAULT)) {
            areaRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot qs : queryDocumentSnapshots) {
                            if (qs.get(AREA_NAME) != null && qs.get(AREA_NAME).toString().trim().equals(areaName)) {
                                deliveryCharge = qs.get(DELIVERY_CHARGE).toString().trim();
                                Log.d(TAG, "onSuccess: AREA=" + qs.get(AREA_NAME));
                                Log.d(TAG, "onSuccess: delivery=" + qs.get(DELIVERY_CHARGE));
                                Log.d(TAG, "onSuccess: deliveryCharge=" + deliveryCharge);
                            }
                        }

                        txtDeliveryCharge.setText(String.format("+₹%s",
                                getDeliveryCharge()));
                        grandTotal = multiOrderAdapter2.getGrandTotal(tot)
                                + getDeliveryCharge();

                        deductedAmount = multiOrderAdapter2.getCostTobeDeducted();
                        if (deductedAmount > 0) {
                            txtcostToBeDeducted.setText(String.format(Locale.getDefault(),
                                    "-₹%.1f", deductedAmount));
                        } else {
                            deductedLl.setVisibility(View.GONE);
                        }
                        Log.d(TAG, "setDeliveryCharge: grandTotal int=" + grandTotal);
                        //set the GrandTotal on the Order Btn

                        txtGrandTotal.setText(String.format(Locale.getDefault(),
                                "₹%.1f", grandTotal));

                        txtPlaceOrderTot.setText(String.format(Locale.getDefault()
                                , "Place Order - ₹%.1f", grandTotal));


                    }

                    hidePb();
                }

            }).addOnFailureListener(e -> {
                hidePb();
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


    private void showPd(String title) {
        pd.setTitle(title);
        pd.show();
    }

    private void hidePd() {
        pd.cancel();
    }

    private void showPb() {
        pb.setVisibility(View.VISIBLE);
    }

    private void hidePb() {
        pb.setVisibility(View.GONE);
    }

    private void animate() {
        Button btn = lottieLytRl.findViewById(R.id.refresh_btn);
        orderCompletedAnimation.setRepeatCount(1);
        Log.d(TAG, "animate: called");
        if (lottieLytRl.getVisibility() == View.GONE)
            lottieLytRl.setVisibility(View.VISIBLE);

        /*hiding in this activity no need*/
        if (btn.getVisibility() == View.VISIBLE)
            btn.setVisibility(View.GONE);

    }

    private void unAnimate() {
        Log.d(TAG, "unAnimate: called");
        if (lottieLytRl.getVisibility() == View.VISIBLE)
            lottieLytRl.setVisibility(View.GONE);
        txtPlaceOrderTot.setEnabled(false);
        placeOrderLl.setEnabled(false);
        txtPlaceOrderTot.setText("Order Completed");
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String getRupee(String s) {
        return "₹" + s;
    }


    private void NotificationBuild(String title, String msg, String longMsg) {
        Database d = new Database(productRepo);
        Notification n = Utiles.buildNotification(this, title,
                msg, longMsg);

        notificationManager.notify(1, n);

        String adminTitle = "New Request";
        String adminMsg = "New Order Request Of " +
                d.getActualCount() +
                " items has been Created";
        //New Order Request Of 2 items of has been Created


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendNotificationToAdmin(adminTitle, adminMsg);


            }
        }, 2000);


    }

    private void sendNotificationToAdmin(String title, String LongMSG) {

        unAnimate();
        productRepo.deleteAllProduct();
        sp.edit().putBoolean("Item_Deleted", true).apply();

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
                                                MultiOrderSummaryActivity.this,
                                                "Order Not Created",
                                                getString(R.string.order_not_received),
                                                getString(R.string.order_not_received));

                                        notificationManager.notify(2, nt);//it will not override the
                                        //same notification with same Channel id
                                        Log.d(TAG, "onResponse: error=" + response.code());
                                        Log.d(TAG, "onResponse: response.body().success =" + response.body().success);
                                    } else {
                                        Log.d(TAG, "onResponse: Null");
                                        Log.d(TAG, "onResponse: code=" + response.body().success);
                                    }
                                } else {
                                    Log.d(TAG, "onResponse: failed code=" + response.code());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<MyResponse> call, Throwable t) {
                                t.getStackTrace();
                                Notification nt = Utiles.buildNotification(
                                        MultiOrderSummaryActivity.this,
                                        "Order Not Created",
                                        getString(R.string.order_not_received),
                                        getString(R.string.order_not_received));

                                notificationManager.notify(2, nt);//it will not override the
                                //same notification with same Channel id
                                Log.d(TAG, "onFailure: " + t.getMessage());

                            }
                        });
                    }
                }
        );

       /* hidePd();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }

    private static class Database {
        private ProductRepo productRepo;

        public Database(ProductRepo productRepo) {
            this.productRepo = productRepo;
        }


        private Double getSpecificOptionQty(String pid) {
            try {
                return Double.parseDouble(productRepo.getOptionQty(pid));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return 0.0;
        }

        private List<DocumentReference> getActualDocRef() {
            try {
                List<DocumentReference> docs = new ArrayList<>();
                for (ProductTable PT : productRepo.getAllProducts()) {
                    if (!PT.getProductStatus().trim().equalsIgnoreCase(O_F_S)) {
                        DocumentReference d = FirebaseFirestore
                                .getInstance()
                                .collection(PRODUCT_LIST_COLLECTION)
                                .document(PT.getProductId().trim());
                        docs.add(d);
                    }
                }
                return docs;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        private double getTotalPrice() {
            double tot = 0.0;
            try {
                for (ProductTable PT : productRepo.getAllProducts()) {
                    /*if (!PT.getProductStatus().trim().equalsIgnoreCase(O_F_S))*/
                    tot += Double.parseDouble(PT.getProductPrice());
                }
                return tot;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return tot;
        }


        private List<DocumentReference> getDocsRef() {
            try {
                List<DocumentReference> documentReferenceList = new ArrayList<>();
                for (ProductTable PT : productRepo.getAllProducts()) {
                    DocumentReference d = FirebaseFirestore
                            .getInstance()
                            .collection(PRODUCT_LIST_COLLECTION)
                            .document(PT.getProductId());
                    documentReferenceList.add(d);
                }
                return documentReferenceList;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        private List<String> getAllOptionQty() {
            String[] s = {"optionQty"};
            List<String> optionQty = new ArrayList<>();
            try {
                for (ProductTable PT : productRepo.getAllProducts()) {
                    if (!PT.getProductStatus().trim().equalsIgnoreCase(O_F_S)) {
                        optionQty.add(PT.getOptionQty());
                    }
                }
                return optionQty;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.asList(s);
        }


        private List<String> getAllNames() {
            String[] s = {"getAllNames"};
            List<String> Names = new ArrayList<>();
            try {
                for (ProductTable PT : productRepo.getAllProducts()) {
                    if (!PT.getProductStatus().trim().equalsIgnoreCase(O_F_S)) {
                        Names.add(PT.getProductName());
                    }
                }
                return Names;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.asList(s);
        }


        private List<String> getAllDesc() {
            String[] s = {"getAllDesc"};
            List<String> descList = new ArrayList<>();
            try {

                for (ProductTable p : productRepo.getAllProducts()) {
                    if (!p.getProductStatus().trim().equalsIgnoreCase(O_F_S))
                        descList.add(p.getOptionQty() + " " + p.getProductUnit() + " of " + p.getProductName()
                                + " with total Rs of ₹" + p.getOriginalPrice() + "/"
                                + p.getOriginalQty() + p.getProductUnit()
                                + " total is ₹" +
                                p.getProductPrice());
                    //1.0 kg of APPLE WASHINGTON with total Rs of ₹50/0.25kg total is ₹200.0
                }
                return descList;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.asList(s);
        }

        private List<String> getAllImage() {
            String[] s = {"getAllImage"};
            List<String> Imgs = new ArrayList<>();
            try {
                for (ProductTable p : productRepo.getAllProducts()) {
                    if (!p.getProductStatus().trim().equalsIgnoreCase(O_F_S))
                        Imgs.add(p.getProductImage());

                }
                return Imgs;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.asList(s);
        }

        private List<String> getAllUniquePid() {
            String[] s = {"getAllUniquePid"};
            List<String> UniquIDs = new ArrayList<>();
            try {
                for (ProductTable p : productRepo.getAllProducts()) {
                    if (!p.getProductStatus().trim().equalsIgnoreCase(O_F_S))
                        UniquIDs.add(p.getUniquePid());

                }

                return UniquIDs;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.asList(s);
        }

        private int getActualCount() {
            //count of the product which are not out of stock;
            int c = 0;
            try {
                for (ProductTable p : productRepo.getAllProducts()) {
                    if (!p.getProductStatus().trim().equalsIgnoreCase(O_F_S)) {
                        c++;
                    }
                }
                return c;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return c;
        }

        private int getCount() {
            try {
                return productRepo.getProductCount();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        }

        private ProductTable getAProduct(String id) {
            try {
                return productRepo.getAProduct(id);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
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