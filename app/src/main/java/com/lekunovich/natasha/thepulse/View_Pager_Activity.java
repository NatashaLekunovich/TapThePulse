package com.lekunovich.natasha.thepulse;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class View_Pager_Activity  extends FragmentActivity
        implements View.OnClickListener, ViewPager.OnPageChangeListener{
    static final String LOG_TAG = "myLogs";
    ViewPager mViewPager;
    private MyFragmentPagerAdapter mAdapter;
    private int dotsCount;
    private ImageView[] dots;
    private LinearLayout pager_indicator;

    @Override
    protected void onResume() {
        super.onResume();


    }
    public void Finish(String fragment4){
        if(fragment4 == "finish"){
         System.exit(0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        } return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__pager);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        setPageViewIndicator();
    }

    private void setPageViewIndicator() {
        dotsCount =  mAdapter.getCount();
        dots = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.green));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);

            final int presentPosition = i;
            dots[presentPosition].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mViewPager.setCurrentItem(presentPosition);
                    return true;
                }
            });
            pager_indicator.addView(dots[i], params);
        } dots[0].setImageDrawable(getResources().getDrawable(R.drawable.red));

    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.green));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.red));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        static final int PAGE_COUNT = 4;
        public MyFragmentPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                      return ViewPager_Fr1.newInstance(0);
                case 1:
                     return ViewPager_Fr2.newInstance(1);
                case 2:
                    return ViewPager_Fr3.newInstance(2);
                case 3:
                    return ViewPager_Fr4.newInstance(3);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
}
