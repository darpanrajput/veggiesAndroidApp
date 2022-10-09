package com.darpan.project.veggiesadmin.activity.feedback;

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
import com.darpan.project.veggiesadmin.adapter.FeedBackAdapter;
import com.darpan.project.veggiesadmin.firebaseModal.FeedbackModal;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import static com.darpan.project.veggiesadmin.constant.Constants.FEEDBACK_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.LIMIT;
import static com.darpan.project.veggiesadmin.constant.Constants.PAGE_COUNT;

public class FeedBackActivity extends AppCompatActivity
implements SwipeRefreshLayout.OnRefreshListener {
    private FeedBackAdapter feedBackAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String TAG = "FeedBackActivity:";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_bck);
        recyclerView = findViewById(R.id.feedback_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);
       getData();
    }

    private void getData() {
        CollectionReference productListRef = firebaseFirestore.
                collection(FEEDBACK_COLLEC);

        Query query = productListRef.
                orderBy("userName", Query.Direction.ASCENDING)
                .limit(LIMIT);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();

        FirestorePagingOptions<FeedbackModal> options =
                new FirestorePagingOptions.Builder<FeedbackModal>()
                        .setQuery(query, config, snapshot -> {
                            FeedbackModal Modal =
                                    snapshot.toObject(FeedbackModal.class);
                            //ModalList.add(Modal);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                          /*  if (Modal != null)
                                Modal.setSnapId(id);*/
                            return Objects.requireNonNull(Modal);
                        })
                        .build();

        feedBackAdapter = new FeedBackAdapter(options);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(feedBackAdapter);

        feedBackAdapter.startListening();
        feedBackAdapter.setItemClick((snapshot, position) -> {

        });

        feedBackAdapter.setOnloadingStateChange(loadingState -> {
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
    protected void onDestroy() {
        super.onDestroy();
        if (feedBackAdapter!=null){
            feedBackAdapter.startListening();
        }
    }

    @Override
    public void onRefresh() {
        feedBackAdapter.refresh();
    }
}