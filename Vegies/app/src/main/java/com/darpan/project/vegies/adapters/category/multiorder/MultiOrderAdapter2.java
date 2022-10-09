package com.darpan.project.vegies.adapters.category.multiorder;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.firebaseModal.ProductModalForeSale;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;

import java.util.List;
import java.util.Locale;

import static com.darpan.project.vegies.constant.Constants.O_F_S;

public class MultiOrderAdapter2
        extends RecyclerView.Adapter<MultiOrderAdapter2.Holder> {
    private static final String TAG = "MultiOrderAdapter2:";
    private List<ProductTable> productTables;
    private List<ProductModalForeSale> PMSList;
    private Context context;
    private double costTobeDeducted = 0.0;

    public MultiOrderAdapter2(List<ProductTable> productTables,
                              List<ProductModalForeSale> PMSList, Context context) {
        this.productTables = productTables;
        this.PMSList = PMSList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_multi_order, parent, false);

        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.setIsRecyclable(false);
        ProductModalForeSale firebaseModal = PMSList.get(position);
        ProductTable PT = productTables.get(position);
        if (PT != null) {
            holder.txtTitle.setText(PT.getProductName());
            String imgUrl = PT.getProductImage();
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.ezgifresize)
                    .error(R.drawable.empty)
                    .into(holder.itemImage);
            holder.totalPrice.setText(geTotalPrice(PT));
            holder.txtPriceWithQty.setText(PriceAndQty(PT));

            if (isQFS(firebaseModal)||isOutOfQty(firebaseModal,PT)) {
                holder.totalPrice.setPaintFlags(holder.totalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.totalPrice.setTextColor(context.getResources().getColor(R.color.colorRad));
                holder.txtSorryOutOfStock.setText(context.getString(R.string.OFS));//Out Of Stock

                costTobeDeducted += Double.parseDouble(PT.getProductPrice());
                Log.d(TAG, "onBindViewHolder: OFS Called");
                Log.d(TAG, "onBindViewHolder: " + firebaseModal.getProductPrice());
                //setCharges(firebaseModal.getProductPrice());//if OFS then oly reduce the grand total

            } else {
                holder.txtSorryOutOfStock.setTextColor(context.getResources().getColor(R.color.black_color));
                holder.txtSorryOutOfStock.setText(R.string.avialable);
                Log.d(TAG, "onBindViewHolder: Available called");
            }


        }
    }

    private String geTotalPrice(ProductTable PT) {
        double cost = Double.parseDouble(PT.getProductPrice());
        String strCost = String.format(Locale.getDefault(), "%.2f", cost);
        return getRupee(strCost);
        //it was the priceAfter discount(in cart Adapter) now converted in productPrice


    }

    private String getRupee(String s) {
        return "₹" + s;
    }

    @Override
    public int getItemCount() {
        if (productTables != null) {
            return productTables.size();
        }
        return 0;
    }

    private boolean isEqual(String s1, String s2) {
        return s1.equals(s2);
    }

    private boolean isQFS(ProductModalForeSale p) {
        return p.getProductStatus().trim().equalsIgnoreCase(O_F_S);
    }

    private boolean isOutOfQty(ProductModalForeSale p, ProductTable t) {
        int stock=p.getStockQuantity();
        double demand=Double.parseDouble(t.getOptionQty());
        return demand>stock;
    }

    private String PriceAndQty(ProductTable PT) {

        int savedQty = PT.getSavedQty();
        String originalPrice = PT.getOriginalPrice();
        String totalQty = PT.getOptionQty() + " " + PT.getProductUnit() + "\n";
        String Price_and_QTY = totalQty + savedQty + "x" + getRupee(originalPrice);
        return Price_and_QTY;


    }

    static class Holder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtPriceWithQty, totalPrice, txtSorryOutOfStock;
        private ImageView itemImage;

        public Holder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_name);
            itemImage = itemView.findViewById(R.id.item_image);
            txtPriceWithQty = itemView.findViewById(R.id.txt_price_and_item);
            totalPrice = itemView.findViewById(R.id.txt_total_item_price);
            txtSorryOutOfStock = itemView.findViewById(R.id.txt_sorry_out_of_stock);
        }
    }

    public double getGrandTotal(double grandTotal) {
        Log.d(TAG, "getGrandTotal: " + grandTotal);
        Log.d(TAG, "getGrandTotal CostTobeDeducted: " + costTobeDeducted);
        return grandTotal - costTobeDeducted;
    }

    public double getCostTobeDeducted() {
        Log.d(TAG, "getCostTobeDeducted: " + costTobeDeducted);
        return costTobeDeducted;
    }
}
