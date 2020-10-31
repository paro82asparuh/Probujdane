package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class NaukaVyzDBHelper extends SQLiteOpenHelper {

    public NaukaVyzDBHelper(Context context) {
        //super(context, getDBpath() , null, 1);
        super(context, new File(context.getExternalFilesDir(null), "nauka_vyzpitanie_sqlite.db").getAbsolutePath() , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor getChapters () {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT ID, Chapter_Level, Chapter_Title, Chapter_Content, Chapter_Indentation FROM table1 " +
                        "ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }


}
