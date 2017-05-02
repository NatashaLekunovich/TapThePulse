package com.lekunovich.natasha.thepulse;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dialog_Fragment extends DialogFragment  {
    DB db;
    MyFragment1 myFragment1;
    MyFragment2 myFragment2;
    History history;
    EditText subEditText;
    Settings_Fragment settings_fragment;
    final String LOG_TAG = "myLogs";
    String curDate_format;
    public static String curDate = "";
    public static String curTime = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        history = new History();
        myFragment2 = new MyFragment2();
        myFragment1 = new MyFragment1();
        settings_fragment = new Settings_Fragment();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_window, null);
        subEditText = (EditText) view.findViewById(R.id.comment);

        Bundle mArgs = getArguments();
        final int pulse_Value = mArgs.getInt("key");
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String comment = subEditText.getText().toString();
                long msTime = System.currentTimeMillis();
                Date curDateTime = new Date(msTime);
                Date cTime = new Date(msTime);
                SimpleDateFormat formatter = new SimpleDateFormat("dd'.'MM'.'yyyy");
                SimpleDateFormat form = new SimpleDateFormat("HH:mm");
                curDate = formatter.format(curDateTime);
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy'.'MM'.'dd");
                try {
                    Date d = formatter.parse(curDate);
                    curDate_format = df1.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                curTime = form.format(cTime);
                db = new DB(getActivity());
                db.open();
                db.addRec(curDate, curDate_format, curTime, pulse_Value, comment);
                dialog.dismiss();
                FragmentManager fragmentManager1 = getFragmentManager();
                fragmentManager1.beginTransaction().replace(R.id.container, myFragment2).commit();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}

