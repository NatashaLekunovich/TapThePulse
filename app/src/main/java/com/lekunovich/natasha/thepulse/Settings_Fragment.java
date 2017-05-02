package com.lekunovich.natasha.thepulse;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class Settings_Fragment extends Fragment  {
    String[] gender = {"Female", "Male"};
    Button save_settings;
    EditText date_edit;
    String date_str;
    int spinnerPosition;
    Spinner spinner;
    Fragment statist_period;
    String a;
    SharedPreferences sharedPreferences;
    static final String LOG_TAG = "myLogs";

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
         a = sharedPreferences.getString("set", "");
        if (a == "first")
        { ((Toolbar)getActivity().findViewById(R.id.toolbar)).setNavigationIcon(R.drawable.back);}
       date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                datePickerFragment.show(ft, "DatePicker");
            }
        });

        save_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //если зашли из статистики при незаполненных настройках
              if (a == "first") {
                  if(Save_Changed()) {
                      Fragment statist_period = new Statistics_for_the_period();
                      FragmentManager myFragmentManager2 = getFragmentManager();
                      myFragmentManager2.beginTransaction().replace(R.id.container, statist_period).commit();
                     ((TextView) getActivity().findViewById(R.id.action_text)).setText("Statistics for the period");
                  }
                } else
                if(Save_Changed()) {
                    MyFragment2 fr2 = new MyFragment2();
                    FragmentManager myFragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction1 = myFragmentManager.beginTransaction();
                    fragmentTransaction1.replace(R.id.container, fr2);
                    fragmentTransaction1.commit();
                   ((TextView) getActivity().findViewById(R.id.action_text)).setText("Measurement");
                }

            }
        });
        // адаптер
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("set", "four");
        editor.commit();
    }


    public boolean Save_Changed() {
        date_str = date_edit.getText().toString();
         if(date_str.length() == 0){
             Toast.makeText(getActivity(), "Введите дату рождения! ", Toast.LENGTH_SHORT).show();
             return false;
         } else {
             SharedPreferences.Editor editor;
             editor = sharedPreferences.edit();
             editor.putInt("spinner", spinnerPosition);
             editor.putString("str", date_str);
             editor.putString("set", "three");
             editor.apply();
             return true;
         }
    }

    // восстановить сохраненные данные при загрузке фрагмента
   public void Resume_Setting()
    {
        int position = sharedPreferences.getInt("spinner",-1);
        String date_getString = sharedPreferences.getString("str", "").toString();
        if(position > -1)
            spinner.setSelection(position);
        date_edit.setText(date_getString);
    }
    @Override
    public void onResume() {
        super.onResume();
        Resume_Setting();
    }

    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener  {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
            DatePicker dp = dpd.getDatePicker();
            //установка максимальной даты
            Date today = new Date();
            c.setTime(today);
            c.add(Calendar.YEAR, -15);
            long maxDate = c.getTime().getTime();
            dp.setMaxDate(maxDate);
            return dpd;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            date_str = String.format("%02d", day) + "." + String.format("%02d", month+1) + "." + String.format("%04d", year);
            date_edit.setText(date_str);
        }
    }

}
