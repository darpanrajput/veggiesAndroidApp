package com.darpan.project.vegies.activity.feedback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.firebaseModal.FeedbackModal;
import com.darpan.project.vegies.firebaseModal.UserModal;

import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.feedback_coll;

public class FeedbackActivity extends AppCompatActivity {

    private TextInputEditText edFeedback;
    private Button btnSubmit;
    private RatingBar ratingBar;

    private ProgressDialog pd;
    private FirebaseUser user;
    private CollectionReference userCol = FirebaseFirestore.getInstance().
            collection(USER_COLLECTION);
    private CollectionReference feedbkRef = FirebaseFirestore.getInstance().
            collection(feedback_coll);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        edFeedback = findViewById(R.id.ed_feedback);
        btnSubmit = findViewById(R.id.btn_submit);
        ratingBar = findViewById(R.id.ratingBar);
        getSupportActionBar().setTitle("Feedback");


        pd = new ProgressDialog(this);
        user = FirebaseAuth.getInstance().getCurrentUser();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edFeedback.getText() != null && !edFeedback.getText().toString().isEmpty()) {
                    edFeedback.setError(null);
                    getUser(edFeedback.getText().toString().trim());
                } else {
                    edFeedback.setError("Empty Field");
                    return;
                }
            }
        });
    }

    private void showPd(String title) {
        pd.setTitle(title);
        pd.show();
    }

    private void hidePd() {
        pd.cancel();
    }

    private void getUser(String feedback) {
        showPd("Loading...");

        userCol.document(user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    UserModal UM = documentSnapshot.toObject(UserModal.class);
                    if (UM != null)
                        saveFd(feedback, UM, Float.toString(ratingBar.getRating()));

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.getMessage());

                hidePd();

            }
        });
    }

    private void saveFd(String feedback, UserModal UM, String rating) {
        feedbkRef.document(user.getUid()).set(new FeedbackModal(rating,
                feedback,
                UM.getEmail(),
                UM.getUserId(),
                UM.getName()), SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Feedback Submitted");
                        hidePd();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                        hidePd();
                    }
                });
    }

    private void showToast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }
}