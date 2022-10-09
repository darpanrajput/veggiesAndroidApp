package com.darpan.project.veggiesadmin.activity.area;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.adapter.area.AreaAdapter;
import com.darpan.project.veggiesadmin.firebaseModal.AreaModal;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.Objects;

import static com.darpan.project.veggiesadmin.constant.Constants.AREA_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.LIMIT;
import static com.darpan.project.veggiesadmin.constant.Constants.PAGE_COUNT;

public class ShowAreaActivity extends AppCompatActivity
        implements AreaAdapter.OnItemClick,
        SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView areaRv;
    private AreaAdapter areaAdapter;
    private FirebaseFirestore firebaseFirestore;
    private Button dialogOk;
    private static final String TAG = "ShowAreaActivity: ";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Dialog dialog;
    private EditText dialogEditText; /*dialogDelCharge*/;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_area);

        firebaseFirestore = FirebaseFirestore.getInstance();
        areaRv = findViewById(R.id.area_rv);
        Button addNewAreabtn = findViewById(R.id.add_area_btn);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);

         addNewAreabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                dialogOk.setOnClickListener(c -> {
                    if (checkEdt()) {
                        saveArea();
                        dialog.dismiss();
                    }
                });

            }
        });

        initialiseDialog();
        getData();


    }

    private void initialiseDialog() {
        dialog = new Dialog(ShowAreaActivity.this, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_change_item);

        dialogEditText = dialog.findViewById(R.id.edit_text);
        dialogEditText.setHint("Enter Area Name");

        dialogOk = dialog.findViewById(R.id.Ok_btn);
        Button dialogCancel = dialog.findViewById(R.id.cancel_btn);

       /* dialogDelCharge = dialog.findViewById(R.id.edit_delivery_charge);
        dialogDelCharge.setVisibility(View.VISIBLE);
        dialogDelCharge.setHint("Enter Delivery Charge");
*/
        dialogCancel.setOnClickListener(v -> dialog.dismiss());


    }

    private void saveArea() {
        swipeRefreshLayout.setRefreshing(true);
        CollectionReference areaRef = firebaseFirestore.
                collection(AREA_COLLEC);
        AreaModal areaModal = new AreaModal();
        areaModal.setAreaName(dialogEditText.getText().toString().trim());
        areaModal.setDeliveryCharge("0");

        areaRef.add(areaModal)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "onSuccess: area doc id=" + documentReference.getId());

                    Toast.makeText(getApplicationContext(), "New Area Added",
                            Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    areaAdapter.refresh();

                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: Exception=" + e.getMessage());
                    Toast.makeText(ShowAreaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean checkEdt() {
       /* if (dialogDelCharge.getText().toString().trim().isEmpty()) {
            dialogDelCharge.setError("Charge Require");
            return false;
        }*/

        if (dialogEditText.getText().toString().trim().isEmpty()) {
            dialogEditText.setError("Name Require");
            return false;
        }
        return true;
    }

    private void getData() {
        CollectionReference AreaRef = firebaseFirestore.
                collection(AREA_COLLEC);

        Query query = AreaRef.
                orderBy("areaName", Query.Direction.ASCENDING)
                .limit(LIMIT);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();

        FirestorePagingOptions<AreaModal> options =
                new FirestorePagingOptions.Builder<AreaModal>()
                        .setQuery(query, config, snapshot -> {
                            AreaModal Modal =
                                    snapshot.toObject(AreaModal.class);
                            //ModalList.add(Modal);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                          /*  if (Modal != null)
                                Modal.setSnapId(id);*/
                            return Objects.requireNonNull(Modal);
                        })
                        .build();

        areaAdapter = new AreaAdapter(options);
        areaRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        areaRv.setItemAnimator(new DefaultItemAnimator());
        areaRv.setAdapter(areaAdapter);
        areaAdapter.startListening();
        areaAdapter.setItemClick(ShowAreaActivity.this);

        areaAdapter.setOnloadingStateChange(loadingState -> {
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

        if (areaAdapter != null)
            areaAdapter.stopListening();
    }

    @Override
    public void setOnItemClick(DocumentSnapshot snapshot, int position) {
        Log.d(TAG, "setOnItemClick: id=" + snapshot.getId());
    }

    @Override
    public void OnDeleteClick(DocumentSnapshot snapshot, int pos) {
        Log.d(TAG, "OnDeleteClick: ");

        CollectionReference AreaRef = firebaseFirestore.
                collection(AREA_COLLEC);
        AreaRef.document(snapshot.getId())
                .delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(getApplicationContext(),
                    "Deleted", Toast.LENGTH_SHORT).show();
            areaAdapter.refresh();
        });

    }

    @Override
    public void OnNameChangeClick(DocumentSnapshot snapshot, int pos) {
        dialog.show();
        dialogEditText.setText(snapshot.getString("areaName"));
       /* dialogDelCharge.setText(snapshot.getString("deliveryCharge"));

        dialogDelCharge.setVisibility(View.GONE);*/
        dialogOk.setOnClickListener(c -> {
            if (checkEdt()) {
                dialog.dismiss();
                AreaModal areaModal = new AreaModal();
                areaModal.setAreaName(dialogEditText.getText().toString().trim());
                /*areaModal.setDeliveryCharge(dialogDelCharge.getText().toString().trim());*/
                areaModal.setDeliveryCharge("0");

                CollectionReference AreaRef = firebaseFirestore.
                        collection(AREA_COLLEC);
                AreaRef.document(snapshot.getId())
                        .set(areaModal, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ShowAreaActivity.this,
                                    "value Updated",
                                    Toast.LENGTH_SHORT).show();
                            areaAdapter.refresh();
                        });

            }
        });


    }

    @Override
    public void onRefresh() {
        areaAdapter.refresh();
    }
}