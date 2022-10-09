package com.darpan.project.vegies.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.darpan.project.vegies.activity.veggiesCart.VeggiesCartActivity;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.darpan.project.vegies.constant.Constants.INTENT_SOURCE;
import static com.darpan.project.vegies.constant.Constants.O_F_S;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_CLASS;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_ID;
import static com.darpan.project.vegies.constant.Constants.PRODUCT_LIST_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.USER_COLLECTION;
import static com.darpan.project.vegies.constant.Constants.u_blockName;
import static com.darpan.project.vegies.constant.Constants.u_blockNo;
import static com.darpan.project.vegies.constant.Constants.u_email;
import static com.darpan.project.vegies.constant.Constants.u_fullAddress;
import static com.darpan.project.vegies.constant.Constants.u_landmark;
import static com.darpan.project.vegies.constant.Constants.u_mobile;
import static com.darpan.project.vegies.constant.Constants.u_username;

public class ItemDetailedActivity extends AppCompatActivity
        implements View.OnClickListener {

    private Intent intent;
    private ProductModalForeSale productModalForeSale;
    private List<ProductModalForeSale> productModalForeSaleList;
    private ImageView itemImg, imgBack, imgCart;

    private TextView txtTotalCount, txtDesc, txtTitle, itemType, PriceText,
            txtQtyPlusUnit, txtItemCount, txtDiscount, txtStock, txtMRP;

    private RelativeLayout lvlCart;

    private LinearLayout lvlPricelist, plusLl, minusLl;

    private Button btnAddtocart, btnOrder;
    private ProductViewModal productViewModal;
    private ProductRepo productRepo;
    private static final String TAG = "ItemDetailedActivity:";
    private int itemCounter = 1;
    private int costAfterDiscount;

    private OrderModal orderModal;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference userRef = firebaseFirestore.collection(USER_COLLECTION);
    private FirebaseUser user;
    private ProgressDialog pd;
    private String productId;
    private Spinner optionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detailed);
        intent = getIntent();

        productModalForeSaleList = new ArrayList<>();
        pd = new ProgressDialog(ItemDetailedActivity.this);
        productId = intent.getStringExtra(PRODUCT_ID);
        productRepo = new ProductRepo(ItemDetailedActivity.this);

        initViews();
        if (productId != null) {
            setData(productId);
        }

        productViewModal = ViewModelProviders.of(this).get(ProductViewModal.class);

        productViewModal.getAllCartData().observe(this, new Observer<List<ProductTable>>() {
            @Override
            public void onChanged(List<ProductTable> productTables) {
                Log.d(TAG, "view modal onChanged: called");
                txtTotalCount.setText(String.valueOf(getCartCount()));
            }
        });

    }

    private void setData(String productId) {
        showPd("loading");
        DocumentReference ref = firebaseFirestore.collection(PRODUCT_LIST_COLLECTION)
                .document(productId);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ds) {
                if (ds.exists()) {
                    ProductModalForeSale p = ds.toObject(ProductModalForeSale.class);
                    if (p != null) {
                        showData(p);
                        productModalForeSale = p;
                    }
                }
                hidePd();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePd();
                ShowToast(e.getMessage());
            }
        });

    }

    private void initViews() {
        itemImg = findViewById(R.id.item_img);
        imgBack = findViewById(R.id.img_back);
        imgCart = findViewById(R.id.img_cart);
        txtTotalCount = findViewById(R.id.txt_total_count);
        lvlCart = findViewById(R.id.lvl_cart);
        txtTitle = findViewById(R.id.txt_title);
        txtDesc = findViewById(R.id.txt_desc);
        optionSpinner=findViewById(R.id.options_spinner);
        lvlPricelist = findViewById(R.id.lvl_pricelist);
        btnAddtocart = findViewById(R.id.btn_addtocart);
        btnOrder = findViewById(R.id.btn_order);
        itemType = findViewById(R.id.item_type);
        PriceText = findViewById(R.id.price_txt);
        txtQtyPlusUnit = findViewById(R.id.txt_qty_unit);
        txtItemCount = findViewById(R.id.txt_item_count);
        plusLl = findViewById(R.id.plus_ll);
        minusLl = findViewById(R.id.minus_ll);
        txtDiscount = findViewById(R.id.txt_discount);
        txtStock = findViewById(R.id.txt_stock);
        txtMRP = findViewById(R.id.txt_mrp);

        settingClicks();
    }

    private void settingClicks() {
        imgBack.setOnClickListener(this);
        lvlCart.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
        btnAddtocart.setOnClickListener(this);
        plusLl.setOnClickListener(this);
        minusLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();

                break;
            case R.id.lvl_cart:
                startActivity(new Intent(
                        ItemDetailedActivity.this, VeggiesCartActivity.class));

                break;

            case R.id.btn_addtocart:
                addItemToCart();

                break;


            case R.id.plus_ll:
                addItem();
                break;

            case R.id.minus_ll:
                minusItem();

                break;

            case R.id.btn_order:
                //*order this product directly*//*
                checkUser();
                break;
        }
    }


    private void showData(ProductModalForeSale productModalForeSale) {
        productModalForeSaleList.add(productModalForeSale);
        txtTitle.setText(productModalForeSale.getProductName());
        txtDesc.setText(productModalForeSale.getProductDescription());
        Glide.with(this).load(productModalForeSale.getProductImage())
                .thumbnail(Glide.with(this).load(R.drawable.ezgifresize)).into(itemImg);
        itemType.setText(String.format("Product type: %s", productModalForeSale.getProductType()));
        txtTotalCount.setText(String.valueOf(getCartCount()));
        costAfterDiscount = (int) getDiscountPrice(productModalForeSale.getProductDiscount(),
                productModalForeSale.getProductPrice());
        txtMRP.setText(String.format("MRP: %s", productModalForeSale.getProductPrice()));

        PriceText.setText(getString(R.string.currency) + costAfterDiscount);
        String qtyPlusUnit = String.valueOf(productModalForeSale.getProductQuantity())
                + " " + productModalForeSale.getProductUnit();

        txtQtyPlusUnit.setText(qtyPlusUnit);
        txtItemCount.setText(String.valueOf(itemCounter));
        if (productModalForeSale.getProductDiscount() > 0) {

            txtMRP.setPaintFlags(txtMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtDiscount.setVisibility(View.VISIBLE);
            txtDiscount.setText("Discount: " + productModalForeSale.getProductDiscount() + "%");
        }

        if (productModalForeSale.getProductStatus().trim().equalsIgnoreCase(O_F_S)
                || productModalForeSale.getStockQuantity() <= 1) {
            txtStock.setText("OUT OF STOCK");
            txtStock.setAllCaps(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                txtStock.setTextColor(getColor(R.color.colorAccent));
            } else {
                txtStock.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            btnOrder.setEnabled(false);
            btnOrder.setBackgroundResource(R.drawable.bg_out_of_stock_btn);
            btnOrder.setText("UnAvailable");
        }

        if (productModalForeSale.getStockQuantity() <= 10 && productModalForeSale.getStockQuantity() > 1) {
            txtStock.setText(String.format("only %s Left",
                    String.valueOf(productModalForeSale.getStockQuantity())));
            txtStock.setAllCaps(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                txtStock.setTextColor(getColor(R.color.colorAccent));
            } else {
                txtStock.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }

        setSpinner(productModalForeSale);


    }

    private void setSpinner(ProductModalForeSale p) {
        ArrayList<String> qty = new ArrayList<>();
        for (String option : p.getOptionQty()) {

            qty.add(option + " " + p.getProductUnit());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                ItemDetailedActivity.this,
                android.R.layout.simple_list_item_1, qty);
        arrayAdapter.setDropDownViewResource(android.R.layout.
                simple_spinner_dropdown_item);

        optionSpinner.setAdapter(arrayAdapter);

        optionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String op = parent.getSelectedItem().toString();
                double option = Double.parseDouble(op.replace(p.getProductUnit(), "")
                        .trim());

                String effectedPrice =
                       String.format(Locale.getDefault(),
                                "%.2f",getNewCost(getCostAfterDiscount(p), option));
                PriceText.setText(getRupee(effectedPrice));
                txtQtyPlusUnit.setText(op.trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private String getRupee(String s) {
        return "₹" + s;
    }

    private void addItemToCart() {
        ProductModalForeSale PD = productModalForeSale;

        if (disableCartIfAlreadyAdded(PD.getProductId().trim())) {
            ProductTable PT = new ProductTable();
            PT.setProductName(PD.getProductName());
            PT.setProductDescription(PD.getProductDescription());
            PT.setProductId(PD.getProductId());
            PT.setProductCategory(PD.getProductCategory());

            PT.setProductPrice(PriceText.getText().toString()
                    .replace("₹","").trim());

            PT.setDiscountPer("0"); //discount is already calculated
            PT.setProductType(PD.getProductType());

            PT.setOptionQty(optionSpinner.getSelectedItem().toString().
                    replace(PD.getProductUnit(),"").trim());

            PT.setProductStatus(PD.getProductStatus());
            PT.setProductUnit(PD.getProductUnit());
            PT.setProductImage(PD.getProductImage());
            PT.setUniquePid(PD.getUniquePid());
            PT.setStockQuantity(String.valueOf(PD.getStockQuantity()));

            PT.setSavedQty(1);
            PT.setOriginalPrice(PriceText.getText().toString()
                    .replace("₹","").trim());

            PT.setOriginalQty(optionSpinner.getSelectedItem()
                    .toString().replace(PD.getProductUnit(),"").trim());


            productViewModal.insert(PT);
            Toast.makeText(this, PD.getProductName().toLowerCase() + " added to cart", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, PD.getProductName() +
                    " Can not Add to cart", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean disableCartIfAlreadyAdded(String pid) {
        try {
            if (pid.equals(productViewModal.getProductId(pid))) {
                btnAddtocart.setBackgroundResource(R.drawable.bg_out_of_stock_btn);
                btnAddtocart.setText("Already Added");
                btnAddtocart.setEnabled(false);
                //item exist in SQLITE

                return false;
            } else if (productRepo.getAProduct(pid.trim())!=null &&productRepo.getAProduct(pid.trim()).getProductStatus()
                    .trim().equalsIgnoreCase(O_F_S)) {
                btnAddtocart.setBackgroundResource(R.drawable.bg_out_of_stock_btn);
                btnAddtocart.setText("OUT OF STOCK");
                //disable if the item is out of stock
                btnAddtocart.setEnabled(false);
                return false;

            } else {
                btnAddtocart.setBackgroundResource(R.drawable.bg_contiinue_green);
                btnAddtocart.setText("Add To Cart");
                btnAddtocart.setEnabled(true);
                return true;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }


    private double getCostAfterDiscount(ProductModalForeSale p) {
        if (p.getProductDiscount() > 0) {
            double dicAmt = (p.getProductDiscount() / 100d) * p.getProductPrice();

            return p.getProductPrice() - dicAmt;
        } else {
            return p.getProductPrice();
        }

    }

    private void addItem() {
        if (!productModalForeSale.getProductStatus().trim().equalsIgnoreCase(O_F_S) &&
                productModalForeSale.getStockQuantity() > itemCounter) {
            itemCounter++;
            txtItemCount.setText(String.valueOf(itemCounter));
            String P_T = getString(R.string.currency) +
                    getNewCost(costAfterDiscount, itemCounter);

            PriceText.setText(P_T);

            String qtyPlusUnit = getNewQuantity(productModalForeSale.
                            getProductQuantity(),
                    itemCounter)
                    + " " + productModalForeSale.getProductUnit();

            txtQtyPlusUnit.setText(qtyPlusUnit);
        } else {
            ShowToast("Can not add More Items");
            plusLl.setBackground(getDrawable(R.drawable.plus_disabled));
        }


    }

    private void minusItem() {

        if (itemCounter > 1) {
            itemCounter--;
            if (productModalForeSale.getStockQuantity() > itemCounter) {
                plusLl.setBackground(getDrawable(R.drawable.plus));
            }
            txtItemCount.setText(String.valueOf(itemCounter));
            String P_T = getString(R.string.currency) +
                    getNewCost(costAfterDiscount, itemCounter);

            PriceText.setText(P_T);

            String qtyPlusUnit = getNewQuantity(productModalForeSale.getProductQuantity(), itemCounter)
                    + " " + productModalForeSale.getProductUnit();

            txtQtyPlusUnit.setText(qtyPlusUnit);

        }
    }


    private void orderItem() {
        createOrderModal();
        Intent i = new Intent(ItemDetailedActivity.this,
                PaymentOrderActivity.class);
        i.putExtra(INTENT_SOURCE,"FROM_ITEMDETAILED_ACTIVITY");
        i.putExtra(PRODUCT_CLASS, orderModal);
        startActivity(i);
    }

    private void createOrderModal() {
        orderModal = new OrderModal();
        orderModal.setProductModalForeSales(productModalForeSaleList);
        orderModal.setTotalAmount((int) Math.round(getNewCost(costAfterDiscount, itemCounter)));
        orderModal.setTotalItemCount(itemCounter);

    }

    private int getCartCount() {
        try {
            return productRepo.getProductCount();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();

        }
        return 0;
    }

    private double getDiscountPrice(int discount, int actualPrice) {
        if (discount > 0) {
            double result = (discount / 100.0f) * actualPrice;
            Log.d(TAG, "getDiscountPrice: " + (actualPrice - result));
            return actualPrice - result;

        } else {
            return (double) actualPrice;
        }
    }

    private void ShowToast(String msg) {
        Toast.makeText(ItemDetailedActivity.this,
                msg, Toast.LENGTH_SHORT).show();

    }

    private double getNewCost(double cost, double multiple) {
        if (cost > 0 && multiple > 0)
            return cost * multiple;
        else
            return cost;
    }

    private int getNewQuantity(int oldqty, int itemCount) {
        if (itemCount > 0)
            return oldqty * itemCount;
        else return oldqty;
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
                        startActivity(new Intent(ItemDetailedActivity.this,
                                ProfileActivity.class));
                        Toast.makeText(getApplicationContext(), "Fill Your Profile",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        orderItem();
                    }

                } else {
                    hidePd();
                    ShowToast("Please Fill Your Details");
                    startActivity(new Intent(ItemDetailedActivity.this,
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