package com.darpan.project.veggiesadmin.activity.areaBlock;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.adapter.blocks.BlockNamesAdapter;
import com.darpan.project.veggiesadmin.adapter.blocks.BlockNumberAdapter;
import com.darpan.project.veggiesadmin.firebaseModal.blocks.BlockName;
import com.darpan.project.veggiesadmin.firebaseModal.blocks.BlockNumber;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.darpan.project.veggiesadmin.constant.Constants.AREA_BLOCK_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.AREA_KEY_BLOCK_NAME;
import static com.darpan.project.veggiesadmin.constant.Constants.AREA_KEY_BLOCK_NUMBER;
import static com.darpan.project.veggiesadmin.constant.Constants.LIMIT;
import static com.darpan.project.veggiesadmin.constant.Constants.PAGE_COUNT;

public class AreaBlockActivity extends AppCompatActivity
        implements
        SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView blockNameRv, blockNumberRv;
    private BlockNamesAdapter blockNamesAdapter;
    private BlockNumberAdapter blockNumberAdapter;
    private FirebaseFirestore firebaseFirestore;
    private Button addBlockName, addBlockNumberBtn, dialogOk, dialogCancel;
    private static final String TAG = "AreaBlockActivity: ";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Dialog dialog;
    private EditText dialogEditText;
   /* private Spinner blockNumberSpinner;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> blocNumberList = new ArrayList<>();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_block);

        findView();

    }

    private void findView() {


        firebaseFirestore = FirebaseFirestore.getInstance();
        blockNameRv = findViewById(R.id.area_block_rv);
        blockNumberRv = findViewById(R.id.area_block_no_rv);
        addBlockName = findViewById(R.id.add_block_name_btn);
        addBlockNumberBtn = findViewById(R.id.add_block_number_btn);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);

        addBlockName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                dialogOk.setOnClickListener(c -> {
                    if (checkEdt()) {
                        saveBlockName();
                        dialog.dismiss();
                    }
                });

            }
        });

        addBlockNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditText.setHint("Enter Block Number");
                dialog.show();

                dialogOk.setOnClickListener(c -> {
                    if (checkEdt()) {
                        saveBlockNo();
                        dialog.dismiss();
                    }
                });
            }
        });
        initialiseDialog();
        getBlockNames();
        getBlockNumbers();

    }


    private void initialiseDialog() {
        dialog = new Dialog(AreaBlockActivity.this, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_change_item);

        dialogEditText = dialog.findViewById(R.id.edit_text);
        dialogEditText.setHint("Enter Block Name");

        /*  blockNumberSpinner = dialog.findViewById(R.id.block_number_spinner);*/
        dialogOk = dialog.findViewById(R.id.Ok_btn);
        dialogCancel = dialog.findViewById(R.id.cancel_btn);

        dialogCancel.setOnClickListener(v -> dialog.dismiss());


    }

    private void getBlockNames() {
        CollectionReference AreaRef = firebaseFirestore.
                collection(AREA_BLOCK_COLLEC)
                .document("blocks")
                .collection("blockNames");

        Query query = AreaRef.
                orderBy("blockName", Query.Direction.ASCENDING)
                .limit(LIMIT);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();

        FirestorePagingOptions<BlockName> options =
                new FirestorePagingOptions.Builder<BlockName>()
                        .setQuery(query, config, snapshot -> {
                            BlockName Modal =
                                    snapshot.toObject(BlockName.class);
                            //ModalList.add(Modal);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                          /*  if (Modal != null)
                                Modal.setSnapId(id);*/
                            return Objects.requireNonNull(Modal);
                        })
                        .build();

        blockNamesAdapter = new BlockNamesAdapter(options);
        blockNameRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        blockNameRv.setItemAnimator(new DefaultItemAnimator());
        blockNameRv.setAdapter(blockNamesAdapter);
        blockNamesAdapter.startListening();
        /*  blockNamesAdapter.setItemClick(AreaBlockActivity.this);*/

        blockNamesAdapter.setOnloadingStateChange(loadingState -> {
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
        blockNamesAdapter.setItemClick(new BlockNamesAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(DocumentSnapshot snapshot, int position) {
                Log.d(TAG, "setOnItemClick: called");
            }

            @Override
            public void OnDeleteClick(DocumentSnapshot snapshot, int pos) {
                Log.d(TAG, "OnDeleteClick: called");
                AreaRef.document(snapshot.getId())
                        .delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(),
                            "Name Deleted", Toast.LENGTH_SHORT).show();
                    blockNamesAdapter.refresh();

                });
            }

            @Override
            public void OnNameChangeClick(DocumentSnapshot snapshot, int pos) {
                dialog.show();
                dialogEditText.setText(snapshot.getString(AREA_KEY_BLOCK_NAME));
                dialogOk.setOnClickListener(c -> {
                    if (checkEdt()) {
                        dialog.dismiss();
                        Map<String, Object> nameMap = new HashMap<>();
                        nameMap.put(AREA_KEY_BLOCK_NAME, dialogEditText.getText().toString().trim());
                        AreaRef.document(snapshot.getId())
                                .set(nameMap, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AreaBlockActivity.this,
                                            "Name Updated",
                                            Toast.LENGTH_SHORT).show();
                                    blockNamesAdapter.refresh();

                                });

                    }
                });
            }
        });


    }

    private void getBlockNumbers() {
        CollectionReference AreaRef = firebaseFirestore.
                collection(AREA_BLOCK_COLLEC)
                .document("blocks")
                .collection("blockNumbers");

        Query query = AreaRef.
                orderBy("blockNumber", Query.Direction.ASCENDING)
                .limit(LIMIT);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();

        FirestorePagingOptions<BlockNumber> options =
                new FirestorePagingOptions.Builder<BlockNumber>()
                        .setQuery(query, config, snapshot -> {
                            BlockNumber Modal =
                                    snapshot.toObject(BlockNumber.class);
                            //ModalList.add(Modal);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                          /*  if (Modal != null)
                                Modal.setSnapId(id);*/
                            return Objects.requireNonNull(Modal);
                        })
                        .build();

        blockNumberAdapter = new BlockNumberAdapter(options);

        blockNumberRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        blockNumberRv.setItemAnimator(new DefaultItemAnimator());
        blockNumberRv.setAdapter(blockNumberAdapter);
        blockNumberAdapter.startListening();
        /*  blockNumberAdapter.setItemClick(AreaBlockActivity.this);*/

        blockNumberAdapter.setOnloadingStateChange(loadingState -> {
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


        blockNumberAdapter.setItemClick(new BlockNumberAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(DocumentSnapshot snapshot, int position) {
                Log.d(TAG, "setOnItemClick: called blockNumberAdapter");
            }

            @Override
            public void OnDeleteClick(DocumentSnapshot snapshot, int pos) {
                AreaRef.document(snapshot.getId())
                        .delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(),
                            "Name Deleted", Toast.LENGTH_SHORT).show();
                    blockNumberAdapter.refresh();

                });
            }

            @Override
            public void OnNameChangeClick(DocumentSnapshot snapshot, int pos) {
                dialog.show();
                dialogEditText.setText(snapshot.getString(AREA_KEY_BLOCK_NUMBER));
                dialogOk.setOnClickListener(c -> {
                    if (checkEdt()) {
                        dialog.dismiss();
                        Map<String, Object> Map = new HashMap<>();
                        Map.put(AREA_KEY_BLOCK_NUMBER,
                                dialogEditText.getText().toString().trim());

                        AreaRef.document(snapshot.getId())
                                .set(Map, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AreaBlockActivity.this,
                                            "Number Updated",
                                            Toast.LENGTH_SHORT).show();
                                    blockNumberAdapter.refresh();

                                });

                    }
                });
            }
        });


    }


    private boolean checkEdt() {
        if (dialogEditText.getText().toString().trim().isEmpty()) {
            dialogEditText.setError("Name Require");
            return false;
        }
        return true;
    }

    private void saveBlockName() {
        swipeRefreshLayout.setRefreshing(true);
        CollectionReference areaRef = firebaseFirestore.
                collection(AREA_BLOCK_COLLEC)
                .document("blocks")
                .collection("blockNames");

        String name = dialogEditText.getText().toString().trim();
        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("blockName", name);
        areaRef.add(nameMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "onSuccess: area block doc id="
                            + documentReference.getId());

                    Toast.makeText(getApplicationContext(), "New  Block Name Added",
                            Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    blockNamesAdapter.refresh();

                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: Exception=" + e.getMessage());
                    Toast.makeText(AreaBlockActivity.this,
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveBlockNo() {
        swipeRefreshLayout.setRefreshing(true);
        CollectionReference areaRef = firebaseFirestore.
                collection(AREA_BLOCK_COLLEC)
                .document("blocks")
                .collection("blockNumbers");

        String number = dialogEditText.getText().toString().trim();
        // areaBlockModal.setBlockNumber(blockNumberSpinner.getSelectedItem().toString().trim());
        Map<String, Object> numberMap = new HashMap<>();
        numberMap.put("blockNumber", number);
        areaRef.add(numberMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "onSuccess: area block doc id="
                            + documentReference.getId());

                    Toast.makeText(getApplicationContext(), "New  Block Number Added",
                            Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    blockNumberAdapter.refresh();

                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: Exception=" + e.getMessage());
                    Toast.makeText(AreaBlockActivity.this,
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRefresh() {
        blockNamesAdapter.refresh();
        blockNumberAdapter.refresh();
    }


    //private method of your class
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (blockNamesAdapter != null)
            blockNamesAdapter.stopListening();
        if (blockNumberAdapter != null)
            blockNumberAdapter.stopListening();
    }
}