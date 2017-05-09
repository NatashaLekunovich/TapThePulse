package com.lekunovich.natasha.thepulse.Menu.Measurement;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.lekunovich.natasha.thepulse.Menu.History;
import com.lekunovich.natasha.thepulse.Menu.Settings_Fragment;
import com.lekunovich.natasha.thepulse.R;

public class MyFragment2 extends Fragment implements View.OnTouchListener {
    public int counter1;
    TextView  timer;
    ProgressBar progressBar;
    static final String LOG_TAG = "myLogs";
    History history;
    Settings_Fragment settings_fragment;
    MyFragment1 fr1;
    Handler customHandler = new Handler();
    LinearLayout lin_img;
    SharedPreferences sharedPreferences;
    ImageView imageView;
    Bitmap bmOriginal, bm2;
    Thread t;
    LinearLayout.LayoutParams img, img2;
    private long startTime = 0L, timeInMillisecond = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private int sec = 0, minuts = 0, millis = 0;
    FragmentManager myFragmentManager;

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMillisecond = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMillisecond;
            sec = (int) (updateTime/1000);
            minuts = sec/60;
            sec%=60;
            millis = (int)(updateTime%1000);
            timer.setText(String.format("%02d",minuts) + ":" + String.format("%02d", sec) + "." + String.format("%03d", millis));
            customHandler.postDelayed(this,0);
            progressBar.setProgress(sec);
            if(minuts == 1 && sec == 0)  {
                stopTimer();
                //передача данных в окно вывода результата
                Bundle bundle = new Bundle();
                bundle.putInt("tag", counter1);
                fr1.setArguments(bundle);
                String repl = sharedPreferences.getString("timer", "");
                if(repl == "stop"){
                   stopTimer();
                }else {
                    stopTimer();
                    myFragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction1 = myFragmentManager.beginTransaction();
                    fragmentTransaction1.replace(R.id.container, fr1);
                    fragmentTransaction1.commit();
                    ((TextView) getActivity().findViewById(R.id.action_text)).setText("Measurement");
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        history = new History();
        settings_fragment = new Settings_Fragment();
        fr1 = new MyFragment1();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurment, null);
        ((Toolbar)getActivity().findViewById(R.id.toolbar)).setNavigationIcon(R.drawable.bar);
        sharedPreferences = getActivity().getSharedPreferences("settings", 0);
        SharedPreferences.Editor  ed = sharedPreferences.edit();
        ed.putString("measurement", "open");
        ed.apply();
        lin_img = (LinearLayout) view.findViewById(R.id.lin_img);
        timer = (TextView) view.findViewById(R.id.timer);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        img = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        img.gravity = Gravity.CENTER_HORIZONTAL;
        imageView = new ImageView(getActivity());
        // Конвертируем Drawable в Bitmap и выводим в ImageView
        bmOriginal = BitmapFactory.decodeResource(getResources(),
                R.drawable.big_heart);
        bm2 = BitmapFactory.decodeResource(getResources(),R.drawable.small_heart);
        imageView.setImageBitmap(bmOriginal);
        imageView.setOnTouchListener(this);
        lin_img.addView(imageView, img);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        counter1 = 0;
        progressBar.setProgress(0);
        timer.setText(String.format("%02d", minuts) + ":" + String.format("%02d", sec) + "." + String.format("%03d", millis));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                counter1++;
                Log.d(LOG_TAG, String.valueOf(counter1));
                if(counter1 ==1 ){
                     t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startTime = SystemClock.uptimeMillis();
                            customHandler.postDelayed(updateTimerThread, 0);
                            Log.d(LOG_TAG, "thread start");
                        }
                    });
                    t.start();
                    SharedPreferences.Editor ed = sharedPreferences.edit();
                    ed.putString("timer", "start");
                    ed.apply();
                }

                // Вычисляем ширину и высоту изображения
                int width = bmOriginal.getWidth();
                int height = bmOriginal.getHeight();
                int halfWidth =  width / 2;
                int halfHeight = height / 2;

                // Выводим уменьшенную картинку в ImageView
                Bitmap bmHalf = Bitmap.createScaledBitmap(bm2,halfWidth,
                       halfHeight, false);
                img2 = new LinearLayout.LayoutParams(halfWidth, halfHeight);
                img2.gravity = Gravity.CENTER;
                img2.setMargins(0, 15, 0, 5);
                imageView.setImageBitmap(bmHalf);
                imageView.setLayoutParams(img2);
                break;

            case MotionEvent.ACTION_UP:
                int width2 = bmOriginal.getWidth();
                int height2 = bmOriginal.getHeight();
                Bitmap bmHalf2 = Bitmap.createScaledBitmap(bmOriginal, width2,
                        height2, false);
                imageView.setImageBitmap(bmHalf2);
                imageView.setLayoutParams(img);
                break;
        }
        return true;
    }

    public boolean stopTimer(){
        //остановка таймера
        timeSwapBuff+=timeInMillisecond;
        customHandler.removeCallbacks(updateTimerThread);
        sec = 0; millis = 0; minuts = 0;
        startTime = 0L; timeInMillisecond = 0L; timeSwapBuff = 0L; updateTime = 0L;
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor  ed = sharedPreferences.edit();
        ed.putString("measurement", "close");
        ed.apply();
    }
}
