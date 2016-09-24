package com.example.sachin.fms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sachin.fms.dataSets.SavedFilterData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "information.db";

    // Contacts table name
    private static final String TABLE_Name = "filterData";

    // Contacts Table Columns names
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_CODE = "CODE";
    private static final String COLUMN_DESC = "DESC";
    private static final String COLUMN_TYPE = "TYPE";

    private static final String COLUMN_CHECKED = "CHECKED";

    private static final String KEY_NAME = "name";


    private static final String[] COLUMNS = {COLUMN_CODE, COLUMN_DESC, COLUMN_CHECKED};


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE filterData ( CODE TEXT PRIMARY KEY, DESC TEXT ,TYPE TEXT, CHECKED TEXT )";
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS  filterData");
        this.onCreate(db);


    }

    public int checkForData(String type) {

        SQLiteDatabase db = this.getReadableDatabase();

        int count = 0;
        String[] args = {type};
        String query = "select COUNT(*) from filterData ";
        String query2 = "select COUNT(*) from filterData where TYPE = 'P'";

        Cursor cursor = db.rawQuery(query2, null);

        Log.e("RAW QUERY ", db.rawQuery(query2, null).toString());

        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        Log.e("COunt", Integer.toString(count));

        return count;

    }


    public boolean insert(SavedFilterData data) {

        boolean createSuccessfully = false;

        //1. get reference to the database
        SQLiteDatabase db = this.getWritableDatabase();
        //2. create content value to add column/value

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CODE, data.getCode());
            values.put(COLUMN_DESC, data.getDesc());
            values.put(COLUMN_TYPE, data.getDesc());
            values.put(COLUMN_CHECKED, data.getChecked());
            Log.e("CODE", data.getCode());
            Log.e("DESC", data.getDesc());
            Log.e("TYPE", data.getType());
            Log.e("CHECKED", data.getChecked());

            //3. Insert

            db.insert(TABLE_Name, null, values);

            //4.close
            db.close();
            createSuccessfully = true;
        } catch (Exception ex) {
            createSuccessfully = false;
        }


        return createSuccessfully;
    }


    public List<SavedFilterData> getSavedData(String type) {
        //1. get database reference
        SQLiteDatabase db = this.getReadableDatabase();


        //2. build the query

        String query = "Select * from filterData where TYPE=?";


        Cursor cursor = db.rawQuery(query, new String[]{type});
        List<SavedFilterData> list = new ArrayList<>();
        SavedFilterData data = null;
        if (cursor.moveToFirst()) {
            do {
                data = new SavedFilterData();
                data.setCode(cursor.getString(0));
                data.setDesc(cursor.getString(1));
                data.setType(cursor.getString(2));
                data.setChecked(cursor.getString(3));
                Log.e("CODE", cursor.getString(0));
                Log.e("DESC", cursor.getString(1));
                Log.e("TYPE", cursor.getString(2));
                Log.e("CHECKED", cursor.getString(3));

                list.add(data);

            }
            while (cursor.moveToNext());

        }
        cursor.close();

        return list;

    }


    public int updateData(String code, String checked) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(COLUMN_CHECKED, checked);

        int i = db.update(TABLE_Name, values, COLUMN_CODE + "= ?", new String[]{code});

        db.close();

        return i;
    }

    public List<SavedFilterData> getAll() {
        List<SavedFilterData> list = new ArrayList<>();


        String query = "Select * from filterData";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        SavedFilterData data = null;

        if (cursor.moveToFirst()) {
            do {
                data = new SavedFilterData();
                data.setCode(cursor.getString(0));
                data.setDesc(cursor.getString(1));
                data.setType(cursor.getString(2));
                data.setChecked(cursor.getString(3));

                list.add(data);

            }
            while (cursor.moveToNext());

        }
        cursor.close();

        return list;

    }

    public boolean isChecked(String code) {
        List<SavedFilterData> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "Select * from filterData where CODE=?";

        String checked = "";
        boolean check = false;

        Cursor cursor = db.rawQuery(query, new String[]{code});
        if (cursor != null) {
            cursor.moveToFirst();
            checked = cursor.getString(0);


            cursor.close();

        }

        if (checked.equalsIgnoreCase("0")) {
            check = false;
        } else if (checked.equalsIgnoreCase("1")) {
            check = true;
        }

        return check;
    }
}
