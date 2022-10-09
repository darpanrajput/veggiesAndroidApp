package com.darpan.project.vegies.activity.newUIDesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.activity.newUIDesign.adapters.categoryAdapter;
import com.darpan.project.vegies.activity.filtered.FliteredActivity;
import com.darpan.project.vegies.firebaseModal.CategoryModal;

import java.util.Objects;

import static com.darpan.project.vegies.constant.Constants.categoryName;

public class AllCategories extends AppCompatActivity {
    private static final String TAG = "AllCategories; ";
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    CollectionReference categoryRef=firebaseFirestore
            .collection("veggiesApp/Veggies/democategory");
    categoryAdapter catAdapter;
    private RecyclerView categoryRv;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);
        categoryRv=findViewById(R.id.category_rv);
        refreshLayout=findViewById(R.id.swipe_refresh);

        getCategoryDataCall();
    }

    private void getCategoryDataCall() {
        String category = getIntent().getStringExtra(categoryName);
        Log.d(TAG, "getCategoryDataCall: category=" + category);

        Query query = categoryRef.orderBy(categoryName, Query.Direction.ASCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<CategoryModal> options =
                new FirestorePagingOptions.Builder<CategoryModal>()
                        .setQuery(query, config, new SnapshotParser<CategoryModal>() {
                            @NonNull
                            @Override
                            public CategoryModal parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                CategoryModal categoryModal = snapshot.toObject(CategoryModal.class);
                                //categoryModalArrayList.add(categoryModal);
                                String id = snapshot.getId();
                                Log.d(TAG, "parseSnapshot: id=" + id);
                                if (categoryModal != null)
                                    categoryModal.setSnapId(id);
                                return Objects.requireNonNull(categoryModal);
                            }
                        })
                        .build();

        catAdapter = new categoryAdapter(options);

        categoryRv.setAdapter(catAdapter);

        catAdapter.startListening();

        catAdapter.setItemClick((Ds, position) -> {
            Log.d(TAG, "setOnItemClick: Category=" + Ds.getString(categoryName));
            startActivity(new Intent(AllCategories.this,
                    FliteredActivity.class)
                    .putExtra(categoryName, Ds.getString(categoryName)));
        });

        catAdapter.setOnLoadingStateChange(loadingState -> {
            switch (loadingState) {
                case LOADING_INITIAL:
                    Log.d(TAG, "onStateChange: LOADING_INITIAL");
                    refreshLayout.setRefreshing(true);
                    break;

                case LOADING_MORE:
                    Log.d(TAG, "onStateChange:LOADING_MORE ");
                    refreshLayout.setRefreshing(true);
                    break;

                case LOADED:
                    Log.d(TAG, "onStateChange: LOADED");
                    refreshLayout.setRefreshing(false);
                    break;

                case ERROR:
                    Toast.makeText(AllCategories.this,
                            "Error Occurred!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onStateChange: ERROR");
                    refreshLayout.setRefreshing(false);
                    break;

                case FINISHED:
                    refreshLayout.setRefreshing(false);
                    Log.d(TAG, "onStateChange:FINISHED ");
                    break;
            }
        });


    }
}