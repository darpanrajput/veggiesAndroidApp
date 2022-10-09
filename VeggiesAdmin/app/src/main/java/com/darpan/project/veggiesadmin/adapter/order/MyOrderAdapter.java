package com.darpan.project.veggiesadmin.adapter.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.darpan.project.veggiesadmin.firebaseModal.OrderPlacedModal2;
import com.darpan.project.veggiesadmin.R;
import com.bumptech.glide.Glide;


public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyHolder> {
    private OrderPlacedModal2 OPM;
    private Context ct;

    public MyOrderAdapter(OrderPlacedModal2 OPM, Context ct) {
        this.OPM = OPM;
        this.ct = ct;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_order_items, parent, false);

        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.name.setText(OPM.getOrderName().get(position));
        holder.qty.setText(String.format("Qty: %s",OPM.getOptionQty().get(position)));
        holder.qtyAndunit.setText(getQTYandUnit(OPM, position));
        Glide.with(ct)
                .load(OPM.getOrderImage().get(position))
                .error(R.drawable.empty)
                .into(holder.img);
        holder.price.setText(getPrice(OPM,position));



    }

    private String getPrice(OrderPlacedModal2 o, int p) {
        //1.0 kg of APPLE WASHINGTON with total Rs of ₹50/0.25kg total is ₹200.0
        if (o.getOrderDescription().size() > 1) {
            String des=o.getOrderDescription().get(p).split("₹")[2].trim();
            return "Rs: "+des;
        }else {
            return o.getTotalPrice();
        }
    }


    private String getQTYandUnit(OrderPlacedModal2 o, int pos) {
        String des = o.getOrderDescription()
                .get(pos)
                .split(" ")[1];
        String qty = o.getOptionQty().get(pos);
        return qty + des;
    }

    @Override
    public int getItemCount() {
        if (OPM != null)
            return Integer.parseInt(OPM.getTotalItem().trim());
        return 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView name, qty, qtyAndunit, price;
        private ImageView img;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.od_name);
            qty = itemView.findViewById(R.id.od_qty);
            qtyAndunit = itemView.findViewById(R.id.od_qty_unit);
            price = itemView.findViewById(R.id.od_price);
            img = itemView.findViewById(R.id.od_img);
        }
    }
}
