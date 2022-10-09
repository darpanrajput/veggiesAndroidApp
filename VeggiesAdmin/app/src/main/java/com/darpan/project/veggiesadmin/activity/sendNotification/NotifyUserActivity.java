package com.darpan.project.veggiesadmin.activity.sendNotification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.darpan.project.veggiesadmin.activity.user.CustomerActivity;
import com.darpan.project.veggiesadmin.fcm.APIService;
import com.darpan.project.veggiesadmin.fcm.Client;
import com.darpan.project.veggiesadmin.fcm.Data;
import com.darpan.project.veggiesadmin.fcm.MyResponse;
import com.darpan.project.veggiesadmin.fcm.NotificationSender;
import com.darpan.project.veggiesadmin.firebaseModal.UserNotiModal;
import com.darpan.project.veggiesadmin.util.Util;
import com.darpan.project.veggiesadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.darpan.project.veggiesadmin.constant.Constants.TOKEN_DOC;
import static com.darpan.project.veggiesadmin.constant.Constants.TOKEN_KEY;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_COLLECTION;
import static com.darpan.project.veggiesadmin.constant.Constants.USER_TOKEN_COLLEC;

public class NotifyUserActivity extends AppCompatActivity {
    TextInputEditText title, message;
    private Button send, selectUser;
    private static final String TAG = "NotifyUserActivity:";
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_user);

        pd = new ProgressDialog(this);
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send_notification_btn);
        selectUser = findViewById(R.id.selec_user_btn);

        send.setOnClickListener(v -> getData());
    }

    private void getData() {
        if (checkEdit()) {
            pd.setTitle("Sending..");

            title.setError(null);
            message.setError(null);
            processNotification(title.getText().toString().trim(),
                    message.getText().toString().trim());
        }
    }

    private void processNotification(String Title, String Message) {
        CollectionReference user = FirebaseFirestore
                .getInstance().collection(USER_COLLEC);
        user.get().addOnSuccessListener(qds -> {
            if (qds != null && !qds.isEmpty()) {
                for (DocumentSnapshot Ds : qds) {
                    if (Ds != null && Ds.exists()) {
                        if (Ds.getString("userId") != null)
                            sendNotification(Ds, Title, Message);
                    }
                }
            }

        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                pd.dismiss();
                Toast.makeText(NotifyUserActivity.this,
                        "completed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NotifyUserActivity.this, e.getMessage()
                        , Toast.LENGTH_SHORT).show();
                e.getStackTrace();
            }
        });


    }

    private boolean checkEdit() {
        if (title.getText().toString().trim().isEmpty()) {
            title.setError("Error");
            return false;
        }
        if (message.getText().toString().trim().isEmpty()) {
            message.setError("Error");
            return false;
        }


        return true;

    }

    public void selectUser(View view) {
        startActivity(new Intent(getApplicationContext(),
                CustomerActivity.class));

    }


    public void sendNotification(DocumentSnapshot ds, String title, String LongMSG) {
        CreatNotiInDataBase(ds, title, LongMSG);

        APIService apiService = Client.getClient("https://fcm.googleapis.com/").
                create(APIService.class);
        Data data = new Data(title, LongMSG);

        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                .document(ds.getString("userId"))
                .collection(USER_TOKEN_COLLEC)
                .document(TOKEN_DOC)
                .get().addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                Map<String, Object> tokenMAp = documentSnapshot.getData();
                String userToken = tokenMAp.get(TOKEN_KEY).toString().trim();

                Log.d(TAG, "onSuccess: called userToken=" + userToken);
                NotificationSender sender =
                        new NotificationSender(data, userToken);

                apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MyResponse> call,
                                           @NonNull Response<MyResponse> response) {
                        if (response.code() == 200) {
                            if (response.body() != null && response.body().success != 1) {
                                Log.d(TAG, "onResponse: error=" + response.code());
                                Log.d(TAG, "onResponse: response.body().success =" + response.body().success);
                            } else {
                                Log.d(TAG, "onResponse: Null");
                                Log.d(TAG, "onResponse: Null Code =" + response.body().success);

                            }
                        } else {
                            Log.d(TAG, "onResponse: failed code=" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MyResponse> call, Throwable t) {
                        t.getStackTrace();
                        Log.d(TAG, "onFailure: " + t.getMessage());

                    }
                });

            }

        }).addOnFailureListener(e -> {
            toast(e.getMessage());
            Log.d(TAG, "onFailure: exception=" + e.getMessage());
        });

    }

    private void CreatNotiInDataBase(DocumentSnapshot ds, String title, String Msg) {
        CollectionReference notiRef = FirebaseFirestore
                .getInstance().collection(USER_COLLECTION)
                .document(ds.getString("userId"))
                .collection("notification");
        UserNotiModal userNotiModal = new UserNotiModal(
                Msg,
                Util.getCurrentTime(), /*get time in 24hrs fpr and store in database*/
                title

        );


        Task<DocumentReference> task = notiRef.add(userNotiModal);

        try {
            Task<List<DocumentReference>> tasks = Tasks.whenAllSuccess(task);
            tasks.addOnSuccessListener(documentReferences -> {
                if (documentReferences != null
                        && !documentReferences.isEmpty()) {
                    String id = documentReferences.get(0).getId();
                    notiRef.document(id)
                            .update("snapId", id);
                }

            });

        } catch (Exception e) {
            e.getStackTrace();
            Log.d(TAG, "CreatNotiInDataBase: Exception=" + e.getMessage());
        }


    }


    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

/*
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selec_user_btn:
                break;
            case R.id.send_notification_btn:

                break;

        }

    }*/
}