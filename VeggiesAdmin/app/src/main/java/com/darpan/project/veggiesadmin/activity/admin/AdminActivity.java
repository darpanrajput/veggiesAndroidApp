package com.darpan.project.veggiesadmin.activity.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.activity.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    private EditText user, pass;
    private Button loginBtn;
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);



        user = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_password);
        loginBtn = findViewById(R.id.btn_login);
        progressBar=findViewById(R.id.progress_bar);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth();
            }
        });

        if (firebaseAuth.getCurrentUser() != null) {
            System.out.println("already Signed In");

            // already signed in
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userId", firebaseAuth.getCurrentUser().getUid());
            progressBar.setVisibility(View.GONE);
            startActivity(intent);
            finish();
        }
    }

    private void auth() {
        progressBar.setVisibility(View.VISIBLE);
        if (user.getText().toString().isEmpty()) {
            user.setError("error");
            return;
        }
        if (pass.getText().toString().isEmpty()) {
            pass.setError("error");
            return;
        }

        String email = user.getText().toString().trim();
        String pas = pass.getText().toString().trim();
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, pas)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(AdminActivity.this,
                                MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}