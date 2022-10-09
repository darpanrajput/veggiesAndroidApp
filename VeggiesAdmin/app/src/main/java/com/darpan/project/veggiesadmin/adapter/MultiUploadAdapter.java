package com.darpan.project.veggiesadmin.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.darpan.project.veggiesadmin.projectModal.MultiUploadModal;
import com.darpan.project.veggiesadmin.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultiUploadAdapter extends
        ListAdapter<MultiUploadModal, MultiUploadAdapter.holder> {
    private static final String TAG = "MultiUploadAdapter: ";
    private List<MultiUploadModal> modalList = new ArrayList<>();
    private Context context;

    public MultiUploadAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<MultiUploadModal> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MultiUploadModal>() {
                @Override
                public boolean areItemsTheSame(@NonNull MultiUploadModal oldItem,
                                               @NonNull MultiUploadModal newItem) {
                    return oldItem.getProductId().equals(newItem.getProductId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull MultiUploadModal oldItem,
                                                  @NonNull MultiUploadModal newItem) {
                    return areContentSame(oldItem, newItem);
                }
            };

    private static boolean areContentSame(MultiUploadModal o, MultiUploadModal n) {
        return o.getProductName().equals(n.getProductName()) &&
                o.getFileName().equals(n.getFileName()) &&
                o.getImageUri().equals(n.getImageUri()) &&
                o.getProductPrice() == n.getProductPrice() &&
                o.getProductQuantity() == n.getProductQuantity() &&
                o.getProgress() == n.getProgress() &&
                o.getProductQuantity() == n.getProductQuantity();

    }


    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.upload_progres_layout, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        MultiUploadModal modal = modalList.get(position);
        holder.setIsRecyclable(false);

        Glide.with(context).load(modal.getImageUri())
                .error(R.drawable.ic_no_image)
                .into(holder.Iv);

        String name = modal.getFileName();
        Uri location = Uri.parse(modal.getImageUri());
        String wholeName = name + "-" + getFileName(location);
        holder.Tv.setText(wholeName);
        holder.upload_file_qty_unit.setText(modal.getProductQuantity());
        holder.QTy.setText(String.format("QTY: %s", modal.getProductQuantity()));
        holder.upload_file_price.setText(String.format("Rs: %s", modal.getProductPrice()));
        holder.Pb.setProgress(modal.getProgress());

    }

    @Override
    public int getItemCount() {
        if (modalList != null) {
            return modalList.size();
        }
        return 0;
    }

    static class holder extends RecyclerView.ViewHolder {
        ImageView Iv;
        ProgressBar Pb;
        TextView Tv, QTy, upload_file_qty_unit, upload_file_price;


        public holder(@NonNull View itemView) {
            super(itemView);
            Tv = itemView.findViewById(R.id.upload_file_name);
            Pb = itemView.findViewById(R.id.upload_progress);
            Iv = itemView.findViewById(R.id.upload_image);
            upload_file_price = itemView.findViewById(R.id.upload_file_price);
            upload_file_qty_unit = itemView.findViewById(R.id.upload_file_qty_unit);
            QTy = itemView.findViewById(R.id.upload_file_qty);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;

        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));


                }
            }
                /* Log.e(TAG, e.getMessage());
                System.out.println(TAG + "Exception in get fileName ():" + e.getMessage());*/

        }
        try {
            if (result == null) {
                result = uri.getPath();
                int cut = 0;
                if (result != null) {
                    cut = result.lastIndexOf("/");
                }
                if (cut != -1) {
                    if (result != null) {
                        result = result.substring(cut + 1);
                    }

                }
            }
        } catch (Exception e) {

            System.out.println(TAG + " Exception wile eauting result==null:" + e.getMessage());
        }

        return result;

    }
}
