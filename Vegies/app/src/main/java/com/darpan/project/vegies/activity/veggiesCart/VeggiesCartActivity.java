package com.darpan.project.vegies.activity.veggiesCart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.activity.payment.PaymentOrderActivity;
import com.darpan.project.vegies.activity.profile.ProfileActivity;
import com.darpan.project.vegies.firebaseModal.ProductModalForeSale;
import com.darpan.project.vegies.projectModal.OrderModal;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;
import com.darpan.project.vegies.roomdatabase.repo.ProductRepo;
import com.darpan.project.vegies.roomdatabase.viewModal.ProductViewModal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.darpan.project.vegies.constant.Constants.INTENT_SOURCE;
import static com.darpan.project.vegies.constant.Constants.ITEM_PRICE_LIST;
import static com.darpan.project.vegies.constant.Constants.ITEM_QTY_LIST;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_CLASS;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.u_blockName;
import static com.darpan.project.vegies.constant.Constants.u_blockNo;
import static com.darpan.project.vegies.constant.Constants.u_email;
import static com.darpan.project.vegies.constant.Constants.u_fullAddress;
import static com.darpan.project.vegies.constant.Constants.u_landmark;
import static com.darpan.project.vegies.constant.Constants.u_mobile;
import static com.darpan.project.vegies.constant.Constants.u_username;

public class VeggiesCartActivity extends AppCompatActivity
{
    private static final String TAG = "VeggiesCartActivity:";
    private ProductRepo productRepo;

    private LinearLayout lLCart;
    private TextView txtItem;
    private TextView totalAmount;
    private TextView txtContinue;
    private TextView txtEmpty;
    private List<ProductTable> cartDataList;
    private ProductViewModal productViewModal;
    private VeggiesCartAdapter cartAdapter;
    private RecyclerView cartRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String totalItem = "";
    private String totalItemsAmount = "";
    private Context cartContext;
    private OrderModal orderModal;
    private List<ProductModalForeSale> PMSList = new ArrayList<>();

