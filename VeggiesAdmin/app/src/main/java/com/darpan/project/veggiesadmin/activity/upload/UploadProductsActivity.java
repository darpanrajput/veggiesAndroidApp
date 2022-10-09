package com.darpan.project.veggiesadmin.activity.upload;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.firebaseModal.ProductModalForeSale;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import pub.devrel.easypermissions.AppSettingsDialog;

import static com.darpan.project.veggiesadmin.constant.Constants.PRODUCT_IMAGES_STORAGE_REF;


public class UploadProductsActivity extends AppCompatActivity {
    private TextView txtExcelFile;
    private Button uploadPhotosBtn, selectExcelBtn, finalUploadBtn;
    private static final int CHOOSE_EXCEL_FILE = 101;
    private static final int PICK_IMAGE_REQUEST = 102;
    private static final String TAG = "UploadProductsActivity:";
    private ArrayList<Uri> uri = new ArrayList<>();
    private List<ProductModalForeSale> productList = new ArrayList<>();
    private ProgressDialog pd;
    private StorageReference mStorage = FirebaseStorage.getInstance().
            getReference(PRODUCT_IMAGES_STORAGE_REF);

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_products);
        pd = new ProgressDialog(this);
        uploadPhotosBtn = findViewById(R.id.upload_all_photos_btn);
        selectExcelBtn = findViewById(R.id.select_excel_file);
        txtExcelFile = findViewById(R.id.txt_excel_file);
        finalUploadBtn = findViewById(R.id.upload_btn);

        uploadPhotosBtn.setOnClickListener(v -> {
                   // uploadPhotoFirst();
                }
                /*SelectPhotos()*/);

        selectExcelBtn.setOnClickListener(v -> {
            //selectExcel()
            //
                });


        finalUploadBtn.setOnClickListener(v -> {
           /* if (!finalUploadBtn.isEnabled()) {
                t("Please Select Both");
            } else if (!uri.isEmpty() && !productList.isEmpty()) {
                if (uri.size() == productList.size()) {
                    uploadWholeData();
                } else {
                    if (uri.size() > productList.size()) {
                        showAlert("Large Image set",
                                "Images are greater than the Excel Entry");
                    } else {
                        showAlert("Large Excel Entry",
                                "Excel Entries are greater than the Excel Entry");
                    }

                }

            } else {
                t("List Are Empty");
                Log.d(TAG, "onCreate: uriSize=" + uri.size());
                Log.d(TAG, "onCreate: productList Size=" + productList
                        .size());

            }*/

            //FinallyUploadImages();
            if (!productList.isEmpty()) {
                //uploadPureExcelData(productList);

            } else {
                showAlert("Empty List", "Product list is empty size=" +
                        productList.size());
            }
        });

        Button merge = findViewById(R.id.merge_btn);
        merge.setVisibility(View.VISIBLE);
        merge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mergingData();*/
            }
        });
    }

    private void FinallyUploadImages() {
        CollectionReference productDummyImages = FirebaseFirestore
                .getInstance().collection("productDummyImages");
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            //this method will prevent accidental uploads
            t("Upload in progress");
        } else {
            showPd("uploading Images");
            for (int i = 0; i < uri.size(); i++) {
                final StorageReference fileReference =
                        mStorage.child(getImageName(uri.get(i)));
                int finalI = i;
                mUploadTask = fileReference.putFile(uri.get(i))
                        .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri URL) {
                                Map<String, Object> map = new HashMap<>();
                                String name = getImageName(uri.get(finalI));
                                Log.d(TAG, "onSuccess: Image Name=" + name);
                                map.put("imageName", name.trim());
                                map.put("imageUrl", URL.toString());
                                productDummyImages.add(map);
                            }
                        }))
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                pd.setProgress((int) progress);
                            }
                        });


            }
            new AlertDialog.Builder(this)
                    .setTitle("ready to upload Excel File")
                    .setMessage("The list is Ready to upload Are you Want to upload the Excel data?")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        dialog.dismiss();
                        if (!productList.isEmpty()) {
                            uploadPureExcelData(productList);
                        } else {
                            t("Error Why product List is Still Empty");
                        }

                    })
                    .setCancelable(false)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();

            hidePd();
        }
    }

    private void uploadPhotoFirst() {
        if (!uri.isEmpty()) uri.clear();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadWholeData() {
        CollectionReference productDummyImages = FirebaseFirestore
                .getInstance().collection("productDummyImages");

        if (mUploadTask != null && mUploadTask.isInProgress()) {
            //this method will prevent accidental uploads
            t("Upload in progress");
        } else {
            showPd("uploading Images");
            for (int i = 0; i < uri.size(); i++) {
                for (int j = 0; j < productList.size(); j++) {
                    if (productList.get(j).getProductName().contains(getFileName(uri.get(i)))) {
                        final StorageReference fileReference =
                                mStorage.child(getFileName(uri.get(i)));
                        int finalI = i;
                        mUploadTask = fileReference.putFile(uri.get(i))
                                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri URL) {
                                        Map<String, Object> map = new HashMap<>();
                                        String name = productList.get(finalI).getProductName().trim();
                                        Log.d(TAG, "onSuccess: Image Name=" + name);
                                        map.put("imageName", name.trim());
                                        map.put("imageUrl", URL.toString());
                                        productDummyImages.add(map);
                                    }
                                }));
                    }
                }
            }
            new AlertDialog.Builder(this)
                    .setTitle("ready to upload Excel File")
                    .setMessage("The list is Ready to upload Are you Want to upload the Excel data?")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        dialog.dismiss();
                        if (!productList.isEmpty()) {
                            uploadProdList(productList);
                        } else {
                            t("Error Why product List is Still Empty");
                        }

                    })
                    .setCancelable(false)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();

            hidePd();
        }


    }

    private void showAlert(String title, String Message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(Message)
                .setNegativeButton("Cancel",
                        (dialog, which) ->
                                dialog.dismiss()).show();

    }

    private void SelectPhotos() {
        if (!uri.isEmpty()) uri.clear();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_EXCEL_FILE) {
            Log.d(TAG, "onActivityResult: excel");
            Log.d(TAG, "onActivityResult: resultcode=" + resultCode);
            Log.d(TAG, "onActivityResult: data=" + data);

            if (resultCode == RESULT_OK && data != null
                    && data.getData() != null) {

                Uri path = data.getData();

                String newPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + getFileName(path);//root internal storage

                String path1 = path.getPath();
                Log.d(TAG, "getExcelData: path=" + path1);
                Log.d(TAG, "getExcelData: uri=" + path);
                Log.d(TAG, "getExcelData: file name=" + getFileName(path));
                Log.d(TAG, "getExcelData: Absolute path=" + newPath);
                if (newPath.contains("/root_path"))
                    newPath = newPath.replace("/root_path", "");

                RwCsv(newPath);

                String file = getFileName(data.getData());
                txtExcelFile.setText(file);
            }
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            Log.d(TAG, "onActivityResult: PickImage");
            Log.d(TAG, "onActivityResult: resultcode=" + resultCode);
            Log.d(TAG, "onActivityResult: data=" + data);

            if (resultCode == RESULT_OK && data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        uri.add(data.getClipData().getItemAt(i).getUri());
                       /* Log.d(TAG, "onActivityResult Image: Name="+i+" "+
                                getImageName(data.getClipData().getItemAt(i).getUri())
                        .split(".")[0].trim());*/
                    }

                   /* if (!uri.isEmpty() && !productList.isEmpty()) {
                        finalUploadBtn.setEnabled(true);
                    }*/

                } else if (data.getData() != null) {
                    String imagePath = data.getData().getPath();
                    uri.add(Uri.parse(imagePath));
                }
            }
        } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Log.d(TAG, "onActivityResult: Requested Return To same");
        } else {
            Toast.makeText(this, "invalid", Toast.LENGTH_SHORT).show();
        }


    }

    private void t(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }

    private void selectExcel() {
        String[] mimeTypes = {"*/*"};//for all files
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

    private void RwCsv(String path) {
        if (!productList.isEmpty()) productList.clear();
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Reading data");
        pd.show();
        File file = new File(path);
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
            CsvRow row;
            while ((row = csvParser.nextRow()) != null) {
                createProdList(row);
                System.out.println("get count=" + row.getFieldCount());
                System.out.println("get fields=" +
                        Arrays.toString(row.getFields().toArray()));
                System.out.println("Read line: " + row);
                System.out.println("First column of line: " +
                        row.getField(0) + " type=" + row.getField(0).getClass().getName());
                //  System.out.println("First column Name: " + row.getField("productName"));
                System.out.println("Sixth column of line: " +
                        row.getField(6) + " type=" + row.getField(6).getClass().getName());
            }
            if (!uri.isEmpty() && !productList.isEmpty()) {
                finalUploadBtn.setEnabled(true);
            }


        } catch (Exception e) {
            pd.dismiss();
            System.out.println("Stack Trace=" + Arrays.toString(e.getStackTrace()));
            Log.d(TAG, "RwCsv: CsvParser Exception="
                    + e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        pd.dismiss();
    }

    private void createProdList(CsvRow row) {
        ProductModalForeSale prod = new ProductModalForeSale();
        prod.setUniquePid(row.getField(0));
        prod.setProductName(row.getField(1));
        prod.setProductCategory(row.getField(2));
        prod.setProductType(row.getField(3));
        prod.setProductDescription(row.getField(4));
        prod.setProductUnit(row.getField(5));
        prod.setProductPrice(Integer.parseInt(row.getField(6)));
        prod.setProductQuantity(1);
        prod.setProductDiscount(Integer.parseInt(row.getField(8)));
        prod.setProductStatus(row.getField(9));
        prod.setisPublished(Boolean.parseBoolean(row.getField(10)));
        prod.setStockQuantity(Integer.parseInt(row.getField(11)));
        prod.setProductId("No ID");
        prod.setProductImage(row.getField(13));

        String[] strings = row.getField(14).split("\\s,\\s");
        prod.setOptionQty(Arrays.asList(strings));


        //just ad this whole product in the list
        productList.add(prod);

    }

    private void uploadPureExcelData(List<ProductModalForeSale> productList) {
        showPd("uploading Excel Data");
        CollectionReference prodRef = FirebaseFirestore
                .getInstance().collection("productList");
        for (ProductModalForeSale pm : productList) {
            String id = prodRef.document().getId();
            pm.setProductId(id);
            prodRef.document(id)
                    .set(pm, SetOptions.merge());
        }
        t(" excel Data Uploaded");
        hidePd();
    }

    private void uploadProdList(List<ProductModalForeSale> productList) {
        showPd("uploading Excel Data");
        CollectionReference prodRef = FirebaseFirestore
                .getInstance().collection("DemoProducts");
        for (ProductModalForeSale pm : productList) {
            String id = prodRef.document().getId();
            pm.setProductId(id);
            prodRef.document(id)
                    .set(pm, SetOptions.merge());
        }
        t(" excel Data Uploaded");
        hidePd();

        new AlertDialog.Builder(this)
                .setTitle("Ready to merge")
                .setMessage("The Excel data is ready To Merge withe Images That You uploaded" +
                        " are you Ready ?")
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();

                    mergingData();
                })
                .setCancelable(false)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();


    }

    private void mergingData() {
        CollectionReference productDummyImages = FirebaseFirestore
                .getInstance().collection("productDummyImages");

        CollectionReference productRef = FirebaseFirestore
                .getInstance().collection("productList");
        showPd("Merging..."); Task t1 = productRef.get();
        Task t2 = productDummyImages.get();
        Task<List<QuerySnapshot>> allTask = Tasks.whenAllSuccess(t1, t2);


        allTask.addOnSuccessListener(querySnapshots -> {
            for (QueryDocumentSnapshot qds : querySnapshots.get(0)) {
                if (qds.exists()) {
                    ProductModalForeSale PMS = qds.toObject(ProductModalForeSale.class);
                    for (QueryDocumentSnapshot qds1 : querySnapshots.get(1)) {
                        if (qds1.exists()) {
                            if (qds1.getString("imageName").split(".")[0]
                                    .trim().contains(PMS.getProductName().trim())) {
                                Log.d(TAG, "mergingData: ProductName=" + PMS.getProductName());
                                Log.d(TAG, "mergingData: ImageNAme=" + qds1.getString("imageName"));
                                productRef.document(PMS.getProductId().trim())
                                        .update("productImage",
                                                qds1.getString("imageUrl"));
                            /*    productDummyImages
                                        .document(qds1.getId()).delete();*/

                            }
                        }
                    }

                }
            }
            hidePd();
            t("updated done");

        }).addOnFailureListener(e -> {
            t(e.getMessage());
            e.getStackTrace();

            Log.e(TAG, "onFailure: ", e);
        });


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
            e.printStackTrace();
            System.out.println(TAG + " Exception while executing result==null:"
                    + e.getMessage());
        }

        return result;

    }

    private String getImageName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("file")) {
            fileName = uri.getLastPathSegment();
        } else {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, new String[]{
                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                }, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    Log.d(TAG, "name is " + fileName);
                }
            } finally {

                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return fileName;
    }

    private String getFileExtension(Uri image) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(image));

    }

    private void showPd(String title) {
        pd.setTitle(title);
        pd.show();
    }

    private void hidePd() {
        pd.dismiss();
    }
}