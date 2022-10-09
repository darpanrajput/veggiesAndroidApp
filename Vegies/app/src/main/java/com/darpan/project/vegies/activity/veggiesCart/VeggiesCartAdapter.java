package com.darpan.project.vegies.activity.veggiesCart;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;
import com.darpan.project.vegies.roomdatabase.repo.ProductRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class VeggiesCartAdapter extends RecyclerView.Adapter<VeggiesCartAdapter.cartHolder> {
    private List<ProductTable> cartModalList;
    private Context cartContext;
    private ProductRepo productRepo;
    private static final String TAG = "VeggiesCartAdapter:";
    private deleteClickListener listener;
    private ArrayAdapter<String> arrayAdapter;
    private OnProductUpdate productUpdate;
    private SparseBooleanArray sparseBooleanArray;//this will help us Avoid to call spinnerItemsSelected For the FirstTime on View Initialisation


    public VeggiesCartAdapter(List<ProductTable> cartModalList, Context cartContext) {
        this.cartModalList = cartModalList;
        this.cartContext = cartContext;
        productRepo = new ProductRepo(cartContext);
        sparseBooleanArray = new SparseBooleanArray();


    }

    public VeggiesCartAdapter() {
    }

    @NonNull
    @Override
    public cartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.veggies_cart_item_lyt, parent, false);

        return new cartHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull cartHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.oldPriceTxt.setPaintFlags(holder.oldPriceTxt.getPaintFlags() |
                Paint.STRIKE_THRU_TEXT_FLAG);
        holder.itemCost.setTag(position);
        sparseBooleanArray.put(position, false);
        holder.index = position;
        Log.d(TAG, "onBindViewHolder: Called");
        if (cartModalList != null && cartModalList.size() != 0) {
            ProductTable cartModal = cartModalList.get(position);

            Glide.with(cartContext)
                    .load(cartModal.getProductImage())
                    .thumbnail(Glide.with(cartContext).load(R.drawable.lodingimage))
                    .into(holder.img_icon);
            holder.itemName.setText(cartModal.getProductName());

            String QTY = cartModal.getOptionQty() + cartModal.getProductUnit();
            holder.itemQty.setText(QTY);
            /* holder.itemCount.setText(String.valueOf(cartModal.getSavedQty()));*/
            holder.oldPriceTxt.setVisibility(View.GONE);
            holder.itemCost.setText(getRupee(cartModal.getProductPrice()));

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
            /*setSpinner(holder, cartModal);*/
            arrayAdapter = new ArrayAdapter<>(cartContext,
                    android.R.layout.simple_list_item_1, getDropDownList(cartModal));


            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            holder.optionQtySpinner.setAdapter(arrayAdapter);

            /*holder.optionQtySpinner.setSelection(getSpinnerIndex(holder.optionQtySpinner.getAdapter(),
                    createOptionValue(cartModal)));*/
        }

    }


    @Override
    public int getItemCount() {
        if (cartModalList != null) {
            return cartModalList.size();
        }
        return 0;
    }

    class cartHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQty, itemCost, /*itemCount*/
                oldPriceTxt;
        ImageView deleteItem, /*minusItem, plusItem*/
                img_icon;
        Context itemCtx;
        Spinner optionQtySpinner;
        int index = -1;


        public cartHolder(@NonNull View itemView) {
            super(itemView);
            itemCtx = itemView.getContext();
            itemName = itemView.findViewById(R.id.item_name);
            itemQty = itemView.findViewById(R.id.item_qty_plus_unit);
            itemCost = itemView.findViewById(R.id.item_price);
            deleteItem = itemView.findViewById(R.id.img_delete);
            img_icon = itemView.findViewById(R.id.img_icon);
            oldPriceTxt = itemView.findViewById(R.id.old_price_txt);
            optionQtySpinner = itemView.findViewById(R.id.cart_option_qty_spinner);

            optionQtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (sparseBooleanArray.get(index)) {
                        Log.d(TAG, "onItemSelected: item =" + parent.getSelectedItem());
                        Log.d(TAG, "onItemSelected: Inx=" + parent.getSelectedItemPosition());
                        String op = parent.getSelectedItem().toString();
                        itemQty.setText(op);
                        Log.d(TAG, "cartHolder: index=" + index);
                        Log.d(TAG, "cartHolder: pos=" + itemCost.getTag());
                        double option = Double.parseDouble(op.replace(cartModalList.get(index).getProductUnit(), "")
                                .trim());//replace kg with "" 20.90 kg -->20.90

                        double factor = getFactor(Double.parseDouble(cartModalList.get(index).getOriginalPrice()),
                                Double.parseDouble(cartModalList.get(index).getOriginalQty()));
                        String effectedPrice =
                                String.format(Locale.getDefault(), "%.2f", option * factor);
                        itemCost.setText(getRupee(effectedPrice));

                        Log.d(TAG, "onItemSelected: " + option);
                        cartModalList.get(index).setProductPrice(String.valueOf(option * factor));
                        cartModalList.get(index).setOptionQty(String.valueOf(option));
                        productRepo.update(cartModalList.get(index));
                        //productUpdate.ProductUpdate(cartModalList.get(index));

                    }
                    sparseBooleanArray.put(index, true);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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


    private String getRupee(String s) {
        return "₹" + s;
    }


    private String createOptionValue(ProductTable Pt) {
        /*this will help to regenerate the string of option
         * like--> 1 kg (qty + productUnit)*/
        return Pt.getOptionQty() + " " + Pt.getProductUnit();

    }

    private int getSpinnerIndex(SpinnerAdapter adapter, String s) {
        /*find the index of the string s in inside the specific spinner*/
        int spinnerPosition = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (s != null && adapter.getItem(i).toString().contains(s)) {
                spinnerPosition = i;
            }
        }
        return spinnerPosition;
    }

    private ArrayList<String> getDropDownList(ProductTable p) {
        ArrayList<String> qty = new ArrayList<>();
        for (String option : p.getAvailableOptions()) {

            qty.add(option + " " + p.getProductUnit());
        }
        return qty;
    }

    private double getFactor(double price, double qty) {
        /*suppose  originalPrice='16.00', originalQty='2'
         then factor should be (16/2=)8 that means 8rs/kg*/

        return price / qty;
    }

    public interface OnProductUpdate {
        void ProductUpdate(ProductTable productTable);
    }

    public void setOnUpdateProduct(OnProductUpdate o) {
        productUpdate = o;
    }

}
