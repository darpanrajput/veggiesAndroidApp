package com.darpan.project.vegies.activity.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.adapters.category.notification.NotificationAdapter;
import com.darpan.project.vegies.firebaseModal.NotificationModal;

import java.util.Objects;

import static com.darpan.project.vegies.constant.Constants.PAGE_COUNT;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView notiRv;
    private NotificationAdapter notificationAdapter;
    private static final String TAG = "NotificationActivity:";
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setTitle("Notifications");

        notiRv = findViewById(R.id.noti_rv);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            id = user.getUid();
            setData();
        } else {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }

    }

    private void setData() {
        ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Loading..");
        pd.show();
        CollectionReference notiRef = firebaseFirestore.collection(USER_COLLECTION)
                .document(id).collection("notification");

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();

        FirestorePagingOptions<NotificationModal> options =
                new FirestorePagingOptions.Builder<NotificationModal>()
                        .setQuery(notiRef, config, snapshot -> {
                            NotificationModal modal = snapshot.toObject(NotificationModal.class);
                            //categoryModalArrayList.add(categoryModal);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                            if (modal != null)
                                modal.setSnapId(id);

                            return Objects.requireNonNull(modal);
                        }).build();

        notiRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        notificationAdapter = new NotificationAdapter(options);
        notiRv.setItemAnimator(new DefaultItemAnimator());
        notiRv.setAdapter(notificationAdapter);
        notificationAdapter.startListening();
        pd.dismiss();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.option_menu, menu);
        menu.getItem(0).setTitle("Clear All");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.cancel:
                clearAll();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAll() {
        CollectionReference notiRef = FirebaseFirestore
                .getInstance().collection(USER_COLLECTION)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("notification");
        notiRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot ds : queryDocumentSnapshots) {
                        notiRef.document(ds.getId()).delete();
                    }
                    notificationAdapter.refresh();
                    Toast.makeText(NotificationActivity.this, "All Cleared",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationAdapter != null) {
            notificationAdapter.stopListening();
        }
    }
}