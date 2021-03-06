package com.grigorov.asparuh.probujdane;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

/**
 * Created by agr on 30/11/2019.
 */

public class playlistsDBhelper extends SQLiteOpenHelper {

    private static Context contextFromConstructor;

    SQLiteDatabase db;

    public playlistsDBhelper(Context context) {
        super(context, new File(context.getExternalFilesDir(null), "playlists_sqlite.db").getAbsolutePath() , null, 1);
        contextFromConstructor = context;
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE table1 ( " +
                    " ID VARCHAR(32000), Name VARCHAR(32000), Songs VARCHAR(32000), PlayType VARCHAR(32000) " +
                    " );"
            );
        } catch (SQLException mSQLException) {
            Log.e("Loginerror", "getproductData >>" + mSQLException.toString());
        }
        try {
            db.execSQL("INSERT INTO table1 (ID, Name, Songs, PlayType) VALUES ("+
                    "'1', "+
                    "'"+contextFromConstructor.getResources().getString(R.string.panevritmia_string)+"', "+
                    "'150 151 152 153 154 155 156 157 158 159 160 161 162 163 164 165 166 167 168 169 170'"+", "+
                    "'1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1'"+
                    ");"
            );
        } catch (SQLException mSQLException) {
            Log.e("Loginerror", "getproductData >>" + mSQLException.toString());
        }
        try {
            db.execSQL("INSERT INTO table1 (ID, Name, Songs, PlayType) VALUES ("+
                    "'2', "+
                    "'"+contextFromConstructor.getResources().getString(R.string.panevritmia_intrumental_string)+"', "+
                    "'150 151 152 153 154 155 156 157 158 159 160 161 162 163 164 165 166 167 168 169 170'"+", "+
                    "'2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2'"+
                    ");"
            );
        } catch (SQLException mSQLException) {
            Log.e("Loginerror", "getproductData >>" + mSQLException.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getAll () {
        Cursor res=null;
        try {
            res = db.rawQuery("SELECT ID, Name, Songs, PlayType FROM table1 " +
                            "ORDER BY CAST(ID AS int) ASC" +
                            ";"
                    , null);
        } catch (SQLException mSQLException) {
            Log.e("Loginerror", "getproductData >>" + mSQLException.toString());
        }
        return res;
    }

    public Cursor getPlaylist (String playlistID) {
        Cursor res =  db.rawQuery(  "SELECT Name, Songs, PlayType " +
                        "FROM table1 WHERE " +
                        "ID='"+playlistID+"' " +
                        //"ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    @Override
    public synchronized void close () {
        super.close();
        if (db != null) {
            db.close();
        }
    }

}
