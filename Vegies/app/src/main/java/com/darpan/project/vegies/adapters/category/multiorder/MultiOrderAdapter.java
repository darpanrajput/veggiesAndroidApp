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
import com.darpan.project.vegies.projectModal.MultiOrder;
import com.darpan.project.vegies.projectModal.OrderModal;

import static com.darpan.project.vegies.constant.Constants.O_F_S;

public class MultiOrderAdapter extends RecyclerView.Adapter<MultiOrderAdapter.holder> {

    private static final String TAG = "MultiOrderAdapter:";
    private OrderModal orderModalList;
    private Context ct;
    private MultiOrder multiOrder;
    private int costTobeDeducted = 0;
    private int grandTotal;


    public MultiOrderAdapter(OrderModal orderModalList, MultiOrder MO, Context ct) {
        this.orderModalList = orderModalList;
        this.ct = ct;
        this.multiOrder = MO;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_multi_order, parent, false);

        return new holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        ProductModalForeSale firebaseModal = orderModalList.getProductModalForeSales().
                get(position);
        int eachItemActualCost = multiOrder.getEchItemPriceList().get(position);
        int eachItemDemandedQty = multiOrder.getEachItemQtyList().get(position);

        if (firebaseModal != null) {
            holder.txtTitle.setText(firebaseModal.getProductName());

            String imgUrl = firebaseModal.getProductImage();
            Glide.with(ct)
                    .load(imgUrl)
                    .placeholder(R.drawable.ezgifresize)
                    .error(R.drawable.empty)
                    .into(holder.itemImage);


            holder.totalPrice.setText(geTotalPrice(firebaseModal));

            holder.txtPriceWithQty.setText(PriceAndQty(firebaseModal, eachItemActualCost, eachItemDemandedQty));
            if (checkStock(firebaseModal.getStockQuantity(), eachItemDemandedQty)
                    && !firebaseModal.getProductStatus().trim().equalsIgnoreCase(O_F_S)) {

                //eachItemDemandedQty=is the required Qty (Known as getSaved Qty) coming from the cart activity
                //getStockQuantity= is the updated qty from the firebase (updated in MultiOrderActivity inside setItemsInOrderModal() method)
                holder.txtSorryOutOfStock.setTextColor(ct.getResources().getColor(R.color.black_color));

                holder.txtSorryOutOfStock.setText(R.string.avialable);
                Log.d(TAG, "onBindViewHolder: Available called");
            } else {
                //also if the status is set to OFS by the admin
                holder.totalPrice.setPaintFlags(holder.totalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.totalPrice.setTextColor(ct.getResources().getColor(R.color.colorRad));
                holder.txtSorryOutOfStock.setText(ct.getString(R.string.OFS));//Out Of Stock

                costTobeDeducted += firebaseModal.getProductPrice();
                Log.d(TAG, "onBindViewHolder: OFS Called");
                Log.d(TAG, "onBindViewHolder: " + firebaseModal.getProductPrice());
                //setCharges(firebaseModal.getProductPrice());//if OFS then oly reduce the grand total

            }

        }


    }

    @Override
    public int getItemCount() {
        if (orderModalList.getProductModalForeSales() != null)
            return orderModalList.getProductModalForeSales().size();

        return 0;
    }

    static class holder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtPriceWithQty, totalPrice, txtSorryOutOfStock;
        private ImageView itemImage;

        public holder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_name);
            itemImage = itemView.findViewById(R.id.item_image);
            txtPriceWithQty = itemView.findViewById(R.id.txt_price_and_item);
            totalPrice = itemView.findViewById(R.id.txt_total_item_price);
            txtSorryOutOfStock = itemView.findViewById(R.id.txt_sorry_out_of_stock);
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

    private String getRupee(String s) {
        return "₹" + s;
    }

    private boolean checkStock(int stock, int demanded) {
        return stock > demanded;
    }

    private String PriceAndQty(ProductModalForeSale PT, int ActualPrice, int demandedQty) {

        int disOrCostPrice = (int) getDiscountPrice(PT.getProductDiscount(), ActualPrice);
        String Price_and_QTY = demandedQty + " item(s) x " +
                ct.getString(R.string.currency) +
                disOrCostPrice;
        return Price_and_QTY;


    }

    private String geTotalPrice(ProductModalForeSale PT) {

        return getRupee(String.valueOf(PT.getProductPrice()));//it was the priceAfter discount(in cart Adapter) now converted in productPrice


    }


    public int getGrandTotal(int grandTotal) {
        Log.d(TAG, "getGrandTotal: " + grandTotal);
        Log.d(TAG, "getGrandTotal CostTobeDeducted: " + costTobeDeducted);
        return grandTotal - costTobeDeducted;
    }

    public int getCostTobeDeducted() {
        Log.d(TAG, "getCostTobeDeducted: " + costTobeDeducted);
        return costTobeDeducted;
    }


}
