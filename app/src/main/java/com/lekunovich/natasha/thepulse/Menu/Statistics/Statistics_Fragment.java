package com.lekunovich.natasha.thepulse.Menu.Statistics;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.lekunovich.natasha.thepulse.Menu.Settings_Fragment;
import com.lekunovich.natasha.thepulse.Menu.Statistics.Statistics_for_the_fragment.Statistics_for_the_period;
import com.lekunovich.natasha.thepulse.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Statistics_Fragment extends Fragment{
    Button show_stat;
    SharedPreferences prefs;
    EditText edit_first_date, edit_last_date;
    private int number_et = 0;
    private String first_format,  last_format;
    static final String LOG_TAG = "myLogs";
    Calendar dateAndTime = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view_stat = inflater.inflate(R.layout.statistics, null);
        ((Toolbar)getActivity().findViewById(R.id.toolbar)).setNavigationIcon(R.drawable.bar);
        edit_first_date = (EditText) view_stat.findViewById(R.id.edit_first_date);
        edit_last_date = (EditText) view_stat.findViewById(R.id.edit_last_date);
        // текущее время минус неделя установить при запуске
        long msTime_first = System.currentTimeMillis() - 1000*60*60*24*7;
        String first_date = set_Date(msTime_first);
        edit_first_date.setText(first_date);
        // сегодняшний день установить при запуске
        long msTime_last = System.currentTimeMillis();
        String last_date = set_Date(msTime_last);
        edit_last_date.setText(last_date);
        show_stat = (Button) view_stat.findViewById(R.id.show_stat);
        show_stat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            show_stat.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            return false;
            }
        });
        show_stat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String s1 = edit_first_date.getText().toString();
            String s2 = edit_last_date.getText().toString();
            prefs = getActivity().getSharedPreferences("settings", 0);
            // конвертировать дату в обратный формат для использования в БД
            SimpleDateFormat formatter = new SimpleDateFormat("dd'.'MM'.'yyyy");
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy'.'MM'.'dd");
            try {
                Date d = formatter.parse(s1);
                Date d2 = formatter.parse(s2);
                first_format = df1.format(d);
                last_format = df1.format(d2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
                // передать данные для отображения
            SharedPreferences.Editor e = prefs.edit();
            e.putString("f", s1);
            e.putString("l", s2);
            e.putString("f_f", first_format);
            e.putString("f_l", last_format);
            e.apply();

            // если в "Settings" заполнены данные
            if(prefs.contains("str")){
                Fragment statist_period = new Statistics_for_the_period();
                FragmentManager myFragmentManager2 = getFragmentManager();
                myFragmentManager2.beginTransaction().replace(R.id.container, statist_period).commit();
                ((TextView) getActivity().findViewById(R.id.action_text)).setText("Statistics for the period");
            } else {
                e.putString("set", "first");
                e.apply();
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
            number_et = 1;
            setDate();
            }
        });
        edit_last_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            number_et = 2;
            setDate();
            }
        });
        return view_stat;
    }

    public void setDate(){
        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if(number_et == 1) {
                edit_first_date.setText(DateUtils.formatDateTime(getActivity(), dateAndTime.getTimeInMillis(), DateUtils.FORMAT_NUMERIC_DATE));
            }
            if(number_et == 2) {
                edit_last_date.setText(DateUtils.formatDateTime(getActivity(), dateAndTime.getTimeInMillis(), DateUtils.FORMAT_NUMERIC_DATE));
            }
            }
        };
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), d, dateAndTime.get(Calendar.YEAR),
            dateAndTime.get(Calendar.MONTH),
            dateAndTime.get(Calendar.DAY_OF_MONTH));
        DatePicker dp = dpd.getDatePicker();
        long maxDate = System.currentTimeMillis();
        dp.setMaxDate(maxDate);
        dpd.show();
    }

    //установить первоначальную дату в EditText
    public String set_Date(long msTime){
        Date curDateTime = new Date(msTime);
        SimpleDateFormat formatter = new SimpleDateFormat("dd'.'MM'.'yyyy");
        String curDate = formatter.format(curDateTime);
        return curDate;
    }
}
