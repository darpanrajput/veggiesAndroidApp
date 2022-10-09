package com.darpan.project.vegies.adapters.category.filter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.firebaseModal.ProductModalForeSale;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;
import com.darpan.project.vegies.roomdatabase.repo.ProductRepo;


import java.util.ArrayList;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.darpan.project.vegies.constant.Constants.IS_CORRECT_TIME;
import static com.darpan.project.vegies.constant.Constants.O_F_S;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;


public class FilteringAdapter
        extends FirestorePagingAdapter<ProductModalForeSale, FilteringAdapter.FilteredHolder> {

    private ProductRepo productRepo;
    private static final String TAG = "FilteringAdapter:";
    private Context saleCtx;
    private OnItemClick listener;
    private OnLoadingStateChanged onLoadingStateChanged;
    private OnCartBagClick onCartBagClick;
    private boolean isRightTimeToDeliver;



    public FilteringAdapter(@NonNull FirestorePagingOptions<ProductModalForeSale> options, Context saleCtx) {
        super(options);
        this.saleCtx = saleCtx;
        isRightTimeToDeliver=saleCtx.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
                .getBoolean(IS_CORRECT_TIME,true);

    }


    @Override
    protected void onBindViewHolder(@NonNull FilteredHolder holder, int position, @NonNull ProductModalForeSale model) {
        holder.priceoofer.setPaintFlags(holder.priceoofer.getPaintFlags() |
                Paint.STRIKE_THRU_TEXT_FLAG);
        setSpinner(holder, model);
        Glide.with(holder.mctx)
                .load(model.getProductImage())
                .thumbnail(Glide.with(holder.mctx).load(R.drawable.lodingimage))
                .dontAnimate()
                .into(holder.imgIcon);
        holder.txtTitle.setText(model.getProductName());
        holder.desc.setText(model.getProductDescription());

        if (model.getProductDiscount() > 0) {
            holder.lvlOffer.setVisibility(View.VISIBLE);
            Log.d(TAG, "onBindViewHolder: DICS>0 called");
            double res = getDiscountPrice(model.getProductDiscount(), model.getProductPrice());
            Log.d(TAG, "onBindViewHolder: discount=" + model.getProductDiscount());
            String priceOffer = holder.mctx.getString(R.string.currency) +
                    model.getProductPrice();
            holder.priceoofer.setText(priceOffer);

            String txtPrice = holder.mctx.getString(R.string.currency) +
                    Math.round(res);
            holder.txtPrice.setText(txtPrice);

            String disc = model.getProductDiscount() + "% off";
            holder.priceoofer.setVisibility(View.VISIBLE);
            holder.txtOffer.setText(disc);


        } else {
            String txtPrice = holder.mctx.getString(R.string.currency) + model.getProductPrice();
            holder.lvlOffer.setVisibility(View.GONE);
            holder.priceoofer.setVisibility(View.GONE);
            holder.txtPrice.setText(txtPrice);
        }
        /*int qrt = getSavedQuantity(model.getProductId());*/
            Log.d(TAG, "onBindViewHolder: qrt=" + isAlreadySaved(model));

            if (isAlreadySaved(model)) {
                holder.lvlCardbg.setBackground(
                        holder.mctx.getDrawable(R.drawable.bg_red_shape));
                holder.imgCard.setImageDrawable(holder.mctx.getDrawable(R.drawable.ic_minus_rounded));
            } else {
                holder.lvlCardbg.setBackground(holder.mctx.getDrawable(R.drawable.bg_green_plus));
                holder.imgCard.setImageDrawable(holder.mctx.getDrawable(R.drawable.ic_plus_rounded));

            }


        Log.d(TAG, "onBindViewHolder: NAME="+model.getProductName());
        Log.d(TAG, "onBindViewHolder: STATUS="+model.getProductStatus());
        Log.d(TAG, "onBindViewHolder: QTY="+model.getStockQuantity());

        if (model.getProductStatus().trim().equalsIgnoreCase(O_F_S)
                ||model.getStockQuantity() < 2) {
            Log.d(TAG, "onBindViewHolder: ifOUT OFSTOCK Called");
            String finishTxt = model.getProductName() + " (OUT OF STOCK)";
            holder.txtTitle.setText(finishTxt);
            holder.lvlCardbg.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.txtTitle.setTextColor(holder.mctx.
                        getColor(R.color.colorAccent));
            } else {
                holder.txtTitle.setTextColor(holder.mctx.getResources().
                        getColor(R.color.colorAccent));
            }

        }else {
            holder.lvlCardbg.setVisibility(View.VISIBLE);
            holder.txtTitle.setText(model.getProductName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.txtTitle.setTextColor(holder.mctx.
                        getColor(R.color.black_color));
            } else {
                holder.txtTitle.setTextColor(holder.mctx.getResources().
                        getColor(R.color.black_color));
            }

        }

        holder.lvlCardbg.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                boolean isSaved = isAlreadySaved(model);

                Log.d(TAG, "FilteringAdapter: isRightTimeToDeliver="+isRightTimeToDeliver);

                if (position != RecyclerView.NO_POSITION && onCartBagClick != null) {
                    onCartBagClick.setCartBagClick(getItem(position), position);

                    if (isRightTimeToDeliver) {
                        if (isSaved) {
                      /*if the card is already saved and the user clicked it twice then delete th
                      item from databases*/
                            productRepo.deleteThisCart(model.getProductId());
                            Toast.makeText(holder.mctx, "Item Deleted", Toast.LENGTH_SHORT).show();
                            /*change the UI*/
                            holder.lvlCardbg.setBackground(holder.mctx.getDrawable(R.drawable.bg_green_plus));
                            holder.imgCard.setImageDrawable(holder.mctx.getDrawable(R.drawable.ic_plus_rounded));


                        } else {

                            ProductTable PT = new ProductTable();
                            PT.setProductName(model.getProductName());
                            PT.setProductDescription(model.getProductDescription());
                            PT.setProductId(model.getProductId());
                            PT.setProductCategory(model.getProductCategory());


                            PT.setProductPrice(holder.txtPrice.getText().toString()
                                    .replace("₹", "").trim());

                            PT.setDiscountPer("0");
                            PT.setProductType(model.getProductType());

                            PT.setOptionQty(holder.selectQtySpinner.getSelectedItem()
                                    .toString().replace(model.getProductUnit(), "").trim());

                            PT.setProductStatus(model.getProductStatus());
                            PT.setProductUnit(model.getProductUnit());
                            PT.setProductImage(model.getProductImage());
                            PT.setUniquePid(model.getUniquePid());
                            PT.setStockQuantity(String.valueOf(model.getStockQuantity()));
                            PT.setSavedQty(1);
                            PT.setOriginalPrice(holder.txtPrice.getText().toString()
                                    .replace("₹", "").trim());
                            PT.setOriginalQty(holder.selectQtySpinner.getSelectedItem()
                                    .toString().replace(model.getProductUnit(), "").trim());
                            PT.setAvailableOptions(new ArrayList<String>(model.getOptionQty()));


                            productRepo.insert(PT);
                            Toast.makeText(holder.mctx, "Item Added To Cart", Toast.LENGTH_SHORT).show();
                            holder.lvlCardbg.setBackground(holder.mctx.getDrawable(R.drawable.bg_red_shape));
                            holder.imgCard.setImageDrawable(holder.mctx.getDrawable(R.drawable.ic_minus_rounded));

                        }
                    }
                }

                Log.d(TAG, "onClick: qty=" + isSaved);

            }
            });

          holder.imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    if (getItemCount() == 0) {
                        Toast.makeText(holder.mctx, "item Not Found !!!", Toast.LENGTH_SHORT).show();

                    } else {
                        listener.setOnItemClick(getItem(position), position);

                    }
                }
            }
        });



    }


    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        if (onLoadingStateChanged != null) {
            onLoadingStateChanged.onStateChange(state);
        }
        super.onLoadingStateChanged(state);
    }

    @NonNull
    @Override
    public FilteredHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_prod_list, parent, false);
        productRepo = new ProductRepo(saleCtx);


        return new FilteredHolder(V);
    }

    static class FilteredHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtOffer, txtPrice, priceoofer, desc;
        private LinearLayout lvlSubitem, lvlOffer, lvlCardbg;
        private ImageView imgIcon, imgCard;
        private Context mctx;
        private Spinner selectQtySpinner;

        public FilteredHolder(@NonNull View itemView) {
            super(itemView);
            mctx = itemView.getContext();
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.price);
            txtOffer = itemView.findViewById(R.id.txt_offer);
            lvlSubitem = itemView.findViewById(R.id.lvl_subitem);
            lvlOffer = itemView.findViewById(R.id.lvl_offer);
            imgIcon = itemView.findViewById(R.id.img_icon);
            lvlCardbg = itemView.findViewById(R.id.lvl_cardbg);
            imgCard = itemView.findViewById(R.id.img_card);
            priceoofer = itemView.findViewById(R.id.priceoofer);
            desc = itemView.findViewById(R.id.txt_desc);
            selectQtySpinner = itemView.findViewById(R.id.qty_spinner);

        }
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

    private boolean isAlreadySaved(ProductModalForeSale p) {
        try {
            return p.getProductId().equals(productRepo.getProductId(p.getProductId()));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    /*private int getSavedQuantity(String PID) {

        try {
            return productRepo.getSavedQty(PID);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }*/

    private double getActualCost(ProductModalForeSale p) {
        if (p.getProductDiscount() > 0) {
            double dicAmt = (p.getProductDiscount() / 100d) * p.getProductPrice();
            return p.getProductPrice() - dicAmt;

        } else {
            return p.getProductPrice();
        }
    }

    private double getNewCost(double cost, double multiple) {
        if (cost > 0 && multiple > 0)
            return cost * multiple;
        else
            return cost;
    }

    public interface OnItemClick {
        void setOnItemClick(DocumentSnapshot Ds, int position);
    }

    public void setItemClick(OnItemClick listener) {
        this.listener = listener;
    }

    public interface OnLoadingStateChanged {
        void onStateChange(LoadingState loadingState);
    }

    public void setOnLoadingStateChange(OnLoadingStateChanged stateChange) {
        onLoadingStateChanged = stateChange;

    }

    public interface OnCartBagClick {
        void setCartBagClick(DocumentSnapshot Ds, int position);
    }

    public void setCartBagClick( OnCartBagClick onCartBagClick) {
        this.onCartBagClick = onCartBagClick;
    }

    private void setSpinner(FilteredHolder h, ProductModalForeSale p) {
        ArrayList<String> qty = new ArrayList<>();
        for (String option : p.getOptionQty()) {

            qty.add(option + " " + p.getProductUnit());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(h.mctx,
                android.R.layout.simple_list_item_1, qty);

        h.selectQtySpinner.setAdapter(arrayAdapter);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        h.selectQtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: Inx=" + parent.getSelectedItem());
                /* savedQty=parent.getSelectedItemPosition();*/
                String op = parent.getSelectedItem().toString();
                double option = Double.parseDouble(op.replace(p.getProductUnit(), "")
                        .trim());

                String effectedPrice =
                        String.format(Locale.getDefault(),"%.2f",getNewCost(getActualCost(p), option));
                h.txtPrice.setText(getRupee(effectedPrice));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getRupee(String s) {
        return "₹" + s;
    }
}
