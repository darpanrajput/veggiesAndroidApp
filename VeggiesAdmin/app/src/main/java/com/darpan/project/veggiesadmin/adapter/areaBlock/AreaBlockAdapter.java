package com.darpan.project.veggiesadmin.adapter.areaBlock;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.firebaseModal.AreaBlockModal;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

public class AreaBlockAdapter
        extends FirestorePagingAdapter<AreaBlockModal, AreaBlockAdapter.blockHolder> {
    private OnItemClick listener;
    private OnLoadingStateChanged onLoadingStateChanged;

    public AreaBlockAdapter(@NonNull FirestorePagingOptions<AreaBlockModal> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull blockHolder holder, int position, @NonNull AreaBlockModal model) {
        holder.blockName.setText(model.getBlockName());
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
    public blockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_area, parent, false);
        return new blockHolder(v);
    }


    class blockHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        private TextView blockName;

        public blockHolder(@NonNull View itemView) {
            super(itemView);
            blockName = itemView.findViewById(R.id.area_name);

            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(v -> {
                int po = getAdapterPosition();
                if (po != RecyclerView.NO_POSITION && listener != null) {
                    listener.setOnItemClick(getItem(po), po);
                }
            });

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                switch (item.getItemId()) {
                    case 1:
                        listener.OnNameChangeClick(getItem(pos), pos);
                        return true;
                    case 2:
                        listener.OnDeleteClick(getItem(pos), pos);
                        return true;


                }
            }


            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem changeName = menu.add(Menu.NONE, 1, 1, "Change");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete Area");

            changeName.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }
    }


    public interface OnItemClick {
        void setOnItemClick(DocumentSnapshot snapshot, int position);

        void OnDeleteClick(DocumentSnapshot snapshot, int pos);

        void OnNameChangeClick(DocumentSnapshot snapshot, int pos);
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
}
