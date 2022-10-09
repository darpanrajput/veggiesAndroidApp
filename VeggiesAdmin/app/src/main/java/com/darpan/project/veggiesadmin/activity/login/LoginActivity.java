package com.darpan.project.veggiesadmin.activity.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.activity.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener stateListener;
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "LoginActivity:";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.redirectingBar);

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
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userId", firebaseAuth.getCurrentUser().getUid());
            progressBar.setVisibility(View.GONE);
            startActivity(intent);
            finish();
        } else {
            System.out.println("not signed in");
            // not signed in
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.EmailBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            System.out.println(response);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                System.out.println("Successfully signed in");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                System.out.println("user name=" + user.getDisplayName());
                System.out.println("user email=" + user.getEmail());
                System.out.println("user number= " + user.getPhoneNumber());
                System.out.println("getProviderId =" + user.getProviderId());
                System.out.println("getPhotoUrl=" + user.getPhotoUrl());
                System.out.println("user id=" + user.getUid());
                System.out.println("token data=" + user.getIdToken(true));


                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userId", user.getUid());
                startActivity(intent);
                finish();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                System.out.println("Sign in failed");
                if (response == null) {
                    // User pressed back button. NOTE: This is where the back action is
                    //taken care of
                    finish();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //Show No Internet Notification
                    Snackbar.make(progressBar.getRootView(), "No Network", Snackbar.LENGTH_SHORT).show();

                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    //Shown Unknown Error Notification
                    Snackbar.make(progressBar.getRootView(), "Unknown Error:" + response.getError().getErrorCode(),
                            Snackbar.LENGTH_SHORT).show();
                }
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseAuth.removeAuthStateListener(stateListener);
        Log.d(TAG, "onDestroy: called");
    }
}