package com.darpan.project.vegies.adapters.category.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.Utils.Utiles;
import com.darpan.project.vegies.firebaseModal.NotificationModal;

import java.text.ParseException;

public class NotificationAdapter extends
        FirestorePagingAdapter<NotificationModal, NotificationAdapter.NotiHolder> {

    public NotificationAdapter(@NonNull FirestorePagingOptions<NotificationModal> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotiHolder holder, int position, @NonNull NotificationModal model) {


        holder.time.setText(get12hrs(model.getTime()).trim());
        holder.title.setText(model.getTitle());
        holder.msg.setText(model.getMessage());

    }

    @NonNull
    @Override
    public NotiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);

        return new NotiHolder(v);
    }

    static class NotiHolder extends RecyclerView.ViewHolder {
        TextView title, msg, time;

        public NotiHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.noti_time);
            msg = itemView.findViewById(R.id.noti_msg);
            title = itemView.findViewById(R.id.noti_title);
        }
    }

    private String get12hrs(String time) {
        try {
            return Utiles.convertIn12Hrs(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "nan";
    }
}
