package com.lekunovich.natasha.thepulse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ViewPager_Fr4 extends Fragment implements View.OnClickListener{
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String LOG_TAG = "myLogs";
    Button finish;
    View_Pager_Activity view_pager_activity;
    static ViewPager_Fr4 newInstance (int page){
        ViewPager_Fr4 pageFragment = new ViewPager_Fr4();
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
        View view = inflater.inflate(R.layout.view_pager_fragment4, null);
        finish = (Button) view.findViewById(R.id.finish);
        finish.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
       finish.setBackgroundColor(Color.parseColor("#ead7ad"));
        String s = "finish";
        view_pager_activity = new View_Pager_Activity();
        view_pager_activity.Finish(s);
    }
}
