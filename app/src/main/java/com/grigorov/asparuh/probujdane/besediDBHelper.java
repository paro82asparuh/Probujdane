package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by agr on 10/10/2017.
 */

public class besediDBHelper extends SQLiteOpenHelper {

    public final String besediDatabaseName = "besedi_sqlite.db";

    public besediDBHelper(Context context) {
        //super(context, getDBpath() , null, 1);
        super(context, new File(context.getExternalFilesDir(null), "besedi_sqlite.db").getAbsolutePath() , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getbesediInfo (String besediType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT Name, Day_of_Month, Month_, Year_ " +
                                    "FROM table1 WHERE Variant='1' " +
                                    "And " +
                                        "(Type_1='"+besediType+"' OR" +
                                        " Type_2='"+besediType+"' OR" +
                                        " Type_3='"+besediType+"' OR" +
                                        " Type_4='"+besediType+"' ) " +
                                    "ORDER BY CAST(Year_ AS int) ASC, CAST(Month_ AS int) ASC, CAST(Day_of_Month AS int) ASC" +
                                    ";"
                                    , null );
        return res;
    }

    public Cursor getbesediInfoFiltered (String besediType, Integer selectedFromYears, Integer selectedToYears) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT Name, Day_of_Month, Month_, Year_ " +
                        "FROM table1 WHERE Variant='1' " +
                        "And " +
                        "CAST(Year_ AS int)>="+selectedFromYears.toString() +" " +
                        "And " +
                        "CAST(Year_ AS int)<="+selectedToYears.toString() +" " +
                        "And " +
                        "(Type_1='"+besediType+"' OR" +
                        " Type_2='"+besediType+"' OR" +
                        " Type_3='"+besediType+"' OR" +
                        " Type_4='"+besediType+"' ) " +
                        "ORDER BY CAST(Year_ AS int) ASC, CAST(Month_ AS int) ASC, CAST(Day_of_Month AS int) ASC" +
                        ";"
                , null );
        return res;
    }

}
