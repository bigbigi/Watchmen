package com.auto.watchmen.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.auto.watchmen.bean.DataInfo;

import java.util.ArrayList;

public class DataInfoDb extends SQLiteOpenHelper {
    private Context mContext;
    private static final int VERSION = 1;
    private static final String DBNAME = "dataInfoDb";
    private static final String TABLE_NAME = "dataInfo";

    private static final String ROW_ID = "row_id";
    private static final String NAME = "name";
    private static final String TIME = "time";
    private static final String BEGIN = "begin";
    private static final String END = "end";
    private static final String MAX = "max";
    private static final String MIN = "min";


    private Object object = new Object();

    private static final String CREATE_TABLE_SQL = "create table if not exists %s (%s INTEGER PRIMARY KEY,%s text,%s Long default 0,%s Float default 0,%s Float default 0,%s Float default 0,%s Float default 0)";

    private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DataInfoDb(Context context) {
        super(context, DBNAME, null, VERSION);
        mContext = context;
    }

    public boolean check(DataInfo record) {
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

    public void addRecord(DataInfo record) {
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

    public void delRecord(DataInfo record) {
        if (record == null) return;
        synchronized (object) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                db.delete(TABLE_NAME, " time=?", new String[]{String.valueOf(record.getTime())});
                db.close();
            }
        }

    }

    public ArrayList<DataInfo> getRecord(String name) {
        ArrayList<DataInfo> recordList = new ArrayList<DataInfo>();
        synchronized (object) {
            Cursor cursor = null;
            DataInfo record = null;
            SQLiteDatabase db = getReadableDatabase();
            try {
                String sql = "select * from " + TABLE_NAME + " where name=" + name + " order by " + "time";
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

    public ArrayList<DataInfo> getDateRecord() {
        return getRecordBySql("select name,max(time) as time,max(max) as max,min(min) as min,begin,(select end from dataInfo t2 where strftime('%Y-%m-%d',t2.time/1000,'unixepoch')=strftime('%Y-%m-%d',t1.time/1000,'unixepoch') order by time desc limit 1) as end from " + TABLE_NAME + " t1 group by strftime('%Y-%m-%d',time/1000,'unixepoch')");
    }

    public ArrayList<DataInfo> getWeekRecord() {
        return getRecordBySql("select name,max(time) as time,max(max) as max,min(min) as min,begin,(select end from dataInfo t2 where strftime('%Y-%W',t2.time/1000,'unixepoch')=strftime('%Y-%W',t1.time/1000,'unixepoch') order by time desc limit 1) as end from " + TABLE_NAME + " t1 group by strftime('%Y-%W',time/1000,'unixepoch')");
    }

    public ArrayList<DataInfo> getRecordBySql(String sql) {
        ArrayList<DataInfo> recordList = new ArrayList<DataInfo>();
        synchronized (object) {
            Cursor cursor = null;
            DataInfo record = null;
            SQLiteDatabase db = getReadableDatabase();
            try {
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
                Log.e("big", "Exception:" + e);
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


    public static ContentValues convert2Value(DataInfo record) {
        if (null != record) {
            ContentValues values = new ContentValues();
            values.put(NAME, record.getName());
            values.put(TIME, record.getTime());
            values.put(BEGIN, record.getBegin());
            values.put(END, record.getEnd());
            values.put(MAX, record.getMax());
            values.put(MIN, record.getMin());
            return values;
        }
        return null;
    }

    public static DataInfo convert2Record(Cursor cursor) {
        if (null != cursor) {
            DataInfo record = new DataInfo();
            record.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            record.setTime(cursor.getLong(cursor.getColumnIndex(TIME)));
            record.setBegin(cursor.getFloat(cursor.getColumnIndex(BEGIN)));
            record.setEnd(cursor.getFloat(cursor.getColumnIndex(END)));
            record.setMax(cursor.getFloat(cursor.getColumnIndex(MAX)));
            record.setMin(cursor.getFloat(cursor.getColumnIndex(MIN)));
            return record;
        }
        return null;
    }

    private static volatile DataInfoDb mInstance;

    public synchronized static DataInfoDb getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DataInfoDb.class) {
                if (mInstance == null) {
                    mInstance = new DataInfoDb(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(CREATE_TABLE_SQL, TABLE_NAME, ROW_ID, NAME, TIME, BEGIN, END, MAX, MIN));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL(DROP_TABLE_SQL);
            db.execSQL(CREATE_TABLE_SQL);
        }
    }
}
