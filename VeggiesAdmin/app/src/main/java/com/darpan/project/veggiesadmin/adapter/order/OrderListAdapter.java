package com.darpan.project.veggiesadmin.adapter.order;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.darpan.project.veggiesadmin.firebaseModal.OrderPlacedModal2;
import com.darpan.project.veggiesadmin.util.Util;
import com.darpan.project.veggiesadmin.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

import static com.darpan.project.veggiesadmin.constant.Constants.PENDING;
import static com.darpan.project.veggiesadmin.constant.Constants.REJECTED;

public class OrderListAdapter
        extends FirestorePagingAdapter<OrderPlacedModal2, OrderListAdapter.orderHolder> {
    private OnItemClick listener;
    private OnLoadingStateChanged onLoadingStateChanged;

    public OrderListAdapter(@NonNull FirestorePagingOptions<OrderPlacedModal2> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull orderHolder holder, int position, @NonNull OrderPlacedModal2 model) {

        holder.oId.setText(String.format("ID :%s ", model.getSnapId()));
        holder.orderQty.setText(getAllQty(model));
        holder.orderStatus.setText(String.format("Status: %s",
                model.getOrderStatus()));
        if (model.getOrderStatus().contains(PENDING) ||
                model.getOrderStatus().contains(REJECTED)) {
            holder.orderStatus.setTextColor(
                    holder.orderStatus.getContext()
                            .getResources()
                            .getColor(R.color.colorRad)
            );
        }
        holder.orderAmt.setText(String.format("Total: %s",
                getRupee((model.getTotalPrice()))));

        holder.orderDate.setText(
                getDate(Long.parseLong(model.getDateOfOrder().trim())));

        holder.orderTime.setText(
                Util.convertIn12Hrs(
                        model.getOrderTiming().trim()));
        holder.totalitem.setText(model.getTotalItem());


        if (model.getOrderQuantity().size() > 1) {
            StringBuilder builder = new StringBuilder();
            for (String s : model.getUniquePid()) {
                builder.append(s).append(", ");

            }
            holder.UniquePid.setText(builder.toString());

        } else {
            StringBuilder builder = new StringBuilder();
            for (String s : model.getUniquePid()) {
                builder.append(s);

            }
            holder.UniquePid.setText(builder.toString());
        }





    }

    @NonNull
    @Override
    public orderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_list, parent, false);
        return new orderHolder(v);
    }


    private String getAllQty(OrderPlacedModal2 o) {
        double qty = 0;
        for (String s : o.getOptionQty()) {
            qty += Double.parseDouble(s);
        }

        String uni = o.getOrderDescription().toString().split(" ")[1];
        return "Total Qty: " + qty + uni;

    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        if (onLoadingStateChanged != null) {
            onLoadingStateChanged.onStateChange(state);
        }
        super.onLoadingStateChanged(state);

    }


    class orderHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {
        private TextView oId, orderQty, orderDate, orderTime, orderAmt, orderStatus,
                UniquePid, totalitem;

        public orderHolder(@NonNull View itemView) {
            super(itemView);
            oId = itemView.findViewById(R.id.order_id);
            orderQty = itemView.findViewById(R.id.order_qty);
            orderDate = itemView.findViewById(R.id.order_date);
            orderAmt = itemView.findViewById(R.id.order_amount);
            orderTime = itemView.findViewById(R.id.order_time);
            orderStatus = itemView.findViewById(R.id.order_status);
            UniquePid = itemView.findViewById(R.id.unique_pid);
            totalitem = itemView.findViewById(R.id.total_item);


            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener
                            != null) {
                        listener.setOnItemClick(getItem(position), position);
                    }
                }
            });

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                switch (item.getItemId()) {
                    case 1:
                        listener.OnDeleteClick(getItem(position), position);
                        return true;
                    case 2:
                        listener.OnRejectClick(getItem(position), position);
                        return true;

                    case 3:
                        listener.OnAcceptClick(getItem(position), position);
                        return true;

                    case 4:
                        listener.OnDeliveredClick(getItem(position), position);
                }
            }
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete This Order");
            MenuItem Reject = menu.add(Menu.NONE, 2, 2, "Reject This Order");
            MenuItem Accept = menu.add(Menu.NONE, 3, 3, "Accept This Order");
            MenuItem Delivered = menu.add(Menu.NONE, 4, 4, "Order Delivered");

            delete.setOnMenuItemClickListener(this);
            Reject.setOnMenuItemClickListener(this);
            Accept.setOnMenuItemClickListener(this);
            Delivered.setOnMenuItemClickListener(this);
        }
    }


    private String getRupee(String s) {
        return "Rs " + s;
    }

    public interface OnItemClick {
        void setOnItemClick(DocumentSnapshot snapshot, int position);

        void OnDeleteClick(DocumentSnapshot snapshot, int pos);

        void OnAcceptClick(DocumentSnapshot snapshot, int pos);

        void OnRejectClick(DocumentSnapshot snapshot, int pos);

        void OnDeliveredClick(DocumentSnapshot snapshot, int pos);

       /* void OnDeleteClick(DocumentSnapshot snapshot,int pos);
        void OnImageClick(DocumentSnapshot snapshot,int pos);
        void OnNameChangeClick(DocumentSnapshot snapshot,int pos);*/


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

    private String getDate(long dt) {
        return Util.getDate(dt);
    }
}
