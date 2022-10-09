package com.darpan.project.veggiesadmin.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.darpan.project.veggiesadmin.firebaseModal.CategoryModal;
import com.darpan.project.veggiesadmin.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

public class CategoryListAdapter extends
        FirestorePagingAdapter<CategoryModal, CategoryListAdapter.categoryHolder> {
    private OnItemClick listener;
    private OnLoadingStateChanged onLoadingStateChanged;
    private static final String TAG = "CategoryListAdapter: ";

    public CategoryListAdapter(@NonNull FirestorePagingOptions<CategoryModal> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull categoryHolder holder, int position, @NonNull CategoryModal model) {
        //DocumentSnapshot documentSnapshot = getItem(position);

        holder.categoryName.setText(model.getCategoryName());
        Glide.with(holder.ctx)
                .load(model.getCategoryImage())
                .thumbnail(Glide.with(holder.ctx).load(R.drawable.ezgifresize))
                .into(holder.categoryImage);


    }

    @NonNull
    @Override
    public categoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_list, parent, false);
        return new categoryHolder(v);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        if (onLoadingStateChanged != null) {
            onLoadingStateChanged.onStateChange(state);
        }
        super.onLoadingStateChanged(state);
    }


    class categoryHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private TextView categoryName;
        private ImageView categoryImage;
        private Context ctx;

        public categoryHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
            categoryImage = itemView.findViewById(R.id.category_image);
            ctx = itemView.getContext();

            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        if (getItemCount() == 0) {
                            Toast.makeText(itemView.getContext(),
                                    "Category Not Found !!!",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            listener.setOnItemClick(getItem(position), position);
                        }
                    }
                }
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
                menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem changeImage = menu.add(Menu.NONE, 1, 1, "Change Image");
            MenuItem changeName = menu.add(Menu.NONE, 2, 2, "Change Category Name");
            MenuItem delete = menu.add(Menu.NONE, 3, 3, "Delete Whole Category");

            changeImage.setOnMenuItemClickListener(this);
            changeName.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();
            if ( listener != null  && position != RecyclerView.NO_POSITION) {
                switch (item.getItemId()){
                    case 1:
                        listener.OnImageClick(getItem(position),position);
                        return true;
                    case 2:
                        listener.OnNameChangeClick(getItem(position),position);
                        return true;
                    case 3:
                        listener.OnDeleteClick(getItem(position),position);
                        return true;

                }

            }

            return false;
        }
    }


    public interface OnItemClick {
        void setOnItemClick(DocumentSnapshot snapshot, int position);
        void OnDeleteClick(DocumentSnapshot snapshot,int pos);
        void OnImageClick(DocumentSnapshot snapshot,int pos);
        void OnNameChangeClick(DocumentSnapshot snapshot,int pos);


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
