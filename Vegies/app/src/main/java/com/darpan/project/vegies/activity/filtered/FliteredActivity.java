package com.darpan.project.vegies.activity.filtered;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.darpan.project.vegies.BuildConfig;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.Utils.Utiles;
import com.darpan.project.vegies.activity.ItemDetailedActivity;
import com.darpan.project.vegies.activity.about.AboutActivity;

import com.darpan.project.vegies.activity.contact.ContactUsActivity;
import com.darpan.project.vegies.activity.developer.DeveloperActivity;
import com.darpan.project.vegies.activity.feedback.FeedbackActivity;
import com.darpan.project.vegies.activity.login.LoginActivity;
import com.darpan.project.vegies.activity.notification.NotificationActivity;
import com.darpan.project.vegies.activity.order.AllOrderActivity;
import com.darpan.project.vegies.activity.privacy.FAQActivity;
import com.darpan.project.vegies.activity.profile.ProfileActivity;
import com.darpan.project.vegies.activity.veggiesCart.VeggiesCartActivity;
import com.darpan.project.vegies.adapters.category.filter.FilteringAdapter;
import com.darpan.project.vegies.adapters.category.search.SearchAdapter;
import com.darpan.project.vegies.firebaseModal.ProductModalForeSale;
import com.darpan.project.vegies.firebaseModal.offer.OfferModal;
import com.darpan.project.vegies.roomdatabase.viewModal.ProductViewModal;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

import static com.darpan.project.vegies.constant.Constants.DELIVERY_TIME_COLLEC;
import static com.darpan.project.vegies.constant.Constants.DELIVERY_TIME__DOC_NAME;
import static com.darpan.project.vegies.constant.Constants.GLOBAL_TIME_SLOT_KEY;
import static com.darpan.project.vegies.constant.Constants.IS_CORRECT_TIME;
import static com.darpan.project.vegies.constant.Constants.IS_PUBLISHED;
import static com.darpan.project.vegies.constant.Constants.NOTI_COUNT;
import static com.darpan.project.vegies.constant.Constants.PAGE_COUNT;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;
import static com.darpan.project.vegies.constant.Constants.PRIVACY_URL;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_ID;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_LIST_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.categoryName;
import static com.darpan.project.vegies.constant.Constants.u_email;
import static com.darpan.project.vegies.constant.Constants.u_username;

