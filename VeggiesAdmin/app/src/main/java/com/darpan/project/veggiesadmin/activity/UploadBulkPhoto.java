package com.darpan.project.veggiesadmin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.darpan.project.veggiesadmin.adapter.bulk.PhotoAdapter;
import com.darpan.project.veggiesadmin.adapter.bulk.PhotoModal;
import com.darpan.project.veggiesadmin.firebaseModal.ProductModalForeSale;
import com.darpan.project.veggiesadmin.projectModal.AllImageModal;
import com.darpan.project.veggiesadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

import static com.darpan.project.veggiesadmin.constant.Constants.PRODUCT_LIST_COLLECTION;

public class UploadBulkPhoto extends AppCompatActivity {


    private static final int CHOOSE_EXCEL_FILE = 101;
    private CollectionReference prodRef;
    private RecyclerView photoRv, prodRv;
    private PhotoModal photoModal;
    private Button selectBtn;
    private PhotoAdapter photoAdapter;

    private List<ProductModalForeSale> PMSLIST = new ArrayList<>();
    private static final String TAG = "UploadBulkPhoto:";
    private EditText optionKg, optionOthers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bulk_photo);


        prodRef = FirebaseFirestore.getInstance().collection(PRODUCT_LIST_COLLECTION);

        photoRv = findViewById(R.id.photos_rv);
        prodRv = findViewById(R.id.product_list_rv);
        selectBtn = findViewById(R.id.select_btn);
        selectBtn.setVisibility(View.GONE);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectExcel();
            }
        });
        // getPhoto();
        /*getData();*/

        //   delete();

        //   createCollectionFortheImagesInProductCollection();
        optionKg = findViewById(R.id.edit_kg_option);
        optionOthers = findViewById(R.id.edit_other_option);

        Button updateUptionButton = findViewById(R.id.upload_option_btn);
        updateUptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UploadBulkPhoto.this)
                        .setTitle("Upload Ready")
                        .setMessage("This will upload the data on the firebase")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                               // creatOptionArray();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });

        Button deleteOption = findViewById(R.id.delete_option);
        deleteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // delete();
            }
        });


    }

    private void creatOptionArray() {
        String[] kg = optionKg.getText().toString().split("\\s*,\\s*");
        List<String> kgList = Arrays.asList(kg);

        String[] other = optionOthers.getText().toString().split("\\s*,\\s*");
        List<String> othersList = Arrays.asList(other);
        CollectionReference prodref = FirebaseFirestore.getInstance()
                .collection(PRODUCT_LIST_COLLECTION);

        Map<String, List<String>> opMAp = new HashMap<>();

        prodref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                if (ds.exists()) {
                    ProductModalForeSale p = ds.toObject(ProductModalForeSale.class);
                    if (p.getProductUnit().contains("kg")) {
                        p.setOptionQty(kgList);
                        prodref.document(ds.getId())
                                .set(p, SetOptions.merge());
                    } else {
                        p.setOptionQty(othersList);
                        prodref.document(ds.getId())
                                .set(p, SetOptions.merge());
                    }
                }
            }

        });


    }


    private void createCollectionFortheImagesInProductCollection() {
        ProgressDialog pd = new ProgressDialog(this);
        List<AllImageModal> allImageModals = new ArrayList<>();

        CollectionReference prodImage =
                FirebaseFirestore
                        .getInstance()
                        .collection("veggiesApp/Veggies/allProductImages");
        CollectionReference prodRef =
                FirebaseFirestore
                        .getInstance()
                        .collection(PRODUCT_LIST_COLLECTION);
        prodRef.get()
                .addOnSuccessListener(qds -> {
                    if (qds.size() > 0) {
                        for (DocumentSnapshot ds : qds) {
                            if (ds.exists()) {
                                ProductModalForeSale p = ds.toObject(ProductModalForeSale.class);
                                AllImageModal allImageModal = new AllImageModal(
                                        p.getProductName(),
                                        p.getProductImage(),
                                        p.getProductId()
                                );
                                prodImage.add(allImageModal);

                            }
                        }
                    }


                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isComplete()) {
                    pd.dismiss();
                    Toast.makeText(UploadBulkPhoto.this, "Completed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UploadBulkPhoto.this, "Not Completed", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.getStackTrace();
                pd.dismiss();
                Toast.makeText(UploadBulkPhoto.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void delete() {
        Map<String, Object> deleteSong = new HashMap<>();
        deleteSong.put("optionQty", FieldValue.delete());
        /*  deleteSong.put("uniquePid", FieldValue.delete());*/

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection(PRODUCT_LIST_COLLECTION);
        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                if (ds.exists()) {
                    collectionReference
                            .document(ds.getId())
                            .update(deleteSong);

                }
            }

            t("deleted");
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                t(e.getMessage());

                e.getStackTrace();
            }
        });


    }


    private void getData() {
        prodRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot qds) {
                for (int i = 0; i < qds.size(); i++) {
                    if (!qds.isEmpty()) {
                        String id = qds.getDocuments().get(i).getId();
                        ProductModalForeSale PMS = qds.getDocuments().get(i).
                                toObject(ProductModalForeSale.class);
                        PMS.setProductId(id.trim());
                        PMSLIST.add(PMS);

                    }
                }
                //updateId();


                t("updated");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                t(e.getMessage());
                e.getStackTrace();
            }
        });

    }

    private void updateId() {

        for (ProductModalForeSale p : PMSLIST) {
            Log.d(TAG, "PMLIST ID=" + p.getProductId());

            prodRef.document(p.getProductId())
                    .set(p, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Updated=" + p.getProductId());

                        }
                    });
        }

    }

    private void t(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }

    private void selectExcel() {
        String[] mimeTypes = {"application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
        };

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        startActivityForResult(intent, CHOOSE_EXCEL_FILE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_EXCEL_FILE) {

            Log.d(TAG, "onActivityResult: excel");
            Log.d(TAG, "onActivityResult: resultcode=" + resultCode);
            Log.d(TAG, "onActivityResult: data=" + data);

            if (resultCode == RESULT_OK && data != null) {
                Log.d(TAG, "onActivityResult: excel data");
                Log.d(TAG, "onActivityResult: ExcelName=" +
                        getFileName(data.getData()));
            }
        } else {
            Toast.makeText(this, "invalid", Toast.LENGTH_SHORT).show();
        }
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