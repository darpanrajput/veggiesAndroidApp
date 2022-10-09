package com.darpan.project.vegies.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.darpan.project.vegies.BuildConfig;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.activity.about.AboutActivity;

import com.darpan.project.vegies.activity.contact.ContactUsActivity;
import com.darpan.project.vegies.activity.developer.DeveloperActivity;
import com.darpan.project.vegies.activity.feedback.FeedbackActivity;
import com.darpan.project.vegies.activity.filtered.FliteredActivity;
import com.darpan.project.vegies.activity.login.LoginActivity;
import com.darpan.project.vegies.activity.notification.NotificationActivity;
import com.darpan.project.vegies.activity.order.AllOrderActivity;
import com.darpan.project.vegies.activity.privacy.FAQActivity;
import com.darpan.project.vegies.activity.profile.ProfileActivity;
import com.darpan.project.vegies.activity.veggiesCart.VeggiesCartActivity;
import com.darpan.project.vegies.adapters.category.CategoryAdapter;
import com.darpan.project.vegies.firebaseModal.CategoryModal;
import com.darpan.project.vegies.firebaseModal.offer.OfferModal;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;
import com.darpan.project.vegies.roomdatabase.viewModal.ProductViewModal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

import static com.darpan.project.vegies.constant.Constants.CATEGORY_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.FIREBASE_USER_ID;
import static com.darpan.project.vegies.constant.Constants.FIRESTORE_KEY_EMAIL;
import static com.darpan.project.vegies.constant.Constants.FIRESTORE_KEY_PHONE;
import static com.darpan.project.vegies.constant.Constants.FIRESTORE_KEY_PHOTOURL;
import static com.darpan.project.vegies.constant.Constants.FIRESTORE_KEY_USER_NAME;
import static com.darpan.project.vegies.constant.Constants.NOTI_COUNT;
import static com.darpan.project.vegies.constant.Constants.PAGE_COUNT;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;
import static com.darpan.project.vegies.constant.Constants.PRIVACY_URL;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.categoryName;
import static com.darpan.project.vegies.constant.Constants.u_email;
import static com.darpan.project.vegies.constant.Constants.u_username;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private FirebaseFirestore firebaseFirestore;
    private Context context;
    private String USER_ID;
    private FirebaseUser User;
    private static final String TAG = "RealMainActivity:";
    private SharedPreferences sp;
    private LinearLayout homeLvl, profileLvl, orderLvl, addressLvl,
            feedbackLvl, contactLvl, shareLvl, logoutLvl, devLvl,
            aboutLvl, faqLvl, privacyLvl;
    private RelativeLayout includedAppBarHome, includedCategoryLayout;
    private RecyclerView categoryRv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView txtActiontitle, txtNoti, txtCountcard;
    private RelativeLayout rltCart, rltNoti;
    private TextView txtfirstl, txtName, txtEmail;
    private CategoryAdapter adapter;
    private ImageView imgClose;
    private ProductViewModal productViewModal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        context = MainActivity.this;
        USER_ID = getIntent().getStringExtra(FIREBASE_USER_ID);
        User = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        productViewModal = ViewModelProviders.of(this).get(ProductViewModal.class);

        CreateNewUserInFirestore();
        InitialiseDrawerView();
        setDrawer();

        productViewModal.getAllCartData().observe(this, new Observer<List<ProductTable>>() {
            @Override
            public void onChanged(List<ProductTable> productTables) {
                txtCountcard.setText(String.valueOf(productTables.size()));
            }
        });

        offer();
    }

    private void InitialiseDrawerView() {
        /*the main actvity view*/
        drawerLayout = findViewById(R.id.drawer_layout);

        /*header views  */
        txtfirstl = findViewById(R.id.txtfirstl);
        txtName = findViewById(R.id.txt_name);
        txtEmail = findViewById(R.id.txt_email);

        /*drawer menu*/
        imgClose = findViewById(R.id.img_close);
        homeLvl = findViewById(R.id.lvl_home);
        profileLvl = findViewById(R.id.myprofile);
        orderLvl = findViewById(R.id.myoder);
        addressLvl = findViewById(R.id.address);
        feedbackLvl = findViewById(R.id.feedback);
        contactLvl = findViewById(R.id.contact);
        shareLvl = findViewById(R.id.share);
        logoutLvl = findViewById(R.id.logout);
        devLvl = findViewById(R.id.dev_ll);
        aboutLvl = findViewById(R.id.about);
        faqLvl = findViewById(R.id.faq);
        privacyLvl = findViewById(R.id.privacy);


        //setting clicks on drawer menus
        imgClose.setOnClickListener(this);
        homeLvl.setOnClickListener(this);
        profileLvl.setOnClickListener(this);
        orderLvl.setOnClickListener(this);
        addressLvl.setOnClickListener(this);
        feedbackLvl.setOnClickListener(this);
        contactLvl.setOnClickListener(this);
        shareLvl.setOnClickListener(this);
        logoutLvl.setOnClickListener(this);
        devLvl.setOnClickListener(this);
        aboutLvl.setOnClickListener(this);
        faqLvl.setOnClickListener(this);
        privacyLvl.setOnClickListener(this);

        /*the app bar view*/
        includedAppBarHome = findViewById(R.id.included_app_bar_home);
        txtActiontitle = includedAppBarHome.findViewById(R.id.txt_actiontitle);
        toolbar = includedAppBarHome.findViewById(R.id.toolbar);
        rltCart = includedAppBarHome.findViewById(R.id.rlt_cart);
        rltCart.setOnClickListener(this);
        rltNoti = includedAppBarHome.findViewById(R.id.rlt_noti);
        rltNoti.setOnClickListener(this);
        txtNoti = findViewById(R.id.txt_noti);//not visible;
        txtCountcard = includedAppBarHome.findViewById(R.id.txt_countcard);

        if (sp.getInt(NOTI_COUNT, 0) > 0) {
            txtNoti.setVisibility(View.VISIBLE);
            txtNoti.setText(String.valueOf(1));
        } else {
            txtNoti.setVisibility(View.GONE);
        }

        /*another layout in appbar showing recyclerview category*/

        includedCategoryLayout = includedAppBarHome.
                findViewById(R.id.included_category_lyt);
        categoryRv = includedCategoryLayout.findViewById(R.id.category_rv);
        swipeRefreshLayout = includedCategoryLayout.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);
        setData();
    }

    private void setData() {
        Log.d(TAG, "getDataCall: called");
        CollectionReference cr = FirebaseFirestore.getInstance()
                .collection(CATEGORY_COLLECTION);
        Query query = cr.orderBy(categoryName, Query.Direction.ASCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();

        FirestorePagingOptions<CategoryModal> options =
                new FirestorePagingOptions.Builder<CategoryModal>()
                        .setQuery(query, config, snapshot -> {
                            CategoryModal categoryModal = snapshot.toObject(CategoryModal.class);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                            if (categoryModal != null)
                                categoryModal.setSnapId(id);
                            return Objects.requireNonNull(categoryModal);
                        })
                        .build();

        categoryRv.setLayoutManager(new
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CategoryAdapter(options);
        categoryRv.setItemAnimator(new DefaultItemAnimator());
        categoryRv.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnLoadingStateChange(loadingState1 -> {
            switch (loadingState1) {
                case LOADING_INITIAL:
                    Log.d(TAG, "onStateChange: LOADING_INITIAL");
                    swipeRefreshLayout.setRefreshing(true);
                    break;

                case LOADING_MORE:
                    Log.d(TAG, "onStateChange:LOADING_MORE ");
                    swipeRefreshLayout.setRefreshing(true);
                    break;

                case LOADED:
                    Log.d(TAG, "onStateChange: LOADED");
                    swipeRefreshLayout.setRefreshing(false);
                    break;

                case ERROR:
                    Toast.makeText(MainActivity.this,
                            "Error Occurred!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onStateChange: ERROR");
                    swipeRefreshLayout.setRefreshing(false);
                    break;

                case FINISHED:
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "onStateChange:FINISHED ");
                    break;
            }
        });
        adapter.setItemClick((Ds, position) -> {
            Log.d(TAG, "setOnItemClick: Category=" + Ds.getString(categoryName));
            startActivity(new Intent(MainActivity.this,
                    FliteredActivity.class)
                    .putExtra(categoryName, Ds.getString(categoryName)));
        });
    }

    private void CreateNewUserInFirestore() {
        /*create user if not exist*/

        if (User == null) {
             /*if user does not exit that means account is deleted that's why
            moving back to Main Login Activity*/
            Toast.makeText(context, "No User Found ,Moving Back to Home in 3 sec",
                    Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(context, LoginActivity.class));
                    finish();

                }
            }, 3000);

        }

        firebaseFirestore.collection(USER_COLLECTION).document(USER_ID)
                .set(createFieldsForUser(), SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: Success"))
                .addOnFailureListener(e -> Toast.makeText(context,
                        "Failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());

    }

    private Map<String, Object> createFieldsForUser() {

        Map<String, Object> FirestoreUser = new HashMap<>();
        if (User.getDisplayName() != null && !User.getDisplayName().isEmpty()) {
            FirestoreUser.put(FIRESTORE_KEY_USER_NAME, User.getDisplayName());

        }
        if (User.getEmail() != null && !User.getEmail().isEmpty()) {
            FirestoreUser.put(FIRESTORE_KEY_EMAIL, User.getEmail());
        }

        if (User.getPhotoUrl() != null && !User.getPhotoUrl().toString().isEmpty()) {
            FirestoreUser.put(FIRESTORE_KEY_PHOTOURL, User.getPhotoUrl().toString());

        }
        if (User.getPhoneNumber() != null && !User.getPhoneNumber().isEmpty()) {
            FirestoreUser.put(FIRESTORE_KEY_PHONE, User.getPhoneNumber());
            Log.d(TAG, "createFieldsForUser: mobile=" + User.getPhoneNumber());

        }
        return FirestoreUser;
    }

    private void setDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        char first = ' ';


        if (User != null && User.getDisplayName() != null && User.getEmail() != null) {
            Log.d(TAG, "setDrawer: User!=null if call");
            if (!User.getDisplayName().isEmpty())
                first = User.getDisplayName().charAt(0);

            txtName.setText("" + User.getDisplayName());
            txtEmail.setText("" + User.getEmail());
            txtActiontitle.setText("Hello " + User.getDisplayName());

            if (User.getDisplayName().isEmpty() && User.getEmail().isEmpty()) {
                getDrawerWelcomeText();
            }
        }

        Log.e("first", "-->" + first);
        txtfirstl.setText("" + first);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close:
                closeDrawer();

                break;
            case R.id.lvl_home:
                closeDrawer();
                break;

            case R.id.myprofile:
                closeDrawer();
                startActivity(new Intent(MainActivity.this,
                        ProfileActivity.class));
                break;
            case R.id.myoder:
                startActivity(new Intent(
                        context,
                        AllOrderActivity.class));
                closeDrawer();
                break;

            case R.id.address:
                startActivity(new Intent(MainActivity.this,
                        ProfileActivity.class));
                closeDrawer();
                break;
            case R.id.feedback:
                startActivity(new Intent(MainActivity.this,
                        FeedbackActivity.class));

                closeDrawer();
                break;
            case R.id.contact:
                closeDrawer();
                startActivity(new Intent(MainActivity.this,
                        ContactUsActivity.class));

                break;
            case R.id.logout:
                closeDrawer();
                SignOut();
                break;

            case R.id.dev_ll:
                startActivity(new Intent(MainActivity.this,
                        DeveloperActivity.class));
                break;

            case R.id.about:
                startActivity(new Intent(MainActivity.this,
                        AboutActivity.class));

                closeDrawer();
                break;


            case R.id.faq:
                startActivity(new Intent(MainActivity.this,
                        FAQActivity.class));
                closeDrawer();
                break;

            case R.id.rlt_cart:
                startActivity(new Intent(MainActivity.this,
                        VeggiesCartActivity.class));
                break;

            case R.id.rlt_noti:
                sp.edit().putInt(NOTI_COUNT, 0).apply();
                startActivity(new Intent(MainActivity.this,
                        NotificationActivity.class));

                break;
            case R.id.share:
                closeDrawer();
                shareApp();
                break;

            case R.id.privacy:
                createTabs(PRIVACY_URL);
                break;


        }

    }

    private void getDrawerWelcomeText() {
        Log.d(TAG, "getDrawerWelcomeText: ");
        /*if the user authenticate through mobile the
         * there will be no data to display at the drawer*/
        Map<String, Object> map = new HashMap<>();
        if (User != null) {
            firebaseFirestore.collection(USER_COLLECTION).document(USER_ID).get()
                    .addOnSuccessListener(dS -> {
                        if (dS.exists() && dS.getData() != null) {
                            map.put(u_email, dS.getData().get(u_email));
                            map.put(u_username, dS.getData().get(u_username));
                            Log.d(TAG, "map user=" + dS.getData().get(u_username));
                            Log.d(TAG, "map userEmail=" + dS.getData().get(u_email));

                            char first = ' ';
                            if (dS.getData().get(u_username) != null) {
                                first = dS.getData().get(u_username).toString().charAt(0);
                                txtName.setText(dS.getData().get(u_username).toString());
                            } else {
                                txtName.setText(" ");
                            }

                            txtfirstl.setText("" + first);
                            if ((dS.getData().get(u_email) != null)) {
                                txtEmail.setText(dS.getData().get(u_email).toString());
                                txtActiontitle.setText("Hello " + dS.getData().get(u_username).toString());
                            }
                        }
                    });
        }

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(categoryRv.getRootView(), message,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void createTabs(String url) {

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

    private void SignOut() {
        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(task -> {
                    // user is now signed out
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();

                }).addOnFailureListener(e -> showToast(e.getMessage()));
    }

    @Override
    public void onRefresh() {
        adapter.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null)
            adapter.stopListening();
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.getStackTrace();
        }
    }

    private void offer() {
        RelativeLayout root = includedAppBarHome.findViewById(R.id.include_banner_offer_lyt);

        TextView textView = root.findViewById(R.id.offer_txt);

        RelativeLayout hugeOffer = includedAppBarHome.findViewById(R.id.include_huge_offer_lyt);

        ImageView hugeOfferImage = hugeOffer.findViewById(R.id.offer_img);
        DocumentReference offer = firebaseFirestore
                .collection("veggiesApp/Veggies/Offer")
                .document("bannerOffer");
        DocumentReference offerHuge = firebaseFirestore
                .collection("veggiesApp/Veggies/Offer")
                .document("FullscreenOffer");
        offer.get().addOnSuccessListener(ds -> {
            if (ds.exists()) {
                OfferModal f = ds.toObject(OfferModal.class);
                Log.d(TAG, "offer: f="+f.getOfferVisibility());
                if (f != null)
                    if (f.getOfferVisibility()) {
                        if (root.getVisibility() == View.GONE) {
                            root.setVisibility(View.VISIBLE);
                            textView.setText(f.getOfferText());
                        }
                    } else {
                        if (root.getVisibility() == View.VISIBLE)
                            root.setVisibility(View.GONE);
                    }
            }
        });

        offerHuge.get().addOnSuccessListener(ds -> {
            if (ds.exists()) {
                Log.d(TAG, "offer: Ds Exist="+ds);
                if (ds.getBoolean("offerVisibility")) {
                    Log.d(TAG, "offerVisibility: "+ds.getBoolean("offerVisibility"));
                    if (hugeOffer.getVisibility() == View.GONE) {
                        hugeOffer.setVisibility(View.VISIBLE);
                        Glide.with(this)
                                .load(ds.getString("offerImage"))
                                .thumbnail(Glide.with(this).load(R.drawable.ezgifresize))
                                .into(hugeOfferImage);
                    }
                } else {
                    if (hugeOffer.getVisibility() == View.VISIBLE)
                        hugeOffer.setVisibility(View.GONE);
                }
            }
        });

        ImageView closeBigOffer, closeShortOffer;
        closeBigOffer = hugeOffer.findViewById(R.id.close_huge_offer);
        closeShortOffer = root.findViewById(R.id.banner_close_offer);

        closeBigOffer.setOnClickListener(v -> {
            if (hugeOffer.getVisibility() == View.VISIBLE) hugeOffer.setVisibility(View.GONE);
        });

        closeShortOffer.setOnClickListener(v -> {
            if (root.getVisibility() == View.VISIBLE) root.setVisibility(View.GONE);
        });
    }

}