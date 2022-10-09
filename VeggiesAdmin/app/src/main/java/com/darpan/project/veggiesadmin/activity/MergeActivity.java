package com.darpan.project.veggiesadmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.firebaseModal.ProductModalForeSale;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeActivity extends AppCompatActivity {
    private static final String TAG = "MergeActivity:";
    Button merge, update, correctBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);

        merge = findViewById(R.id.merge_btn);
        update = findViewById(R.id.update_btn);
        correctBtn = findViewById(R.id.correct_optionQty);

        merge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                merg();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNames();
            }
        });

        correctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correctTheOptions();
            }
        });
    }

    private void correctTheOptions() {
        ProgressDialog ps = new ProgressDialog(this);
        ps.show();
        CollectionReference dummy = FirebaseFirestore
                .getInstance()
                .collection("veggiesApp/Veggies/productList");
        dummy.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                    if (q.exists()) {
                        ProductModalForeSale p = q.toObject(ProductModalForeSale.class);
                        String s = p.getOptionQty().get(0);
                        Log.d(TAG, "onSuccess: options=" + s);
                        if (s.contains(",")) {
                            String[] options = s.split(",");
                            p.setOptionQty(Arrays.asList(options));
                            dummy.document(p.getProductId())
                                    .set(p, SetOptions.merge());

                        }
                    }
                }
                ps.dismiss();
                t("updated");
            }
        }).addOnFailureListener(e -> {
            t(e.getMessage());
            e.getStackTrace();
            Log.d(TAG, "onFailure:" +
                    "exception " + e.getStackTrace());
        });
    }

    private void updateNames() {
        CollectionReference dummy = FirebaseFirestore
                .getInstance()
                .collection("productDummyImages");
        dummy.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot s : queryDocumentSnapshots) {
                if (s.exists()) {
                    String name = s.getString("imageName");

                    if (s.contains(".")) {
                        name = name.split("\\.")[0].trim();
                    }
                    Log.d(TAG, "updateNames: imageName" + name);
                    Map<String, Object> map = new HashMap<>();
                    map.put("imageName", name);
                    dummy.document(s.getId())
                            .update(map);
                }
            }

            t("updated");
        });
    }

    private void merg() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Merging");
        CollectionReference dummyRef = FirebaseFirestore
                .getInstance()
                .collection("productDummyImages");
        CollectionReference prodRef = FirebaseFirestore
                .getInstance()
                .collection("veggiesApp/Veggies/productList");


        Task prodTask = prodRef.get();
        Task dummyTask = dummyRef.get();
        Task<List<QuerySnapshot>> allTask = Tasks.whenAllSuccess(prodTask, dummyTask);
        allTask.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for (QueryDocumentSnapshot qds : querySnapshots.get(0)) {
                    if (qds.exists()) {
                        ProductModalForeSale PMS = qds.toObject(ProductModalForeSale.class);
                        for (QueryDocumentSnapshot qds1 : querySnapshots.get(1)) {
                            if (qds1.exists()) {
                                if (qds1.getString("imageName")
                                        .trim().contains(PMS.getProductName().trim())) {
                                    Log.d(TAG, "mergingData: ProductName=" + PMS.getProductName());
                                    Log.d(TAG, "mergingData: ImageNAme=" + qds1.getString("imageName"));
                                    prodRef.document(PMS.getProductId().trim())
                                            .update("productImage",
                                                    qds1.getString("imageUrl"));
                            /*    productDummyImages
                                        .document(qds1.getId()).delete();*/

                                }
                            }
                        }

                    }
                }
                pd.dismiss();
                t("updated done");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                t(e.getMessage());
                e.getStackTrace();
            }
        });
    }

    private void t(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}