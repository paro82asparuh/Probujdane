package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by agr on 15/12/2018.
 */

public class MolitviDBHelper extends SQLiteOpenHelper {

    public final String besediDatabaseName = "formuli_sqlite.db";

    private SQLiteDatabase db;

    public MolitviDBHelper(Context context) {
        //super(context, getDBpath() , null, 1);
        super(context, new File(context.getExternalFilesDir(null), "molitvi_sqlite.db").getAbsolutePath() , null, 1);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getMolitvi () {
        Cursor res =  db.rawQuery(  "SELECT ID, Title, Text FROM table1 " +
                        "ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor searchInMolitvi (String query) {
        Cursor res =  db.rawQuery(  "SELECT " +
                        "offsets(table1) AS offs, ID, Title, Text " +
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
        result = result + "Title:" + query + " OR ";
        result = result + "Text:"+ query;
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
