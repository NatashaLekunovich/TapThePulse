package com.lekunovich.natasha.thepulse;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,  MyFragment2.OnHeadlineSelectedListener {
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
    long back_pressed;
    DrawerLayout drawer;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    NavigationView navigationView;
    static final String LOG_TAG = "myLogs";
    public static int state = 0;
    String a;
    String timer_str, repl;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
           e.commit();
       }
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

       if(savedInstanceState == null){
           FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
           fragmentTransaction.add(R.id.container, myFragment2);
           fragmentTransaction.commit();
       }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                timer_str = sharedPreferences.getString("timer", "");
                    myFragment2.StopTimer();
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                timer_str = sharedPreferences.getString("timer", "");
                // если таймер был запущен
                if (timer_str == "start") {
                    Replace_Fragment(myFragment2, "Measurement");
                    myFragment2.counter1 = 0;
                    try {
                        myFragment2.onResume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("timer", "stop");
                    editor.commit();
                    timer_str = sharedPreferences.getString("timer", "");
                }
            }
        };
       toggle.setDrawerIndicatorEnabled(false);
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               a = sharedPreferences.getString("set", "");
               if (state == 1) {
                   FragmentTransaction fragmentTransaction1 = myFragmentManager.beginTransaction();
                   fragmentTransaction1.replace(R.id.container, statistics);
                   fragmentTransaction1.commit();
                   action_bar_text.setText("Statistics");
                   state = 0;
           }
              else if (a == "first") {
               // если настройки загружены из статистики
               SharedPreferences.Editor  ed = sharedPreferences.edit();
               ed.putString("set", "three");
               ed.commit();
                   FragmentTransaction fragmentTransaction1 = myFragmentManager.beginTransaction();
                   fragmentTransaction1.replace(R.id.container, statistics);
                   fragmentTransaction1.commit();
                   action_bar_text.setText("Statistics");
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
    @Override
    public void onArticleSelected(int count) {
        //передача данных в окно вывода результата
        Bundle bundle = new Bundle();
        bundle.putInt("tag", count);
        fr1.setArguments(bundle);
        repl = sharedPreferences.getString("timer", "");
        if(repl == "stop"){
            myFragment2.StopTimer();
        }else {
            myFragment2.StopTimer();
            //замена франмента
            FragmentTransaction fragmentTransaction1 = myFragmentManager.beginTransaction();
            fragmentTransaction1.replace(R.id.container, fr1);
            fragmentTransaction1.commit();}
    }
    // аппаратная кнопка Home
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        myFragment2.StopTimer();
        myFragment2.counter1 = 0;
    }

    private  void openQuitDialog(){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(MainActivity.this);
        quitDialog.setTitle("Вы уверены, что хотите выйти? ");
        quitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myFragment2.StopTimer();
                finish();
            }
        });
        quitDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        quitDialog.show();
    }
    @Override
    public void onBackPressed() {
        a = sharedPreferences.getString("set", "");
        Log.d(LOG_TAG, "a back = " + a);
        //чтобы перейти от статистики за период к статистике по кнопке back
        if (state == 1) {
            FragmentTransaction fragmentTransaction1 = myFragmentManager.beginTransaction();
            fragmentTransaction1.replace(R.id.container, statistics);
            fragmentTransaction1.commit();
            action_bar_text.setText("Statistics");
            state = 0;
        } else  if(a == "first"){
            SharedPreferences.Editor  ed = sharedPreferences.edit();
            ed.putString("set", "three");
            ed.commit();
            FragmentTransaction fragmentTransaction1 = myFragmentManager.beginTransaction();
            fragmentTransaction1.replace(R.id.container, statistics);
            fragmentTransaction1.commit();
            action_bar_text.setText("Statistics");
        }else
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                myFragment2.onResume();
            } else if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
                myFragment2.StopTimer();
            }
        //если кнопка нажата 2 раза
            if (back_pressed + 800 > System.currentTimeMillis()) {
                openQuitDialog();
            } else back_pressed = System.currentTimeMillis();
        }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == R.id.action_settings) {
          return true;
      }
      return super.onOptionsItemSelected(item);
  }*/
    public void Replace_Fragment(Fragment fr, String action_bar){
        FragmentTransaction fragmentTransaction1 = myFragmentManager.beginTransaction();
        fragmentTransaction1.replace(R.id.container, fr);
        fragmentTransaction1.commit();
        action_bar_text.setText(action_bar);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        myFragment2.StopTimer();
        if (id == R.id.measure) {
            Replace_Fragment(myFragment2, "Measurement");
        } else if (id == R.id.history) {
            Replace_Fragment(history, "History");
            toolbar.setNavigationIcon(R.drawable.bar);
        } else if (id == R.id.statistics) {
            Replace_Fragment(statistics, "Statistics");

        } else if (id == R.id.settings) {
            Replace_Fragment(settings, "Settings");
            toolbar.setNavigationIcon(R.drawable.bar);
        } else if (id == R.id.instructions) {
            Replace_Fragment(instructions,"Instructions" );
            toolbar.setNavigationIcon(R.drawable.bar);
        }
         else if (id == R.id.store) {
            Replace_Fragment(app_store, "AppStore");
            action_bar_text.setText("AppStore");
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
