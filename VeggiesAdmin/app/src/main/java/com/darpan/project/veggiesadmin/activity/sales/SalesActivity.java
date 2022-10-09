package com.darpan.project.veggiesadmin.activity.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.firebaseModal.ProductModalForeSale;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static com.darpan.project.veggiesadmin.constant.Constants.DELIVERED;
import static com.darpan.project.veggiesadmin.constant.Constants.PRODUCT_LIST_COLLECTION;

public class SalesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        TextView sales=findViewById(R.id.sales_txt);
        ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.show();


      CollectionReference ref= FirebaseFirestore.getInstance().collection(PRODUCT_LIST_COLLECTION);

        Query q=ref.whereEqualTo("productStatus",DELIVERED);
        q.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot qds) {
               pd.dismiss();
               if (qds!=null && !qds.isEmpty()) {
                   int saleCounter=0;
                   for (DocumentSnapshot d : qds) {
                       ProductModalForeSale PMS=d.toObject(ProductModalForeSale.class);
                      if (PMS!=null) {
                          saleCounter += PMS.getProductPrice();

                      }
                   }
                   sales.setText(String.format("Rs %s",saleCounter));

               }else { sales.setText("NAN");
                   Toast.makeText(getApplicationContext(), "Sales Not Available", Toast.LENGTH_SHORT).show();
               }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                sales.setText("NAN");
            }
        });


    }
}