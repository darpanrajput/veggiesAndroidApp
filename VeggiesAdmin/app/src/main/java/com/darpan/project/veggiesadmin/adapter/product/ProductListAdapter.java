package com.darpan.project.veggiesadmin.adapter.product;

import android.util.Log;
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

import com.darpan.project.veggiesadmin.firebaseModal.ProductModalForeSale;
import com.darpan.project.veggiesadmin.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProductListAdapter
        extends FirestorePagingAdapter<ProductModalForeSale, ProductListAdapter.Holder> {
    private OnItemClick listener;
    private static final String TAG = "ProductListAdapter:";
    private OnLoadingStateChanged onLoadingStateChanged;

    public ProductListAdapter(@NonNull FirestorePagingOptions<ProductModalForeSale> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull ProductModalForeSale model) {

        holder.txtTitle.setText(model.getProductName());
        holder.txtDesc.setText(model.getProductDescription());
        holder.UniquePid.setText(String.format("Unique Item Id: %s",
                model.getUniquePid()));
        holder.stockLeft.setText(String.format("Stock Left: %s"
        ,model.getStockQuantity()));

        Glide.with(holder.itemImg.getContext())
                .load(model.getProductImage())
                .error(R.drawable.empty)
                .thumbnail(Glide.with(holder.itemImg.getContext())
                        .load(R.drawable.ezgifresize))
                .into(holder.itemImg);

        Log.d(TAG, "onBindViewHolder: visibility="+model.getisPublished());
        Log.d(TAG, "onBindViewHolder: id="+model.getProductId());
        if(model.getisPublished()){
            holder.visibilityImg.setImageResource(R.drawable.ic_visibility_on);
        }else {
            holder.visibilityImg.setImageResource(R.drawable.ic_visibility_off_24);
        }


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
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prod_list, parent, false);
        return new Holder(v);
    }

    class Holder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener{
        private TextView txtTitle, txtDesc,UniquePid,stockLeft;
        private ImageView itemImg,visibilityImg;

        public Holder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            itemImg = itemView.findViewById(R.id.item_image);
            txtDesc = itemView.findViewById(R.id.txt_desc);
            UniquePid=itemView.findViewById(R.id.txt_unique_pid);
            visibilityImg=itemView.findViewById(R.id.visibility_img);
            stockLeft=itemView.findViewById(R.id.stock_left);

            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener
                            != null) {
                        listener.setOnItemClick(getItem(position),position);
                    }
                }
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null){
                switch (item.getItemId() ) {
                    case 1 :listener.OnDeleteClick(getItem(position), position);
                    return true;

                    case 2:listener.OnVisibilityClick(getItem(position),position);
                    return true;
                }
            }
                return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete This Product");
            MenuItem Visibility = menu.add(Menu.NONE, 2, 2, "Change Visibility");
            delete.setOnMenuItemClickListener(this);
            Visibility.setOnMenuItemClickListener(this);
        }
    }


    public interface OnItemClick {
        void setOnItemClick(DocumentSnapshot snapshot, int position);
        void OnDeleteClick(DocumentSnapshot snapshot,int pos);
        void OnVisibilityClick(DocumentSnapshot snapshot,int pos);
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
