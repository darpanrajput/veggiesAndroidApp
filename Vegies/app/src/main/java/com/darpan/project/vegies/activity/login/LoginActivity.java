package com.darpan.project.vegies.activity.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.darpan.project.vegies.activity.newUIDesign.MainActivity2;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.darpan.project.vegies.activity.MainActivity;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.Utils.Utiles;

import com.darpan.project.vegies.application.MyApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static com.darpan.project.vegies.constant.Constants.FIREBASE_USER_ID;
import static com.darpan.project.vegies.constant.Constants.HASH_KEY;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity:";
    private RelativeLayout rl;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener stateListener;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                System.out.println("onAuthStateChanged called");
            }
        };
        firebaseAuth.addAuthStateListener(stateListener);
        if (firebaseAuth.getCurrentUser() != null) {
            System.out.println("already Signed In");


            // already signed in
            /*Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(FIREBASE_USER_ID, firebaseAuth.getCurrentUser().getUid());
            startActivity(intent);
            finish();*/

            Intent intent = new Intent(this, MainActivity2.class);
            intent.putExtra(FIREBASE_USER_ID, firebaseAuth.getCurrentUser().getUid());
            startActivity(intent);
            finish();

        } else {
            System.out.println("not signed in");
            // not signed in
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build());
//                    new AuthUI.IdpConfig.FacebookBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            /* .setLogo(R.drawable.logo)*/
                            .setTheme(R.style.LoginTheme)
                            .build(),
                    RC_SIGN_IN);
        }

       //  generateKeyhash();
    }

    private void generateKeyhash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.darpan.project.vegies",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                        .edit().putString(HASH_KEY, Base64.encodeToString(md.digest(), Base64.DEFAULT))
                        .apply();

            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            System.out.println(response);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                System.out.println("Successfully signed in");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    System.out.println("user name=" + user.getDisplayName());
                    System.out.println("user email=" + user.getEmail());
                    System.out.println("user number= " + user.getPhoneNumber());
                    System.out.println("getProviderId =" + user.getProviderId());
                    System.out.println("getPhotoUrl=" + user.getPhotoUrl());
                    System.out.println("user id=" + user.getUid());
                    System.out.println("token data=" + user.getIdToken(true));
                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity2.class);
                     /*Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);*/
                    intent.putExtra(FIREBASE_USER_ID, user.getUid());
                    startActivity(intent);
                    finish();
                }


                //https://graph.facebook.com/2648509908724388/picture?type=large
                //https://graph.facebook.com/2648509908724388/picture?width=500&width=500
                // ...

                /*for using gmail
                 * we have token data=com.google.android.gms.tasks.zzu@30b40d5*/
                /*for facebook we get the following
                 * token data=com.google.android.gms.tasks.zzu@e761c20*/

                    /*let result = photoURL;
                        if (providerId.includes('google')) {
                          result = photoURL.replace('s96-c', 's400-c');
                        } else if (providerId.includes('facebook')) {
                          result = `${photoURL}?type=large`;
                        }
                        return result;

                        https://lh3.googleusercontent.com/a-/AOh14GgRzWxmIpSUXL1Pgrg7pxtXMXAsR2eMt41XaMYM=s96-c
                        we just need tp increase the size of number 96 to get bigger profile at this url

                        https://lh3.googleusercontent.com/a-/AOh14GgRzWxmIpSUXL1Pgrg7pxtXMXAsR2eMt41XaMYM=s400-c
                        */


            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                // System.out.println("Sign in failed");
                if (response == null) {
                    // User pressed back button. NOTE: This is where the back action is
                    //taken care of
                    // Toast.makeText(this, "sign in failed", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //Show No Internet Notification

                    Snackbar.make(rl, "No Network", Snackbar.LENGTH_SHORT).show();

                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    //Shown Unknown Error Notification
                    String err = "Unknown Error:" + response.getError().getErrorCode();
                    Utiles.showToast(err, MyApplication.mContext);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        firebaseAuth.removeAuthStateListener(stateListener);
        System.out.println(TAG + "removeAuthStateListener");
        super.onDestroy();
    }


}