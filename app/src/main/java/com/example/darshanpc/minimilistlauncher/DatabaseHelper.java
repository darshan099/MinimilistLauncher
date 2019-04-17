package com.example.darshanpc.minimilistlauncher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String database="favourite.db";

    public DatabaseHelper(Context context) {
        super(context, database,null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE favourite(name TEXT,package TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS favourite");
        onCreate(sqLiteDatabase);
    }
    public void add_favourite_app(String appname,String packagename)
    {
        SQLiteDatabase sql=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",appname);
        contentValues.put("package",packagename);
        sql.insert("favourite",null,contentValues);
        Log.i("added",appname);
        Log.i("addpack", packagename);
    }
    public ArrayList get_favourite_app()
    {
        SQLiteDatabase sql=this.getWritableDatabase();
        ArrayList favouritelist=new ArrayList();
        Cursor cursor=sql.rawQuery("SELECT * FROM  favourite",null);
        while (cursor.moveToNext())
        {
            favouritelist.add(cursor.getString(0));
        }
        return favouritelist;
    }
}
