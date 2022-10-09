package com.darpan.project.veggiesadmin.activity.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.adapter.user.customerAdapter;
import com.darpan.project.veggiesadmin.firebaseModal.UserModal;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import static com.darpan.project.veggiesadmin.constant.Constants.LIMIT;
import static com.darpan.project.veggiesadmin.constant.Constants.PAGE_COUNT;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_COLLECTION;

public class CustomerActivity extends AppCompatActivity
implements SwipeRefreshLayout.OnRefreshListener
,customerAdapter.OnItemClick{
    private static final String TAG = "CustomerActivity:";
    private RecyclerView userRv;
    private customerAdapter cA;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        userRv = findViewById(R.id.user_rv);
        userRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);


        getdata();

    }

    private void getdata() {

        CollectionReference productListRef = FirebaseFirestore.getInstance().
                collection(USER_COLLECTION);

        Query query = productListRef.
                orderBy("name", Query.Direction.ASCENDING)
                .limit(LIMIT);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();


        FirestorePagingOptions<UserModal> options =
                new FirestorePagingOptions.Builder<UserModal>()
                        .setQuery(query, config, snapshot -> {
                            UserModal Modal =
                                    snapshot.toObject(UserModal.class);
                            //ModalList.add(Modal);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                          /*  if (Modal != null)
                                Modal.setSnapId(id);*/
                            return Objects.requireNonNull(Modal);
                        })
                        .build();


        cA = new customerAdapter(options);

        userRv.setItemAnimator(new DefaultItemAnimator());
        userRv.setAdapter(cA);
        cA.startListening();
        cA.setItemClick(CustomerActivity.this);

        cA.setOnloadingStateChange(loadingState -> {
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


    @Override
    public void onRefresh() {
        cA.refresh();

    }

    @Override
    public void setOnItemClick(DocumentSnapshot snapshot, int position) {
        Log.d(TAG, "setOnItemClick: snapId="+snapshot.getId());
        Log.d(TAG, "setOnItemClick: Name="+snapshot.getString("name"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cA!=null){cA.startListening();}
    }
}