package com.darpan.project.vegies.adapters.category.Cart;

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
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;
import com.darpan.project.vegies.roomdatabase.repo.ProductRepo;


import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.cartHolder>  {
    private List<ProductTable> cartModalList;
    private Context cartContext;
    private ProductRepo productRepo;
    private static final String TAG = "CartAdapter:";
    private deleteClickListener listener;

    public CartAdapter(List<ProductTable> cartModalList,Context cartContext) {
        this.cartModalList = cartModalList;
        this.cartContext = cartContext;
        productRepo = new ProductRepo(cartContext);

    }

    public CartAdapter() {
    }

    @NonNull
    @Override
    public cartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_lyt, parent, false);

        return new cartHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull cartHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.oldPriceTxt.setPaintFlags(holder.oldPriceTxt.getPaintFlags() |
                Paint.STRIKE_THRU_TEXT_FLAG);
        if (cartModalList != null && cartModalList.size() != 0){
            ProductTable cartModal = cartModalList.get(position);
            Glide.with(cartContext)
                    .load(cartModal.getProductImage())
                    .thumbnail(Glide.with(cartContext).load(R.drawable.lodingimage))
                    .into(holder.img_icon);
            holder.itemName.setText(cartModal.getProductName());

            String QTY = cartModal.getOptionQty() + cartModal.getProductUnit();
            holder.itemQty.setText(QTY);
            holder.itemCount.setText(String.valueOf(cartModal.getSavedQty()));
            holder.oldPriceTxt.setVisibility(View.GONE);


          String  multiples = "(₹" + cartModal.getOriginalPrice() +
                  "x" + cartModal.getSavedQty()+")";//(90 Rs x 2)

            String I_COST = cartContext.getString(R.string.currency) +
                    cartModal.getProductPrice()+multiples;

            holder.itemCost.setText(I_COST);


            holder.minusItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int previousSavedQty = cartModal.getSavedQty();
                    Log.d(TAG, "onClick: QTY=" + previousSavedQty);
                    if (previousSavedQty <= 1) {
                        //if the previous quantity is 1 and user again clicked it then
                        //delete this item
                        /*just simple delete the cart*/
                        productRepo.deleteThisCart(cartModal.getProductId());
                        removeCartItem(position);


                    } else {
                        /*decrement by 1*/
                       cartModal.setSavedQty(previousSavedQty - 1);

                       double newOptionQty=(Double.parseDouble(cartModal.getOriginalQty()
                               ))* (previousSavedQty - 1);

                       double newCost=(previousSavedQty - 1)
                               *Double.parseDouble(cartModal.getOriginalPrice());

                       cartModal.setProductPrice(String.valueOf(newCost));
                       cartModal.setOptionQty(String.valueOf(newOptionQty));

                       productRepo.update(cartModal);

                        holder.itemCount.setText(String.valueOf(previousSavedQty - 1));
                    }

                }
            });

            holder.plusItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int previousSavedQty = cartModal.getSavedQty();


                    Log.d(TAG, "onClick: plus Qty=" + previousSavedQty);
                    cartModal.setSavedQty(previousSavedQty +1);
                    double newOptionQty=(Double.parseDouble(cartModal.getOriginalQty()))*
                            (previousSavedQty +1);

                    double newCost= (previousSavedQty +1)*
                            Double.parseDouble(cartModal.getOriginalPrice());
                    cartModal.setProductPrice(String.valueOf(newCost));
                    cartModal.setOptionQty(String.valueOf(newOptionQty));

                    productRepo.update(cartModal);

                     int newPrice = previousSavedQty + 1;

                    Log.d(TAG, "onClick: newPrice=" + newPrice);
                    Log.d(TAG, "onClick: ActualCost=" + cartModal.getProductPrice());

                    holder.itemCount.setText(String.valueOf(previousSavedQty + 1));



                }
            });

            holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: deleteImage");
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        productRepo.deleteThisCart(cartModal.getProductId());
                        removeCartItem(position);
                        listener.deleteThis(cartModal, position);

                    }

                }
            });

        }

    }



    @Override
    public int getItemCount() {
        if (cartModalList != null) {
            return cartModalList.size();
        }
        return 0;
    }

    static class cartHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQty, itemCost, itemCount, oldPriceTxt;
        ImageView deleteItem, minusItem, plusItem, img_icon;
        Context itemCtx;


        public cartHolder(@NonNull View itemView) {
            super(itemView);
            itemCtx = itemView.getContext();
            itemName = itemView.findViewById(R.id.item_name);
            itemQty = itemView.findViewById(R.id.item_qty_plus_unit);
            itemCost = itemView.findViewById(R.id.item_price);
            deleteItem = itemView.findViewById(R.id.img_delete);
            minusItem = itemView.findViewById(R.id.img_minus);
            plusItem = itemView.findViewById(R.id.img_plus);
            img_icon = itemView.findViewById(R.id.img_icon);
            itemCount = itemView.findViewById(R.id.txtcount);
            oldPriceTxt = itemView.findViewById(R.id.old_price_txt);


        }
    }
    private void removeCartItem(int pos) {
        cartModalList.remove(pos);
        notifyDataSetChanged();
    }

    public interface deleteClickListener {
        void deleteThis(ProductTable item, int pos);
    }

    public void setDeleteItemClick(deleteClickListener listener) {
        this.listener = listener;

    }
}
