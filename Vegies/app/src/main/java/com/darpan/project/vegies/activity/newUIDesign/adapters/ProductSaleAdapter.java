package com.darpan.project.vegies.activity.newUIDesign.adapters;


import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.firebaseModal.ProductModalForeSale;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;
import com.darpan.project.vegies.roomdatabase.repo.ProductRepo;


import java.util.concurrent.ExecutionException;

import static com.darpan.project.vegies.constant.Constants.O_F_S;


public class ProductSaleAdapter
        extends FirestoreRecyclerAdapter<ProductModalForeSale, ProductSaleAdapter.Holder> {

    private ProductRepo productRepo;
    private OnItemClick listener;
    private static final String TAG = "ProductSaleAdapter:";
    private Context saleCtx;
    private OnCartBagClick onCartBagClick;

    public ProductSaleAdapter(@NonNull FirestoreRecyclerOptions<ProductModalForeSale> options,
                              Context saleCtx) {
        super(options);
        this.saleCtx = saleCtx;

    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder,  int position,
                                    @NonNull ProductModalForeSale model) {

        holder.priceoofer.setPaintFlags(holder.priceoofer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Glide.with(holder.mctx)
                .load(model.getProductImage())
                .thumbnail(Glide.with(holder.mctx).load(R.drawable.lodingimage))
                .into(holder.imgIcon);

        holder.txtTitle.setText(model.getProductName());
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

       // int qrt = getSavedQuantity(model.getProductId());

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
            Log.d(TAG, "onBindViewHolder: if OUT OF STOCK Called");
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)  {
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

                Log.d(TAG, "onClick: qty=" + isSaved);

                if (isSaved) {
                    Toast.makeText(holder.mctx, "This product is already inserted", Toast.LENGTH_SHORT).show();

                } else {
                    if (position!=RecyclerView.NO_POSITION && onCartBagClick!=null){
                        onCartBagClick.setCartBagClick(getItem(position),position);
                    }
                    ProductTable PT=new ProductTable();
                    PT.setProductName(model.getProductName());
                    PT.setProductDescription(model.getProductDescription());
                    PT.setProductId(model.getProductId());
                    PT.setProductCategory(model.getProductCategory());

                    PT.setProductPrice(holder.txtPrice.getText().toString()
                            .replace("₹","").trim());

                    PT.setDiscountPer("0");
                    PT.setProductType(model.getProductType());

                  /*  PT.setOptionQty(holder.selectQtySpinner.getSelectedItem()
                            .toString().replace(model.getProductUnit(),"").trim());*/
                    PT.setOptionQty("1");

                    PT.setProductStatus(model.getProductStatus());
                    PT.setProductUnit(model.getProductUnit());
                    PT.setProductImage(model.getProductImage());
                    PT.setUniquePid(model.getUniquePid());
                    PT.setStockQuantity(String.valueOf(model.getStockQuantity()));
                    PT.setSavedQty(1);
                    PT.setOriginalPrice(holder.txtPrice.getText().toString()
                            .replace("₹","").trim());
                    /*PT.setOriginalQty(holder.selectQtySpinner.getSelectedItem()
                            .toString().replace(model.getProductUnit(),"").trim());
*/
                    PT.setOriginalQty("1");

                    productRepo.insert(PT);
                    Toast.makeText(holder.mctx, "Item Added To Cart", Toast.LENGTH_SHORT).show();
                    holder.lvlCardbg.setBackground(holder.mctx.getDrawable(R.drawable.bg_red_shape));
                    holder.imgCard.setImageDrawable(holder.mctx.getDrawable(R.drawable.ic_minus_rounded));

                }
            }
        });

     /*   holder.lvlCardbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = getSavedQuantity(model.getProductId());

                Log.d(TAG, "onClick: qty=" + qty);

                if (qty >= 1) {
                    Toast.makeText(holder.mctx, "This product is already inserted", Toast.LENGTH_SHORT).show();

                } else {
                    //Note:have added the discounted price no need
                    // to re-evaluate the price just get it  through getPriceAfterDiscount
                    productRepo.insert(new ProductTable(
                            model.isPublished(),
                            model.getProductCategory(),
                            model.getProductId(),
                            model.getProductDescription(),
                            model.getProductName(),
                            model.getProductPrice(),

                            (int) getDiscountPrice(
                                    model.getProductDiscount(),
                                    model.getProductPrice()
                            ),//it is the price after discount which can be equal to the actual product cost

                            model.getProductDiscount(),//it is the discount percent from fireStore
                            (int) getDiscountPrice(model.getProductDiscount(),
                                    model.getProductPrice())//it is the price before  discount which can be equal to the actual product cost
                            ,//it will be saved as the price before the discount which is the actual cost
                            model.getProductQuantity(),
                            model.getStockQuantity(),
                            model.getProductStatus(),
                            model.getProductType(),
                            model.getProductUnit(),
                            model.getProductImage()
                            , 1));
                    holder.lvlCardbg.setBackground(holder.mctx.getDrawable(R.drawable.bg_red_shape));
                    holder.imgCard.setImageDrawable(holder.mctx.getDrawable(R.drawable.ic_minus_rounded));

                }
            }
        });*/

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

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.product_lits_item, parent, false);
        productRepo = new ProductRepo(saleCtx);


        return new Holder(V);
    }

    public interface OnCartBagClick {
        void setCartBagClick(ProductModalForeSale PMS, int position);
    }

    public void setCartBagClick( OnCartBagClick onCartBagClick) {
        this.onCartBagClick = onCartBagClick;
    }


    private boolean isAlreadySaved(ProductModalForeSale p) {
        try {
            return p.getProductId().equals(productRepo.getProductId(p.getProductId()));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        // @BindView(R.id.txtTitle)
        TextView txtTitle;
        // @BindView(R.id.txt_offer)
        TextView txtOffer;
        // @BindView(R.id.price)
        TextView txtPrice;
        //   @BindView(R.id.priceoofer)
        TextView priceoofer;

        // @BindView(R.id.lvl_subitem)
        LinearLayout lvlSubitem;

        // @BindView(R.id.lvl_offer)
        LinearLayout lvlOffer;
        // @BindView(R.id.img_icon)
        ImageView imgIcon;

        // @BindView(R.id.lvl_cardbg)
        LinearLayout lvlCardbg;
        //  @BindView(R.id.img_card)
        ImageView imgCard;

        Context mctx;

        public Holder(@NonNull View itemView) {
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

    private int getSavedQuantity(String PID) {

        try {
            return productRepo.getProductCount();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public interface OnItemClick {
        void setOnItemClick(ProductModalForeSale productModalForeSale, int position);
    }

    public void setItemClick(OnItemClick listener) {
        this.listener = listener;
    }
}