    private List<Integer> eachItemPriceList = new ArrayList<>();
    private List<Integer> eachItemDemandQtyList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference userRef = firebaseFirestore.collection(USER_COLLECTION);
    private FirebaseUser user;
    private ProgressDialog pd;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veggies_cart);
        productRepo = new ProductRepo(getApplicationContext());
        getSupportActionBar().setTitle("Cart");
        cartContext = VeggiesCartActivity.this;
        orderModal = new OrderModal();
        pd = new ProgressDialog(this);
        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        ProductViewModal productViewModal = ViewModelProviders.of(this)
                .get(ProductViewModal.class);
        productViewModal.getAllCartData().observe(this, productTables ->
                Log.d(TAG, "onChanged: " + productTables.toString()));
        InitView();
        try {
            LiveCartData();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void InitView() {
        lLCart = findViewById(R.id.ll_card);//ll card
        txtItem = findViewById(R.id.txt_item);//total item
        txtContinue = findViewById(R.id.text_continue);//order text
        txtEmpty = findViewById(R.id.txt_empty);//invisible empty text
        totalAmount = findViewById(R.id.total_Amount);//total amount


        txtContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderModal != null) {
                    checkUser();
                } else {
                    ShowToast("Order Is Empty");
                }
            }
        });
    }

    private void LiveCartData() throws ExecutionException, InterruptedException {
        productRepo = new ProductRepo(this);

        totalItem = String.valueOf(getTotalProductCount());
        totalItemsAmount = String.valueOf(getTotalAmount());

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cartRecyclerView = findViewById(R.id.cart_rv);
        cartRecyclerView.setLayoutManager(linearLayoutManager);

        cartDataList = new ArrayList<>();
        cartAdapter = new VeggiesCartAdapter(productRepo.getAllProducts()
                , VeggiesCartActivity.this);
        cartRecyclerView.setAdapter(cartAdapter);
        cartAdapter.setDeleteItemClick((item, pos) -> {ShowToast(
                item.getProductName().toLowerCase() + " Removed");
            sp.edit().putBoolean("Item_Deleted",true).apply();
            cartAdapter.notifyDataSetChanged();
        });

        productViewModal = ViewModelProviders.of(this).get(ProductViewModal.class);
        productViewModal.getAllCartData().observe(this, new Observer<List<ProductTable>>() {
            @Override
            public void onChanged(List<ProductTable> productTables) {
                Log.d(TAG, "onChanged: ");
                if (productTables.size() == 0) {
                    // show message
                    txtEmpty.setVisibility(View.VISIBLE);
                    txtItem.setText("Total :0 Item");
                    txtContinue.setVisibility(View.GONE);
                    totalAmount.setText("Total :" + getString(R.string.currency) + "0");
                } else {
                    totalItem = String.valueOf(getTotalProductCount());
                    totalItemsAmount = String.valueOf(getTotalAmount());

                    txtItem.setText("Total :" + totalItem + " Item");
                    totalAmount.setText("Total :" + getString(R.string.currency) +
                            totalItemsAmount);
                    createModal(productTables);


                }

            }
        });


    }

    private void createModal(List<ProductTable> productTable) {
        /*  ProductModalForeSale PMS = new ProductModalForeSale();*/
        if (!eachItemPriceList.isEmpty()) eachItemPriceList.clear();
        if (!eachItemDemandQtyList.isEmpty()) eachItemDemandQtyList.clear();
        if (!PMSList.isEmpty()) PMSList.clear();

        for (ProductTable PT : productTable) {
            Log.d(TAG, "createModal: PT:Name =" + PT.getProductName());
            String[]optionQty={"007"};
            ProductModalForeSale p=new ProductModalForeSale();
            p.setUniquePid("007");
            p.setisPublished(true);
            p.setOptionQty(Arrays.asList(optionQty));
            p.setProductCategory(PT.getProductCategory());
            p.setProductDescription(PT.getProductDescription());
            p.setProductName(PT.getProductName());
            p.setProductQuantity(PT.getSavedQty());
            p.setProductId(PT.getProductId());
            p.setProductDiscount(0);
            p.setProductImage(PT.getProductImage());
            p.setProductPrice((int)Math.round(Double.parseDouble(PT.getProductPrice())));
            p.setStockQuantity((int)Math.round(Double.parseDouble(PT.getStockQuantity())));
            p.setProductStatus(PT.getProductStatus());
            p.setProductType(PT.getProductType());
            p.setProductUnit(PT.getProductUnit());

            PMSList.add(p);
            eachItemPriceList.add((int)Math.round(Double.parseDouble(PT.getProductPrice())));//that actual price of each item in cart that can give the discounted price(if given) in MultiOrderAdapter
            eachItemDemandQtyList.add(PT.getSavedQty());//saved  qty
        }
        //setting the actual list firebase collection
        orderModal.setProductModalForeSales(PMSList);
        orderModal.setTotalAmount((int)getTotalAmount());
        orderModal.setTotalItemCount(productTable.get(0).getSavedQty());//it will be used in single order summary to calculate the items count for single item
        Log.d(TAG, "createModal: getTotalAmount= " + getTotalAmount());
        // orderModal.setTotalItemCount(getSavedQty(PMS.getProductId()));//if a modal of size =1 is transferred then ite will to single order summary

        Log.d(TAG, "createModal: Name: " + orderModal.getProductModalForeSales()
                .get(0).getProductName());
    }



    private List<String> getOriginalPrice() {
        try {
            List<String> s = new ArrayList<>();
            for (ProductTable pt : productRepo.getAllProducts()) {
                s.add(pt.getProductPrice());
            }
            return s;

        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    private List<String> getOriginalQty() {
        try {
            List<String> s = new ArrayList<>();
            for (ProductTable pt : productRepo.getAllProducts()) {
                s.add(pt.getOptionQty());
            }
            return s;

        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    private double getTotalAmount() {
        try {
            int total = 0;
            for (ProductTable pt : productRepo.getAllProducts()) {
                total += Math.round(Double.parseDouble(pt.getProductPrice()));
            }

            return total;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getTotalProductCount() {
        try {
            return productRepo.getProductCount();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void ShowToast(String msg) {
        Toast.makeText(cartContext, msg, Toast.LENGTH_SHORT).show();
    }

    private void checkUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "getUserInfo:called ");
        String n = "getUserInfo";
        showPd("Getting Profile..");

        DocumentReference userDoc = userRef.document(user.getUid());

        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    if (!checkUserDataIfExistOnFirebase(documentSnapshot.getData())) {
                        startActivity(new Intent(cartContext,
                                ProfileActivity.class));
                        Toast.makeText(getApplicationContext(), "Fill Your Profile",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        proceedFurther();
                    }

                } else {
                    hidePd();
                    ShowToast("Please Fill Your Details");
                    startActivity(new Intent(cartContext,
                            ProfileActivity.class));
                }
                Log.d(TAG, "onSuccess: " + n);
                hidePd();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePd();
                ShowToast(e.getMessage());
                Log.d(TAG, "onFailure: " + n);
            }
        });
    }

    private void proceedFurther() {
        Intent i = new Intent(VeggiesCartActivity.this,
                PaymentOrderActivity.class);

        i.putExtra(PRODUCT_CLASS, orderModal);
        i.putExtra(INTENT_SOURCE,"FROM_CART_ACTIVITY");
        i.putIntegerArrayListExtra(ITEM_PRICE_LIST, (ArrayList<Integer>) eachItemPriceList);
        i.putIntegerArrayListExtra(ITEM_QTY_LIST, (ArrayList<Integer>) eachItemDemandQtyList);
        startActivity(i);
        finish();

    }

    private boolean checkUserDataIfExistOnFirebase(Map<String, Object> data) {
        if (data.get(u_username) == null) return false;
        if (data.get(u_username) != null && data.get(u_username).toString().trim().isEmpty())
            return false;


        if (data.get(u_email) == null) return false;
        if (data.get(u_email) != null && data.get(u_email).toString().trim().isEmpty())
            return false;


        if (data.get(u_fullAddress) == null) return false;
        if (data.get(u_fullAddress) != null && data.get(u_fullAddress).toString().trim().isEmpty())
            return false;


        if (data.get(u_landmark) == null) return false;
        if (data.get(u_landmark) != null && data.get(u_landmark).toString().trim().isEmpty())
            return false;


        if (data.get(u_blockName) == null) return false;
        if (data.get(u_blockName) != null && data.get(u_blockName).toString().trim().isEmpty())
            return false;


        if (data.get(u_blockNo) == null) return false;
        if (data.get(u_blockNo) != null && data.get(u_blockNo).toString().trim().isEmpty())
            return false;


        if (data.get(u_mobile) == null) return false;
        if (data.get(u_mobile) != null && data.get(u_mobile).toString().trim().isEmpty())
            return false;


        return true;
    }

    private void hidePd() {
        pd.cancel();
    }

    private void showPd(String s) {
        pd.setTitle(s);
        pd.show();
    }


}