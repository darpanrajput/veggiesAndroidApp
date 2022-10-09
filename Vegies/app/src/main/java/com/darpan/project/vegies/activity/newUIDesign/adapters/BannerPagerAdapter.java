package com.darpan.project.vegies.activity.newUIDesign.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.firebaseModal.BannerModal;

import java.util.List;


public class BannerPagerAdapter extends PagerAdapter {
    private List<BannerModal> bannerModalList;
    private Context context;
    private LayoutInflater layoutInflater;

    public BannerPagerAdapter(Context context, List<BannerModal> bannerModalList) {
        this.bannerModalList = bannerModalList;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        if (bannerModalList != null) {
            return bannerModalList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.item_banner,
                container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        Glide.with(context)
                .load(bannerModalList.get(position).getBannerImage())
                .placeholder(R.drawable.empty).into(imageView);
        container.addView(itemView);
        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
            }
        });
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);

    }
}
