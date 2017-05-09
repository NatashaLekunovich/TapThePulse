package com.lekunovich.natasha.thepulse.Menu.Statistics.Statistics_for_the_fragment;

import android.content.SharedPreferences;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lekunovich.natasha.thepulse.DB;
import com.lekunovich.natasha.thepulse.MainActivity;
import com.lekunovich.natasha.thepulse.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.exp;


public class Statistics_for_the_period extends Fragment {
    MainActivity main;
    DB db;
    TextView pulse_period, max, min, avg;
    Cursor cursor, cursor2, cursor3, cursor4;
    RelativeLayout llMain;
    LinearLayout linear_layout_st;
    SharedPreferences sharedPreferences;
    static final String LOG_TAG = "myLogs";
   // String first_format, last_format;
    private double max_pul;
   // int max_gender;
    private int n = 20;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view_stat = inflater.inflate(R.layout.statistics_for_the_period, null);
        pulse_period = (TextView) view_stat.findViewById(R.id.pulse_period);
        llMain = (RelativeLayout) view_stat.findViewById(R.id.llMain);
        linear_layout_st = (LinearLayout) view_stat.findViewById(R.id.linear_layout_st);
        max = (TextView) view_stat.findViewById(R.id.max);
        min = (TextView) view_stat.findViewById(R.id.min);
        avg = (TextView) view_stat.findViewById(R.id.avg);
        main.state = 1;
        ((Toolbar)getActivity().findViewById(R.id.toolbar)).setNavigationIcon(R.drawable.back);
        // получить разрешение экрана
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.d(LOG_TAG, "x = " + width + "y =  " + height);
        if(height <= 854){
            pulse_period.setTextSize(18);
            max.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
            min.setTextSize(40);
            avg.setTextSize(40);
        }
        // получить дату рождения
        sharedPreferences = getActivity().getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("set", "two");
        editor.apply();
        String date_getString = sharedPreferences.getString("str", "").toString();
        int position = sharedPreferences .getInt("spinner",-1);
        long msTime_last = System.currentTimeMillis();
        Date curDateTime_last = new Date(msTime_last);
        SimpleDateFormat formatter_last = new SimpleDateFormat("dd'.'MM'.'yyyy");
        String curDate_last = formatter_last.format(curDateTime_last);
        String roar = date_getString.substring(6);
        String cur = curDate_last.substring(6);
        int a = Integer.parseInt(roar);
        int b  = Integer.parseInt(cur);
      //получить возраст из настроек
        int age = b-a;

        if (position == 0) {
           max_pul = 190.2/(1+exp(0.0453*(age-107.5)));
           diagram(max_pul, age);

        }  else if(position == 1) {
           max_pul = 203.7/(1+exp(0.033*(age-104.3)));
           diagram(max_pul, age);
        }
        return view_stat;
    }

    @Override
    public void onResume() {
        super.onResume();
        db = new DB(getActivity());
        db.open();
        sharedPreferences = getActivity().getSharedPreferences("settings", 0);

        //получить период для статистики
        String pulse_Val = sharedPreferences.getString("f", "").toString();
        String pulse_Val2 = sharedPreferences.getString("l", "").toString();
        String first_format =  sharedPreferences.getString("f_f", "").toString();
        String last_format =  sharedPreferences.getString("f_l", "").toString();
        pulse_period.setText(pulse_Val + " - " + pulse_Val2);
        cursor = db.Max(first_format, last_format);
        getHeartRate(cursor, max);

        cursor2 = db.getMinPulse(first_format, last_format);
        getHeartRate(cursor2, min);

        cursor3 = db.getAVGPulse(first_format, last_format);
        getHeartRate(cursor3, avg);
        //отрисовка столбиков
        cursor4 = db.getPulse(first_format, last_format);
        if(cursor4.moveToFirst()) {
            int pulseIndex_avg = cursor4.getColumnIndex("pulse");
            do {
                int s = cursor4.getInt(pulseIndex_avg);
                int dp_px = pxToDp(s);
                RelativeLayout.LayoutParams linParams = new RelativeLayout.LayoutParams(50, dp_px);
                linParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                linParams.setMargins(n, 0, 0, 0);
                LinearLayout ll = new LinearLayout(getActivity());
                ll.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cursor4_getPulse));
                llMain.addView(ll, linParams);
                n = n + 70;
            } while (cursor4.moveToNext());
            cursor4.close();
        }

    }

    public void getHeartRate(Cursor cursor_cursor, TextView tv) {
        if (cursor_cursor.moveToFirst()) {
            int pulseIndex = cursor_cursor.getColumnIndex("pulse");
            do {
                int ee = cursor_cursor.getInt(pulseIndex);
                tv.setText(String.valueOf(ee));
            } while (cursor_cursor.moveToNext());
            cursor_cursor.close();
        }
    }

    //перевод пикселей в dp
    public int pxToDp(int px){
        int valueInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
        return valueInDp;
    }

    public void zone(int height, int margin, int backgroundColor){
        RelativeLayout.LayoutParams linParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        linParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linParams.setMargins(0, 0, 0, margin);
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setBackgroundColor(backgroundColor);
        llMain.addView(ll, linParams);
    }

    public void diagram(double max_pulse, int cab){
        // slow_heart = 38;
        int rest_p = 0;
        int x1 = pxToDp(38);
        //int color1 = Color.parseColor("#49c1df");
        zone(x1, 0 , ContextCompat.getColor(getActivity(), R.color.diagr_color1));
        if(cab>=15 || cab <=49 ){
            rest_p = 60;
        } else if(cab>=50 || cab<=59){
            rest_p = 64;
        }else if(cab>=60 || cab<=80){
            rest_p = 69;
        }
        //rest = (int)rest_p;
        int x2 = pxToDp(rest_p);
        //int color2 = Color.parseColor("#59e6dd");
        zone(x2, x1 ,ContextCompat.getColor(getActivity(), R.color.diagr_color2));

        //recovery = (int)(max_pulse*0.5);
        int x3 = pxToDp((int)(max_pulse*0.5));
        //int color3 = Color.parseColor("#68e191");
        zone(x3, x2 ,ContextCompat.getColor(getActivity(), R.color.diagr_color3));
        //fat_burning = (int)(max_pulse * 0.6);
        int x4 = pxToDp((int)(max_pulse * 0.6));
        //int color4 = Color.parseColor("#ade8a2");
        zone(x4, x3 ,ContextCompat.getColor(getActivity(), R.color.diagr_color4));

        //aerobic = (int)(max_pulse * 0.7);
        int x5 = pxToDp((int)(max_pulse * 0.7));
        //int color5 = Color.parseColor("#e7eaad");
        zone(x5, x4 ,ContextCompat.getColor(getActivity(), R.color.diagr_color5));

        //anaerobic = (int)(max_pulse * 0.8);
        int x6 = pxToDp((int)(max_pulse * 0.8));
        //int color6 = Color.parseColor("#ead7ad");
        zone(x6, x5 ,ContextCompat.getColor(getActivity(), R.color.diagr_color6));

        int x7 = pxToDp((int)max_pul);
        //int color7 = Color.parseColor("#e5b88f");
        zone(x7, x6 ,ContextCompat.getColor(getActivity(), R.color.diagr_color7));

        int x8 = pxToDp(50);
        //int color8 = Color.parseColor("#e69990");
        zone(x8, x7 ,ContextCompat.getColor(getActivity(), R.color.diagr_color8));

    }


    @Override
    public void onPause() {
        super.onPause();
        main.state = 0;
    }
}
