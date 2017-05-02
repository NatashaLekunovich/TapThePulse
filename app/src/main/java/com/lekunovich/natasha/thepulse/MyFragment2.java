package com.lekunovich.natasha.thepulse;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyFragment2 extends Fragment implements View.OnTouchListener {
    public int counter1;
    TextView  timer;
    ProgressBar progressBar;
    static final String LOG_TAG = "myLogs";
    History history;
    Settings_Fragment settings_fragment;
    Handler customHandler = new Handler();
    LinearLayout lin_img;
   SharedPreferences sharedPreferences;

   ImageView imageView;
    Bitmap bmOriginal, bm2;
    Thread t;
    LinearLayout.LayoutParams img, img2;
    long StartTime = 0L, TimeInMillisecond = 0L, TimeSwapBuff = 0L, UpdateTime = 0L;
    int sec = 0, minuts = 0, millis = 0;
    OnHeadlineSelectedListener mCallback;

    public interface OnHeadlineSelectedListener {
         void onArticleSelected(int count);
    }
    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            TimeInMillisecond = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeSwapBuff + TimeInMillisecond;
            sec = (int) (UpdateTime/1000);
            minuts = sec/60;
            sec%=60;
            millis = (int)(UpdateTime%1000);
            timer.setText(String.format("%02d",minuts) + ":" + String.format("%02d", sec) + "." + String.format("%03d", millis));
            customHandler.postDelayed(this,0);
            progressBar.setProgress(sec);
            if(minuts == 1 && sec == 0)  {
                StopTimer();
                //передача данных в окно вывода результата
                mCallback.onArticleSelected(counter1);
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        history = new History();
        settings_fragment = new Settings_Fragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurment, null);
        ((Toolbar)getActivity().findViewById(R.id.toolbar)).setNavigationIcon(R.drawable.bar);
        sharedPreferences = getActivity().getSharedPreferences("settings", 0);
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
                            StartTime = SystemClock.uptimeMillis();
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
    public boolean StopTimer(){
                //остановка таймера
                TimeSwapBuff+=TimeInMillisecond;
                customHandler.removeCallbacks(updateTimerThread);
                sec = 0; millis = 0; minuts = 0;
                StartTime = 0L; TimeInMillisecond = 0L; TimeSwapBuff = 0L; UpdateTime = 0L;
        return true;
    }
}
