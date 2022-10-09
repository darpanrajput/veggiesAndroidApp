package com.darpan.project.vegies.fcm;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

import static com.darpan.project.vegies.constant.Constants.TOKEN_DOC;
import static com.darpan.project.vegies.constant.Constants.TOKEN_KEY;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.USER_TOKEN_COLLEC;

class MyFirebaseIdService
        extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseIdService: ";
    FirebaseUser firebaseUser;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: called");
         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       String refreshToken = FirebaseInstanceId.getInstance().getToken();
      /*  FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (!task.isSuccessful()) {
                            Log.d(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        if (task.getResult() != null)

                            refreshToken = task.getResult().getToken();
                    }
                });
*/

        if (firebaseUser != null) {
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        Token token1 = new Token(refreshToken);
        Map<String, Object> tokenMAp = new HashMap<>();
        tokenMAp.put(TOKEN_KEY, token1);
        Log.d(TAG, "updateToken: " + token1.getToken());
        String id = firebaseUser.getUid();
        FirebaseFirestore.getInstance()
                .collection(USER_COLLECTION)
                .document(id).collection(USER_TOKEN_COLLEC)
                .document(TOKEN_DOC).set(tokenMAp);
       /*  for admin app
       tokenMAp.put("tokenKey",token1);
        FirebaseFirestore.getInstance()
                .collection("veggiesApp/Veggies/adminToken").document("token")
                .set(tokenMAp, SetOptions.merge());*/


    }
}
