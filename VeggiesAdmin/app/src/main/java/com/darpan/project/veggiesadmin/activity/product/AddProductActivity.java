package com.darpan.project.veggiesadmin.activity.product;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.firebaseModal.CategoryModal;
import com.darpan.project.veggiesadmin.firebaseModal.ProductModalForeSale;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.darpan.project.veggiesadmin.constant.Constants.CATEGORY_COLLECTION;
import static com.darpan.project.veggiesadmin.constant.Constants.PICK_IMAGE_REQUEST;
import static com.darpan.project.veggiesadmin.constant.Constants.PRODUCT_IMAGES_STORAGE_REF;
import static com.darpan.project.veggiesadmin.constant.Constants.PRODUCT_LIST_COLLECTION;

public class AddProductActivity extends AppCompatActivity
        implements View.OnClickListener {
    private static final String TAG = "AddProductActivity:";
    private Spinner statusSpinner, prodTypeSpinner, prodCategorySpinner,optionQtySpinner;
    private EditText prodName, prodDesc, prodPrice, prodDesCount,
            prodStock,UniquePid,optionQty;
    private List<String> categoryList = new ArrayList<>();

    private ImageView productImage;

    private String category, prodStatus, prodType, prodId;
    private Boolean isPublished = true;
    private Uri ImageUri;
    private ProgressDialog pd;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference prodRef = firebaseFirestore.
            collection(PRODUCT_LIST_COLLECTION);
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private CollectionReference categoryRef = firebaseFirestore.
            collection(CATEGORY_COLLECTION);
    private CollectionReference ProdRef = firebaseFirestore
            .collection(PRODUCT_LIST_COLLECTION);
    private Button SaveBtn;
    private Spinner unitSpinner;
    private String spinnerSelectedUnit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setTitle("Add New Product");

        pd = new ProgressDialog(this);
        statusSpinner = findViewById(R.id.status_spinner);
        prodCategorySpinner = findViewById(R.id.category_spinner);
        prodTypeSpinner = findViewById(R.id.type_spinner);
        SaveBtn = findViewById(R.id.save_btn);
        unitSpinner=findViewById(R.id.unit_spinner);
        optionQtySpinner = findViewById(R.id.option_qty_spinner);


        ArrayAdapter<String> optionAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"0.25",
                        "0.5", "0.75", "1", "1.5", "2", "3", "4", "5"});
        optionAdapter.setDropDownViewResource(android.R.layout.
                simple_spinner_dropdown_item);
        optionQtySpinner.setAdapter(optionAdapter);


        ArrayAdapter<String> TypeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new String[]{"Regular", "Organic"});
        TypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prodTypeSpinner.setAdapter(TypeAdapter);


        ArrayAdapter<String> UnitAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"kg", "bunch","pcs","dozen","Lit","pack"});
        UnitAdapter.setDropDownViewResource(android.R.layout.
                simple_spinner_dropdown_item);
        unitSpinner.setAdapter(UnitAdapter);



        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Available", "Out Of Stock"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);


        prodCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (String) parent.getSelectedItem();
                // prodCategory.setText(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = (String) parent.getSelectedItem();
            }
        });


        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelectedUnit=parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerSelectedUnit=parent.getItemAtPosition(0).toString();
            }
        });

        prodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prodType = (String) parent.getSelectedItem();
                //prodType.setText(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prodType = (String) parent.getSelectedItem();
            }
        });

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prodStatus = (String) parent.getSelectedItem();
                // prodStatus.setText(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prodStatus = (String) parent.getSelectedItem();

            }
        });

        optionQtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!optionQty.getText().toString().isEmpty()){
                    String s = optionQty.getText().toString().trim();
                    if (s.charAt(s.length() - 1) == ',') {
                        s = s + parent.getSelectedItem().toString().trim();
                    }
                    s = s + "," + parent.getSelectedItem().toString().trim();
                    optionQty.setText(s);
                }else {
                    String s=parent.getSelectedItem().toString();
                    optionQty.setText(s);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String s = optionQty.getText().toString().trim();
                optionQty.setText(s);
            }
        });



        findView();
        setUpCategorySpinner();

    }

    private void setUpCategorySpinner() {
        if (!categoryList.isEmpty()) categoryList.clear();
        categoryRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            if (ds.exists()) {
                                CategoryModal CM = ds.toObject(CategoryModal.class);
                                if (CM != null)
                                    categoryList.add(CM.getCategoryName());
                            }
                        }

                        /*setting the spinner*/

                        if (categoryList.size() > 0) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, categoryList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            prodCategorySpinner.setAdapter(adapter);

                        }
                    } else {
                        showToast("No category");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: exception=" + e.getMessage());
            }
        });
    }

    private void findView() {
        UniquePid=findViewById(R.id.edt_unique_pid);
        optionQty=findViewById(R.id.edt_option_qty);


        prodName = findViewById(R.id.edt_name);
        prodDesc = findViewById(R.id.edt_desc);

        prodPrice = findViewById(R.id.edt_price);
        prodDesCount = findViewById(R.id.edt_discount);
      /*  prodQty = findViewById(R.id.edt_qty);*/
       /* prodUnit = findViewById(R.id.edt_unit);*/
        prodStock = findViewById(R.id.edt_stock);
        productImage = findViewById(R.id.product_img);

        /*setting clicks*/
        productImage.setOnClickListener(this);
        SaveBtn.setOnClickListener(this);

    }

    private void setTitle(String title) {

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    private void showPd(String t) {
        pd.setTitle(t);
        pd.show();
    }

    private void hidePd() {
        pd.cancel();
    }

    private void showToast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }


    private boolean checkEdtText() {
        if (prodName.getText().toString().isEmpty()) {
            prodName.setError("Name Required");
            return false;
        }

        if (prodDesc.getText().toString().isEmpty()) {
            prodDesc.setError("Description Required");
            return false;
        }

        if (prodPrice.getText().toString().isEmpty()) {
            prodPrice.setError("Name Required");
            return false;
        }

        if (prodDesCount.getText().toString().isEmpty()) {
            prodDesCount.setError("Enter ZERO For None");
            return false;
        }

        if (prodStock.getText().toString().isEmpty()) {
            prodStock.setError("Stock Required");
            return false;
        }

        if (UniquePid.getText().toString().isEmpty()) {
            UniquePid.setError("Item Id Is required");
            return false;
        }

        if (optionQty.getText().toString().isEmpty()) {
            optionQty.setError("Option Qty Is required");
            return false;
        }

        return true;

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
            Log.d(TAG, "onActivityResult: IMAGE URI="+ImageUri.toString());
            Glide.with(this)
                    .load(ImageUri)
                    .error(R.drawable.empty)
                    .into(productImage);
            //  uploadCover();

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.product_img:
                SelectPhotos();

                break;

            case R.id.save_btn:
                closeKeyboard();

                if (ImageUri != null && !ImageUri.toString().isEmpty()) {
                    saveData();
                    Log.d(TAG, "onClick: ImagUri="+ImageUri.toString());
                }

                else
                    showToast("Please Select Image");


        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void saveData() {
        if (checkEdtText()) {
            showPd("Saving Data");
            ProductModalForeSale Pms = new ProductModalForeSale();
            Pms.setProductName(prodName.getText().toString().trim());
            Pms.setProductDescription(prodDesc.getText().toString());
            //   Pms.setProductImage(PMS.getProductImage()); will set when image uploaded
            Pms.setProductCategory(category.trim());
            //  Pms.setProductId(PMS.getProductId());//will be set when we create doc in collec
            Pms.setisPublished(isPublished);

            // String p = prodPrice.getText().toString().replace("Rs ", "");
            Pms.setProductPrice(Integer.parseInt(
                    prodPrice.getText().toString().trim()
            ));
            //String d = prodDesCount.getText().toString().replace("Rs ", "");
            Pms.setProductDiscount(Integer.parseInt(
                    prodDesCount.getText().toString().trim()
            ));

            Pms.setProductQuantity(1);

            Pms.setProductStatus(prodStatus.trim());
            Pms.setProductType(prodType.trim());
            Pms.setProductUnit(spinnerSelectedUnit);

            Pms.setStockQuantity(Integer.
                    parseInt(prodStock.getText().toString().trim()));

            Pms.setUniquePid(UniquePid.getText().toString().trim());

            String[]other=optionQty.getText().toString().split("\\s*,\\s*");
            List<String>othersList= Arrays.asList(other);

            Pms.setOptionQty(othersList);

            /*first we create a document to get the id*/

            prodRef.add(Pms)
                    .addOnSuccessListener(documentReference ->
                            uploadImage(documentReference.getId(), Pms))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hidePd();
                            showToast(e.getMessage());
                            Log.d(TAG, "onFailure: E=" + e.getMessage());
                        }
                    });


        }else {
            showToast("Please Select an image also");
        }
    }

    private void uploadImage(String docId, ProductModalForeSale PMS) {
        showPd("Uploading Image");
        StorageReference prodImagStorage = storage.getReference(PRODUCT_IMAGES_STORAGE_REF);
        final StorageReference fileReference = prodImagStorage.child(
                getFileName(ImageUri) + "-" +
                        prodName.getText().toString().trim()
                        + "-" + System.currentTimeMillis()
                        + "." + getFileExtension(ImageUri));
        fileReference.putFile(ImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        /*now save the new doc with image*/
                        fileReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        PMS.setProductImage(uri.toString());
                                        PMS.setProductId(docId);
                                        ProdRef.document(docId)
                                                .set(PMS, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        showToast("Saved");
                                                        hidePd();
                                                    }
                                                });

                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.getMessage());
                hidePd();
            }
        });
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


}