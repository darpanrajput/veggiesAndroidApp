package com.darpan.project.veggiesadmin.activity.order;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.darpan.project.veggiesadmin.adapter.order.OrderListAdapter;
import com.darpan.project.veggiesadmin.fcm.APIService;
import com.darpan.project.veggiesadmin.fcm.Client;
import com.darpan.project.veggiesadmin.fcm.Data;
import com.darpan.project.veggiesadmin.fcm.MyResponse;
import com.darpan.project.veggiesadmin.fcm.NotificationSender;
import com.darpan.project.veggiesadmin.firebaseModal.OrderPlacedModal2;
import com.darpan.project.veggiesadmin.firebaseModal.UserModal;
import com.darpan.project.veggiesadmin.firebaseModal.UserNotiModal;
import com.darpan.project.veggiesadmin.projectModal.ExcelModal;
import com.darpan.project.veggiesadmin.util.Util;
import com.darpan.project.veggiesadmin.R;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.darpan.project.veggiesadmin.constant.Constants.ACCEPTED;
import static com.darpan.project.veggiesadmin.constant.Constants.CANCELLED;
import static com.darpan.project.veggiesadmin.constant.Constants.CUSTOMER_KEY;
import static com.darpan.project.veggiesadmin.constant.Constants.DELIVERED;
import static com.darpan.project.veggiesadmin.constant.Constants.LIMIT;
import static com.darpan.project.veggiesadmin.constant.Constants.ORDER_PLACED_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.PAGE_COUNT;
import static com.darpan.project.veggiesadmin.constant.Constants.PENDING;
import static com.darpan.project.veggiesadmin.constant.Constants.REJECTED;
import static com.darpan.project.veggiesadmin.constant.Constants.TODAY;
import static com.darpan.project.veggiesadmin.constant.Constants.TOKEN_DOC;
import static com.darpan.project.veggiesadmin.constant.Constants.TOKEN_KEY;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_COLLECTION;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_ORDER_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_TOKEN_COLLEC;

