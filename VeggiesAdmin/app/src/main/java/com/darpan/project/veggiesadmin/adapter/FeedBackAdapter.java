package com.darpan.project.veggiesadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darpan.project.veggiesadmin.firebaseModal.FeedbackModal;
import com.darpan.project.veggiesadmin.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

public class FeedBackAdapter
        extends FirestorePagingAdapter<FeedbackModal, FeedBackAdapter.Holder> {
    private OnItemClick listener;
    private OnLoadingStateChanged onLoadingStateChanged;
    private static final String TAG = "FeedBackAdapter:";
    public FeedBackAdapter(@NonNull FirestorePagingOptions<FeedbackModal> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull FeedbackModal model) {
        holder.rating.setRating(Float.parseFloat(model.getRating()));
        holder.userName.setText(model.getUserName());
        holder.feedback.setText(model.getFeedback());
        char ic = model.getUserName().charAt(0);
        holder.userIcon.setText(String.valueOf(ic));
        holder.email.setText(model.getUserEmail());

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);
        return new Holder(v);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        if (onLoadingStateChanged != null) {
            onLoadingStateChanged.onStateChange(state);
        }
        super.onLoadingStateChanged(state);

    }

    static class Holder extends RecyclerView.ViewHolder {
        private TextView feedback, userName, userIcon,email;
        private RatingBar rating;

        public Holder(@NonNull View itemView) {
            super(itemView);

            feedback = itemView.findViewById(R.id.feedback);
            userIcon = itemView.findViewById(R.id.user_icon);
            userName = itemView.findViewById(R.id.user_name);
            rating = itemView.findViewById(R.id.rating_bar);
            email=itemView.findViewById(R.id.user_email);
        }
    }

    public interface OnItemClick {
        void setOnItemClick(DocumentSnapshot snapshot, int position);
       // void OnDeleteClick(DocumentSnapshot snapshot,int pos);
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
