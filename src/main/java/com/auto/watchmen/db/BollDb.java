package com.auto.watchmen.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.auto.watchmen.bean.BollInfo;
import com.auto.watchmen.bean.BollInfo;

import java.util.ArrayList;

public class BollDb extends SQLiteOpenHelper {
    private Context mContext;
    private static final int VERSION = 1;
    private static final String DBNAME = "bollDb";
    private static final String TABLE_NAME = "bollInfo";

    private static final String ROW_ID = "row_id";
    private static final String NAME = "name";
    private static final String TIME = "time";
    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";
    private static final String MIDDLE = "middle";

//    private long time;
//    private float top;
//    private float bottom;
//    private float middle;

    private Object object = new Object();

    private static final String CREATE_TABLE_SQL = "create table if not exists %s (%s INTEGER PRIMARY KEY,%s text,%s Long default 0,%s Float default 0,%s Float default 0,%s Float default 0)";

    private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public BollDb(Context context) {
        super(context, DBNAME, null, VERSION);
        mContext = context;
    }

    public boolean check(BollInfo record) {
        boolean haveRecord = false;
        synchronized (object) {
            Cursor c = null;
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                c = db.query(TABLE_NAME, null, " time=?", new String[]{String.valueOf(record.getTime())}, null, null, null);
                haveRecord = c.moveToNext();
            }
            db.close();
        }
        return haveRecord;
    }

//    public boolean check(String value, String userId) {
//        boolean haveRecord = false;
//        synchronized (object) {
//            SQLiteDatabase db = getWritableDatabase();
//            if (db != null) {
//                Cursor c = db.query(TABLE_NAME, null, " time=?", new String[]{userId, value}, null, null, null);
//                haveRecord = c.moveToNext();
//            }
//            db.close();
//        }
//        return haveRecord;
//    }

    public void addRecord(BollInfo record) {
        if (check(record)) {
            return;
        }
        synchronized (object) {
            if (null != record) {
                SQLiteDatabase db = getWritableDatabase();
                ContentValues values = convert2Value(record);
                if (null != values) {
                    db.insert(TABLE_NAME, null, values);
                }
                db.close();
            }
        }
    }

    public void delRecord(BollInfo record) {
        if (record == null) return;
        synchronized (object) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                db.delete(TABLE_NAME, " time=?", new String[]{String.valueOf(record.getTime())});
                db.close();
            }
        }

    }

    public ArrayList<BollInfo> getRecord() {
        ArrayList<BollInfo> recordList = new ArrayList<BollInfo>();
        synchronized (object) {
            Cursor cursor = null;
            BollInfo record = null;
            SQLiteDatabase db = getReadableDatabase();
            try {
                String sql = "select * from " + TABLE_NAME + " order by " + "time";
                cursor = db.rawQuery(sql, null);
                if (cursor != null && cursor.getCount() != 0) {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        if (cursor.moveToNext()) {
                            record = convert2Record(cursor);
                            recordList.add(record);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                db.close();
            }
            Log.d("big", "arraylist record= " + recordList.size());
        }
        return recordList;
    }

    public boolean delAllRecord(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return false;
        }
        synchronized (object) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                db.delete(TABLE_NAME, null, null);
                db.close();
            }
        }
        return false;
    }


    public static ContentValues convert2Value(BollInfo record) {
        if (null != record) {
            ContentValues values = new ContentValues();
            values.put(NAME, record.getName());
            values.put(TIME, record.getTime());
            values.put(TOP, record.getTop());
            values.put(BOTTOM, record.getBottom());
            values.put(MIDDLE, record.getMiddle());
            return values;
        }
        return null;
    }

    public static BollInfo convert2Record(Cursor cursor) {
        if (null != cursor) {
            BollInfo record = new BollInfo();
            record.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            record.setTime(cursor.getLong(cursor.getColumnIndex(TIME)));
            record.setTop(cursor.getFloat(cursor.getColumnIndex(TOP)));
            record.setBottom(cursor.getFloat(cursor.getColumnIndex(BOTTOM)));
            record.setMiddle(cursor.getFloat(cursor.getColumnIndex(MIDDLE)));
            return record;
        }
        return null;
    }

    private static volatile BollDb mInstance;

    public synchronized static BollDb getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BollDb.class) {
                if (mInstance == null) {
                    mInstance = new BollDb(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format(CREATE_TABLE_SQL, TABLE_NAME, ROW_ID, NAME, TIME, TOP, BOTTOM, MIDDLE);
        Log.d("big", "oncreate-->" + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL(DROP_TABLE_SQL);
            db.execSQL(CREATE_TABLE_SQL);
        }
    }
}
