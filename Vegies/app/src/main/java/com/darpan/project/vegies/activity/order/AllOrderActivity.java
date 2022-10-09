package com.darpan.project.vegies.activity.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.adapters.category.allorder.AllOrderAdapter;
import com.darpan.project.vegies.firebaseModal.OrderPlacedModal;

import static com.darpan.project.vegies.constant.Constants.OPTION_QTY;
import static com.darpan.project.vegies.constant.Constants.ORDER_CLASS;
import static com.darpan.project.vegies.constant.Constants.PAGE_COUNT;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.USER_ORDER_COLLEC;

public class AllOrderActivity extends AppCompatActivity
implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView orderRv;
    private FirebaseFirestore ff = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private String userId;
    private CollectionReference userOrderRef;
    private CollectionReference userRef;
    private AllOrderAdapter adapter;
    private static final String TAG = "AllOrderActivity:";
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_order);
        orderRv = findViewById(R.id.order_list_rv);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);

        Initialise();
    }

    private void Initialise() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            userId = user.getUid();
        userRef = ff.collection(USER_COLLECTION);
        userOrderRef = userRef.document(userId).collection(USER_ORDER_COLLEC);

        getData();

    }

    private void getData() {
        Query query = userOrderRef.orderBy("dateOfOrder", Query.Direction.ASCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();

        FirestorePagingOptions<OrderPlacedModal> options = new FirestorePagingOptions.
                Builder<OrderPlacedModal>()
                .setQuery(query, config, new SnapshotParser<OrderPlacedModal>() {
                    @NonNull
                    @Override
                    public OrderPlacedModal parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        String id = snapshot.getId();
                        OrderPlacedModal OPM = snapshot.toObject(OrderPlacedModal.class);
                        //Log.d(TAG, "parseSnapshot: id=" + id);
                       // Log.d(TAG, "parseSnapshot: url="+snapshot.get("orderImage"));
                      //  Log.d(TAG, "parseSnapshot:OPM url="+OPM.getOrderImage());

                        if (OPM != null) {
                            OPM.setOrderId(id);
                            /*OPM.setOptionQty(snapshot.getString(OPTION_QTY))*/;
                            /* OPM.setUniquePid(snapshot.getString(UNIQUE_PID));*/
                           /* if (OPM.getOrderId().trim().equals(userOrderRef))
                            userOrderRef.document(id).set(OPM, SetOptions.merge());*/

                            return OPM;
                        }
                        return OPM;
                    }
                })
                .build();


        orderRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new AllOrderAdapter(options);
        orderRv.setItemAnimator(new DefaultItemAnimator());
        orderRv.setAdapter(adapter);
        adapter.startListening();


        adapter.setOnloadingStateChange(loadingState -> {
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
                    Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show();
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

        adapter.setItemClick(new AllOrderAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(DocumentSnapshot snapshot, int position) {
                OrderPlacedModal OPM = snapshot.toObject(OrderPlacedModal.class);
                Log.d(TAG, "setOnItemClick: called=name" + OPM.getOrderId());
                Intent i = new Intent(AllOrderActivity.this,
                        MyOrderActivity.class);
                i.putExtra(ORDER_CLASS, OPM);
                i.putExtra("orderId",snapshot.getId());
                startActivity(i);
            }
        });



    }

    @Override
    public void onRefresh() {
        adapter.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null)
            adapter.stopListening();
    }

    private void showToast(String m) {
        Toast.makeText(AllOrderActivity.this,
                m, Toast.LENGTH_SHORT).show();

    }
}