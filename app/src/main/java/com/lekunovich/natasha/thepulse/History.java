package com.lekunovich.natasha.thepulse;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public  class History extends Fragment  {
DB db;
    ListView lvData;
    final String LOG_TAG = "myLogs";
    Cursor cursor;
    public static final int MSG_ANIMATION_REMOVE 	= 2;
    SimpleCursorAdapter scAdapter1;
    View view_history;
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MSG_ANIMATION_REMOVE: // Старт анимации удаления
                    View view = (View)msg.obj;
                    view.startAnimation(getDeleteAnimation(0, (msg.arg2 == 0) ? -view.getWidth() : 2 * view.getWidth(), msg.arg1));
                    break;
            }
        }
    };

    public Handler getHandler()
    {
        return handler;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view_history = inflater.inflate(R.layout.history, null);
        db = new DB(getActivity());
        db.open();
        cursor = db.getAllData();
        String[] from = new String[]{DB.KEY_DATE, DB.KEY_TIME, DB.COLUMN_PULSE, DB.COLUMN_COMMENT};
        int[] to = new int[]{R.id.date_time, R.id.time, R.id.pulse, R.id.comment};
         scAdapter1 = new SimpleCursorAdapter(view_history.getContext(), R.layout.item_history, cursor, from, to);
        lvData = (ListView) view_history.findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter1);
        lvData.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

       final SwipeDetector swipeDetector = new SwipeDetector();
        lvData.setOnTouchListener(swipeDetector);
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Message msg = new Message();
                msg.arg1 = position;
                // Если был обнаружен свайп, то удаляем айтем
                if (swipeDetector.swipeDetected()){
                        msg.what = MSG_ANIMATION_REMOVE;
                        msg.arg2 = swipeDetector.getAction() == SwipeDetector.Action.LR ? 1 : 0;
                        msg.obj = view;
                        db.delRec(id);
                        getHandler().sendMessage(msg);
            }
            }
        });
        return view_history;
    }

   private Animation getDeleteAnimation(float fromX, float toX, int position)
    {
        Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
        animation.setStartOffset(100);
        animation.setDuration(800);
        animation.setAnimationListener(new DeleteAnimationListenter(position));
        animation.setInterpolator(AnimationUtils.loadInterpolator(getActivity(),
                android.R.anim.anticipate_overshoot_interpolator));
        return animation;
    }
        public class DeleteAnimationListenter implements Animation.AnimationListener
        {
            int position;
            public DeleteAnimationListenter(int position)
            {
                this.position = position;
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                cursor = db.getAllData();
                String[] from = new String[]{DB.KEY_DATE, DB.KEY_TIME, DB.COLUMN_PULSE, DB.COLUMN_COMMENT};
                int[] to = new int[]{R.id.date_time, R.id.time, R.id.pulse, R.id.comment};
                scAdapter1 = new SimpleCursorAdapter(view_history.getContext(), R.layout.item_history, cursor, from, to);
                lvData = (ListView) view_history.findViewById(R.id.lvData);
                lvData.setAdapter(scAdapter1);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationStart(Animation animation) {

            }
        }
}
