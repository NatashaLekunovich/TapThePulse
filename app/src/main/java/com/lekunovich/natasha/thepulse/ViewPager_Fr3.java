package com.lekunovich.natasha.thepulse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ViewPager_Fr3 extends Fragment{
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String LOG_TAG = "myLogs";
    static ViewPager_Fr3 newInstance (int page){
        ViewPager_Fr3 pageFragment = new ViewPager_Fr3();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return  pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pager_fragment3, null);
        return view;
    }
}
