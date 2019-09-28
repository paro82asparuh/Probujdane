package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;


/**
 * Created by agr on 20/01/2019.
 */

public class musicDBHelper extends SQLiteOpenHelper {

    public musicDBHelper(Context context) {
        super(context, new File(context.getExternalFilesDir(null), "music_sqlite.db").getAbsolutePath() , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getPathsPanevritmiaTRaks () {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT File_Name FROM table1 " +
                        "WHERE Type_='Panevrtimia' " +
                        "ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor getSongsInfo (String inputType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT ID, Title, File_Name " +
                        "FROM table1 WHERE " +
                        "Type_='"+inputType+"' " +
                        "ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor getSongSingle (String songID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT Title, Text, Type_, File_Name " +
                        "FROM table1 WHERE " +
                        "ID='"+songID+"' " +
                        //"ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

}
