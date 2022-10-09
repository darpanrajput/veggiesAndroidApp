package com.darpan.project.veggiesadmin.adapter.user;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.darpan.project.veggiesadmin.adapter.product.ProductListAdapter;
import com.darpan.project.veggiesadmin.firebaseModal.UserModal;
import com.darpan.project.veggiesadmin.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

public class customerAdapter
        extends FirestorePagingAdapter<UserModal, customerAdapter.Holder> {
    private OnItemClick listener;
    private OnLoadingStateChanged onLoadingStateChanged;

    public customerAdapter(@NonNull FirestorePagingOptions<UserModal> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position,
                                    @NonNull UserModal model) {
        holder.name.setText(String.format("Name: %s", model.getName()));
        holder.email.setText(String.format("Email: %s", model.getEmail()));
        holder.phone.setText(String.format("Mobile: %s", model.getMobile()));

        holder.address.setText(String.format("Address: %s", getAddress(model)));

        Glide.with(holder.photo.getContext())
                .load(model.getPhotoUrl())
                .error(R.drawable.empty)
                .thumbnail(Glide.with(holder.photo.getContext()).load(R.drawable.ezgifresize))
                .into(holder.photo);

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new Holder(v);
    }


    class Holder extends RecyclerView.ViewHolder {
        private TextView name, email, phone, address;
        private ImageView photo;

        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            photo = itemView.findViewById(R.id.photo);


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


    }

    public interface OnItemClick {
        void setOnItemClick(DocumentSnapshot snapshot, int position);
        /*  void OnDeleteClick(DocumentSnapshot snapshot,int pos);*/
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

    private String getAddress(UserModal u) {
        return "Block Name: " + u.getBlockName() + "\n" +
                "Block No: " + u.getBlockNo() + "\n" +
                "Full Address: " + u.getFullAddress() + "\n" +
                "LandMark: " + u.getLandmark() + "\n" +
                "Mobile: " + u.getMobile() + "\n" +
                "PIN :" + u.getPin();

    }
}
