package com.darpan.project.vegies.activity.contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darpan.project.vegies.R;


import java.util.Arrays;

public class ContactUsActivity extends AppCompatActivity {
    private TextView Mobile, Email;
    private static final int REQUEST_PERMISSION_CODE = 1001;
    private static final int REQUEST_CALL = 911;
    private static final String REQUEST_WRITE_PERMISSION =
            Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String TAG = "ContactUsActivity: ";
    private LinearLayout mobileLL,emailLL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Mobile = findViewById(R.id.mobile);
        Email = findViewById(R.id.txt_email);
        mobileLL=findViewById(R.id.mobile_ll);
        emailLL=findViewById(R.id.email_ll);


        mobileLL.setOnClickListener(v -> new AlertDialog.Builder(ContactUsActivity.this)
                .setIcon(R.drawable.ic_call)
                .setTitle("Make a CAll")
                .setMessage(getString((R.string.call_message)) + "\nis it ok?")
                .setNegativeButton("cancel", (dialog, which) ->
                        Toast.makeText(ContactUsActivity.this,
                        "Operation Denied", Toast.LENGTH_SHORT).show())

                .setPositiveButton("Call", (dialog, which) ->
                        makePhoneCall())
                .show());


        emailLL.setOnClickListener(v -> new AlertDialog.Builder(ContactUsActivity.this)
                .setTitle("Email Us?")
                .setIcon(R.drawable.ic_mail)
                .setMessage("Mail Us About Any Bug Or About Features Enhancement")
                .setPositiveButton("Ok", (dialog, which) -> {
                    intent_for_mail_feedback("Your Feedback");
                    dialog.dismiss();
                }).setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                .show());


        isNeedGrantPermission();
    }

    public static boolean hasPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(ContactUsActivity.this,
                        "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void isNeedGrantPermission() {
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                if (ContextCompat.checkSelfPermission(this, REQUEST_WRITE_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            ContactUsActivity.this, REQUEST_WRITE_PERMISSION)) {
                        final String msg = String.format(getString(R.string.format_request_permision), getString(R.string.app_name));

                        AlertDialog.Builder localBuilder = new AlertDialog.Builder(
                                ContactUsActivity.this);
                        localBuilder.setTitle("Permission Required!");
                        localBuilder
                                .setMessage(msg).setNeutralButton("Grant",
                                (paramAnonymousDialogInterface, paramAnonymousInt) -> ActivityCompat.requestPermissions(ContactUsActivity.this,
                                        new String[]{REQUEST_WRITE_PERMISSION}, REQUEST_PERMISSION_CODE))
                                .setNegativeButton("Cancel", (paramAnonymousDialogInterface, paramAnonymousInt) -> {


                                    paramAnonymousDialogInterface.dismiss();
                                    finish();
                                });
                        localBuilder.show();

                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{REQUEST_WRITE_PERMISSION},
                                REQUEST_PERMISSION_CODE);
                    }

                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Exception In Permision="+
                    Arrays.toString(e.getStackTrace()));
        }
    }

    private void makePhoneCall() {
        String contactNumber = Mobile.getText().toString().trim().replace("+91 ",
                "");
        if (contactNumber.length() > 0) {

            System.out.println(contactNumber);
            if (ContextCompat.checkSelfPermission(ContactUsActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ContactUsActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + contactNumber;
                System.out.println(contactNumber);
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Toast.makeText(ContactUsActivity.this,
                    "Invalid Number", Toast.LENGTH_SHORT).show();
        }
    }

    private void intent_for_mail_feedback(String feedBackMessage) {
        String[] recipients = {getString(R.string.email)};
        Intent send = new Intent(Intent.ACTION_SEND);
        send.putExtra(Intent.EXTRA_EMAIL, recipients);
        send.putExtra(Intent.EXTRA_SUBJECT, "Feedback about the App");
        send.putExtra(Intent.EXTRA_TEXT, feedBackMessage);
        send.putExtra(Intent.EXTRA_CC, "mailcc@gmail.com");
        send.setType("text/html");
        /*   send.setType("message/rfc822");*/
        send.setPackage("com.google.android.gm");
        /* startActivity(send);*/

        startActivity(Intent.createChooser(send, "Send mail"));


    }

}
