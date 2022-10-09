package com.darpan.project.veggiesadmin.adapter.blocks;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.darpan.project.veggiesadmin.firebaseModal.blocks.BlockName;
import com.darpan.project.veggiesadmin.firebaseModal.blocks.BlockNumber;
import com.darpan.project.veggiesadmin.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

public class BlockNumberAdapter
        extends FirestorePagingAdapter<BlockNumber, BlockNumberAdapter.NameHolder> {

    private OnItemClick listener;
    private OnLoadingStateChanged onLoadingStateChanged;
    public BlockNumberAdapter(@NonNull FirestorePagingOptions<BlockNumber> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NameHolder holder, int position,
                                    @NonNull BlockNumber model) {
        holder.blockName.setText(model.getBlockNumber());

    }

    @NonNull
    @Override
    public NameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_area, parent, false);
        return new NameHolder(v);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        if (onLoadingStateChanged != null) {
            onLoadingStateChanged.onStateChange(state);
        }
        super.onLoadingStateChanged(state);

    }

    class NameHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener{
        private TextView blockName;
        public NameHolder(@NonNull View itemView) {
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
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete ");

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
