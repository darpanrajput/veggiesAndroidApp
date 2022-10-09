package com.darpan.project.vegies.activity.splash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.darpan.project.vegies.R;

import com.darpan.project.vegies.activity.login.LoginActivity;
import com.darpan.project.vegies.activity.walkthrough.InfoActivity;
import com.darpan.project.vegies.application.MyApplication;

import static com.darpan.project.vegies.constant.Constants.FIRST_TIME;
import static com.darpan.project.vegies.constant.Constants.HASH_KEY;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private static final String TAG = "SplashActivity:";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        System.out.println((TAG + "onCreate: hash="
                + sp.getString(HASH_KEY, "default hash key")));

        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(() -> {
            if (internetChack()) {
                if (sp.getBoolean(FIRST_TIME, false)) {
                    startActivity(new Intent(SplashActivity.this,
                            LoginActivity.class));

                } else {
                    startActivity(new Intent(SplashActivity.this,
                            InfoActivity.class));
                }
                finish();
            } else {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(SplashActivity.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Please Check Your Internet Connection")
                        .setCancelable(false)
                        .setPositiveButton("Exit", (dialog, id) -> finish());
                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.show();
            }

        }, SPLASH_TIME_OUT);

    }
    public static boolean internetChack() {
        ConnectivityManager ConnectionManager = (ConnectivityManager)
                MyApplication.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    }
