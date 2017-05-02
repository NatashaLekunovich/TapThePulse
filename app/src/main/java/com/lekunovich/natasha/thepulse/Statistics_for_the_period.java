package com.lekunovich.natasha.thepulse;

import android.content.SharedPreferences;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.exp;


public class Statistics_for_the_period extends Fragment {
    TextView pulse_period, max, min, avg;
    DB db;
    String  pulse_Val, pulse_Val2, first_format, last_format;
    Cursor cursor, cursor2, cursor3, cursor5;
    RelativeLayout llMain;
    LinearLayout linear_layout_st;
    SharedPreferences sharedPreferences;
    static final String LOG_TAG = "myLogs";
    double max_pul, rest_p;
    int max_gender, anaerobic, aerobic, fat_burning, recovery, rest, slow_heart;
    int n = 20;
    MainActivity main;
    @Override
    public void onResume() {
        super.onResume();
        db = new DB(getActivity());
        db.open();
        sharedPreferences = getActivity().getSharedPreferences("settings", 0);

        //получить период для статистики
        pulse_Val = sharedPreferences.getString("f", "").toString();
        pulse_Val2 = sharedPreferences.getString("l", "").toString();
        first_format =  sharedPreferences.getString("f_f", "").toString();
        last_format =  sharedPreferences.getString("f_l", "").toString();
        pulse_period.setText(pulse_Val + " - " + pulse_Val2);
        cursor = db.Max(first_format, last_format);
          if (cursor.moveToFirst()){
                int pulseIndex = cursor.getColumnIndex("pulse");
                do {
                    int ee = cursor.getInt(pulseIndex);
                    max.setText(String.valueOf(ee));
                } while (cursor.moveToNext());
                 cursor.close();
            }
            cursor2 = db.getMinPulse(first_format, last_format);
            if(cursor2.moveToFirst()){
                int pulseIndex_min = cursor2.getColumnIndex("pulse");
                do {
                    int min_p = cursor2.getInt(pulseIndex_min);
                    min.setText(String.valueOf(min_p));
                } while (cursor2.moveToNext());
                cursor2.close();
            }
            cursor3 = db.getAVGPulse(first_format, last_format);
             if(cursor3.moveToFirst()){
                int pulseIndex_avg = cursor3.getColumnIndex("pulse");
                do {
                    int s = cursor3.getInt(pulseIndex_avg);
                    avg.setText(String.valueOf(s));
                } while (cursor3.moveToNext());
                cursor3.close();
            }

            cursor5 = db.getPulse(first_format, last_format);
            if(cursor5.moveToFirst()) {
                int pulseIndex_avg = cursor5.getColumnIndex("pulse");
                do {
                    int s = cursor5.getInt(pulseIndex_avg);
                    int dp_px = pxToDp(s);
                    RelativeLayout.LayoutParams linParams = new RelativeLayout.LayoutParams(50, dp_px);
                    linParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    linParams.setMargins(n, 0, 0, 0);
                    LinearLayout ll = new LinearLayout(getActivity());
                    ll.setBackgroundColor(Color.parseColor("#80ffffff"));
                    llMain.addView(ll, linParams);
                    n = n + 70;
                } while (cursor5.moveToNext());
                cursor5.close();
            }

    }
    //перевод пикселей в dp
    public int pxToDp(int px){
        int valueInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
        return valueInDp;
    }

public void Zone(int height, int margin, int color){
    RelativeLayout.LayoutParams linParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
    linParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    linParams.setMargins(0, 0, 0, margin);
    LinearLayout ll = new LinearLayout(getActivity());
    ll.setBackgroundColor(color);
    llMain.addView(ll, linParams);
}
    public void Diagram(double max_pulse, int cab){
        slow_heart = 38;
        int x1 = pxToDp(slow_heart);
        int color1 = Color.parseColor("#49c1df");
        Zone(x1, 0 ,color1);
        if(cab>=15 || cab <=49 ){
            rest_p = 60;
        } else if(cab>=50 || cab<=59){
            rest_p = 64;
        }else if(cab>=60 || cab<=80){
            rest_p = 69;
        }
        rest = (int)rest_p;
        int x2 = pxToDp(rest);
        int color2 = Color.parseColor("#59e6dd");
        Zone(x2, x1 ,color2);

        recovery = (int)(max_pulse*0.5);
        int x3 = pxToDp(recovery);
        int color3 = Color.parseColor("#68e191");
        Zone(x3, x2 ,color3);
        fat_burning = (int)(max_pulse * 0.6);
        int x4 = pxToDp(fat_burning);
        int color4 = Color.parseColor("#ade8a2");
        Zone(x4, x3 ,color4);

        aerobic = (int)(max_pulse * 0.7);
        int x5 = pxToDp(aerobic);
        int color5 = Color.parseColor("#e7eaad");
        Zone(x5, x4 ,color5);

        anaerobic = (int)(max_pulse * 0.8);
        int x6 = pxToDp(anaerobic);
        int color6 = Color.parseColor("#ead7ad");
        Zone(x6, x5 ,color6);

        int x7 = pxToDp(max_gender);
        int color7 = Color.parseColor("#e5b88f");
        Zone(x7, x6 ,color7);

        int x8 = pxToDp(50);
        int color8 = Color.parseColor("#e69990");
        Zone(x8, x7 ,color8);

    }
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
        editor.commit();
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
        int cabc = b-a;

       if (position == 0) {
           max_pul = 190.2/(1+exp(0.0453*(cabc-107.5)));
            max_gender = (int)max_pul;
            Log.d(LOG_TAG, " max gender" + max_gender);
            Diagram(max_pul, cabc);

       } else if(position == 1) {
               max_pul = 203.7/(1+exp(0.033*(cabc-104.3)));
                max_gender = (int)max_pul;
                Log.d(LOG_TAG, " max " + max_gender);
                Diagram(max_pul, cabc);
            }
        return view_stat;
    }

    @Override
    public void onPause() {
        super.onPause();
        main.state = 0;
    }
}
