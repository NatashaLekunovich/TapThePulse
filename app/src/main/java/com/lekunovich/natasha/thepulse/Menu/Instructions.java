package com.lekunovich.natasha.thepulse.Menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lekunovich.natasha.thepulse.R;

/**
 * Created by я on 17.03.2017.
 */

public class Instructions extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view_instructions = inflater.inflate(R.layout.instructions, null);
        return view_instructions;
    }
}
