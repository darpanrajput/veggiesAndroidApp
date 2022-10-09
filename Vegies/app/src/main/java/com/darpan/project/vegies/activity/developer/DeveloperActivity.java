package com.darpan.project.vegies.activity.developer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.projectModal.DevModal;

import java.util.ArrayList;
import java.util.List;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

public class DeveloperActivity extends AppCompatActivity
        implements View.OnClickListener{
    private TextView fbTitle, inTitle, twTitle, ytTitle, linTitle;
    private TextView fbDesc, inDesc, twDesc, ytDesc, linDesc;

    private ImageView devImg, fbImg, twImg, inImg, ytImg, linImg;
    private RelativeLayout included_dev_lyt;
    private TextView devtxt;
    private List<DevModal> devModals = new ArrayList<>();
    private CardView fb,lin,in,tw,yt;
    private static final String TAG = "DeveloperActivity;";
    private static String websiteUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        devImg = findViewById(R.id.dev_logo);
        devtxt = findViewById(R.id.dev_txt);


        included_dev_lyt = findViewById(R.id.include_dev_lyt);

        fb = included_dev_lyt.findViewById(R.id.fb_card);
        in = included_dev_lyt.findViewById(R.id.in_card);
        lin = included_dev_lyt.findViewById(R.id.lin_card);
        tw = included_dev_lyt.findViewById(R.id.tw_card);
        yt = included_dev_lyt.findViewById(R.id.yt_card);


        fbTitle = included_dev_lyt.findViewById(R.id.fb_title);
        fbDesc = included_dev_lyt.findViewById(R.id.fb_description);
        fbImg = included_dev_lyt.findViewById(R.id.fb_img);

        inTitle = included_dev_lyt.findViewById(R.id.in_title);
        inDesc = included_dev_lyt.findViewById(R.id.in_description);
        inImg = included_dev_lyt.findViewById(R.id.in_img);

        twTitle = included_dev_lyt.findViewById(R.id.tw_title);
        twDesc = included_dev_lyt.findViewById(R.id.tw_description);
        twImg = included_dev_lyt.findViewById(R.id.tw_img);

        ytTitle = included_dev_lyt.findViewById(R.id.yt_title);
        ytDesc = included_dev_lyt.findViewById(R.id.yt_description);
        ytImg = included_dev_lyt.findViewById(R.id.yt_img);

        linTitle = included_dev_lyt.findViewById(R.id.lin_title);
        linDesc = included_dev_lyt.findViewById(R.id.lin_description);
        linImg = included_dev_lyt.findViewById(R.id.lin_img);


        devtxt.setOnClickListener(this);
        fb.setOnClickListener(this);
        in.setOnClickListener(this);
        lin.setOnClickListener(this);
        tw.setOnClickListener(this);
        yt.setOnClickListener(this);
        getData();
    }

    private void getData() {
        CollectionReference cr = FirebaseFirestore
                .getInstance().collection("veggiesApp/Veggies/developer");
        cr.document("developerDetails")
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                websiteUrl=documentSnapshot.getString("Website");
                Glide.with(DeveloperActivity.this)
                        .load(documentSnapshot.getString("Image"))
                        .error(R.drawable.empty)
                        .thumbnail(Glide.with(DeveloperActivity.this)
                                .load(R.drawable.ezgifresize))
                        .into(devImg);
                Log.d(TAG, "getData: Image="+documentSnapshot.getString("Image"));

                String combine = documentSnapshot.getString("About") + "\n" +"\n"+
                        documentSnapshot.getString("Email") + "\n" +
                        documentSnapshot.getString("Number") + "\n" +
                        documentSnapshot.getString("Website");
                devtxt.setText(combine);


            }


        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());

        getSocilaLinks();


    }

    private void getSocilaLinks() {
        if (!devModals.isEmpty()) devModals.clear();
        CollectionReference cr = FirebaseFirestore
                .getInstance().collection("veggiesApp/Veggies/developer")
                .document("developerDetails")
                .collection("social");
        cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        DevModal devModal = d.toObject(DevModal.class);
                        devModals.add(devModal);
                    }
                    setData();

                }else {
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());

        for (DevModal devModal:devModals){
            Log.d(TAG, "getSocilaLinks: "+devModal.getImg());
        }

    }

    private void setData() {
        fbTitle.setText(devModals.get(0).getTitle());
        fbDesc.setText(devModals.get(0).getDescription());
        Glide.with(DeveloperActivity.this)
                .load(devModals.get(0).getImg())
                .error(R.drawable.empty)
                .thumbnail(Glide.with(DeveloperActivity.this)
                        .load(R.drawable.ezgifresize))
                .into(fbImg);


        inTitle.setText(devModals.get(1).getTitle());
        inDesc.setText(devModals.get(1).getDescription());
        Glide.with(DeveloperActivity.this)
                .load(devModals.get(1).getImg())
                .error(R.drawable.empty)
                .thumbnail(Glide.with(DeveloperActivity.this)
                        .load(R.drawable.ezgifresize))
                .into(inImg);


        linTitle.setText(devModals.get(2).getTitle());
        linDesc.setText(devModals.get(2).getDescription());
        Glide.with(DeveloperActivity.this)
                .load(devModals.get(2).getImg())
                .error(R.drawable.empty)
                .thumbnail(Glide.with(DeveloperActivity.this)
                        .load(R.drawable.ezgifresize))
                .into(linImg);



        twTitle.setText(devModals.get(3).getTitle());
        twDesc.setText(devModals.get(3).getDescription());
        Glide.with(DeveloperActivity.this)
                .load(devModals.get(3).getImg())
                .error(R.drawable.empty)
                .thumbnail(Glide.with(DeveloperActivity.this)
                        .load(R.drawable.ezgifresize))
                .into(twImg);

        ytTitle.setText(devModals.get(4).getTitle());
        ytDesc.setText(devModals.get(4).getDescription());
        Glide.with(DeveloperActivity.this)
                .load(devModals.get(4).getImg())
                .error(R.drawable.empty)
                .thumbnail(Glide.with(DeveloperActivity.this)
                        .load(R.drawable.ezgifresize))
                .into(ytImg);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dev_txt:
                Log.d(TAG, "onClick: dev");
                createTabs(websiteUrl);

                break;

            case R.id.fb_card:
                Log.d(TAG, "onClick: fb");
                createTabs(devModals.get(0).getUrl());
                break;
            case R.id.in_card:
                createTabs(devModals.get(1).getUrl());
                Log.d(TAG, "onClick: in");
                break;
            case R.id.lin_card:
                createTabs(devModals.get(2).getUrl());
                Log.d(TAG, "onClick: lin");
                break;
            case R.id.tw_card:
                createTabs(devModals.get(3).getUrl());

                Log.d(TAG, "onClick: tw");
                break;
            case R.id.yt_card:
                createTabs(devModals.get(4).getUrl());
                Log.d(TAG, "onClick: yt");
                break;

        }

    }

    private void  createTabs(String url){

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(this.getResources()
                        .getColor(R.color.colorPrimary))
                .setShowTitle(true)
                .build();
        CustomTabsHelper.addKeepAliveExtra(this, customTabsIntent.intent);

        CustomTabsHelper.openCustomTab(this, customTabsIntent,
                Uri.parse(url.trim()),
                new WebViewFallback());
    }
}