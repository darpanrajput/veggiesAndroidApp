package com.darpan.project.veggiesadmin.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;


import com.darpan.project.veggiesadmin.activity.admin.AdminActivity;
import com.darpan.project.veggiesadmin.activity.area.ShowAreaActivity;
import com.darpan.project.veggiesadmin.activity.areaBlock.AreaBlockActivity;
import com.darpan.project.veggiesadmin.activity.category.CategoylListActivity;
import com.darpan.project.veggiesadmin.activity.delivery.DeliveryTimeActivity;
import com.darpan.project.veggiesadmin.activity.excel.ReadExcelActivity;
import com.darpan.project.veggiesadmin.activity.feedback.FeedBackActivity;
import com.darpan.project.veggiesadmin.activity.order.OrderListActivity;
import com.darpan.project.veggiesadmin.activity.product.AddProductActivity;
import com.darpan.project.veggiesadmin.activity.product.ProductListActivity;
import com.darpan.project.veggiesadmin.activity.sales.SalesActivity;
import com.darpan.project.veggiesadmin.activity.sendNotification.NotifyUserActivity;
import com.darpan.project.veggiesadmin.activity.upload.UploadProductsActivity;
import com.darpan.project.veggiesadmin.activity.user.CustomerActivity;
import com.darpan.project.veggiesadmin.fcm.Token;
import com.darpan.project.veggiesadmin.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.darpan.project.veggiesadmin.constant.Constants.ADMIN_TOKEN_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.CATEGORY_IMAGE;
import static com.darpan.project.veggiesadmin.constant.Constants.CATEGORY_REF;
import static com.darpan.project.veggiesadmin.constant.Constants.FIREBASE_USER_ID;
import static com.darpan.project.veggiesadmin.constant.Constants.NOTI_COUNT;
import static com.darpan.project.veggiesadmin.constant.Constants.PICK_IMAGE_REQUEST;
import static com.darpan.project.veggiesadmin.constant.Constants.PREF_NAME;
import static com.darpan.project.veggiesadmin.constant.Constants.TOKEN_DOC;
import static com.darpan.project.veggiesadmin.constant.Constants.TOKEN_KEY;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity:";

    private SharedPreferences sp;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;


    private Context context;
    private String USER_ID;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    private Uri ImageUri;


    private Dialog dialog;
    private EditText dialogEditText;
    private TextView imageFileName;
    private Button dialogOk, dialogCancel,
            uploadImageBtn;

    private ProgressDialog pd;


    //private UploadListAdapter uploadListAdapter;

    // private List<modal> modalList;

    private StorageReference mStorage = FirebaseStorage.getInstance().getReference(CATEGORY_IMAGE);

    private StorageTask mUploadTask;
    private FirebaseFirestore firestoreRef = FirebaseFirestore.getInstance();
    private CollectionReference categoryRef = firestoreRef.collection(CATEGORY_REF);

    private TextView todaysOrder,
            Pending, Deliver, Cancelled, feedback, sales, Customer, Rejected;


    private LinearLayout todaysOrderLL,
            PendingLL, DeliverLL, CancelledLL, feedbackLL, salesLL, CustomerLL,
            RejectedLL;

    private ProgressBar PendingPb, DeliverPb, CancelledPb,
            feedbackPb, salesPb, CustomerPb, RejectedPb;
    private TextView notiCount;
    private static int reqPermission = 123;
    private RelativeLayout rooLyt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*drawer initialization*/
        InitiateDrawer();
        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        /*user authentication*/
        context = MainActivity.this;
        user = FirebaseAuth.getInstance().getCurrentUser();
        USER_ID = getIntent().getStringExtra(FIREBASE_USER_ID);
        firebaseFirestore = FirebaseFirestore.getInstance();

        todaysOrder = findViewById(R.id.todays_order);
        rooLyt = findViewById(R.id.root_rl);
        Deliver = findViewById(R.id.deliver);
        DeliverPb = findViewById(R.id.delivered_pb);

        Pending = findViewById(R.id.pending);
        PendingPb = findViewById(R.id.pending_pb);

        Cancelled = findViewById(R.id.cancelled);
        CancelledPb = findViewById(R.id.cancelled_pb);

        feedback = findViewById(R.id.feedback);
        feedbackPb = findViewById(R.id.feedback_pb);

        sales = findViewById(R.id.sales);
        salesPb = findViewById(R.id.sales_pb);

        Customer = findViewById(R.id.customer);
        CustomerPb = findViewById(R.id.customer_pb);

        Rejected = findViewById(R.id.rejected);
        RejectedPb = findViewById(R.id.rejected_pb);
        notiCount = findViewById(R.id.noti_count);
        /*....................................................................................*/
        todaysOrderLL = findViewById(R.id.today_ll);
        DeliverLL = findViewById(R.id.deliverd_ll);
        PendingLL = findViewById(R.id.pending_ll);
        CancelledLL = findViewById(R.id.cancelled_ll);
        feedbackLL = findViewById(R.id.feedback_ll);
        salesLL = findViewById(R.id.sales_ll);
        CustomerLL = findViewById(R.id.customer_ll);
        RejectedLL = findViewById(R.id.rejected_ll);

        notiCount = findViewById(R.id.noti_count);

        if (sp.getInt(NOTI_COUNT, 0) > 0) {
            notiCount.setText(String.valueOf(sp.getInt(NOTI_COUNT, 0)));
        } else {
            notiCount.setVisibility(View.GONE);
        }

