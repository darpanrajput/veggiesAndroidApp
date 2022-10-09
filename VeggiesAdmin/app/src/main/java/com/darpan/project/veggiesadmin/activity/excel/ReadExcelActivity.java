package com.darpan.project.veggiesadmin.activity.excel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.firebaseModal.OrderPlacedModal;
import com.darpan.project.veggiesadmin.firebaseModal.ProductModalForeSale;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

public class ReadExcelActivity extends AppCompatActivity {
    public static final int READ_EXCEL = 5;
    private static final String TAG = "ReadExcelActivity:";
    private List<ProductModalForeSale>productList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_excel);

        Button btn = findViewById(R.id.read_ex_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPhotos();
            }
        });

        btn.setVisibility(View.VISIBLE);

    }

    private void RwCsv(String path) {
        if (!productList.isEmpty())productList.clear();
        ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("reading data");
        pd.show();
        File file = new File(path);
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
            CsvRow row;

          //  System.out.println("get MAp="+ csvParser.getHeader());

            while ((row = csvParser.nextRow()) != null) {
                createProdList(row);
                System.out.println("get count="+row.getFieldCount());
                System.out.println("get fields="+
                        Arrays.toString(row.getFields().toArray()));
                System.out.println("Read line: " + row);
                System.out.println("First column of line: " +
                        row.getField(0)+" type="+row.getField(0).getClass().getName());
              //  System.out.println("First column Name: " + row.getField("productName"));
                System.out.println("Sixth column of line: " +
                        row.getField(6)+" type="+row.getField(6).getClass().getName());
            }

            new AlertDialog.Builder(this)
                    .setTitle("ready to upload")
                    .setMessage("The list is Ready to upload Are you Want to upload the data?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (!productList.isEmpty()){
                                uploadProdList(productList);
                            }else {
                                Toast.makeText(getApplicationContext(), "Error Why List is Still Empty", Toast.LENGTH_SHORT).show();

                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    }).show();

        } catch (Exception e) {
            pd.dismiss();
            e.getStackTrace();
            Log.d(TAG, "RwCsv: CsvParser Exception=" + e.getMessage());
        }

        pd.dismiss();
    }

    private void uploadProdList(List<ProductModalForeSale> productList) {
        ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("uploading Data");
        pd.show();
        CollectionReference prodRef= FirebaseFirestore
                .getInstance().collection("DemoProducts");
        for (ProductModalForeSale pm:productList){
            String id=prodRef.document().getId();
            pm.setProductId(id);
            prodRef.document(id)
                    .set(pm, SetOptions.merge());
        }
        Toast.makeText(getApplicationContext(), "Data Uploaded",
                Toast.LENGTH_SHORT).show();
        pd.dismiss();

    }

    private void createProdList(CsvRow row) {
        ProductModalForeSale prod=new ProductModalForeSale();
        prod.setProductName(row.getField(0));
        prod.setProductCategory(row.getField(1));
        prod.setProductType(row.getField(2));
        prod.setProductDescription(row.getField(3));
        prod.setProductUnit(row.getField(4));
        prod.setProductPrice(Integer.parseInt(row.getField(5)));
        prod.setProductQuantity(Integer.parseInt(row.getField(6)));
        prod.setProductDiscount(Integer.parseInt(row.getField(7)));
        prod.setProductStatus(row.getField(8));
        prod.setisPublished(Boolean.parseBoolean(row.getField(9)));
        prod.setStockQuantity(Integer.parseInt(row.getField(10)));
        prod.setProductId("empty");
        prod.setProductImage("No image");

        String []strings={"1,2,3,4"};
        prod.setOptionQty(Arrays.asList(strings));
        prod.setUniquePid("Create Code");

        //just ad this whole product in the list
        productList.add(prod);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_EXCEL && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

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


        }
    }

    private void getdata(Uri uri) {
        try {
            /*Sheet sheet=wb.getSheetAt(0);   //getting the XSSFSheet object at given index
Row row=sheet.getRow(vRow); //returns the logical row
Cell cell=row.getCell(vColumn); //getting the cell representing the given column
value=cell.getStringCellValue();    //getting cell value
return value;               //returns the*/

            String path = uri.getPath();
            Log.d(TAG, "getExcelData: path=" + path);
            Log.d(TAG, "getExcelData: uri=" + uri);
            Log.d(TAG, "getExcelData: file name=" + getFileName(uri));


            String newPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + getFileName(uri);//root internal storage

            Log.d(TAG, "getExcelData: Absolute path=" + newPath);


            if (newPath.contains("/root_path"))
                newPath = newPath.replace("/root_path", "");
            FileInputStream file = new FileInputStream(newPath);
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);// creating a blank sheet

            int rownum = 0;
            int rowSize = sheet.getPhysicalNumberOfRows();

            for (int i = 0; i < rowSize; i++) {
                Row row = sheet.getRow(0);
                Log.d(TAG, "getdata: Row=" + row.getCell(0));

            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception "
                    + e.getMessage(), Toast.LENGTH_SHORT).show();

            Log.d(TAG, "ExportList=" + e.getMessage());

            Toast.makeText(getApplicationContext(), "not Saved ",
                    Toast.LENGTH_SHORT).show();

        }

    }

    private void SelectPhotos() {
       /* String[] mimeTypes = {"application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",// .xls & .xlsx
                "application/excel",
                "application/x-excel",
                "application/x-msexcel",
                "text/csv"
        };*/
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

        startActivityForResult(intent, READ_EXCEL);
    }


    private void importData(List<OrderPlacedModal> data, Context context) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();

            Sheet sheet = workbook.createSheet("sheet1");// creating a blank sheet

            int rownum = 0;

            Row row0 = sheet.createRow(rownum++);
            createListHeadings(row0);

            for (OrderPlacedModal opm : data) {
                Row row = sheet.createRow(rownum++);
                createList(opm, row);

            }

            String fileName = "OrderData.xlsx";
            String extStorageDirectory = Environment.getExternalStorageDirectory()
                    .toString();
            Log.d(TAG, "exportData: Path=" + extStorageDirectory);
            File folder = new File(extStorageDirectory, "Order");
            folder.mkdir();
            File file = new File(folder, fileName);
            try {
                file.createNewFile();
                Log.d(TAG, "exportData: filePath=" + file.getPath());
                Log.d(TAG, "exportData: Absolute filePath=" + file.getAbsolutePath());
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e(TAG, " File Excep=" + e1.getMessage());
            }

            try {
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();

                Toast.makeText(context.getApplicationContext(), "file saved", Toast.LENGTH_SHORT).show();
           /* linearLayout.setVisibility(View.VISIBLE);
            clear_text_view_text.startAnimation(animationDown);
            clear_text_view_text.setText("file:/ " + extStorageDirectory + "/Order");
*/
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, " Fil o/p Excep=" + e.getMessage());
            }

        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "Exception " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "ExportList=" + e.getMessage());
            Toast.makeText(context.getApplicationContext(), "not Saved ", Toast.LENGTH_SHORT).show();

        }

    }

    private void createListHeadings(Row row) {

        Cell cell = row.createCell(0);
        cell.setCellValue("Customer Id ");
        cell = row.createCell(1);
        cell.setCellValue("Customer Name");
        cell = row.createCell(2);
        cell.setCellValue("Customer Address ");
        cell = row.createCell(3);
        cell.setCellValue("Order Name");
        cell = row.createCell(4);
        cell.setCellValue("Order Quantity");
        cell = row.createCell(5);
        cell.setCellValue("Item Id");
        cell = row.createCell(6);
        cell.setCellValue("Status");
        cell = row.createCell(7);
        cell.setCellValue("Order Date");
        cell = row.createCell(8);
        cell.setCellValue("Order Time");
        cell = row.createCell(9);
        cell.setCellValue("Total price");
        cell = row.createCell(10);
        cell.setCellValue("Payment Mode");
        cell = row.createCell(11);
        cell.setCellValue("Image Url");
    }

    private void createList(OrderPlacedModal OPM, Row row) {

        Cell cell = row.createCell(0);
        cell.setCellValue(OPM.getCustomerId());
        cell = row.createCell(1);
        cell.setCellValue(OPM.getCustomerName());
        cell = row.createCell(2);
        cell.setCellValue(OPM.getCustomerAddress());
        cell = row.createCell(3);
        cell.setCellValue(OPM.getOrderName());
        cell = row.createCell(4);
        cell.setCellValue(OPM.getOrderQuantity());
        cell = row.createCell(5);
        cell.setCellValue(OPM.getUniquePid());
        cell = row.createCell(6);
        cell.setCellValue(OPM.getOrderStatus());
        cell = row.createCell(7);
        cell.setCellValue(OPM.getDateOfOrder());
        cell = row.createCell(8);
        cell.setCellValue(OPM.getOrderTiming());
        cell = row.createCell(9);
        cell.setCellValue(OPM.getTotalPrice());
        cell = row.createCell(10);
        cell.setCellValue(OPM.getModeOfPayment());
        cell = row.createCell(11);
        cell.setCellValue(OPM.getOrderImage());
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