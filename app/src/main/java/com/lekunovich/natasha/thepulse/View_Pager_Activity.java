package com.lekunovich.natasha.thepulse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class View_Pager_Activity  extends FragmentActivity implements ViewPager.OnPageChangeListener{
    static final String LOG_TAG = "myLogs";
    ViewPager mViewPager;
    MyFragmentPagerAdapter mAdapter;
    private int dotsCount;
    private ImageView[] dots;
    private LinearLayout pager_indicator;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__pager);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
    setPageViewIndicator();
    }

    private void setPageViewIndicator() {
        dotsCount =  mAdapter.getCount();
        dots = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.green, null));
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
        } dots[0].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.red, null));

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.green, null));
        }
        dots[position].setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.red, null));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
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
            return 4;
        }
    }

    public static class ViewPager_Fr1 extends Fragment {
        static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

        static ViewPager_Fr1 newInstance(int page) {
            ViewPager_Fr1 pageFragment = new ViewPager_Fr1();
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
            pageFragment.setArguments(arguments);
            return pageFragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.view_pager_fragment1, null);
            return view;
        }
    }

    public static class ViewPager_Fr2 extends Fragment {
        static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

        static ViewPager_Fr2 newInstance(int page) {
            ViewPager_Fr2 pageFragment = new ViewPager_Fr2();
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
            pageFragment.setArguments(arguments);
            return pageFragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.view_pager_fragment2, null);
            return view;
        }
    }

    public static class ViewPager_Fr3 extends Fragment{
        static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

        static ViewPager_Fr3 newInstance (int page){
           ViewPager_Fr3 pageFragment = new ViewPager_Fr3();
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
            pageFragment.setArguments(arguments);
            return  pageFragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.view_pager_fragment3, null);
            return view;
        }
    }
    public static class ViewPager_Fr4 extends Fragment implements View.OnClickListener{
        static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
        Button finish;
        View_Pager_Activity view_pager_activity;

        static ViewPager_Fr4 newInstance (int page){
            ViewPager_Fr4 pageFragment = new ViewPager_Fr4();
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
            pageFragment.setArguments(arguments);
            return  pageFragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.view_pager_fragment4, null);
            finish = (Button) view.findViewById(R.id.finish);
            finish.setOnClickListener(this);
            return view;
        }

        @Override
        public void onClick(View v) {
            finish.setBackgroundColor(Color.parseColor("#ead7ad"));
            view_pager_activity = new View_Pager_Activity();
            view_pager_activity.Finish("finish");
        }
    }
}
