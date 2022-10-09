package com.darpan.project.vegies.activity.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.Utils.Utiles;
import com.darpan.project.vegies.firebaseModal.AreaModal;
import com.darpan.project.vegies.firebaseModal.UserModal;
import com.darpan.project.vegies.firebaseModal.blocks.BlockName;
import com.darpan.project.vegies.firebaseModal.blocks.BlockNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.appevents.AppEventsLogger.getUserID;
import static com.darpan.project.vegies.constant.Constants.AREA_COLLEC;
import static com.darpan.project.vegies.constant.Constants.BLOCK_NAME;
import static com.darpan.project.vegies.constant.Constants.BLOCK_NUMBER;
import static com.darpan.project.vegies.constant.Constants.DEFAULT;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;
import static com.darpan.project.vegies.constant.Constants.SP_AREA;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.USER_IMAGE_STORAGE_PATH_NAME;
import static com.darpan.project.vegies.constant.Constants.areaBlock_collection_name;
import static com.darpan.project.vegies.constant.Constants.areaDeliverCharge;
import static com.darpan.project.vegies.constant.Constants.areaName;
import static com.darpan.project.vegies.constant.Constants.isPublished;
import static com.darpan.project.vegies.constant.Constants.u_blockName;
import static com.darpan.project.vegies.constant.Constants.u_blockNo;
import static com.darpan.project.vegies.constant.Constants.u_email;
import static com.darpan.project.vegies.constant.Constants.u_fullAddress;
import static com.darpan.project.vegies.constant.Constants.u_landmark;
import static com.darpan.project.vegies.constant.Constants.u_mobile;
import static com.darpan.project.vegies.constant.Constants.u_photoUrl;
import static com.darpan.project.vegies.constant.Constants.u_pin;
import static com.darpan.project.vegies.constant.Constants.u_status;
import static com.darpan.project.vegies.constant.Constants.u_userId;
import static com.darpan.project.vegies.constant.Constants.u_username;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 100;
    // @BindView(R.id.ed_username)
    private TextInputEditText edUsername;

    //  @BindView(R.id.ed_landmark)
    private TextInputEditText edLandmark;
    //  @BindView(R.id.ed_email)
    private TextInputEditText edEmail;
    // @BindView(R.id.ed_alternatmob)
    private TextInputEditText edAlternatemob;
    // @BindView(R.id.txt_save)
    private TextView btnSign, dialogText;

    //SessionManager sessionManager;


    // @BindView(R.id.ed_hoousno)
    private TextInputEditText fullAddress;
    //  @BindView(R.id.ed_society)
    private TextInputEditText edSociety;

    // @BindView(R.id.ed_pinno)
    private TextInputEditText edPin;

    private static final String TAG = "ProfileActivity:";
    private String areaSelect;
    //  List<AreaD> areaDS = new ArrayList<>();
    // @BindView(R.id.spinner)
    private Spinner AreaSpinner, blockNoSpinner, blockNameSpinner;

    private ProgressDialog pd;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private CollectionReference areaRef = firebaseFirestore.collection(AREA_COLLEC);

    private CollectionReference userRef = firebaseFirestore.collection(USER_COLLECTION);

    private List<AreaModal> areaList = new ArrayList<>();
    private List<UserModal> userList = new ArrayList<>();
    private ArrayAdapter<String> AreaArrayAdapter;
    // private StringBuilder fullAddressBuilder = new StringBuilder("");
    private CircleImageView profileImage;
    private String imageFileName;
    private Uri ImageUri;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference(USER_IMAGE_STORAGE_PATH_NAME);
    private StorageTask mUploadTask;
    private LinearLayout saveLl;
    private Dialog confirmDialog;
    private Button dialogOkBtn, dialogCancelBtn;
    private ProgressBar profileProgressBar;

    private Map<String, Object> userMap = new HashMap<>();

    private String BLOCKNAME = "", BLOCKNUMBER = "", AREANAME = "";

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        pd = new ProgressDialog(ProfileActivity.this);

        InitialiseViews();

        getAreaBlocks();
        getArea();

        userMap.put(u_userId, getUserID());
        userMap.put(u_status, true);
        //SelectPhotos
    }

    private void InitialiseViews() {
        edUsername = findViewById(R.id.ed_username);
        edEmail = findViewById(R.id.ed_email);
        edAlternatemob = findViewById(R.id.ed_alternatmob);
        edLandmark = findViewById(R.id.ed_landmark);
        blockNameSpinner = findViewById(R.id.block_name_spinner);
        blockNoSpinner = findViewById(R.id.block_no_spinner);
        AreaSpinner = findViewById(R.id.area_spinner);
        fullAddress = findViewById(R.id.ed_full_addr);
        edPin = findViewById(R.id.ed_pin);
        profileImage = findViewById(R.id.profile_image);
        saveLl = findViewById(R.id.save_ll);
        profileProgressBar = findViewById(R.id.profile_progress_bar);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPhotos();
            }
        });

        saveLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkEmptyFields()) {
                    showConfirmDialog();
                }

            }
        });

    }

    private void showConfirmDialog() {
        confirmDialog = new Dialog(ProfileActivity.this, R.style.Theme_Dialog);
        confirmDialog.setContentView(R.layout.confirm_dailog_lyt);
        dialogCancelBtn = confirmDialog.findViewById(R.id.dialog_cancel);
        dialogOkBtn = confirmDialog.findViewById(R.id.dialog_ok);
        dialogText = confirmDialog.findViewById(R.id.dialog_text);

        dialogOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyFields()) {
                    saveAllData();
                    confirmDialog.cancel();
                }
            }
        });

        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.cancel();
            }
        });
        confirmDialog.show();
        if (fullAddress.getText() != null && !fullAddress.getText().toString().isEmpty())
            dialogText.setText(fullAddress.getText().toString().trim());
    }

    private void getArea() {
        Log.d(TAG, "getArea:called ");
        showPd("Loading Area..");
        if (!areaList.isEmpty()) areaList.clear();


        areaRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess:getArea ");
                hidePd();
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot DQS : task.getResult()) {
                        areaList.add(new AreaModal(
                                (String) DQS.get(areaName),
                                (String) DQS.get(areaDeliverCharge),
                                (boolean) DQS.get(isPublished)));
                    }

                    setAreaArrayAdapter(areaList);
                    Log.d(TAG, "onComplete:getArea ");

                } else {
                    hidePd();
                    Log.d(TAG, "onComplete: IS NOT SUCCESSFUL");
                    showToast("Nop Area");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "onFailure:getArea " + e.toString());
                hidePd();
            }
        });

    }

    private void getUserInfo() {
        Log.d(TAG, "getUserInfo:called ");
        String n = "getUserInfo";
        showPd("Loading Profile..");
        if (!userList.isEmpty()) userList.clear();
        DocumentReference userDoc = userRef.document(getUserID());

        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    setUserDataIfExistOnFirebase(documentSnapshot.getData());
                } else {
                    hidePd();
                    showSnackBar("No User Found");
                }
                Log.d(TAG, "onSuccess: " + n);
                hidePd();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePd();
                showToast(e.getMessage());
                Log.d(TAG, "onFailure: " + n);
            }
        });

       /* userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot DQS : task.getResult()) {
                        UserModal user = new UserModal(
                                (String) DQS.get(u_blockName),
                                (String) DQS.get(u_blockNo),
                                (String) DQS.get(u_email),
                                (String) DQS.get(u_fullAddress),
                                (String) DQS.get(u_landmark),
                                (String) DQS.get(u_mobile),
                                (String) DQS.get(u_username),
                                (String) DQS.get(u_photoUrl),
                                (int) DQS.get(u_pin),
                                (boolean) DQS.get(u_status),
                                (String) DQS.get(u_userId));
                        userList.add(user);
                        hidePd();
                    }
                } else {
                    Log.d(TAG, "onComplete: not successful");
                    hidePd();
                    showToast("No User Found");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                showToast(e.getMessage());
                hidePd();

            }
        });*/


    }


    private void getAreaBlocks() {
        CollectionReference blocknameRef = firebaseFirestore
                .collection(areaBlock_collection_name)
                .document("blocks")
                .collection("blockNames");
        CollectionReference blockNumRef = firebaseFirestore
                .collection(areaBlock_collection_name)
                .document("blocks")
                .collection("blockNumbers");


        Log.d(TAG, "getAreaBlocks:Called ");
        showPd("Loading Area blocks..");
        ArrayList<String> blockName = new ArrayList<>();
        ArrayList<String> blockNumber = new ArrayList<>();

        Task t1 = blocknameRef.orderBy("blockName", Query.Direction.ASCENDING).
                get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d(TAG, "onSuccess: GetBlockName");
            hidePd();
            for (QueryDocumentSnapshot DQS : queryDocumentSnapshots) {
                if (DQS.exists()) {
                    BlockName name = DQS.toObject(BlockName.class);
                    blockName.add(name.getBlockName().trim());
                }
            }
            setBlockNameAa(blockName);
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: " + e.toString());
            hidePd();
            Log.d(TAG, "onFailure: getBlockName");
        });

        Task t2 = blockNumRef.orderBy("blockNumber", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot DQS : queryDocumentSnapshots) {
                if (DQS.exists()) {
                    BlockNumber number = DQS.toObject(BlockNumber.class);
                    blockNumber.add(number.getBlockNumber().trim());
                }
            }
            setBlockNoAa(blockNumber);
        });

        Tasks.whenAllSuccess(t1, t2).addOnSuccessListener(objects -> getUserInfo());

     /*   CollectionReference areaBlockRef = FirebaseFirestore.getInstance().
                collection(areaBlock_collection_name);
        areaBlockRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess: getAreaBlock");
                hidePd();
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot DQS : task.getResult()) {
                        blockName.add(DQS.get(BLOCK_NAME).toString().trim());
                        blockNumber.add(DQS.get(BLOCK_NUMBER).toString().trim());
                    }

                    setBlockNameAa(blockName);
                    setBlockNoAa(blockNumber);
                    getUserInfo();

                    Log.d(TAG, "onComplete: getAreaBlock");
                } else {
                    hidePd();
                    Log.d(TAG, "onComplete: IS NOT SUCCESSFUL");
                    showToast("Failed To get Area Blocks");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "onFailure: " + e.toString());
                hidePd();
                Log.d(TAG, "onFailure: getAreaBlock");
            }
        });*/
    }


    private void setAreaArrayAdapter(List<AreaModal> areaList) {
        Log.d(TAG, "setAreaArrayAdapter: called");
        List<String> areaNames = new ArrayList<>();
        for (int i = 0; i < areaList.size(); i++) {
            areaNames.add(areaList.get(i).getAreaName());
        }

        AreaArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, areaNames);
        AreaArrayAdapter.
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AreaSpinner.setAdapter(AreaArrayAdapter);
        AreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                AREANAME = areaNames.get(position);
                String addr = "Block- " + BLOCKNAME.trim() + " " + BLOCKNUMBER.trim() + ", " + AREANAME;
                fullAddress.setText(addr);

                //saving th area for getting delivery charge in singleOrderSummary
                sp.edit().putString(SP_AREA, AREANAME).apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                AREANAME = AreaSpinner.getItemAtPosition(0).toString();
                String addr = "Block- " + BLOCKNAME.trim() + " " + BLOCKNUMBER.trim() + ", " + AREANAME;
                fullAddress.setText(addr);
                //saving th area for getting delivery charge in singleOrderSummary
                sp.edit().putString(SP_AREA, AREANAME).apply();
            }
        });

        setAreaStringOnAdapter();


    }


    private void setBlockNameAa(List<String> BlockNames) {
        Log.d(TAG, "setBlockNameAa: Called");

        ArrayAdapter<String> blockNameAa = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, BlockNames);

        blockNameAa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        blockNameSpinner.setAdapter(blockNameAa);
        blockNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BLOCKNAME = BlockNames.get(position);
                userMap.put(u_blockName, BlockNames.get(position));
                String addr = "Block- " + BLOCKNAME.trim() + " " + BLOCKNUMBER.trim() + ", " + AREANAME;
                fullAddress.setText(addr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                BLOCKNAME = blockNameSpinner.getItemAtPosition(0).toString().trim();
                String addr = "Block- " + BLOCKNAME.trim() + " " + BLOCKNUMBER.trim() + ", " + AREANAME;
                fullAddress.setText(addr);
            }
        });


    }

    private void setBlockNoAa(List<String> BlockNumber) {
        Log.d(TAG, "setBlockNoAa: called");
        ArrayAdapter<String> blockNoAa = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, BlockNumber);

        blockNoAa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        blockNoSpinner.setAdapter(blockNoAa);
        blockNoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BLOCKNUMBER = BlockNumber.get(position);

                userMap.put(u_blockNo, BlockNumber.get(position));
                String addr = "Block- " + BLOCKNAME.trim() + " " + BLOCKNUMBER.trim() + ", " + AREANAME;
                fullAddress.setText(addr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                BLOCKNUMBER = blockNoSpinner.getItemAtPosition(0).toString().trim();
                String addr = "Block- " + BLOCKNAME.trim() + " " + BLOCKNUMBER.trim() + ", " + AREANAME;
                fullAddress.setText(addr);
            }
        });


    }


    private void showToast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }

    private void showPd(String title) {

        pd.setTitle(title);
        pd.show();
    }

    private void hidePd() {
        pd.cancel();
    }

    private void showProgressbar() {
        if (profileProgressBar.getVisibility() == View.GONE)
            profileProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressbar() {
        if (profileProgressBar.getVisibility() == View.VISIBLE)
            profileProgressBar.setVisibility(View.GONE);
    }


    private void setUserDataIfExistOnFirebase(Map<String, Object> userModal) {
        Log.d(TAG, "setUserDataIfExistOnFirebase: ");

        if (userModal.get(u_email) != null) {
            edEmail.setText(userModal.get(u_email).toString().trim());
        }
        if (userModal.get(u_username) != null) {
            edUsername.setText(userModal.get(u_username).toString().trim());
        }

        if (userModal.get(u_blockName) != null) {
            String myString = userModal.get(u_blockName).toString().trim();//the value you want the position for
            int spinnerPosition = 0;
            //ArrayAdapter<String> myAdap = (ArrayAdapter<String>) blockNameSpinner.getAdapter();//cast to an ArrayAdapter
            SpinnerAdapter spinnerAdapter = blockNameSpinner.getAdapter();
            Log.d(TAG, "myAdap: u_blockName=" + myString);
            // int spinnerPosition = myAdap.getPosition(myString);
            //set the default according to value
            for (int i = 0; i < spinnerAdapter.getCount(); i++) {
                if (spinnerAdapter.getItem(i).toString().contains(myString)) {
                    spinnerPosition = i;
                }
            }

            blockNameSpinner.setSelection(spinnerPosition);

            userMap.put(u_blockName, myString);
        }

        if (userModal.get(u_blockNo) != null) {
            String myString = userModal.get(u_blockNo).toString().trim(); //the value you want the position for
            int spinnerPosition = 0;
            SpinnerAdapter spinnerAdapter = blockNoSpinner.getAdapter();
            Log.d(TAG, "myAdap: u_blockNo=" + myString);
            for (int i = 0; i < spinnerAdapter.getCount(); i++) {
                if (spinnerAdapter.getItem(i).toString().contains(myString)) {
                    spinnerPosition = i;
                }
            }

            blockNoSpinner.setSelection(spinnerPosition);
            userMap.put(u_blockNo, myString);
        }

        if (userModal.get(u_fullAddress) != null) {
            fullAddress.setText(userModal.get(u_fullAddress).toString().trim());

        }
        if (userModal.get(u_pin) != null) {
            edPin.setText(String.valueOf(userModal.get(u_pin)));


        }
        if (userModal.get(u_mobile) != null) {
            edAlternatemob.setText((userModal.get(u_mobile)).toString().trim());

        }
        if (userModal.get(u_landmark) != null) {
            edLandmark.setText((userModal.get(u_landmark)).toString().trim());

        }
        if (userModal.get(u_photoUrl) != null) {
            String photo = Utiles.getFBUrl(userModal.get(u_photoUrl).toString());
            Glide.with(ProfileActivity.this)
                    .load(photo)
                    .placeholder(R.drawable.empty)
                    .error(R.drawable.empty)
                    .dontAnimate()
                    .into(profileImage);
        }


    }

    private void setAreaStringOnAdapter() {
        SpinnerAdapter Sa = AreaSpinner.getAdapter();
        String myString = sp.getString(SP_AREA, DEFAULT);
        int spinnerPosition = 0;
        for (int i = 0; i < Sa.getCount(); i++) {
            if (myString != null && Sa.getItem(i).toString().contains(myString)) {
                spinnerPosition = i;
            }
        }

        AreaSpinner.setSelection(spinnerPosition);


    }

    private String getUserID() {
        return firebaseUser.getUid();
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(edPin.getRootView(), message,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    private boolean checkEmptyFields() {

     /*   if(ed_text.isEmpty() || ed_text.length() == 0 || ed_text.equals("") || ed_text == null)
        {
            //EditText is empty
        }*/

        if (edUsername.getText() != null && !edUsername.getText().toString().isEmpty()) {
            userMap.put(u_username, edUsername.getText().toString().trim());
        } else {
            edUsername.setError("Enter Name");
            return false;
        }

        if (edEmail.getText() != null && !edEmail.getText().toString().isEmpty() && isEmailValid(edEmail.getText().toString())) {
            userMap.put(u_email, edEmail.getText().toString().trim());

        } else {
            edEmail.setError("Enter valid Email");
            return false;
        }
        if (edPin.getText() != null && !edPin.getText().toString().isEmpty()) {
            userMap.put(u_pin, edPin.getText().toString().trim());
        } else {
            edPin.setError("Enter Pin");
            return false;
        }

        if (edAlternatemob.getText() != null && !edAlternatemob.getText().toString().isEmpty()) {
            userMap.put(u_mobile, edAlternatemob.getText().toString().trim());
        } else {
            edAlternatemob.setError("Enter Mobile");
            return false;
        }

        if (edLandmark.getText() != null && !edLandmark.getText().toString().isEmpty()) {
            userMap.put(u_landmark, edLandmark.getText().toString().trim());
        } else {
            edLandmark.setError("Enter Landmark");
            return false;
        }

        if (fullAddress.getText() != null && !fullAddress.getText().toString().isEmpty()) {
            userMap.put(u_fullAddress, fullAddress.getText().toString().trim());
        } else {
            fullAddress.setError("Enter Address");
            return false;
        }


        return true;

    }


    private void SelectPhotos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            ImageUri = data.getData();

            Glide.with(ProfileActivity.this)
                    .load(ImageUri)
                    .placeholder(R.drawable.empty)
                    .into(profileImage);
            //  uploadCover();

        }
    }


    private void uploadAndSaveData() {
        //upload profile and save the user data
        showProgressbar();
        if (ImageUri != null) {
            imageFileName = getFileName(ImageUri);
            final StorageReference fileReference = mStorage.child(imageFileName + "-" + System.currentTimeMillis()
                    + "." + getFileExtension(ImageUri));
            mUploadTask = fileReference.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().
                                    addOnSuccessListener(uri -> {
                                        Map<String, Object> newfield = new HashMap<>();
                                        newfield.put(u_photoUrl, uri.toString());
                                        userRef.document(getUserID()).set(newfield, SetOptions.merge());

                                    }).addOnFailureListener(e -> {
                                showToast(e.getMessage());
                            });
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            showToast("Canceled");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showSnackBar(e.getMessage());
                        }
                    });
        }


        userRef.document(getUserID()).set(userMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Data Saved");
                        hideProgressbar();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Try Again");
                        hideProgressbar();
                    }
                });


    }

    private void saveAllData() {
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            //this method will prevent accidental uploads
            showToast("Upload in Progress");
        } else {
            uploadAndSaveData();
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}