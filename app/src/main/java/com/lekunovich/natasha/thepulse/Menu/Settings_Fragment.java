package com.lekunovich.natasha.thepulse.Menu;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lekunovich.natasha.thepulse.Menu.Measurement.MyFragment2;
import com.lekunovich.natasha.thepulse.Menu.Statistics.Statistics_for_the_fragment.Statistics_for_the_period;
import com.lekunovich.natasha.thepulse.R;

import java.util.Calendar;
import java.util.Date;

public class Settings_Fragment extends Fragment  {
    Button save_settings;
    EditText date_edit;
    Spinner spinner;
    Fragment statist_period;
    SharedPreferences sharedPreferences;
    static final String LOG_TAG = "myLogs";
    private String date_str;
    private int spinnerPosition;
    Calendar dateAndTime = Calendar.getInstance();

    public void replase_fr(Fragment fragment, String name){
        FragmentManager myFragmentManager2 = getFragmentManager();
        myFragmentManager2.beginTransaction().replace(R.id.container, fragment).commit();
        ((TextView) getActivity().findViewById(R.id.action_text)).setText(name);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view_settins = inflater.inflate(R.layout.settings, null);
        statist_period = new Statistics_for_the_period();
        date_edit = (EditText) view_settins.findViewById(R.id.date_edit);
        save_settings = (Button) view_settins.findViewById(R.id.save_settings);
        spinner = (Spinner) view_settins.findViewById(R.id.spinner);
        date_edit.setInputType(InputType.TYPE_NULL);
        sharedPreferences = getActivity().getSharedPreferences("settings", 0);
        //если настройки были загружены из статистики
        final String getShPr = sharedPreferences.getString("set", "");
        if (getShPr.equals("first")) {
            ((Toolbar)getActivity().findViewById(R.id.toolbar)).setNavigationIcon(R.drawable.back);
        }
        date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });
        save_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  save_settings.setTextColor(ContextCompat.getColor(getActivity(), R.color.diagr_color8));
                  //если зашли из статистики при незаполненных настройках
                  if (getShPr.equals("first")) {
                      if(save_Changed()) {
                          Fragment statist_period = new Statistics_for_the_period();
                          replase_fr(statist_period, "Statistics for the period");
                      }
                  } else if(save_Changed()) {
                        MyFragment2 fr2 = new MyFragment2();
                        replase_fr(fr2, "Measurement");
                  }
            }
        });
        // адаптер
        String[] gender = {"Female", "Male"};
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, gender);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
             @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 spinnerPosition = spinner.getSelectedItemPosition();
             }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        return view_settins;
    }

    public void setDate(){
        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateAndTime.set(Calendar.YEAR, year);
                dateAndTime.set(Calendar.MONTH, month);
                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                date_edit.setText(DateUtils.formatDateTime(getActivity(), dateAndTime.getTimeInMillis(), DateUtils.FORMAT_NUMERIC_DATE));
            }
        };
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), d, dateAndTime.get(Calendar.YEAR),
            dateAndTime.get(Calendar.MONTH),
            dateAndTime.get(Calendar.DAY_OF_MONTH));
        DatePicker dp = dpd.getDatePicker();
        Date today = new Date();
        dateAndTime.setTime(today);
        dateAndTime.add(Calendar.YEAR, -15);
        long maxDate = dateAndTime.getTime().getTime();
        dp.setMaxDate(maxDate);
        dpd.show();
    }

    public boolean save_Changed() {
         date_str = date_edit.getText().toString();
         if(date_str.length() == 0){
             Toast.makeText(getActivity(), R.string.date_of_birth, Toast.LENGTH_SHORT).show();
             return false;
         } else {
             SharedPreferences.Editor editor;
             editor = sharedPreferences.edit();
             editor.putInt("spinner", spinnerPosition);
             editor.putString("str", date_str);
             editor.putString("set", "three");
             editor.commit();
             return true;
         }
    }

    // восстановить сохраненные данные при загрузке фрагмента
    public void resume_Setting()
    {
        int position = sharedPreferences.getInt("spinner",-1);
        String date_getString = sharedPreferences.getString("str", "").toString();
        if(position > -1) {
            spinner.setSelection(position);
            date_edit.setText(date_getString);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resume_Setting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("set", "four");
        editor.apply();
    }
}
