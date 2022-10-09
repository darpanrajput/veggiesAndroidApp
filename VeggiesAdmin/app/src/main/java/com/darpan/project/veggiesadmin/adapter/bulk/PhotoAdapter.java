package com.darpan.project.veggiesadmin.adapter.bulk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.darpan.project.veggiesadmin.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.Holder> {


    private Context context;
    private List<PhotoModal> photoModals;

    public PhotoAdapter(Context context, List<PhotoModal> photoModals) {
        this.context = context;
        this.photoModals = photoModals;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photos, parent, false);


        return new Holder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        PhotoModal modal = photoModals.get(0);
        if (modal.getUri() != null) {
            Glide.with(context)
                    .load(modal.getUri())
                    .thumbnail(Glide.with(context).load(R.drawable.ezgifresize))
                    .error(R.drawable.empty)
                    .into(holder.img);
        }

    }

    @Override
    public int getItemCount() {
        if (photoModals != null)
            return photoModals.size();
        return 0;
    }

    static class Holder extends RecyclerView.ViewHolder {

        private ImageView img;

        public Holder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.prod_photo);
        }
    }
}
