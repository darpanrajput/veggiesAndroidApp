package com.darpan.project.veggiesadmin.activity.product;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputType;
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
import android.widget.TextView;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.darpan.project.veggiesadmin.constant.Constants.CATEGORY_COLLECTION;
import static com.darpan.project.veggiesadmin.constant.Constants.PICK_IMAGE_REQUEST;
import static com.darpan.project.veggiesadmin.constant.Constants.PRODUCT_IMAGES_STORAGE_REF;
import static com.darpan.project.veggiesadmin.constant.Constants.PRODUCT_LIST_COLLECTION;
import static com.darpan.project.veggiesadmin.constant.Constants.productId;

public class ChangeProductActivity extends AppCompatActivity
        implements View.OnClickListener {
    private TextView prodName, prodDesc, prodCategory, prodPrice, prodDesCount, prodQty,
            prodStatus, prodType, prodUnit, prodId, prodStock, UniquePid; /*optionQty*/

    private ImageView productImage, visibilityIcon;

    private ProductModalForeSale PMS;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference prodRef = firebaseFirestore.
            collection(PRODUCT_LIST_COLLECTION);

    private CollectionReference categoryRef = firebaseFirestore.
            collection(CATEGORY_COLLECTION);
    private ListenerRegistration listenerRegistration;
    private DocumentReference prodDoc;
    private ProgressDialog pd;
    private static final String TAG = "ChangeProductActivity: ";
    private Spinner statusSpinner, prodTypeSpinner, prodCategorySpinner, optionQtySpinner;
    private Button saveBtn, dialogOk, dialogCancel;
    private List<String> categoryList = new ArrayList<>();
    private Dialog dialog;
    private EditText dialogEditText;
    private Uri ImageUri;

    private EditText edtOptionqty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_product);
        pd = new ProgressDialog(this);

        String docID = getIntent().getStringExtra(productId);
        if (docID != null)
            prodDoc = prodRef.document(docID);

        findView();
    }

    private void findView() {
        UniquePid = findViewById(R.id.unique_pid);
        edtOptionqty = findViewById(R.id.edt_option_qty);
        prodName = findViewById(R.id.prod_name);
        prodDesc = findViewById(R.id.prod_desc);
        prodCategory = findViewById(R.id.prod_category);
        prodPrice = findViewById(R.id.prod_price);
        prodDesCount = findViewById(R.id.prod_discount);
        prodQty = findViewById(R.id.prod_qty);
        prodStatus = findViewById(R.id.prod_status);
        prodType = findViewById(R.id.prod_type);

        prodUnit = findViewById(R.id.prod_unit);
        prodId = findViewById(R.id.order_id);
        prodStock = findViewById(R.id.prod_stock);
        productImage = findViewById(R.id.product_img);
        visibilityIcon = findViewById(R.id.ic_visibility);

        saveBtn = findViewById(R.id.save_btn);
        statusSpinner = findViewById(R.id.status_spinner);
        prodCategorySpinner = findViewById(R.id.category_spinner);
        prodTypeSpinner = findViewById(R.id.type_spinner);
        optionQtySpinner = findViewById(R.id.option_qty_spinner);


        /*setting clicks*/
        UniquePid.setOnClickListener(this);
        /*optionQty.setOnClickListener(this
        );*/
        prodName.setOnClickListener(this);
        prodDesc.setOnClickListener(this);
        prodPrice.setOnClickListener(this);
        prodDesCount.setOnClickListener(this);
        prodQty.setOnClickListener(this);
        prodUnit.setOnClickListener(this);
        prodStock.setOnClickListener(this);
        productImage.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

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


        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Available", "Out Of Stock"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);


        prodCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) parent.getSelectedItem();
                prodCategory.setText(s);
                Log.d(TAG, "onItemSelected: called");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String s = prodCategory.getText().toString();
                prodCategory.setText(s);
            }
        });

        prodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) parent.getSelectedItem();
                prodType.setText(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String s =prodType.getText().toString();
                prodType.setText(s);
            }
        });

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) parent.getSelectedItem();
                prodStatus.setText(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prodStatus.setText(prodStatus.getText().toString());
            }
        });

        optionQtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = edtOptionqty.getText().toString().trim();
                if (s.charAt(s.length() - 1) == ',') {
                    s = s + parent.getSelectedItem().toString().trim();
                }
                s = s + "," + parent.getSelectedItem().toString().trim();
                edtOptionqty.setText(s);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String s = edtOptionqty.getText().toString().trim();
                edtOptionqty.setText(s);
            }
        });


        initialiseDialog();

        getCategories();


    }

    private void initialiseDialog() {
        dialog = new Dialog(ChangeProductActivity.this, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_change_item);
        dialogEditText = dialog.findViewById(R.id.edit_text);

        dialogOk = dialog.findViewById(R.id.Ok_btn);
        dialogCancel = dialog.findViewById(R.id.cancel_btn);

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

    }

    private void getCategories() {
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

    private void getData() {
        if (prodDoc != null) {
            showPd("Please Wait...");
            prodDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot ds) {
                    if (ds.exists()) {
                        PMS = ds.toObject(ProductModalForeSale.class);
                        showData();
                    } else {
                        hidePd();
                        showToast("document not exist");

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hidePd();
                    showToast(e.getMessage());
                    Log.d(TAG, "onFailure: e=" + e.getMessage());
                }
            });

        } else {
            showToast("No Document");
            Log.d(TAG, "getData: Null document");
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        showPd("Loading..");
        if (prodDoc != null) {
            listenerRegistration =
                    prodDoc.addSnapshotListener((documentSnapshot, e) -> {
                        Log.d(TAG, "onStart: addSnapshotListener");
                        if (e != null) {
                            showToast(e.getMessage());
                            Log.d(TAG, "onEvent: exception=" +
                                    e.getMessage());
                            return;
                        }
                        //show data
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            PMS = documentSnapshot.toObject(ProductModalForeSale.class);
                            getData();

                        }
                    });


            hidePd();
        }

    }

    private void showData() {
        Log.d(TAG, "showData: called");
        if (PMS != null) {
            Glide.with(this)
                    .load(PMS.getProductImage())
                    .error(R.drawable.empty)
                    .thumbnail(Glide.with(this).load(R.drawable.ezgifresize))
                    .into(productImage);

            if (PMS.getisPublished()) {
                visibilityIcon.setImageResource(R.drawable.ic_visibility_on);
            } else {
                visibilityIcon.setImageResource(R.drawable.ic_visibility_off_24);
            }

           /* ArrayAdapter<String> optionQtyAdpter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,
                   PMS.getOptionQty());
            optionQtyAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            optionQtySpinner.setAdapter(optionQtyAdpter);
*/

            StringBuilder stringBuilder = new StringBuilder();
            for (String option : PMS.getOptionQty()) {
                stringBuilder.append(option).append(",");

            }
            edtOptionqty.setText(stringBuilder.toString().trim());
            UniquePid.setText(String.valueOf(PMS.getUniquePid()));

            prodName.setText(PMS.getProductName());
            prodDesc.setText(PMS.getProductDescription());
            prodType.setText(PMS.getProductType());
            prodTypeSpinner.setSelection(getIndex(prodTypeSpinner,
                    PMS.getProductType()));


            prodId.setText(PMS.getProductId());
            prodCategory.setText(PMS.getProductCategory());
            prodCategorySpinner.setSelection(getIndex(prodCategorySpinner, PMS.getProductCategory()));

            prodPrice.setText(getRupee(PMS.getProductPrice()));
            prodDesCount.setText(String.format("%s", PMS.getProductDiscount() + "% "));
            prodQty.setText(String.valueOf(PMS.getProductQuantity()));
            prodStock.setText(String.valueOf(PMS.getStockQuantity()));

            prodUnit.setText(PMS.getProductUnit());
            prodStatus.setText(PMS.getProductStatus());
            statusSpinner.setSelection(getIndex(statusSpinner, PMS.getProductStatus()));

            hidePd();
        } else {
            Log.d(TAG, "showData: Null PMS");
        }
    }


    private String getRupee(int s) {
        return "Rs " + s;
    }

    @Override
    protected void onStop() {
        super.onStop();
        listenerRegistration.remove();
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
            changeImage();

            //  uploadCover();

        }
    }

    private void changeImage() {
        if (ImageUri != null) {
            showPd("Uploading Image");
            StorageReference s = FirebaseStorage.getInstance()
                    .getReference(PRODUCT_IMAGES_STORAGE_REF);
            final StorageReference fileReference = s.child(
                    getFileName(ImageUri) + "-" +
                            prodName.getText().toString().trim()
                            + "-" + System.currentTimeMillis()
                            + "." + getFileExtension(ImageUri));


            fileReference.putFile(ImageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            fileReference.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Map<String, Object> newImage = new HashMap<>();
                                        newImage.put("productImage", uri.toString());
                                        if (prodDoc != null) {
                                            prodDoc.update(newImage)
                                                    .addOnSuccessListener(aVoid -> {
                                                        hidePd();
                                                        showToast("Image Uploaded");
                                                    });
                                        } else {
                                            showPd("null document");
                                            hidePd();
                                        }

                                    })).addOnFailureListener(e -> {
                showToast(e.getMessage());
                hidePd();
                Log.d(TAG, "onFailure: Execprtion=" + e.getStackTrace().toString());
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unique_pid:
                dialogEditText.setHint("Change Id");
                dialogEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                createDialog(UniquePid);

                break;

              /*  case R.id.txt_option_qty:
                dialogEditText.setHint("Optional number of qty ");
                dialogEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                createDialog(optionQty);

                break;*/
            case R.id.save_btn:
                saveData();
                break;
            case R.id.product_img:
                SelectPhotos();
                break;

            case R.id.prod_name:
                dialogEditText.setHint("Enter New Name");
                dialogEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                createDialog(prodName);

                break;

            case R.id.prod_desc:
                dialogEditText.setHint("Description");
                dialogEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                createDialog(prodDesc);

                break;

            case R.id.prod_price:
                dialogEditText.setHint("New Price");
                dialogEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                createDialog(prodPrice);
                break;

            case R.id.prod_discount:
                dialogEditText.setHint("Discount offer");
                dialogEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                createDialog(prodDesCount);

                break;
            case R.id.prod_qty:
                dialogEditText.setHint("Enter New Quantity");
                dialogEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                createDialog(prodQty);

                break;

            case R.id.prod_stock:
                dialogEditText.setHint("New Stock");
                dialogEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                createDialog(prodStock);

                break;

            case R.id.prod_unit:
                dialogEditText.setHint("New Unit eg:Kg,gm,ml");
                dialogEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                createDialog(prodUnit);
                break;


        }

    }

    private void createDialog(TextView targetTxtView) {
        Log.d(TAG, "createDialog: ");
        dialogEditText.setText(targetTxtView.getText().toString());

        dialog.show();
        dialogOk.setOnClickListener(v -> {
            String newTxt = dialogEditText.getText().toString().trim();
            if (!newTxt.isEmpty() & !newTxt.equals("")) {
                targetTxtView.setText(newTxt);
                dialogEditText.setError(null);
                closeKeyboard();
                dialog.dismiss();


            } else {
                dialogEditText.setError("Name required");

            }

        });


    }

    private void saveData() {
        showPd("Saving Data");
        ProductModalForeSale Pms = new ProductModalForeSale();
        Pms.setProductName(prodName.getText().toString().trim());
        Pms.setProductDescription(prodDesc.getText().toString());
        Pms.setProductImage(PMS.getProductImage());
        Pms.setProductCategory(prodCategory.getText().toString().trim());
        Pms.setProductId(PMS.getProductId());
        Pms.setisPublished(PMS.getisPublished());
        Pms.setUniquePid(UniquePid.getText().toString().trim());

        String[] other = edtOptionqty.getText().toString().split("\\s*,\\s*");
        List<String> othersList = Arrays.asList(other);
        Pms.setOptionQty(othersList);


        String p = prodPrice.getText().toString().replace("Rs ", "");
        Pms.setProductPrice(Integer.parseInt(
                p.trim()
        ));
        String d = prodDesCount.getText().toString().replace("%", "");
        Pms.setProductDiscount(Integer.parseInt(
                d.trim()
        ));

        Pms.setProductQuantity(1);

        Pms.setProductStatus(prodStatus.getText().toString().trim());
        Pms.setProductType(prodType.getText().toString().trim());
        Pms.setProductUnit(prodUnit.getText().toString().trim());

        Pms.setStockQuantity(Integer.
                parseInt(prodStock.getText().toString().trim()));

        prodRef.document(prodId.getText().toString().trim())
                .set(Pms, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    showToast("Item Saved");
                    hidePd();

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                        hidePd();
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

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString.trim())) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}