public class FliteredActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private FilteringAdapter filteringAdapter;
    private SharedPreferences sp;
    private SearchAdapter searchAdapter;
    private RecyclerView filterRv, searchRecyclerview;
    private SwipeRefreshLayout refreshLayout;
    private static final String TAG = "FliteredActivity:";
    private boolean isRightTimeToDeliver;
    private String startTime, endTime;

    private LinearLayout homeLvl, profileLvl, orderLvl, addressLvl,
            feedbackLvl, contactLvl, shareLvl, logoutLvl, devLvl,
            aboutLvl, faqLvl, privacyLvl;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView txtActiontitle, txtNoti, txtCountcard;
    private RelativeLayout rltCart, rltNoti;
    private TextView txtfirstl, txtName, txtEmail;
    private EditText edSearch;
    private ImageView imgSearch, imgClose;
    private TextView.OnEditorActionListener editorActionListener;
    private FirebaseUser User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flitered);

        User = FirebaseAuth.getInstance().getCurrentUser();
        refreshLayout = findViewById(R.id.swipe_refresh);
        filterRv = findViewById(R.id.filter_rv);
        filterRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(this);
        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        saveDeliveryTimeInPref();
        InitializeView();
        setDrawer();
        getDataCall();
        keyBoardListener();

        ProductViewModal productViewModal = ViewModelProviders.of(this)

                .get(ProductViewModal.class);
        productViewModal.getAllCartData().observe(this, productTables ->
                txtCountcard.setText(String.valueOf(productTables.size())));



    }

    private void InitializeView() {
        /*the main activity view*/
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



        /*action bae views*/
        txtActiontitle = findViewById(R.id.txt_actiontitle);
        toolbar = findViewById(R.id.toolbar);
        rltCart = findViewById(R.id.rlt_cart);
        rltCart.setOnClickListener(this);

        rltNoti = findViewById(R.id.rlt_noti);
        rltNoti.setOnClickListener(this);
        txtNoti = findViewById(R.id.txt_noti);//not visible;
        txtCountcard = findViewById(R.id.txt_countcard);
        edSearch = findViewById(R.id.ed_search);
        if (edSearch.getText().length() == 0) {
            UIUtil.hideKeyboard(this);
        }
        imgSearch = findViewById(R.id.img_search);
        imgSearch.setOnClickListener(this);
        searchRecyclerview = findViewById(R.id.search_Rv);
        searchRecyclerview.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        offer();
    }

    private void setDrawer() {

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
        addTextWatcher();
    }

    private void getDrawerWelcomeText() {
        Log.d(TAG, "getDrawerWelcomeText: ");
        /*if the user authenticate through mobile the
         * there will be no data to display at the drawer*/
        Map<String, Object> map = new HashMap<>();
        if (User != null) {
            FirebaseFirestore.getInstance().
                    collection(USER_COLLECTION).document(User.getUid()).get()
                    .addOnSuccessListener(dS -> {
                        if (dS.exists() && dS.getData() != null) {
                            map.put(u_email, dS.getData().get(u_email));
                            map.put(u_username, dS.getData().get(u_username));
                            Log.d(TAG, "map user=" + dS.getData().get(u_username));
                            Log.d(TAG, "map userEmail=" + dS.getData().get(u_email));
                            char first = dS.getData().get(u_username).toString().charAt(0);
                            txtfirstl.setText("" + first);
                            txtName.setText(dS.getData().get(u_username).toString());
                            txtEmail.setText(dS.getData().get(u_email).toString());
                            txtActiontitle.setText("Hello " + dS.getData().get(u_username).toString());
                        }
                    });
        }

    }


    private void getDataCall() {
        String category = getIntent().getStringExtra(categoryName);
        Log.d(TAG, "getDataCall: category=" + category);
        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection(PRODUCT_LIST_COLLECTION);

        Query query = reference
                .whereEqualTo(IS_PUBLISHED, true)
                .whereEqualTo("productCategory", category.trim())
                .orderBy("productName", Query.Direction.ASCENDING);


        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();

        FirestorePagingOptions<ProductModalForeSale> options =
                new FirestorePagingOptions.Builder<ProductModalForeSale>()
                        .setQuery(query, config, snapshot -> {
                            ProductModalForeSale modalForeSale =
                                    snapshot.toObject(ProductModalForeSale.class);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                            if (modalForeSale != null)
                                return modalForeSale;
                            // modalForeSale.setSnapId(id);
                            return modalForeSale;
                        }).build();


        filteringAdapter = new FilteringAdapter(options
                , FliteredActivity.this);

        filterRv.setAdapter(filteringAdapter);

        filteringAdapter.startListening();

        filteringAdapter.setItemClick((Ds, position) -> {
            Log.d(TAG, "setOnItemClick: PID=" + Ds.getId());
            ProductModalForeSale PMs = Ds.toObject(ProductModalForeSale.class);
            startActivity(new Intent(FliteredActivity.this,
                    ItemDetailedActivity.class)
                    .putExtra(PRODUCT_ID, PMs.getProductId()));
        });

        filteringAdapter.setOnLoadingStateChange(loadingState -> {
            switch (loadingState) {
                case LOADING_INITIAL:
                    Log.d(TAG, "onStateChange: LOADING_INITIAL");
                    refreshLayout.setRefreshing(true);
                    break;

                case LOADING_MORE:
                    Log.d(TAG, "onStateChange:LOADING_MORE ");
                    refreshLayout.setRefreshing(true);
                    break;

                case LOADED:
                    Log.d(TAG, "onStateChange: LOADED");
                    refreshLayout.setRefreshing(false);
                    break;

                case ERROR:
                    Toast.makeText(FliteredActivity.this,
                            "Error Occurred!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onStateChange: ERROR");
                    refreshLayout.setRefreshing(false);
                    break;

                case FINISHED:
                    refreshLayout.setRefreshing(false);
                    Log.d(TAG, "onStateChange:FINISHED ");
                    break;
            }
        });

        filteringAdapter.setCartBagClick(new FilteringAdapter.OnCartBagClick() {
            @Override
            public void setCartBagClick(DocumentSnapshot Ds, int position) {
                if (!isRightTimeToDeliver) {
                    try {
                        String msg= String.format("Order Placing Time is between %s-%s\nPlease Try Again Tomorrow",
                                Utiles.convertIn12Hrs(startTime), Utiles.convertIn12Hrs(endTime));
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FliteredActivity.this);
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("Can Not deliver Now");
                        alertDialog.setMessage(msg);
                        alertDialog.setIcon(R.drawable.ic_not_deliverables);
                        alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
                        alertDialog.show();
                    }catch (Exception e){
                        Log.d(TAG, "setCartBagClick: "+e.getMessage());
                    }

                }
            }
        });

    }


    private void keyBoardListener() {
        editorActionListener = (v, actionId, event) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    if (v.getText().toString().trim().length() != 0) {
                        Log.d(TAG, "onEditorAction: text=" + v.getText().toString());
                        searchItem(v.getText().toString().trim());
                        UIUtil.hideKeyboard(FliteredActivity.this);

                    }
                    break;
            }

            return false;
        };
        edSearch.setOnEditorActionListener(editorActionListener);

    }

    @Override
    public void onRefresh() {
        filteringAdapter.refresh();

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
                startActivity(new Intent(FliteredActivity.this,
                        ProfileActivity.class));
                break;
            case R.id.myoder:
                startActivity(new Intent(
                        getApplicationContext(),
                        AllOrderActivity.class));
                closeDrawer();
                break;

            case R.id.address:
                startActivity(new Intent(FliteredActivity.this,
                        ProfileActivity.class));
                closeDrawer();
                break;
            case R.id.feedback:
                startActivity(new Intent(FliteredActivity.this,
                        FeedbackActivity.class));

                closeDrawer();
                break;
            case R.id.contact:
                closeDrawer();
                startActivity(new Intent(FliteredActivity.this,
                        ContactUsActivity.class));

                break;
            case R.id.logout:
                closeDrawer();
                SignOut();
                break;

            case R.id.dev_ll:
                startActivity(new Intent(FliteredActivity.this,
                        DeveloperActivity.class));
                break;

            case R.id.about:
                startActivity(new Intent(FliteredActivity.this,
                        AboutActivity.class));

                closeDrawer();
                break;


            case R.id.faq:
                startActivity(new Intent(FliteredActivity.this,
                        FAQActivity.class));
                closeDrawer();
                break;

            case R.id.rlt_cart:
                startActivity(new Intent(FliteredActivity.this,
                        VeggiesCartActivity.class));

                break;

            case R.id.rlt_noti:
                txtNoti.setVisibility(View.GONE);
                sp.edit().putInt(NOTI_COUNT, 0).apply();
                startActivity(new Intent(FliteredActivity.this,
                        NotificationActivity.class));

                break;
            case R.id.share:
                closeDrawer();
                shareApp();
                break;

            case R.id.img_search:
                if (edSearch.getText().toString().trim().length() != 0) {
                    searchItem(edSearch.getText().toString().trim());
                    UIUtil.hideKeyboard(FliteredActivity.this);
                } else {
                    if (searchRecyclerview.getVisibility() == View.VISIBLE)
                        searchRecyclerview.setVisibility(View.GONE);
                    if (searchAdapter != null) {
                        searchAdapter.stopListening();
                    }

                }

                break;


            case R.id.privacy:
                createTabs(PRIVACY_URL);
                break;


        }
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

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(filterRv.getRootView(), message,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void SignOut() {
        AuthUI.getInstance()
                .signOut(FliteredActivity.this)
                .addOnCompleteListener(task -> {
                    // user is now signed out
                    startActivity(new Intent(FliteredActivity.this,
                            LoginActivity.class));
                    finish();

                }).addOnFailureListener(e -> showToast(e.getMessage()));
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
            Toast.makeText(FliteredActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.getStackTrace();
        }
    }

    private void addTextWatcher() {
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edSearch.getText().toString().trim().length() != 0) {
                    String ST = s.toString();
                    Log.d(TAG, "afterTextChanged: s=" + ST);
                    Log.d(TAG, "afterTextChanged: edSearch=" +
                            edSearch.getText().toString().trim());
                    filterRv.setVisibility(View.GONE);
                    searchItem(ST);

                } else {
                    if (searchRecyclerview.getVisibility() == View.VISIBLE)
                        searchRecyclerview.setVisibility(View.GONE);
                    if (filterRv.getVisibility() == View.GONE)
                        filterRv.setVisibility(View.VISIBLE);
                    if (searchAdapter != null) searchAdapter.stopListening();

                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        Log.d(TAG, "onResume: ITEM_DELETED=" + sp.getBoolean("Item_Deleted", false));

        if (sp.getBoolean("Item_Deleted", false)
                && filteringAdapter != null) {
            filteringAdapter.refresh();
            if (searchRecyclerview.getVisibility() == View.VISIBLE
                    && searchAdapter != null) {
                searchAdapter.refresh();
            }
            sp.edit().putBoolean("Item_Deleted", false).apply();
        }

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        if (searchAdapter != null) {
            searchAdapter.stopListening();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (filteringAdapter != null) {
            filteringAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void searchItem(String searchText) {

        Log.d(TAG, "searchItem:searchText=" + searchText);
        if (searchRecyclerview.getVisibility() == View.GONE) {
            searchRecyclerview.setVisibility(View.VISIBLE);
        }

        CollectionReference productList = FirebaseFirestore.getInstance().
                collection(PRODUCT_LIST_COLLECTION);
        Query query = productList
                .whereEqualTo(IS_PUBLISHED, true)
                .orderBy("productName").
                        startAt(searchText.toUpperCase())
                .endAt(searchText.toLowerCase() + "\uf8ff");

        Log.d(TAG, "uf8ff: " + searchText + "\uf8ff");

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();
       /*
        final FirestoreRecyclerOptions<ProductModalForeSale> option = new
                FirestoreRecyclerOptions.Builder<ProductModalForeSale>()
                .setQuery(query, ProductModalForeSale.class)
                .build();*/

        FirestorePagingOptions<ProductModalForeSale> options =
                new FirestorePagingOptions.Builder<ProductModalForeSale>()
                        .setQuery(query, config, snapshot -> {
                            ProductModalForeSale modalForeSale =
                                    snapshot.toObject(ProductModalForeSale.class);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                            if (modalForeSale != null)
                                return modalForeSale;
                            // modalForeSale.setSnapId(id);
                            return modalForeSale;
                        }).build();

        searchAdapter = new SearchAdapter(options);
        searchRecyclerview.setAdapter(searchAdapter);
        searchAdapter.startListening();

        searchAdapter.setItemClick((Ds, pos) -> {
            ProductModalForeSale PMS = Ds.toObject(ProductModalForeSale.class);
            Log.d(TAG, "OnSearchItemClick: Search name=" + PMS.getProductName());
            startActivity(new Intent(getApplicationContext(),
                    ItemDetailedActivity.class)
                    .putExtra(PRODUCT_ID, PMS.getProductId()));
        });

        searchAdapter.setOnLoadingStateChange(loadingState -> {
            switch (loadingState) {
                case LOADING_INITIAL:
                    Log.d(TAG, "onStateChange: LOADING_INITIAL");
                    refreshLayout.setRefreshing(true);
                    break;

                case LOADING_MORE:
                    Log.d(TAG, "onStateChange:LOADING_MORE ");
                    refreshLayout.setRefreshing(true);
                    break;

                case LOADED:
                    Log.d(TAG, "onStateChange: LOADED");
                    refreshLayout.setRefreshing(false);
                    break;

                case ERROR:
                    Toast.makeText(FliteredActivity.this,
                            "Error Occurred!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onStateChange: ERROR");
                    refreshLayout.setRefreshing(false);
                    break;

                case FINISHED:
                    refreshLayout.setRefreshing(false);
                    Log.d(TAG, "onStateChange:FINISHED ");
                    break;
            }
        });
    }

    private void saveDeliveryTimeInPref() {
        /* saving the result of start and end time so that it can be used to checked /prevent
         when user clicks on the cart bag if the current time is outof delivery time
         range and then i can use if(!Utiles.compare(startTime, EndTime)) to save this as pref
         and can stop saving the item in the cart*/

        DocumentReference delTimeRef = FirebaseFirestore.getInstance().
                collection(DELIVERY_TIME_COLLEC).document(DELIVERY_TIME__DOC_NAME);
        delTimeRef.get().addOnSuccessListener(ds -> {
            String DT = ds.get(GLOBAL_TIME_SLOT_KEY).toString().trim();
            startTime = DT.split("-")[0].trim();
            endTime = DT.split("-")[1].trim();
            try {
                boolean isCorrectTime = Utiles.compare(startTime, endTime);
                sp.edit().putBoolean(IS_CORRECT_TIME, isCorrectTime).apply();
                isRightTimeToDeliver = isCorrectTime;
                Log.d(TAG, "saveDeliveryTimeInPref: isRightTimeToDeliver="+isRightTimeToDeliver);
                Log.d(TAG, "saveDeliveryTimeInPref: isCorrectTime="+isCorrectTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    private void offer() {
        RelativeLayout root = findViewById(R.id.include_banner_offer_lyt);

        TextView textView = root.findViewById(R.id.offer_txt);


        DocumentReference offer = FirebaseFirestore.getInstance()
                .collection("veggiesApp/Veggies/Offer")
                .document("bannerOffer");

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



        ImageView  closeShortOffer;

        closeShortOffer = root.findViewById(R.id.banner_close_offer);

        closeShortOffer.setOnClickListener(v -> {
            if (root.getVisibility() == View.VISIBLE) root.setVisibility(View.GONE);
        });
    }
}