public class OrderListActivity extends AppCompatActivity
        implements OrderListAdapter.OnItemClick,
        SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView orderRv;
    private OrderListAdapter orderAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String TAG = "OrderListActivity: ";
    private SwipeRefreshLayout swipeRefreshLayout;
    private CollectionReference orderRef = firebaseFirestore.
            collection(ORDER_PLACED_COLLEC);
    private NotificationManagerCompat notificationManager;
    private CollectionReference useref = firebaseFirestore.collection(USER_COLLECTION);

    private List<OrderPlacedModal2> OPMList = new ArrayList<>();

    private List<OrderPlacedModal2> ExcelOrderList = new ArrayList<>();

    private List<UserModal> customerList = new ArrayList<>();

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        orderRv = findViewById(R.id.order_rv);
        Spinner statusSpinner = findViewById(R.id.order_filter_spinner);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);
        pd = new ProgressDialog(OrderListActivity.this);
        notificationManager = NotificationManagerCompat.from(this);
        String filterOption = "All";
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{filterOption//all
                        , TODAY
                        , PENDING,
                        REJECTED,
                        CANCELLED,
                        ACCEPTED,
                        DELIVERED});
        statusAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        statusSpinner.setAdapter(statusAdapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setData(parent.getSelectedItem().toString().trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        setData(filterOption);
    }

    private void setData(String filterOption) {
        if (!OPMList.isEmpty()) OPMList.clear();
        Query query;
        if (filterOption.equalsIgnoreCase("All")) {
            query = orderRef
                    .orderBy("orderTiming", Query.Direction.ASCENDING)
                    .limit(LIMIT);
            orderRef.orderBy("orderTiming", Query.Direction.ASCENDING).
                    get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot qds) {
                    if (!ExcelOrderList.isEmpty()) ExcelOrderList.clear();
                    ExcelOrderList.addAll(qds.toObjects(OrderPlacedModal2.class));
                    filCustomerList(ExcelOrderList);


                }
            });
        } else if (filterOption.equalsIgnoreCase(TODAY)) {
            query = orderRef.whereEqualTo("dateOfOrder",
                    String.valueOf(Util.getToday()))
                    .limit(LIMIT);

            orderRef.whereEqualTo("dateOfOrder",
                    String.valueOf(Util.getToday())).
                    get().addOnSuccessListener(qds -> {
                if (!ExcelOrderList.isEmpty()) ExcelOrderList.clear();
                ExcelOrderList.addAll(qds.toObjects(OrderPlacedModal2.class));
                filCustomerList(ExcelOrderList);
            });


        } else {
            query = orderRef.whereEqualTo("orderStatus", filterOption)
                    .orderBy("orderTiming", Query.Direction.ASCENDING)
                    .limit(LIMIT);

            orderRef.whereEqualTo("orderStatus", filterOption).
                    get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot qds) {
                    if (!ExcelOrderList.isEmpty()) ExcelOrderList.clear();
                    ExcelOrderList.addAll(qds.toObjects(OrderPlacedModal2.class));
                    filCustomerList(ExcelOrderList);
                }
            });
        }

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();
        FirestorePagingOptions<OrderPlacedModal2> options =
                new FirestorePagingOptions.Builder<OrderPlacedModal2>()
                        .setQuery(query, config, snapshot -> {
                            OrderPlacedModal2 Modal =
                                    snapshot.toObject(OrderPlacedModal2.class);
                            OPMList.add(Modal);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                            if (Modal != null)
                                Modal.setSnapId(id);

                            return Objects.requireNonNull(Modal);
                        })
                        .build();


        orderAdapter = new OrderListAdapter(options);
        orderRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        orderRv.setItemAnimator(new DefaultItemAnimator());
        orderRv.setAdapter(orderAdapter);
        orderAdapter.startListening();
        orderAdapter.setItemClick(OrderListActivity.this);


        orderAdapter.setOnloadingStateChange(loadingState -> {
            switch (loadingState) {
                case LOADING_INITIAL:
                    System.out.println("LOADING_INITIAL");
                    swipeRefreshLayout.setRefreshing(true);
                    break;

                case LOADING_MORE:
                    System.out.println("LOADING_MORE");
                    swipeRefreshLayout.setRefreshing(true);
                    /* progressBar.setVisibility(View.GONE);*/

                    break;

                case LOADED:
                    System.out.println("LOADED");
                    swipeRefreshLayout.setRefreshing(false);

                    /*   progressBar.setVisibility(View.GONE);*/
                    break;

                case ERROR:
                    Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();
                    System.out.println("ERROR");
                    /*      progressBar.setVisibility(View.GONE);*/
                    swipeRefreshLayout.setRefreshing(false);

                    break;

                case FINISHED:
                    swipeRefreshLayout.setRefreshing(false);
                    System.out.println("FINISHED");
                    /* progressBar.setVisibility(View.GONE);*/
                    break;
            }
        });


    }

    private void filCustomerList(List<OrderPlacedModal2> excelOrderList) {
        showPd("loading");
        if (!customerList.isEmpty()) customerList.clear();
        List<DocumentReference> documentReferences = new ArrayList<>();
        for (OrderPlacedModal2 o : excelOrderList) {
            DocumentReference dref = useref.document(
                    o.getCustomerId().trim());
            documentReferences.add(dref);
        }
        firebaseFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
            for (DocumentReference d : documentReferences) {
                DocumentSnapshot ds = transaction.get(d);
                UserModal userModal = ds.toObject(UserModal.class);
                customerList.add(userModal);
            }
            return null;
        }).addOnSuccessListener(aVoid -> hidePd()).addOnFailureListener(e -> {
            hidePd();
            toast(e.getMessage());
            e.getStackTrace();
            Log.d(TAG, "onFailure: exception="+e.getMessage());
        });
    }

    private void CreatNotiInDataBase(DocumentSnapshot ds, String title, String Msg) {

        CollectionReference notiRef = FirebaseFirestore
                .getInstance().collection(USER_COLLECTION)
                .document(ds.getString("customerId"))
                .collection("notification");
        UserNotiModal userNotiModal = new UserNotiModal(
                Msg,
                Util.getCurrentTime(), /*get time in 24hrs fpr and store in database*/
                title);

        Task<DocumentReference> task = notiRef.add(userNotiModal);

        try {
            Task<List<DocumentReference>> tasks = Tasks.whenAllSuccess(task);
            tasks.addOnSuccessListener(new OnSuccessListener<List<DocumentReference>>() {
                @Override
                public void onSuccess(List<DocumentReference> documentReferences) {
                    if (documentReferences != null
                            && !documentReferences.isEmpty()) {
                        String id = documentReferences.get(0).getId();
                        notiRef.document(id)
                                .update("snapId", id);
                    }

                }
            });

        } catch (Exception e) {
            e.getStackTrace();
            Log.d(TAG, "CreatNotiInDataBase: Exception=" + e.getMessage());
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderAdapter != null) {
            orderAdapter.stopListening();
        }
    }

    @Override
    public void onRefresh() {
        orderAdapter.refresh();
    }

    @Override
    public void setOnItemClick(DocumentSnapshot snapshot, int position) {
        Log.d(TAG, "setOnItemClick: Called id=" + snapshot.getId());
        Intent i = new Intent(OrderListActivity.this,
                OrderDetail.class);
        //USER_DATA_MODAL
        OrderPlacedModal2 ODM = snapshot.toObject(OrderPlacedModal2.class);
        i.putExtra("ORDER_PLACED_MODAL", ODM);
        startActivity(i);

    }


    private boolean checkStatus(String status) {
        if (status.equalsIgnoreCase(CANCELLED)) {
            Toast.makeText(getApplicationContext(), "Order has been Cancelled By User . You Can not Change That!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (status.equalsIgnoreCase(DELIVERED)) {
            Toast.makeText(getApplicationContext(),
                    "Order has been Delivered to User . " +
                            " You Can not Change That!",
                    Toast.LENGTH_LONG).show();
            return false;
        }


        return true;
    }

    @Override
    public void OnDeleteClick(DocumentSnapshot snapshot, int pos) {
        orderRef.document(snapshot.getId())
                .delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(getApplicationContext(),
                    "Deleted", Toast.LENGTH_SHORT).show();
            orderAdapter.refresh();
        });
        /*deleting from the customer end*/

        DocumentReference userRef = firebaseFirestore.collection(USER_COLLECTION)
                .document(snapshot.getString("customerId")
                        .trim())
                .collection(USER_ORDER_COLLEC)
                .document(snapshot.getId());//order id

        userRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Deleted",
                        Toast.LENGTH_SHORT).show();
                orderAdapter.refresh();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });

    }

    @Override
    public void OnAcceptClick(DocumentSnapshot snapshot, int pos) {

        if (checkStatus(snapshot.getString("orderStatus"))) {
            try {
                if (snapshot.getData() != null)
                    notifyAdmin("Order Status",
                            "Order Accepted",
                            "You Have Accepted an Order From\n" +
                                    "Address:" + OPMList.get(pos).getCustomerAddress() + "\n" +
                                    "Name:" + OPMList.get(pos).getCustomerName() + "\n" +
                                    "On: " + Util.getDate(Long.parseLong(OPMList.get(pos).getDateOfOrder().trim())));

            } catch (Exception e) {
                Log.d(TAG, "OnAcceptClick: excptipn=" + e.getMessage());
                e.getStackTrace();
                notifyAdmin("Order Status",
                        "Order Accepted",
                        "You Have Accepted an Order . It needs To be Delivered In an Hour\n"
                                + " Hurry Up");
            }
            changeOrderStatus(
                    snapshot,
                    ACCEPTED,
                    "Order Accepted"
                    , "Thanks for Ordering!",
                    "Your Order Has Been Accepted It will be delivered Soon\n" +
                            " Thanks for Ordering!"
            );
        }

    }

    @Override
    public void OnRejectClick(DocumentSnapshot snapshot, int pos) {
        if (checkStatus(snapshot.getString("orderStatus"))) {

            changeOrderStatus(snapshot,
                    REJECTED,
                    "Order Rejected",
                    "Your order is Rejected",
                    "Your order is Rejected due to some Uncommon Reasons");
        }
    }

    @Override
    public void OnDeliveredClick(DocumentSnapshot snapshot, int pos) {
        if (checkStatus(snapshot.getString("orderStatus"))) {
            try {
                notifyAdmin("Order Status",
                        "Order Delivered",
                        "You Have Delivered an Order At\n" +
                                "Address:" + OPMList.get(pos).getCustomerAddress() + "\n" +
                                "To:" + OPMList.get(pos).getCustomerName() + "\n" +
                                "On: " + Util.getDate(Long.parseLong(OPMList.get(pos).getDateOfOrder().trim())));

            } catch (Exception e) {
                Log.d(TAG, "OnDeliveredClick: exception=" + e.getMessage());
            }

            changeOrderStatus(
                    snapshot,
                    DELIVERED,
                    "Order Delivered"
                    , "Thanks for Ordering!",
                    "Your Order Has Been Delivered At\n" +
                            "Address:" + snapshot.getString("customerAddress") + "\n" +
                            "To:" + snapshot.getString("customerName") + "\n" +
                            "Thanks for Ordering!"
            );
        }


    }


    private void changeOrderStatus(DocumentSnapshot ds, String status,
                                   String title, String msg, String LongMsg) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Loading..");
        pd.show();
        Map<String, Object> newStatus = new HashMap<>();
        newStatus.put("orderStatus", status);
        orderRef.document(ds.getId())
                .update(newStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference userRef = firebaseFirestore.collection(USER_COLLECTION)
                                .document(Objects.requireNonNull(ds.getString("customerId")
                                        .trim()))
                                .collection(USER_ORDER_COLLEC)
                                .document(ds.getId());

                        userRef.update(newStatus)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        toast(status);

                                        orderAdapter.refresh();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                notifyUser(ds, title, LongMsg);

                                            }
                                        }, 3000);

                                    }
                                }).addOnFailureListener(e -> {
                            pd.dismiss();
                            toast(e.getMessage());
                        });
                    }
                }).addOnFailureListener(e -> {
            toast(e.getMessage());
            pd.dismiss();
            Log.d(TAG, "onFailure: exception=" + e.getMessage());
        });
    }

    private void notifyUser(DocumentSnapshot ds, String title, String LongMSG) {
        CreatNotiInDataBase(ds, title, LongMSG);

        APIService apiService = Client.getClient("https://fcm.googleapis.com/").
                create(APIService.class);
        Data data = new Data(title, LongMSG);

        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                .document(ds.getString(CUSTOMER_KEY))
                .collection(USER_TOKEN_COLLEC)
                .document(TOKEN_DOC)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    Map<String, Object> tokenMAp = documentSnapshot.getData();
                    String userToken = tokenMAp.get(TOKEN_KEY).toString().trim();

                    Log.d(TAG, "onSuccess: called userToken=" + userToken);
                    NotificationSender sender =
                            new NotificationSender(data, userToken);

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
                                    OrderListActivity.this,
                                    "Something went wrong",
                                    "Can not notify user",
                                    "We are not able to send notification to user");

                            notificationManager.notify(1, nt);//it will not override the
                            //same notification with same Channel id
                            Log.d(TAG, "onFailure: " + t.getMessage());

                        }
                    });

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast(e.getMessage());
                Log.d(TAG, "onFailure: exception=" + e.getMessage());
            }
        });

    }

    private void notifyAdmin(String title, String msg, String longMsg) {
        Notification n = Util.buildNotification(this, title,
                msg, longMsg);
        notificationManager.notify(0, n);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.change_order_status_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_all:
                changeStatusOfAll(ACCEPTED,
                        "Order Accepted",
                        "Your Order Has Been Accepted It will be delivered Soon\n" +
                                " Thanks for Ordering!");
                notifyAdmin("Order Status", "You Have Accepted All",
                        "You have Accepted All Order  Hurry Up ! customer Are" +
                                " waiting");
                return true;
            case R.id.reject_all:
                changeStatusOfAll(REJECTED,
                        "Order Rejected",
                        "Your Order Has Been Rejected Due to some Unknown reasons\n" +
                                " Sorry for Inconvenience happen!");

                notifyAdmin("Order Status", "You Have Rejected  All",
                        "You have Rejected All Order ");
                return true;

            case R.id.delete_all:
                new AlertDialog.Builder(this)
                        .setTitle("Do You Want to Delete All?")
                        .setMessage("This Will Delete All products From The List\n" +
                                "User Wouldn't be able To see Any Product\n" +
                                "Are You Really want to do that?")
                        .setPositiveButton("OK", (dialog, which) -> {
                            deleteAll();
                            dialog.dismiss();
                        }).setNegativeButton("Cancel ",
                        (dialog, which) -> dialog.dismiss()).show();


                return true;

            case R.id.export_excel:
              /*  if (!OPMList.isEmpty()) OPMList.isEmpty();
                orderRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds:queryDocumentSnapshots){
                            OPMList.add(ds.toObject(OrderPlacedModal.class));
                        }
                    }
                });*/

                exportData(ExcelOrderList, OrderListActivity.this, "" +
                        "CustomerWise");

                return true;

            case R.id.export_orderwise_excel:
                exportDataOrderWise();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void deleteAll() {
        if (OPMList.size() > 0) {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Deleting all" + "...");
            pd.show();

            for (OrderPlacedModal2 opm : OPMList) {
                orderRef.document(opm.getOrderId())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                                .document(opm.getCustomerId())
                                .collection(USER_ORDER_COLLEC)
                                .document(opm.getOrderId())
                                .delete();

                    }
                });


            }
            pd.dismiss();
            orderAdapter.refresh();
            toast("Deleted All");
        }
    }

    private void changeStatusOfAll(String status, String title, String LongMsg) {
        if (OPMList.size() > 0) {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle(status + "...");
            pd.show();
            for (OrderPlacedModal2 opm : OPMList) {
                Map<String, Object> newStatus = new HashMap<>();
                newStatus.put("orderStatus", status);
                orderRef.document(opm.getOrderId())
                        .update(newStatus)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                                        .document(opm.getCustomerId())
                                        .collection(USER_ORDER_COLLEC)
                                        .document(opm.getOrderId())
                                        .update(newStatus)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                orderAdapter.refresh();

                                                notifyAllUser(opm, title, LongMsg);
                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toast(e.getMessage());
                        e.getStackTrace();
                        pd.dismiss();
                    }
                });
            }
            toast("Status Changed");

        } else {
            toast("Empty data");
        }
    }

    private void notifyAllUser(OrderPlacedModal2 opm, String title, String longMsg) {
        APIService apiService = Client.getClient("https://fcm.googleapis.com/").
                create(APIService.class);
        Data data = new Data(title, longMsg);

        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                .document(opm.getCustomerId())
                .collection(USER_TOKEN_COLLEC)
                .document(TOKEN_DOC)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    Map<String, Object> tokenMAp = documentSnapshot.getData();
                    String userToken = tokenMAp.get(TOKEN_KEY).toString().trim();

                    Log.d(TAG, "onSuccess: called userToken=" + userToken);
                    NotificationSender sender =
                            new NotificationSender(data, userToken);

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
                                    OrderListActivity.this,
                                    "Something went wrong",
                                    "Can not notify user",
                                    "We are not able to send notification to user");

                            notificationManager.notify(1, nt);//it will not override the
                            //same notification with same Channel id
                            Log.d(TAG, "onFailure: " + t.getMessage());

                        }
                    });

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast(e.getMessage());
                Log.d(TAG, "onFailure: exception=" + e.getMessage());
            }
        });
    }


    private void toast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();

    }

    /*private static class export extends AsyncTask<Void, Void, Void> {
        private List<OrderPlacedModal> OPM;
        private WeakReference<Context> context;
        ProgressDialog pd;

        public export(List<OrderPlacedModal> OPM, WeakReference<Context> context) {
            this.OPM = OPM;
            this.context = context;
            pd = new ProgressDialog(context.get());
            pd.setTitle("Working On..");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            exportData(OPM, context.get());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.hide();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }
    }*/


    private void exportData(List<OrderPlacedModal2> data, Context context, String name) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();

            Sheet sheet = workbook.createSheet("sheet1");// creating a blank sheet

            int rownum = 0;

            Row row0 = sheet.createRow(rownum++);
            createListHeadings(row0);

           /* for (OrderPlacedModal2 opm : data) {
                Row row = sheet.createRow(rownum++);
                createList(opm, row);

            }
*/
            for (int idx=0;idx<data.size();idx++) {
                Row row = sheet.createRow(rownum++);
                createList(data.get(idx), row,idx);

            }

            String fileName = name + ".xlsx";
            String extStorageDirectory = Environment.getExternalStorageDirectory()
                    .toString();
            Log.d(TAG, "exportData: Path=" + extStorageDirectory);
            File folder = new File(extStorageDirectory, "Order");
            folder.mkdir();
            File file = new File(folder, fileName);
            try {
                file.createNewFile();
                Log.d(TAG, "exportData: filePath=" + file.getPath());
                Log.d(TAG, "exportData: Absolute filePath=" + file.getAbsolutePath());
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e(TAG, " File Excep=" + e1.getMessage());
            }

            try {
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();

                Toast.makeText(context.getApplicationContext(),
                        "file saved", Toast.LENGTH_SHORT).show();
           /* linearLayout.setVisibility(View.VISIBLE);
            clear_text_view_text.startAnimation(animationDown);
            clear_text_view_text.setText("file:/ " + extStorageDirectory + "/Order");
*/
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, " Fil o/p Excep=" + e.getMessage());
            }

        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "Exception " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "ExportList=" + e.getMessage());
            Toast.makeText(context.getApplicationContext(), "not Saved ", Toast.LENGTH_SHORT).show();

        }

    }

    private void createListHeadings(Row row) {

        Cell cell = row.createCell(0);
        cell.setCellValue("Customer Id ");
        cell = row.createCell(1);
        cell.setCellValue("Product id");
        cell = row.createCell(2);
        cell.setCellValue("Customer Name ");
        cell = row.createCell(3);
        cell.setCellValue("Blocks");
        cell = row.createCell(4);
        cell.setCellValue("Contact No");
        cell = row.createCell(5);
        cell.setCellValue("Order Details");
        cell = row.createCell(6);
        cell.setCellValue("Total price");
        cell = row.createCell(7);
        cell.setCellValue("Total Item");
        cell = row.createCell(8);
        cell.setCellValue("Status");
        cell = row.createCell(9);
        cell.setCellValue("Order Date");
        cell = row.createCell(10);
        cell.setCellValue("Order Time");
        cell = row.createCell(11);
        cell.setCellValue("Full Address");
        cell = row.createCell(12);
        cell.setCellValue("Payment Mode");
       /* cell = row.createCell(13);
        cell.setCellValue("Image Url");*/
    }

   /* private boolean isEnd(List<String> list, String last) {
        if (list.get(list.size() - 1).equals(last)) {
            return true;
        }
        return false;
    }*/

    private String getBlocks(String addr) {
//        String addr="Block- G 45A, new delhi,anant vihar";
        StringBuilder blocks = new StringBuilder();
        System.out.println(Arrays.toString(addr.split("Block- ")));
        String s = addr.split("Block- ")[1];
        blocks.append("block Name-").append(s.split(" ")[0].trim()).append(" and ")
                .append("block Number-").append(s.split(" ")[1].replace(",", "").trim());
        System.out.println(blocks.toString());
        return blocks.toString().trim();

    }

    private void createList(OrderPlacedModal2 OPM, Row row,int pos) {
        Cell cell = row.createCell(0);
        cell.setCellValue(OPM.getCustomerId());

        cell = row.createCell(1);
        StringBuilder ids = new StringBuilder();
        for (String s : OPM.getUniquePid()) {
            ids.append(s).append(",");
        }
        cell.setCellValue(ids.toString());

        cell = row.createCell(2);
        cell.setCellValue(OPM.getCustomerName());


        cell = row.createCell(3);
        cell.setCellValue(getBlocks(OPM.getCustomerAddress()));


        cell = row.createCell(4);
        cell.setCellValue(customerList.get(pos).getMobile());



        cell = row.createCell(5);
        StringBuilder orderDetail = new StringBuilder();
        for (int i=0;i<OPM.getOrderName().size();i++) {
            orderDetail.append(OPM.getOrderName().get(i).toLowerCase())
                    .append("(")
                    .append(OPM.getOptionQty().get(i))
                    .append(" ")
                    .append(getAUnit(OPM.getOrderDescription().get(i)))
                    .append(")");
            if (i<OPM.getOrderName().size()-1){
                orderDetail.append(",");
            }
        }


        cell.setCellValue(orderDetail.toString().trim());


        cell = row.createCell(6);
        cell.setCellValue(OPM.getTotalPrice());

        cell = row.createCell(7);
        cell.setCellValue(OPM.getTotalItem());

        cell = row.createCell(8);
        cell.setCellValue(OPM.getOrderStatus());

        cell = row.createCell(9);
        String date = Util.getDate(Long.parseLong(OPM.getDateOfOrder()));
        cell.setCellValue(date);

        cell = row.createCell(10);
        cell.setCellValue(OPM.getOrderTiming());

        cell = row.createCell(11);
        cell.setCellValue(OPM.getCustomerAddress());

        cell = row.createCell(12);
        cell.setCellValue(OPM.getModeOfPayment());

        /*cell = row.createCell(13);
        StringBuilder imgs = new StringBuilder();
        for (String s : OPM.getOrderImage()) {
            imgs.append(s).append(",");
        }
        cell.setCellValue(imgs.toString());*/
    }

    private void showPd(String t) {

        pd.setTitle(t);
        pd.show();
    }

    private void hidePd() {
        pd.dismiss();
    }

    private void exportDataOrderWise() {
        showPd("Working On...");
        modal m = new modal();
        List<String> id = new ArrayList<>();
        List<String> qty = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> unit = new ArrayList<>();
        List<String>Date=new ArrayList<>();

        List<ExcelModal> excelModals = new ArrayList<>();

        for (OrderPlacedModal2 o : ExcelOrderList) {
            id.addAll(o.getUniquePid());
            qty.addAll(o.getOptionQty());
            name.addAll(o.getOrderName());
            unit.addAll(getUnits(o.getOrderDescription()));
            Date.add(o.getDateOfOrder());
        }

        m.setIds(id);
        m.setQty(qty);
        m.setNames(name);
        m.setUnits(unit);

        System.out.println(m.toString());

        Hashtable<String, ArrayList<Integer>> idAndIndex = new Hashtable<>();


        for (int i = 0; i < m.getIds().size(); i++) {
            ArrayList<Integer> uniquePid = idAndIndex.get(m.getIds().get(i));
            if (uniquePid == null) {
                ArrayList<Integer> Indexes = new ArrayList<>();
                Indexes.add(i);
                idAndIndex.put(m.getIds().get(i), Indexes);
            } else {
                uniquePid.add(i);
                idAndIndex.put(m.getIds().get(i),
                        uniquePid);
            }


        }



        for (Hashtable.Entry<String, ArrayList<Integer>> entry : idAndIndex.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
            ExcelModal e = new ExcelModal();
            double dQty = 0.0;
            for (int index = 0; index < entry.getValue().size(); index++) {

                dQty += Double.parseDouble(qty.get(entry.getValue()
                        .get(index)));
                System.out.println(dQty);

            }
            String unitQty = String.format(Locale.getDefault(), "%.2f", dQty) +
                    " " + unit.get(entry.getValue().get(0));

            e.setTotalOrderQty(unitQty);
            e.setOrderName(name.get(entry.getValue().get(0)));
            e.setProductId(id.get(entry.getValue().get(0)));

            for (String date:Date){
                e.setOrderDate(Util.getDate(Long.parseLong(date.trim())));
            }


            excelModals.add(e);

        }
        exportOrder eo = new exportOrder(
                excelModals,
                "OrderWise",
                OrderListActivity.this
        );
        eo.createSheet();


    }

    private String getAUnit(String orderDescription) {

           return orderDescription.split(" ")[1].trim();

    }

    private List<String> getUnits(List<String> orderDescription) {
        List<String> units = new ArrayList<>();
        for (String s : orderDescription) {
            units.add(s.split(" ")[1].trim());
        }

        return units;
    }

    private String getUniquePid(OrderPlacedModal2 o) {
        StringBuilder ids = new StringBuilder();
        if (o.getUniquePid().size() > 1) {
            for (String s : o.getUniquePid()) {
                ids.append(s).append(",");
            }
            return ids.toString().trim();
        } else
            return ids.append(o.getUniquePid().get(0)).toString()
                    .trim();
    }

    private String getNames(OrderPlacedModal2 o) {
        StringBuilder name = new StringBuilder();
        if (o.getOrderName().size() > 1) {
            for (String s : o.getOrderName()) {
                name.append(s).append(",");
            }
            return name.toString().trim();
        } else
            return name.append(o.getOrderName().get(0)).toString()
                    .trim();
    }

    private String getQtys(OrderPlacedModal2 o) {
        StringBuilder qty = new StringBuilder();
        if (o.getOptionQty().size() > 1) {
            for (String s : o.getOptionQty()) {
                qty.append(s).append(",");
            }
            return qty.toString().trim();
        } else
            return qty.append(o.getOptionQty().get(0)).toString()
                    .trim();
    }


    private class exportOrder {
        private List<ExcelModal> excelModals;
        private String name;
        private Context context;


        public exportOrder(List<ExcelModal> excelModals, String name, Context context) {
            this.excelModals = excelModals;
            this.name = name;
            this.context = context;

        }

        public exportOrder() {
        }

        private void createSheet() {
            try {
                HSSFWorkbook workbook = new HSSFWorkbook();

                Sheet sheet = workbook.createSheet("sheet1");// creating a blank sheet

                int rownum = 0;

                Row row0 = sheet.createRow(rownum++);
                createHeaders(row0);

                for (ExcelModal ex : excelModals) {
                    Row row = sheet.createRow(rownum++);
                    createRows(ex, row);

                }
                String fileName = name + ".xlsx";
                String extStorageDirectory = Environment.getExternalStorageDirectory()
                        .toString();
                Log.d(TAG, "exportData: Path=" + extStorageDirectory);
                File folder = new File(extStorageDirectory, "Order");
                folder.mkdir();
                File file = new File(folder, fileName);

                try {
                    file.createNewFile();
                    Log.d(TAG, "exportData: filePath=" + file.getPath());
                    Log.d(TAG, "exportData: Absolute filePath=" + file.getAbsolutePath());
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Log.e(TAG, " File Excep=" + e1.getMessage());
                }

                try {
                    FileOutputStream fileOut = new FileOutputStream(file);
                    workbook.write(fileOut);
                    fileOut.close();

                    Toast.makeText(context.getApplicationContext(),
                            "file saved", Toast.LENGTH_SHORT).show();
                    hidePd();
                } catch (IOException e) {
                    hidePd();
                    e.printStackTrace();
                    Log.d(TAG, " Fil o/p Excep=" + e.getMessage());
                }


            } catch (Exception e) {
                hidePd();
                Toast.makeText(context.getApplicationContext(), "Exception " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "ExportList=" + e.getMessage());
                Toast.makeText(context.getApplicationContext(), "not Saved ", Toast.LENGTH_SHORT).show();

            }
        }

        private void createHeaders(Row row) {
            Cell cell = row.createCell(0);
            cell.setCellValue("Order Date ");
            cell = row.createCell(1);
            cell.setCellValue("Id ");
            cell = row.createCell(2);
            cell.setCellValue("Product Name");
            cell = row.createCell(3);
            cell.setCellValue("Total Quantity");
            /*cell = row.createCell(3);
            cell.setCellValue("Expected Amt");
            cell = row.createCell(4);
            cell.setCellValue("Status");
            cell = row.createCell(5);
            cell.setCellValue("Order Time");
            cell = row.createCell(6);
            cell.setCellValue("Order Date");*/


        }

        private void createRows(ExcelModal ex, Row row) {
            Cell cell = row.createCell(0);
            cell.setCellValue(ex.getOrderDate());
            cell = row.createCell(1);
            cell.setCellValue(ex.getProductId());
            cell = row.createCell(2);
            cell.setCellValue(ex.getOrderName());
            cell = row.createCell(3);
            cell.setCellValue(ex.getTotalOrderQty());
            cell = row.createCell(4);
            cell.setCellValue(ex.getOrderStatus());
            cell = row.createCell(5);
            cell.setCellValue(ex.getOrderTime());
            cell = row.createCell(6);
            cell.setCellValue(ex.getOrderDate());
        }
    }


}