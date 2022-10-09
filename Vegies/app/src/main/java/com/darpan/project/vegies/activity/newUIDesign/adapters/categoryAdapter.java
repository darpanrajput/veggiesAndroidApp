package com.darpan.project.vegies.activity.newUIDesign.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.firebaseModal.CategoryModal;


public class categoryAdapter extends
        FirestorePagingAdapter<CategoryModal, categoryAdapter.Holder> {

    private static final String TAG = "RealCategoryAdapter:";
    private OnItemClick listener;
    private OnLoadingStateChanged onLoadingStateChanged;

    public categoryAdapter(@NonNull FirestorePagingOptions<CategoryModal> options) {
        super(options);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_category2, parent, false);
        return new Holder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull CategoryModal model) {
     holder.category.setText(model.getCategoryName());
        Glide.with(holder.img.getContext())
                .load(model.getCategoryImage())
                .error(R.drawable.empty)
                .thumbnail(Glide.with(holder.img.getContext()).load(R.drawable.ezgifresize))
                .into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position!=RecyclerView.NO_POSITION && listener!=null){
                    listener.setOnItemClick(getItem(position),position);
                    Log.d(TAG, "onClick: At Item="+position);
                }
            }
        });
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        if (onLoadingStateChanged != null) {
            onLoadingStateChanged.onStateChange(state);
        }
        super.onLoadingStateChanged(state);
    }

    static class Holder extends RecyclerView.ViewHolder {
        private TextView  category;
        private ImageView img;

        public Holder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.txt_title);
            img=itemView.findViewById(R.id.imageView);

        }


    }

    public interface OnItemClick {
        void setOnItemClick(DocumentSnapshot Ds, int position);
    }

    public void setItemClick(OnItemClick listener) {
        this.listener = listener;
    }

    public interface OnLoadingStateChanged {
        void onStateChange(LoadingState loadingState);
    }

    public void setOnLoadingStateChange(OnLoadingStateChanged stateChange) {
        onLoadingStateChanged = stateChange;

    }

}
