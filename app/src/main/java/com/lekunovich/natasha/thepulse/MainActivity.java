package com.lekunovich.natasha.thepulse;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lekunovich.natasha.thepulse.Menu.App_Store;
import com.lekunovich.natasha.thepulse.Menu.History;
import com.lekunovich.natasha.thepulse.Menu.Instructions;
import com.lekunovich.natasha.thepulse.Menu.Measurement.MyFragment1;
import com.lekunovich.natasha.thepulse.Menu.Measurement.MyFragment2;
import com.lekunovich.natasha.thepulse.Menu.Settings_Fragment;
import com.lekunovich.natasha.thepulse.Menu.Statistics.Statistics_Fragment;
import com.lekunovich.natasha.thepulse.Menu.Statistics.Statistics_for_the_fragment.Statistics_for_the_period;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DB db;
    FragmentManager myFragmentManager;
    MyFragment2 myFragment2;
    MyFragment1 fr1;
    TextView action_bar_text;
    History history;
    Instructions instructions;
    Settings_Fragment settings;
    Statistics_Fragment statistics;
    Statistics_for_the_period statistics_for_the_period;
    App_Store app_store;
    DrawerLayout drawer;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    NavigationView navigationView;
    static final String LOG_TAG = "myLogs";
    public static int state = 0;
    public String settings_of_statistics;
    private long back_pressed;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
       getSupportActionBar().setTitle("");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       myFragmentManager = getFragmentManager();
       myFragment2 = new MyFragment2();
       fr1 = new MyFragment1();
       history = new History();
       settings = new Settings_Fragment();
       statistics = new Statistics_Fragment();
       instructions = new Instructions();
       statistics_for_the_period = new Statistics_for_the_period();
       app_store = new App_Store();
       drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       action_bar_text = (TextView) findViewById(R.id.action_text);
       db = new DB(this);
       db.open();
       sharedPreferences = getSharedPreferences("settings", 0);
       // проверяем, первый ли раз открывается программа
       boolean hasVisited = sharedPreferences.getBoolean("hasVisited", false);
       if (!hasVisited) {
           Log.d(LOG_TAG, String.valueOf(hasVisited));
           Intent intent = new Intent(MainActivity.this, View_Pager_Activity.class);
           startActivity(intent);
           // выводим нужную активность
           SharedPreferences.Editor  e = sharedPreferences.edit();
           e.putBoolean("hasVisited", true);
           e.apply();
       }
       if(savedInstanceState == null){
           replace_Fragment(myFragment2, "Measurement");
       }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            String timer_str;

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                myFragment2.stopTimer();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                timer_str = sharedPreferences.getString("timer", "");
                // если таймер был запущен
                if (timer_str.equals("start")) {
                    String measur = sharedPreferences.getString("measurement", "");
                    if(measur.equals("open")) {
                        replace_Fragment(myFragment2, "Measurement");
                        myFragment2.counter1 = 0;
                    }
                    try {
                        myFragment2.stopTimer();
                        myFragment2.onResume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("timer", "stop");
                    editor.apply();
                }
            }
        };
       toggle.setDrawerIndicatorEnabled(false);
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
           settings_of_statistics = sharedPreferences.getString("set", "");
           if (state == 1) {
               replace_Fragment(statistics, "Statistics");
               state = 0;
           } else if (settings_of_statistics.equals("first")) {
               // если настройки загружены из статистики
               SharedPreferences.Editor  ed = sharedPreferences.edit();
               ed.putString("set", "three");
               ed.apply();
               replace_Fragment(statistics, "Statistics");
           } else
               drawer.openDrawer(GravityCompat.START);
           }
       });
       drawer.addDrawerListener(toggle);
       toggle.syncState();
       navigationView = (NavigationView) findViewById(R.id.nav_view);
       navigationView.setNavigationItemSelectedListener(this);
       //выделить первый пункт при открытии меню
       navigationView.getMenu().getItem(0).setChecked(true);
    }

    // аппаратная кнопка Home
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        myFragment2.stopTimer();
        myFragment2.counter1 = 0;
    }

    private  void openQuitDialog(){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(MainActivity.this);
        quitDialog.setTitle("Вы уверены, что хотите выйти? ");
        quitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            myFragment2.stopTimer();
            finish();
            }
        });
        quitDialog.setNegativeButton("No", null);
        quitDialog.show();
    }

    @Override
    public void onBackPressed() {
        settings_of_statistics = sharedPreferences.getString("set", "");
        //чтобы перейти от статистики за период к статистике по кнопке back
        if (state == 1) {
            replace_Fragment(statistics, "Statistics");
            state = 0;
        } else  if(settings_of_statistics.equals("first")){
            SharedPreferences.Editor  ed = sharedPreferences.edit();
            ed.putString("set", "three");
            ed.apply();
            replace_Fragment(statistics, "Statistics");
        }else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
            myFragment2.stopTimer();
        }
        //если кнопка нажата 2 раза
        if (back_pressed + 800 > System.currentTimeMillis()) {
            openQuitDialog();
        }
        else back_pressed = System.currentTimeMillis();
    }

    public void replace_Fragment(Fragment fr, String action_bar){
        myFragment2.stopTimer();
        FragmentTransaction fragmentTransaction1 = myFragmentManager.beginTransaction();
        fragmentTransaction1.replace(R.id.container, fr);
        fragmentTransaction1.commit();
        action_bar_text.setText(action_bar);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        myFragment2.stopTimer();
        if (id == R.id.measure) {
            replace_Fragment(myFragment2, "Measurement");
        } else if (id == R.id.history) {
            replace_Fragment(history, "History");
            toolbar.setNavigationIcon(R.drawable.bar);
        } else if (id == R.id.statistics) {
            replace_Fragment(statistics, "Statistics");

        } else if (id == R.id.settings) {
            replace_Fragment(settings, "Settings");
            toolbar.setNavigationIcon(R.drawable.bar);

        } else if (id == R.id.instructions) {
           replace_Fragment(instructions,"Instructions" );
            toolbar.setNavigationIcon(R.drawable.bar);
        }
         else if (id == R.id.store) {
            replace_Fragment(app_store, "AppStore");
            toolbar.setNavigationIcon(R.drawable.bar);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }
}
