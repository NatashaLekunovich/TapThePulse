package com.lekunovich.natasha.thepulse.Menu;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lekunovich.natasha.thepulse.R;

/**
 * Created by я on 10.04.2017.
 */
public class App_Store extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view_stat = inflater.inflate(R.layout.app_store, null);
        return view_stat;
    }
}
