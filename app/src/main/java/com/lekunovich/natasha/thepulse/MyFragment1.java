package com.lekunovich.natasha.thepulse;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public  class MyFragment1 extends Fragment implements View.OnClickListener{
    TextView tvMessage;
    MyFragment2 myFragment2;
    Button save, restart;
    int recieveInfo;
    static final String LOG_TAG = "myLogs";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, null);
        save = (Button) view.findViewById(R.id.save);
        restart = (Button) view.findViewById(R.id.restart);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        myFragment2 = new MyFragment2();
        Bundle bundle= getArguments();
        recieveInfo = bundle.getInt("tag");
        tvMessage.setText(String.valueOf(recieveInfo));
        save.setOnClickListener(this);
        restart.setOnClickListener(this);
        // получить разрешение экрана
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.d(LOG_TAG, "x = " + width + "y =  " + height);
        if(width>=480) {
            tvMessage.setTextSize(135);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart:
                FragmentManager myFragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction1 = myFragmentManager.beginTransaction();
                fragmentTransaction1.replace(R.id.container, myFragment2);
                fragmentTransaction1.commit();
                ((TextView) getActivity().findViewById(R.id.action_text)).setText("Measurement");
                break;
            case R.id.save:
                Bundle args = new Bundle();
                args.putInt("key", recieveInfo);
                DialogFragment newFragment = new Dialog_Fragment();
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "dlg1");
                myFragment2.counter1 = 0;
                break;
        }
    }
}
