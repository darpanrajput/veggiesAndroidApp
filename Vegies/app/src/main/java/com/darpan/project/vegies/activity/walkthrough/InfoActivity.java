package com.darpan.project.vegies.activity.walkthrough;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.merhold.extensiblepageindicator.ExtensiblePageIndicator;
import com.darpan.project.vegies.Fragments.Info1Fragment;
import com.darpan.project.vegies.Fragments.Info2Fragment;
import com.darpan.project.vegies.Fragments.Info3Fragment;
import com.darpan.project.vegies.R;
import com.darpan.project.vegies.activity.splash.SplashActivity;

import static com.darpan.project.vegies.constant.Constants.FIRST_TIME;
import static com.darpan.project.vegies.constant.Constants.PREF_NAME;

public class InfoActivity extends AppCompatActivity {
    private ViewPager vpPager;
    private MyPagerAdapter adapterViewPager;
    private TextView nextBtn, skipBtn;
    private int selectPage = 0;
    private SharedPreferences sp;
    private ViewPager.OnPageChangeListener onPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_info);

        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        nextBtn = findViewById(R.id.btn_next);
        skipBtn = findViewById(R.id.btn_skip);

        vpPager = findViewById(R.id.vpPager);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        ExtensiblePageIndicator extensiblePageIndicator = (ExtensiblePageIndicator) findViewById(R.id.flexibleIndicator);
        extensiblePageIndicator.initViewPager(vpPager);
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPage = position;
                if (position == 0) {
                    nextBtn.setText("Next");
                } else if (position == 1) {
                    nextBtn.setText("Next");

                } else if (position == 2) {
                    nextBtn.setText("Finish");

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        vpPager.addOnPageChangeListener(onPageChangeListener);

        nextBtn.setOnClickListener(v -> {
            if (selectPage == 0) {
                vpPager.setCurrentItem(1);
            } else if (selectPage == 1) {
                vpPager.setCurrentItem(2);
            } else if (selectPage == 2) {
                startActivity(new Intent(InfoActivity.this, SplashActivity.class));
                sp.edit().putBoolean(FIRST_TIME, true).apply();
                finish();
            }


        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoActivity.this, SplashActivity.class));
                sp.edit().putBoolean(FIRST_TIME, true).apply();
                finish();
            }
        });


    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return Info1Fragment.newInstance("0", "Next");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return Info2Fragment.newInstance("1", "Next");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return Info3Fragment.newInstance("2", "Finish");
                default:
                    return null;
            }

        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            Log.e("page", "" + position);
            return "Page " + position;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            //
            return (Fragment) super.instantiateItem(container, position);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vpPager.removeOnPageChangeListener(onPageChangeListener);
    }
}