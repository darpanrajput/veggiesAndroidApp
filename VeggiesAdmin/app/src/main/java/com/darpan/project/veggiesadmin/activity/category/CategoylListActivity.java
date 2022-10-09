package com.darpan.project.veggiesadmin.activity.category;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.adapter.CategoryListAdapter;
import com.darpan.project.veggiesadmin.firebaseModal.CategoryModal;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


import static com.darpan.project.veggiesadmin.constant.Constants.CATEGORY_COLLECTION;
import static com.darpan.project.veggiesadmin.constant.Constants.CATEGORY_IMAGE;
import static com.darpan.project.veggiesadmin.constant.Constants.PAGE_COUNT;
import static com.darpan.project.veggiesadmin.constant.Constants.PICK_IMAGE_REQUEST;
import static com.darpan.project.veggiesadmin.constant.Constants.categoryImage;
import static com.darpan.project.veggiesadmin.constant.Constants.categoryName;

public class CategoylListActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, CategoryListAdapter.OnItemClick {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Context context;
    private RecyclerView categoryRev;
    private CategoryListAdapter categoryAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "CategoylListActivity:";

    private Dialog dialog;
    private EditText dialogEditText;

    private Button dialogOk, dialogCancel;
    private ProgressDialog pd;
    private CollectionReference categoryRf = firebaseFirestore.collection(CATEGORY_COLLECTION);
    private FirebaseStorage storage;
    private Uri ImageUri;
    private StorageTask mUploadTask;
    private DocumentSnapshot newImageDocumentSnapShot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoyl_list);

        context = CategoylListActivity.this;
        storage=FirebaseStorage.getInstance();

        findView();
    }

    private void findView() {
        pd = new ProgressDialog(context);

        categoryRev = findViewById(R.id.category_rv);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setRefreshing(false);

        swipeRefreshLayout.setOnRefreshListener(this);

        settingUpCategoryData();
    }

    private void settingUpCategoryData() {
        CollectionReference categoryRef = firebaseFirestore.collection(CATEGORY_COLLECTION);
        Query query = categoryRef.orderBy(categoryName, Query.Direction.ASCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();

        FirestorePagingOptions<CategoryModal> options =
                new FirestorePagingOptions.Builder<CategoryModal>()
                        .setQuery(query, config, snapshot -> {
                            CategoryModal categoryModal = snapshot.toObject(CategoryModal.class);
                            //categoryModalArrayList.add(categoryModal);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                            if (categoryModal != null)
                                categoryModal.setSnapId(id);
                            return Objects.requireNonNull(categoryModal);
                        }).build();

        categoryRev.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false));
        categoryAdapter = new CategoryListAdapter(options);
        categoryRev.setItemAnimator(new DefaultItemAnimator());
        categoryRev.setAdapter(categoryAdapter);
        categoryAdapter.startListening();
        categoryAdapter.setItemClick(CategoylListActivity.this);


        categoryAdapter.setOnloadingStateChange(loadingState -> {
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
                    Toast.makeText(context, "Error Occurred!", Toast.LENGTH_SHORT).show();
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


    private void createDialog(String docId) {
        LinearLayout singleImageUploadLl;
        Log.d(TAG, "createDialog: called ");
        dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_add_product);

        singleImageUploadLl = dialog.findViewById(R.id.single_image_upload_ll);
        singleImageUploadLl.setVisibility(View.GONE);

        dialogEditText = dialog.findViewById(R.id.category_edit_text);
        dialogOk = dialog.findViewById(R.id.Ok_btn);
        dialogCancel = dialog.findViewById(R.id.cancel_btn);

        dialogCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = dialogEditText.getText().toString().trim();
                if (!name.isEmpty() & !name.equals("")) {
                    dialogEditText.setError(null);
                    closeKeyboard();
                    saveCategory(name, docId);
                    dialog.dismiss();
                } else {
                    dialogEditText.setError("Name required");

                }

            }
        });


    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void saveCategory(String name, String docId) {
        showPd("saving..");
        Map<String, Object> newNam = new HashMap<>();
        newNam.put(categoryName, name);
        categoryRf.document(docId)
                .update(newNam).addOnSuccessListener(aVoid -> {
            hidePd();
            showSnackBar("Saved ");
            categoryAdapter.refresh();

        }).addOnFailureListener(e -> {
            hidePd();
            showToast("Failed");
            showToast(e.getMessage());

        });
    }

    private void SelectPhotos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            ImageUri = data.getData();

            //  uploadCover();
            saveImage(getFileName(ImageUri), newImageDocumentSnapShot);


        }
    }

    private void saveImage(String fileName, DocumentSnapshot ds) {
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            //this method will prevent accidental uploads
            showToast("Uploading is in progress");
        } else {
            uploadImage(fileName, ds);
        }
    }

    private void uploadImage(String name, DocumentSnapshot sp) {
        StorageReference mStorage = storage.getReference(CATEGORY_IMAGE);
        if (ImageUri != null) {
            showPd("Uploading..");
            final StorageReference fileReference = mStorage.child(name + "-" + System.currentTimeMillis()
                    + "." + getFileExtension(ImageUri));
            mUploadTask = fileReference.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().
                                    addOnSuccessListener(uri -> {
                                        Map<String, Object> newfield = new HashMap<>();
                                        newfield.put("categoryImage", uri.toString());
                                        categoryRf.document(sp.getId()).update(newfield)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        /*delete old image of the same category*/
                                                        storage.getReferenceFromUrl(sp.getString(categoryImage)).delete();
                                                        hidePd();
                                                        showSnackBar("Success");
                                                        categoryAdapter.refresh();
                                                    }
                                                });


                                    }).addOnFailureListener(e -> {
                                hidePd();
                                showToast(e.getMessage());
                            });

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hidePd();
                            showSnackBar(e.getMessage());

                        }
                    });


        }
    }

    private String getFileExtension(Uri image) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(image));

    }

    private String getFileName(Uri uri) {
        String result = null;

        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));


                }
            }
                /* Log.e(TAG, e.getMessage());
                System.out.println(TAG + "Exception in get fileName ():" + e.getMessage());*/

        }
        try {
            if (result == null) {
                result = uri.getPath();
                int cut = 0;
                if (result != null) {
                    cut = result.lastIndexOf("/");
                }
                if (cut != -1) {
                    if (result != null) {
                        result = result.substring(cut + 1);
                    }

                }
            }
        } catch (Exception e) {
            System.out.println(TAG + " Exception wile eauting result==null:" + e.getMessage());
        }

        return result;

    }



    private void showPd(String title) {
        pd.setTitle(title);
        pd.show();

    }

    private void hidePd() {
        pd.cancel();

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(swipeRefreshLayout.getRootView(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();

    }

    @Override
    public void setOnItemClick(DocumentSnapshot snapshot, int position) {
        Log.d(TAG, "setOnItemClick: " + snapshot.getId());
    }

    @Override
    public void OnDeleteClick(DocumentSnapshot sp, int i) {
        showPd("loading...");

        if (sp.getString(categoryImage) != null) {
            StorageReference imageRef = storage.getReferenceFromUrl(sp.getString(categoryImage));
            imageRef.delete().addOnSuccessListener(aVoid -> {
                categoryRf.document(sp.getId()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast("Item Deleted");
                                categoryAdapter.refresh();
                                hidePd();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast("can not be deleted");
                                hidePd();
                            }
                        });

            }).addOnFailureListener(e -> {
                showToast(e.getMessage());
                Log.d(TAG, "onFailure: " + e.getMessage());
                hidePd();

            });
            } else {
                showToast("Null Values");
            }

    }

    @Override
    public void OnImageClick(DocumentSnapshot snapshot, int pos) {
        Log.d(TAG, "OnImageClick:" + pos);
        newImageDocumentSnapShot = snapshot;
        SelectPhotos();

    }

    @Override
    public void OnNameChangeClick(DocumentSnapshot snapshot, int pos) {
        Log.d(TAG, "OnNameChangeClick: " + pos);
        createDialog(snapshot.getId());
    }

    @Override
    public void onRefresh() {
        categoryAdapter.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (categoryAdapter != null)
            categoryAdapter.stopListening();
    }
}