package com.lekunovich.natasha.thepulse;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends Activity {
    final String LOG_TAG = "myLogs";
    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;
    public static final String DB_TABLE = "mytab";
    public static final String COLUMN_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DATE_FORMAT = "date_format";
    public static final String KEY_TIME = "time";
    public static final String COLUMN_PULSE = "pulse";
    public static final String COLUMN_COMMENT = "comment";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    KEY_DATE + " text not null," +
                    KEY_DATE_FORMAT + " text not null," +
                    KEY_TIME + " text not null," +
                    COLUMN_PULSE + " int, " +
                    COLUMN_COMMENT + " text" +
                    ");";

    private final Context mCtx;
    private DBHelper mDBHelper;
    SQLiteDatabase mDB;
    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        //return mDB.query(DB_TABLE, new String[]{COLUMN_ID, KEY_DATE, KEY_DATE_FORMAT, KEY_TIME, COLUMN_PULSE, COLUMN_COMMENT}, null, null, null, null, null);
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    public Cursor Max(String first, String last) {
        String s ="SELECT MAX(" +COLUMN_PULSE+ ") as pulse from "+ DB_TABLE
        + " WHERE "+ KEY_DATE_FORMAT + " BETWEEN '"+first+"' AND '"+last+"'";
        return mDB.rawQuery(s, null);
    }

    public Cursor getPulse(String first, String last){
        String s ="SELECT " +COLUMN_PULSE+ " from "+ DB_TABLE
                + " WHERE "+ KEY_DATE_FORMAT + " BETWEEN '"+first+"' AND '"+last+"'";
        return mDB.rawQuery(s, null);
    }

    public Cursor getMinPulse(String first, String last){
        String s ="SELECT MIN(" +COLUMN_PULSE+ ") as pulse from "+ DB_TABLE
                + " WHERE "+ KEY_DATE_FORMAT + " BETWEEN '"+first+"' AND '"+last+"'";
        return mDB.rawQuery(s, null);
    }

    public Cursor getAVGPulse(String first, String last){
        String s ="SELECT AVG(" +COLUMN_PULSE+ ") as pulse from "+ DB_TABLE
                + " WHERE "+ KEY_DATE_FORMAT + " BETWEEN '"+first+"' AND '"+last+"'";
        return mDB.rawQuery(s, null);
    }

    // добавить запись в DB_TABLE
    public long addRec( String date, String date_format, String time, int pulse, String comment) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE, date);
        cv.put(KEY_DATE_FORMAT, date_format);
        cv.put(KEY_TIME, time);
        cv.put(COLUMN_PULSE, pulse);
        cv.put(COLUMN_COMMENT, comment);
        mDB.insert(DB_TABLE, null, cv);
        return 0;
    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    public class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
