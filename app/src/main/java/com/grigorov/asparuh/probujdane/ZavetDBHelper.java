package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class ZavetDBHelper extends SQLiteOpenHelper{

    private SQLiteDatabase db;

    public ZavetDBHelper(Context context) {
        super(context, new File(context.getExternalFilesDir(null), "zavet_sqlite.db").getAbsolutePath() , null, 1);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor getChapters () {
        Cursor res =  db.rawQuery(  "SELECT ID, Level, Color, Left_Text, Center_Text, Right_Text, Center_Bold FROM table1 " +
                        "ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor searchInNaukaVyz(String query) {
        Cursor res =  db.rawQuery(  "SELECT " +
                        "offsets(table1) AS offs, ID, Chapter, Level, Color, Left_Text, Center_Text, Right_Text, Center_Bold " +
                        "FROM table1 WHERE table1 MATCH " +
                        "'" + getSearchMatchString(query) + "' " +
                        "ORDER BY CAST(length(offs) AS int) DESC " +
                        "LIMIT 50" +
                        ";"
                , null );
        return res;
    }

    private String getSearchMatchString (String query) {
        String result = "";
        result = result + "Left_Text:" + query + " OR ";
        result = result + "Center_Text:" + query + " OR ";
        result = result + "Right_Text:"+ query;
        return result;
    }

    @Override
    public synchronized void close () {
        super.close();
        if (db != null) {
            db.close();
        }
    }

}
