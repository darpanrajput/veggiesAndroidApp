package com.darpan.project.vegies.activity.newUIDesign;

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

import androidx.annotation.NonNull;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.darpan.project.vegies.BuildConfig;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.activity.ItemDetailedActivity;
import com.darpan.project.vegies.activity.about.AboutActivity;

import com.darpan.project.vegies.activity.newUIDesign.adapters.BannerPagerAdapter;
import com.darpan.project.vegies.activity.newUIDesign.adapters.ProductSaleAdapter;
import com.darpan.project.vegies.activity.newUIDesign.adapters.categoryAdapter;
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
import com.darpan.project.vegies.adapters.category.search.SearchAdapter;
import com.darpan.project.vegies.firebaseModal.BannerModal;
import com.darpan.project.vegies.firebaseModal.CategoryModal;
import com.darpan.project.vegies.firebaseModal.ProductModalForeSale;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;
import com.darpan.project.vegies.roomdatabase.viewModal.ProductViewModal;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

import static com.darpan.project.vegies.constant.Constants.BANNER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.IS_PUBLISHED;
import static com.darpan.project.vegies.constant.Constants.KEY_BANNER_IMAGE;
import static com.darpan.project.vegies.constant.Constants.KEY_BANNER_NAME;
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

public class MainActivity2 extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String demoCategory = "veggiesApp/Veggies/democategory";
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private CollectionReference categoryRef = firebaseFirestore.collection(demoCategory);

    private SharedPreferences sp;
    private SearchAdapter searchAdapter;
    private RecyclerView categoryRv, searchRecyclerview, productRv;
    private SwipeRefreshLayout refreshLayout;
    private static final String TAG = "MainActivity2:";

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
    //private CategoryAdapter categoryAdapter;
    private categoryAdapter catAdapter;
    private RelativeLayout rootLyt;
    private Context context;
    /*banner*/
    private BannerPagerAdapter bannerPagerAdapter;
    private List<BannerModal> bannerModalList = new ArrayList<>();
    private TabLayout bannerTabView;
    private ViewPager bannerViewPager;

    private ProductSaleAdapter productSaleAdapter;
    private TextView txt_viewllproduct,seeAllCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        User = FirebaseAuth.getInstance().getCurrentUser();
        refreshLayout = findViewById(R.id.swipe_refresh);
        categoryRv = findViewById(R.id.category_rv);
        categoryRv.setLayoutManager(new GridLayoutManager(this,
                3));

        context = MainActivity2.this;
        refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(this);
        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        InitializeView();
        setDrawer();
        getCategoryDataCall();
        settingUpProductList();
        keyBoardListener();

        ProductViewModal productViewModal= ViewModelProviders.of(this)
                .get(ProductViewModal.class);
        productViewModal.getAllCartData().observe(this, new Observer<List<ProductTable>>() {
            @Override
            public void onChanged(List<ProductTable> productTables) {
                txtCountcard.setText(String.valueOf(productTables.size()));
            }
        });
    }

    private void InitializeView() {
        /*the main activity view*/
        drawerLayout = findViewById(R.id.drawer_layout);
        rootLyt = findViewById(R.id.root_lyt);
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


        bannerViewPager = findViewById(R.id.banner_viewPager);
        bannerTabView = findViewById(R.id.banner_tabview);
        /*setting up the banner data*/
        bannerPagerAdapter = new BannerPagerAdapter(context, getBannerSnapshot());
        bannerViewPager.setAdapter(bannerPagerAdapter);
        bannerTabView.setupWithViewPager(bannerViewPager, true);

        /*product data*/

        productRv = findViewById(R.id.product_rv);
        txt_viewllproduct=findViewById(R.id.txt_viewllproduct);
        seeAllCategory=findViewById(R.id.see );
        txt_viewllproduct.setOnClickListener(this);
        seeAllCategory.setOnClickListener(this);


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


    private void getCategoryDataCall() {
        String category = getIntent().getStringExtra(categoryName);
        Log.d(TAG, "getCategoryDataCall: category=" + category);

        Query query = categoryRef.orderBy(categoryName, Query.Direction.ASCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<CategoryModal> options =
                new FirestorePagingOptions.Builder<CategoryModal>()
                        .setQuery(query, config, new SnapshotParser<CategoryModal>() {
                            @NonNull
                            @Override
                            public CategoryModal parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                CategoryModal categoryModal = snapshot.toObject(CategoryModal.class);
                                //categoryModalArrayList.add(categoryModal);
                                String id = snapshot.getId();
                                Log.d(TAG, "parseSnapshot: id=" + id);
                                if (categoryModal != null)
                                    categoryModal.setSnapId(id);
                                return Objects.requireNonNull(categoryModal);
                            }
                        })
                        .build();

        catAdapter = new categoryAdapter(options);

        categoryRv.setAdapter(catAdapter);

        catAdapter.startListening();

        catAdapter.setItemClick((Ds, position) -> {
            Log.d(TAG, "setOnItemClick: Category=" + Ds.getString(categoryName));
            startActivity(new Intent(MainActivity2.this,
                    FliteredActivity.class)
                    .putExtra(categoryName, Ds.getString(categoryName)));
        });

        catAdapter.setOnLoadingStateChange(loadingState -> {
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
                    Toast.makeText(MainActivity2.this,
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

    private List<BannerModal> getBannerSnapshot() {

        if (!bannerModalList.isEmpty()) bannerModalList.clear();
        firebaseFirestore.collection(BANNER_COLLECTION).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: ");
                        List<DocumentSnapshot> bannerDocList = task.getResult().getDocuments();
                        if (!bannerDocList.isEmpty()) {
                            for (DocumentSnapshot q : bannerDocList) {

                                Map<String, Object> bannerMap = q.getData();
                                bannerModalList.add(
                                        new BannerModal((String) bannerMap.get(KEY_BANNER_IMAGE),
                                                (String) bannerMap.get(KEY_BANNER_NAME)));
                                Log.d(TAG, "onComplete: bnnrUrl=" + bannerMap.get(KEY_BANNER_IMAGE));
                            }
                            bannerPagerAdapter.notifyDataSetChanged();
                        } else {
                            showToast("No banner Image");
                            Log.d(TAG, "onComplete: empty doc banner");
                        }

                    } else {
                        Log.d(TAG, "onComplete: Not Success");
                    }
                });
        return bannerModalList;
    }

    private void settingUpProductList() {
        CollectionReference productListRef = firebaseFirestore.
                collection(PRODUCT_LIST_COLLECTION);


        Query query = productListRef
                 .whereEqualTo(IS_PUBLISHED, true)
                .orderBy("productName", Query.Direction.ASCENDING)
                .limit(20);

        final FirestoreRecyclerOptions<ProductModalForeSale> options = new
                FirestoreRecyclerOptions.Builder<ProductModalForeSale>()
                .setQuery(query, ProductModalForeSale.class)
                .build();


        productRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        productSaleAdapter = new ProductSaleAdapter(options, context);
        productRv.setItemAnimator(new DefaultItemAnimator());
        productRv.setAdapter(productSaleAdapter);
        productSaleAdapter.startListening();
        productSaleAdapter.setItemClick(new ProductSaleAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(ProductModalForeSale productModalForeSale,
                                       int position) {
                Log.d(TAG, "setOnItemClick: PID=" + productModalForeSale.getProductId());
                startActivity(new Intent(MainActivity2.this,
                        ItemDetailedActivity.class)
                        .putExtra(PRODUCT_ID, productModalForeSale.getProductId()));

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
                        UIUtil.hideKeyboard(MainActivity2.this);

                    }
                    break;
            }

            return false;
        };
        edSearch.setOnEditorActionListener(editorActionListener);

    }

    @Override
    public void onRefresh() {
        if (catAdapter != null)
            catAdapter.refresh();
        settingUpProductList();


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
                startActivity(new Intent(MainActivity2.this,
                        ProfileActivity.class));
                break;
            case R.id.myoder:
                startActivity(new Intent(
                        getApplicationContext(),
                        AllOrderActivity.class));
                closeDrawer();
                break;

            case R.id.address:
                startActivity(new Intent(MainActivity2.this,
                        ProfileActivity.class));
                closeDrawer();
                break;
            case R.id.feedback:
                startActivity(new Intent(MainActivity2.this,
                        FeedbackActivity.class));

                closeDrawer();
                break;
            case R.id.contact:
                closeDrawer();
                startActivity(new Intent(MainActivity2.this,
                        ContactUsActivity.class));

                break;
            case R.id.logout:
                closeDrawer();
                SignOut();
                break;

            case R.id.dev_ll:
                startActivity(new Intent(MainActivity2.this,
                        DeveloperActivity.class));
                break;

            case R.id.about:
                startActivity(new Intent(MainActivity2.this,
                        AboutActivity.class));

                closeDrawer();
                break;


            case R.id.faq:
                startActivity(new Intent(MainActivity2.this,
                        FAQActivity.class));
                closeDrawer();
                break;

            case R.id.rlt_cart:
                startActivity(new Intent(MainActivity2.this,
                        VeggiesCartActivity.class));

                break;

            case R.id.rlt_noti:
                txtNoti.setVisibility(View.GONE);
                sp.edit().putInt(NOTI_COUNT, 0).apply();
                startActivity(new Intent(MainActivity2.this,
                        NotificationActivity.class));

                break;
            case R.id.share:
                closeDrawer();
                shareApp();
                break;

            case R.id.img_search:
                if (edSearch.getText().toString().trim().length() != 0) {
                    searchItem(edSearch.getText().toString().trim());
                    UIUtil.hideKeyboard(MainActivity2.this);
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

            case R.id.txt_viewllproduct:
                startActivity(new Intent(MainActivity2.this,
                        FliteredActivity.class)
                        .putExtra(categoryName, "Fruits"));
                break;

            case R.id.see:
                startActivity(new Intent(MainActivity2.this,
                        AllCategories.class));
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
        Snackbar snackbar = Snackbar.make(categoryRv.getRootView(), message,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void SignOut() {
        AuthUI.getInstance()
                .signOut(MainActivity2.this)
                .addOnCompleteListener(task -> {
                    // user is now signed out
                    startActivity(new Intent(MainActivity2.this,
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
            Toast.makeText(MainActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    rootLyt.setVisibility(View.GONE);
                    searchItem(ST);

                } else {
                    if (searchRecyclerview.getVisibility() == View.VISIBLE)
                        searchRecyclerview.setVisibility(View.GONE);
                    if (rootLyt.getVisibility() == View.GONE)
                        rootLyt.setVisibility(View.VISIBLE);
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
                && catAdapter != null) {
            catAdapter.refresh();
            if (productSaleAdapter!=null)
                productSaleAdapter.notifyDataSetChanged();

            if (searchRecyclerview.getVisibility()==View.VISIBLE
                    &&searchAdapter!=null){
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
        if (catAdapter != null) {
            catAdapter.stopListening();
        }

        if (productSaleAdapter != null)
            productSaleAdapter.stopListening();
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

        CollectionReference productListRef = FirebaseFirestore.getInstance().
                collection(PRODUCT_LIST_COLLECTION);
        Query query = productListRef
                .whereEqualTo(IS_PUBLISHED, true)
                .orderBy("productName").
                        startAt(searchText.toUpperCase()).endAt(searchText.toLowerCase() + "\uf8ff");

        Log.d(TAG, "uf8ff: " + searchText + "\uf8ff");

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
    /*    final FirestoreRecyclerOptions<ProductModalForeSale> options = new
                FirestoreRecyclerOptions.Builder<ProductModalForeSale>()
                .setQuery(query, ProductModalForeSale.class)
                .build();*/
        searchAdapter = new SearchAdapter(options);
        searchRecyclerview.setAdapter(searchAdapter);
        searchAdapter.startListening();

        searchAdapter.setItemClick((Ds, pos) -> {
            ProductModalForeSale PMS = Ds.toObject(ProductModalForeSale.class);
            Log.d(TAG, "OnSearchItemClick: Search name=" + PMS.getProductName());
            startActivity(new Intent(MainActivity2.this,
                    ItemDetailedActivity.class)
                    .putExtra(PRODUCT_ID, PMS.getProductId()));
        });
    }
}