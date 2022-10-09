package com.darpan.project.vegies.adapters.category.allorder;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firestore.v1.StructuredQuery;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.Utils.Utiles;
import com.darpan.project.vegies.firebaseModal.OrderPlacedModal;

public class AllOrderAdapter
        extends FirestorePagingAdapter<OrderPlacedModal, AllOrderAdapter.OrderHolder> {
    private OnItemClick listener;
    private OnLoadingStateChanged onLoadingStateChanged;
    private static final String TAG = "AllOrderAdapter:";

    public AllOrderAdapter(@NonNull FirestorePagingOptions<OrderPlacedModal> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderHolder holder, @SuppressLint("RecyclerView") int position,
                                    @NonNull OrderPlacedModal model) {

        holder.orderId.setText("ID \n#" + model.getOrderId());//showing actual order id

        holder.ordertot.setText(getRupee(model.getTotalPrice()));
        holder.orderStatus.setText(model.getOrderStatus());
        String date = Utiles.getDate(Long.parseLong(model.getDateOfOrder().trim()));
        holder.orderdate.setText(date);
        holder.txtUniquePid.setText(getOrderName(model));


       /* holder.txtUniquePid.setText(
                ("Unique Item Id: "+model.getUniquePid()));*/
        //Log.d(TAG, "onBindViewHolder: Optionqty="+model.getOptionQty());

        holder.icRightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    if (getItemCount() == 0) {
                        Toast.makeText(holder.icRightImg.getContext(),
                                "Not Found !!!", Toast.LENGTH_SHORT).show();

                    } else {
                        listener.setOnItemClick(getItem(position), position);
                    }
                }
            }
        });


    }

    private String getOrderName(OrderPlacedModal o) {
        if (o.getOrderName().size() > 1) {
            StringBuilder builder=new StringBuilder();
            for (String s:o.getOrderName()){
                builder.append(s).append(", ");
            }
            return builder.toString().toLowerCase().trim();
        }
        else {
            StringBuilder builder=new StringBuilder();
            for (String s:o.getOrderName()){
                builder.append(s);
            }
            return builder.toString().toLowerCase().trim();
        }
    }

    private void showToast(String m, OrderHolder h) {
        Toast.makeText(h.orderId.getContext(), m, Toast.LENGTH_SHORT).show();
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
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderHolder(v);
    }

    static class OrderHolder extends RecyclerView.ViewHolder {
        TextView orderId, ordertot, orderdate, orderStatus,
                txtUniquePid;
        ImageView icRightImg;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.txt_order_id);
            ordertot = itemView.findViewById(R.id.txt_order_total);
            orderdate = itemView.findViewById(R.id.txt_order_date);
            orderStatus = itemView.findViewById(R.id.txt_order_status);
            icRightImg = itemView.findViewById(R.id.ic_right);
            txtUniquePid = itemView.findViewById(R.id.unique_item_id);
        }
    }


    public interface OnItemClick {
        void setOnItemClick(DocumentSnapshot snapshot, int position);

    }

    public void setItemClick(OnItemClick listener) {
        this.listener = listener;
    }

    public interface OnLoadingStateChanged {
        void onStateChange(LoadingState loadingState);

    }

    public void setOnloadingStateChange(OnLoadingStateChanged stateChange) {
        onLoadingStateChanged = stateChange;

    }

    private String getRupee(String s) {
        return "₹" + s;
    }
}
