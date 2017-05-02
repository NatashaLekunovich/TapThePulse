package com.lekunovich.natasha.thepulse;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Statistics_Fragment extends Fragment{
    Button show_stat;
    String date_str;
    String first_date, last_date;
    SharedPreferences prefs;
    EditText edit_first_date, edit_last_date;
    int a1 = 0;
    static final String LOG_TAG = "myLogs";
    String first_format,  last_format;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view_stat = inflater.inflate(R.layout.statistics, null);
        ((Toolbar)getActivity().findViewById(R.id.toolbar)).setNavigationIcon(R.drawable.bar);
        edit_first_date = (EditText) view_stat.findViewById(R.id.edit_first_date);
        edit_last_date = (EditText) view_stat.findViewById(R.id.edit_last_date);
        // текущее время минус неделя установить при запуске
        long msTime_first = System.currentTimeMillis() - 1000*60*60*24*7;
        first_date = Set_Date(msTime_first, edit_first_date);
        // сегодняшний день установить при запуске
        long msTime_last = System.currentTimeMillis();
        last_date = Set_Date(msTime_last, edit_last_date);
        show_stat = (Button) view_stat.findViewById(R.id.show_stat);
        show_stat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs = getActivity().getSharedPreferences("settings", 0);
                // конвертировать дату в обратный формат для использования в БД
                SimpleDateFormat formatter = new SimpleDateFormat("dd'.'MM'.'yyyy");
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy'.'MM'.'dd");
                try
                {
                    Date d = formatter.parse(first_date);
                    Date d2 = formatter.parse(last_date);
                    first_format = df1.format(d);
                    last_format = df1.format(d2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // передать данные для отображения
                SharedPreferences.Editor e = prefs.edit();
                e.putString("f", first_date);
                e.putString("l", last_date);
                e.putString("f_f", first_format);
                e.putString("f_l", last_format);
                e.commit();

                // если в "Settings" заполнены данные
                if(prefs.contains("str")){
                    Fragment statist_period = new Statistics_for_the_period();
                    FragmentManager myFragmentManager2 = getFragmentManager();
                    myFragmentManager2.beginTransaction().replace(R.id.container, statist_period).commit();
                    ((TextView) getActivity().findViewById(R.id.action_text)).setText("Statistics for the period");
                } else {
                    e.putString("set", "first");
                    e.commit();
                    //если настойки запущены из статистики
                    Settings_Fragment settings_fragment = new Settings_Fragment();
                    FragmentManager myFragmentManager = getFragmentManager();
                    myFragmentManager.beginTransaction().replace(R.id.container, settings_fragment).commit();
                    ((TextView) getActivity().findViewById(R.id.action_text)).setText("Settings");
                }
            }
        });
        //чтобы не появлялась клавиатура
        edit_first_date.setInputType(InputType.TYPE_NULL);
        edit_last_date.setInputType(InputType.TYPE_NULL);
        edit_first_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a1 = 1;
                Open_DatePicker();
            }
        });
        edit_last_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a1 = 2;
                Open_DatePicker();
            }
        });
        return view_stat;
    }
    public String Set_Date(long msTime, EditText edit_date){
        Date curDateTime = new Date(msTime);
        SimpleDateFormat formatter = new SimpleDateFormat("dd'.'MM'.'yyyy");
        String curDate = formatter.format(curDateTime);
        edit_date.setText(curDate);
        return curDate;
    }
    public void Open_DatePicker(){
        DatePickerFr datePickerFragment = new DatePickerFr();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        datePickerFragment.show(ft, "DatePicker");
    }

    public class DatePickerFr extends DialogFragment implements DatePickerDialog.OnDateSetListener  {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
            DatePicker dp = dpd.getDatePicker();
            long maxDate = c.getTime().getTime();
            dp.setMaxDate(maxDate);
            return dpd;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            date_str = String.format("%02d", day) + "." + String.format("%02d", month+1) + "." + String.format("%04d", year);
            if(a1 == 1){
                edit_first_date.setText(date_str);
                first_date = date_str;
            }
            else if(a1 == 2) {
                edit_last_date.setText(date_str);
                last_date = date_str;
            }
        }
    }
}