//        setting clicks;

        todaysOrderLL.setOnClickListener(this);
        DeliverLL.setOnClickListener(this);
        PendingLL.setOnClickListener(this);
        CancelledLL.setOnClickListener(this);
        feedbackLL.setOnClickListener(this);
        salesLL.setOnClickListener(this);
        CustomerLL.setOnClickListener(this);
        RejectedLL.setOnClickListener(this);
        CheckUser();

        Log.d(TAG, "onCreate: ");


        /*today's order*/


        /*
         *//*total pending order*//*
        if (sp.getInt(TOTAL_PENDING, -1) == -1) {
            Pending.setText(String.valueOf(getTotal(
                    PENDING,
                    TOTAL_PENDING
            )));
        } else {
            Pending.setText(
                    String.valueOf(
                            sp.getInt(TOTAL_PENDING, 0)
                    ));
        }


        *//*Total delivered..*//*
        if (sp.getInt(TOTAL_DELIVERED, -1) == -1) {
            Deliver.setText(String.valueOf(getTotal(
                    DELIVERED,
                    TOTAL_DELIVERED
            )));
        } else {
            Deliver.setText(
                    String.valueOf(
                            sp.getInt(TOTAL_DELIVERED, 0)
                    ));
        }

        *//*Total Cancelled*//*

        if (sp.getInt(TOTAL_CANCELED, -1) == -1) {
            Cancelled.setText(String.valueOf(getTotal(
                    CANCELLED,
                    TOTAL_CANCELED
            )));
        } else {
            Cancelled.setText(
                    String.valueOf(
                            sp.getInt(TOTAL_CANCELED, 0)
                    ));
        }

        *//*total rejected*//*
        if (sp.getInt(TOTAL_REJECTED, -1) == -1) {
            Rejected.setText(String.valueOf(getTotal(
                    REJECTED,
                    TOTAL_REJECTED
            )));
        } else {
            Rejected.setText(
                    String.valueOf(
                            sp.getInt(TOTAL_REJECTED, 0)
                    ));
        }



        *//*Total Sales*//*
        if (sp.getInt(TOTAL_SALES, -1) == -1) {
            sales.setText(String.valueOf(getTotalSale()));
        } else {
            sales.setText(String.valueOf(
                    sp.getInt(TOTAL_SALES, 0)
            ));
        }

        *//*Total feedback*//*

        if (sp.getInt(TOTAL_FEEDBACK, -1) == -1) {
            feedback.setText(String.valueOf(getTotalFeedBack()));
        } else {
            feedback.setText(String.valueOf(
                    sp.getInt(TOTAL_FEEDBACK, 0)
            ));
        }

        *//*get total customer*//*
        if (sp.getInt(TOTAL_CUSTOMER, -1) == -1) {
            Customer.setText(String.valueOf(getTotalCustomer()));
        } else {
            Customer.setText(String.valueOf(
                    sp.getInt(TOTAL_CUSTOMER, 0)
            ));
        }*/

        ReadReq();


    }

    @AfterPermissionGranted(123)
    private void ReadReq() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.d(TAG, "ReadReq: Has Permission");
        } else {
            Log.d(TAG, "ReadReq: Request Permssion");
            EasyPermissions.requestPermissions(this,
                    "We need permissions To Perform Some Task",
                    reqPermission, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    private void CheckUser() {

        if (user == null) {
             /*if user does not exit that means account is deleted that's why
            moving back to Main Login Activity*/
            showSnackBar("No User Found ,Moving Back to Home in 3 sec");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, AdminActivity.class));
                    finish();

                }
            }, 3000);

        } else {
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


    }

    private void updateToken(String refreshToken) {
        Token token1 = new Token(refreshToken);
        Map<String, Object> tokenMAp = new HashMap<>();
        tokenMAp.put(TOKEN_KEY, token1.getToken());
        Log.d(TAG, "updateToken: " + token1.getToken());

        FirebaseFirestore.getInstance()
                .collection(ADMIN_TOKEN_COLLEC)
                .document(TOKEN_DOC)
                .set(tokenMAp);

    }

    private void setTitle(String title) {

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    private void InitiateDrawer() {
        drawerLayout = findViewById(R.id.drader_lyt);
        navigationView = findViewById(R.id.navigation_header);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_sub_category);
        setSupportActionBar(toolbar);
        //Objects.requireNonNull(getSupportActionBar()).setTitle("Your Coupons");
        setTitle("Dashboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.dash_board:
                    drawerLayout.closeDrawers();
                    setTitle("Dashboard");
                    break;

                case R.id.add_category:
                    setTitle("Category");
                    createDialog();
                    drawerLayout.closeDrawers();

                    break;

                case R.id.category:
                    startActivity(new Intent(
                            MainActivity.this,
                            CategoylListActivity.class));

                    break;


                case R.id.add_product:
                    startActivity(new Intent(MainActivity.this,
                            AddProductActivity.class));
                    setTitle("Product");

                    break;

                case R.id.product:
                    startActivity(new Intent(MainActivity.this,
                            ProductListActivity.class));


                    break;
                case R.id.see_oder:
                    startActivity(new Intent(
                            this, OrderListActivity.class));
                    break;


                case R.id.area_block:
                    setTitle("Area Block");
                    startActivity(new Intent(MainActivity.this,
                            AreaBlockActivity.class));

                    break;

                case R.id.area:
                    startActivity(new Intent(MainActivity.this,
                            ShowAreaActivity.class));
                    break;


                case R.id.customer:
                    setTitle("Customer");
                    startActivity(new Intent(
                            MainActivity.this,
                            CustomerActivity.class
                    ));
                    drawerLayout.closeDrawers();
                    break;

                case R.id.notification:
                    startActivity(new Intent(MainActivity.this,
                            NotifyUserActivity.class));
                    break;

                case R.id.time:
                    startActivity(new Intent(MainActivity.this,
                            DeliveryTimeActivity.class));
                    drawerLayout.closeDrawers();
                    break;

                case R.id.logout:
                    signOut();
                    drawerLayout.closeDrawers();
                    break;

                default:
                    return false;
            }
            return false;
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);// ad the drawer listner when to open
        actionBarDrawerToggle.syncState();//o syn all the activity like close and open

    }

    private void showProgressDialog() {
        pd = new ProgressDialog(MainActivity.this);
        pd.show();

    }

    private void cancelPd() {
        Handler handler = new Handler();
        handler.postDelayed(() -> pd.cancel(), 2000);


    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.
                make(rooLyt.getRootView(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();

    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(MainActivity.this, AdminActivity.class));
                        finish();
                    }
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
            imageFileName.setText(getFileName(ImageUri));
            //  uploadCover();

        }

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Log.d(TAG, "onActivityResult: Requested Return To same");
        }
    }


    private void createDialog() {
        Log.d(TAG, "createDialog: ");
        dialog = new Dialog(MainActivity.this, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_add_product);
        dialogEditText = dialog.findViewById(R.id.category_edit_text);
        /*  uploadExcelBtn = dialog.findViewById(R.id.upload_excel_btn);*/
        uploadImageBtn = dialog.findViewById(R.id.upload_image_btn);
        imageFileName = dialog.findViewById(R.id.image_file_name);
        /*  excelFileName = dialog.findViewById(R.id.excel_file_name);*/

        dialogOk = dialog.findViewById(R.id.Ok_btn);
        dialogCancel = dialog.findViewById(R.id.cancel_btn);

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /* uploadExcelBtn.setVisibility(View.VISIBLE);*/
                /* excelFileName.setVisibility(View.VISIBLE);*/

            }
        });
        uploadImageBtn.setOnClickListener(v -> {
            SelectPhotos();
            /*  uploadExcelBtn.setVisibility(View.INVISIBLE);*/
            /*excelFileName.setVisibility(View.INVISIBLE);*/

        });
        dialog.show();
        dialogOk.setOnClickListener(v -> {
            String name = dialogEditText.getText().toString().trim();
            if (!name.isEmpty() & !name.equals("")) {
                dialogEditText.setError(null);
                closeKeyboard();
                saveCategory(name);
                dialog.dismiss();
            } else {
                dialogEditText.setError("Name required");

            }

        });


    }


    private void saveCategory(String fileName) {
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            //this method will prevent accidental uploads
            Toast.makeText(MainActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
        } else {
            uploadData(fileName);
        }
    }

    private void uploadData(String category_name) {
        if (ImageUri != null) {
            showProgressDialog();
            final StorageReference fileReference = mStorage.child(category_name + "-" + System.currentTimeMillis()
                    + "." + getFileExtension(ImageUri));
            mUploadTask = fileReference.putFile(ImageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().
                            addOnSuccessListener(uri -> {
                                Map<String, Object> newfield = new HashMap<>();
                                newfield.put("categoryName", category_name);
                                newfield.put("categoryImage", uri.toString());
                                categoryRef.document().set(newfield);
                                hideProgressdialog();
                                if (dialog != null) {
                                    dialog.dismiss();
                                    dialog = null;
                                }

                                showSnackBar("Added new Category");


                            }).addOnFailureListener(e -> {
                        cancelPd();
                        showToast(e.getMessage());
                    }))
                    .addOnProgressListener(taskSnapshot -> {

                    })
                    .addOnFailureListener(e -> {
                        cancelPd();
                        showSnackBar(e.getMessage());

                    });


        }
    }


    private void hideProgressdialog() {
        pd.cancel();
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


   /* private int getTotal(String filterOption, String TOTAL_PREF_OPTION) {
        PendingPb.setVisibility(View.VISIBLE);
        DeliverPb.setVisibility(View.VISIBLE);
        RejectedPb.setVisibility(View.VISIBLE);
        Cancelled.setVisibility(View.VISIBLE);

        List<OrderPlacedModal> OPM = new ArrayList<>();

        CollectionReference orderRef =
                firebaseFirestore.collection(ORDER_PLACED_COLLEC);
        orderRef.whereEqualTo("orderStatus", filterOption)
                .get()
                .addOnSuccessListener(qds -> {
                    if (qds != null && !qds.isEmpty()) {
                        OPM.addAll(qds.toObjects(OrderPlacedModal.class));
                        sp.edit().putInt(TOTAL_PREF_OPTION, OPM.size()).apply();
                        Log.d(TAG, "getTotalPending: OPM SIZE=" + OPM.size());
                        Log.d(TAG, "getTotalPending: PREF=" + sp.getInt(TOTAL_PREF_OPTION,
                                0));

                        PendingPb.setVisibility(View.GONE);
                        DeliverPb.setVisibility(View.GONE);
                        RejectedPb.setVisibility(View.GONE);
                        Cancelled.setVisibility(View.GONE);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        PendingPb.setVisibility(View.GONE);
                        DeliverPb.setVisibility(View.GONE);
                        RejectedPb.setVisibility(View.GONE);
                        Cancelled.setVisibility(View.GONE);
                        showToast(e.getMessage());
                    }
                });

        return OPM.size();
    }

    private int getTodayOrder() {
        List<OrderPlacedModal> OPM = new ArrayList<>();
        CollectionReference orderRef =
                firebaseFirestore.collection(ORDER_PLACED_COLLEC);

        orderRef.whereEqualTo("dateOfOrder",
                String.valueOf(Util.getToday()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {
                        if (qds != null && !qds.isEmpty()) {
                            OPM.addAll(qds.toObjects(OrderPlacedModal.class));
                            Log.d(TAG, "onSuccess: OPM SIZE TODAY=" + OPM.size());
                        }
                    }
                });

        return OPM.size();
    }


    private int getTotalFeedBack() {
        feedbackPb.setVisibility(View.VISIBLE);
        List<FeedbackModal> FM = new ArrayList<>();

        CollectionReference feedbackRef =
                firebaseFirestore.collection(FEEDBACK_COLLEC);

        feedbackRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {
                        if (qds != null && !qds.isEmpty()) {
                            FM.addAll(qds.toObjects(FeedbackModal.class));
                            Log.d(TAG, "onSuccess: OPM SIZE TODAY=" +
                                    FM.size());
                            sp.edit().putInt(TOTAL_FEEDBACK, FM.size()).apply();
                            feedbackPb.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                feedbackPb.setVisibility(View.GONE);
                showToast(e.getMessage());
            }
        });


        return FM.size();
    }

    private int getTotalCustomer() {
        CustomerPb.setVisibility(View.VISIBLE);
        List<UserModal> UM = new ArrayList<>();
        CollectionReference userRef =
                firebaseFirestore.collection(USER_COLLECTION);

        userRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot qds) {
                if (qds != null && !qds.isEmpty()) {
                    UM.addAll(qds.toObjects(UserModal.class));
                    Log.d(TAG, "onSuccess: OPM SIZE TODAY=" + UM.size());
                    CustomerPb.setVisibility(View.GONE);
                    sp.edit().putInt(TOTAL_CUSTOMER, UM.size()).apply();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                feedbackPb.setVisibility(View.GONE);
                showToast(e.getMessage());
            }
        });


        return UM.size();

    }

    private int getTotalSale() {
        salesPb.setVisibility(View.VISIBLE);
        int[] totalSum = {0};
        CollectionReference orderRef =
                firebaseFirestore.collection(ORDER_PLACED_COLLEC);
        orderRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty())

                            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                if (ds.exists() && ds.getString("totalPrice") != null) {

                                    if (ds.getString("orderStatus").equalsIgnoreCase(DELIVERED)) {
                                        totalSum[0] = Integer.
                                                parseInt(ds.getString("totalPrice")
                                                        .trim()) + totalSum[0];

                                    }
                                }
                                sp.edit().putInt(TOTAL_SALES, totalSum[0]).apply();
                            }
                        salesPb.setVisibility(View.GONE);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        salesPb.setVisibility(View.GONE);
                        showToast(e.getMessage());
                    }
                });

        return totalSum[0];


    }
*/



    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh, menu);

        menu.getItem(0).setTitle("Merge Excel Data");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            startActivity(new Intent(MainActivity.this,
                    MergeActivity.class));
            return false;


        }
        return super.onOptionsItemSelected(item);

    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.today_ll:
                startActivity(new Intent(
                        MainActivity.this,
                        OrderListActivity.class));
                sp.edit().putInt(NOTI_COUNT, 0).apply();
                break;

            case R.id.pending_ll:
                startActivity(new Intent(
                        MainActivity.this,
                        OrderListActivity.class
                ));
                break;


            case R.id.deliverd_ll:
                startActivity(new Intent(
                        MainActivity.this,
                        OrderListActivity.class
                ));
                break;


            case R.id.rejected_ll:
                startActivity(new Intent(
                        MainActivity.this,
                        OrderListActivity.class
                ));
                break;


            case R.id.cancelled_ll:
                startActivity(new Intent(
                        MainActivity.this,
                        OrderListActivity.class
                ));
                break;


            case R.id.sales_ll:
                startActivity(new Intent(MainActivity.this
                        , SalesActivity.class));

                break;


            case R.id.feedback_ll:
                startActivity(new Intent(MainActivity.this
                        , FeedBackActivity.class));

                break;


            case R.id.customer_ll:
                startActivity(new Intent(
                        MainActivity.this,
                        CustomerActivity.class
                ));
                break;


        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

}