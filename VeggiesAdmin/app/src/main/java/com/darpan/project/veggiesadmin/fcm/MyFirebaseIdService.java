package com.darpan.project.veggiesadmin.fcm;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

import static com.darpan.project.veggiesadmin.constant.Constants.ADMIN_TOKEN_COLLEC;
import static com.darpan.project.veggiesadmin.constant.Constants.TOKEN_DOC;
import static com.darpan.project.veggiesadmin.constant.Constants.TOKEN_KEY;


class MyFirebaseIdService
        extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseIdService: ";
    FirebaseUser firebaseUser;

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "onNewToken: called");
        super.onNewToken(s);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser != null) {
            updateToken(refreshToken);
            /*FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {

                        if (!task.isSuccessful()) {
                            Log.d(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        if (task.getResult() != null)
                            updateToken(task.getResult().getToken());
                    });*/
        }
    }

    private void updateToken(String refreshToken) {
        Token token1 = new Token(refreshToken);
        Map<String, Object> tokenMAp = new HashMap<>();
        tokenMAp.put(TOKEN_KEY, token1);
        Log.d(TAG, "updateToken: " + token1.getToken());

        //for admin app
        FirebaseFirestore.getInstance()
                .collection(ADMIN_TOKEN_COLLEC)
                .document(TOKEN_DOC)
                .set(tokenMAp);


    }
}
