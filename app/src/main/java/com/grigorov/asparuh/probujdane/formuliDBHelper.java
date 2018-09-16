package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by agr on 19/07/2018.
 */

public class formuliDBHelper extends SQLiteOpenHelper {

    public final String besediDatabaseName = "formuli_sqlite.db";

    public formuliDBHelper(Context context) {
        //super(context, getDBpath() , null, 1);
        super(context, new File(context.getExternalFilesDir(null), "formuli_sqlite.db").getAbsolutePath() , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getFormuli () {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT ID, Title, Text FROM table1 " +
                        "ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

}
