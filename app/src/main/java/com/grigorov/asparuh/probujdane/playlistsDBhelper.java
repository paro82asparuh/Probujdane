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

    public playlistsDBhelper(Context context) {
        super(context, new File(context.getExternalFilesDir(null), "playlists_sqlite.db").getAbsolutePath() , null, 1);
        contextFromConstructor = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE table1 ( " +
                    " ID VARCHAR(32000), Name VARCHAR(32000), Songs VARCHAR(32000) " +
                    " );"
            );
        } catch (SQLException mSQLException) {
            Log.e("Loginerror", "getproductData >>" + mSQLException.toString());
        }
        try {
            db.execSQL("INSERT INTO table1 (ID, Name, Songs) VALUES ("+
                    "'1', "+
                    "'"+contextFromConstructor.getResources().getString(R.string.panevritmia_string)+"', "+
                    "'166 167 168 169 170 171 172 173 174 175 176 177 178 179 180 181 182 183 184 185 186'"+
                    ");"
            );
        } catch (SQLException mSQLException) {
            Log.e("Loginerror", "getproductData >>" + mSQLException.toString());
        }
        try {
            db.execSQL("INSERT INTO table1 (ID, Name, Songs) VALUES ("+
                    "'2', "+
                    "'"+contextFromConstructor.getResources().getString(R.string.panevritmia_intrumental_string)+"', "+
                    "'145 146 147 148 149 150 151 152 153 154 155 156 157 158 159 160 161 162 163 164 165'"+
                    ");"
            );
        } catch (SQLException mSQLException) {
            Log.e("Loginerror", "getproductData >>" + mSQLException.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void initializePlaylistsDB (Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DROP TABLE IF EXISTS table1;");
        } catch (SQLException mSQLException) {
            Log.e("Loginerror", "getproductData >>" + mSQLException.toString());
        }
        //db.execSQL("CREATE VIRTUAL TABLE table1 USING fts4 ( " +
        //        " ID VARCHAR(32000), Name VARCHAR(32000), Songs VARCHAR(32000), " +
        //        " tokenize=unicode61 );"
        //);
    }

    public Cursor getAll () {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=null;
        try {
            res = db.rawQuery("SELECT ID, Name, Songs FROM table1 " +
                            "ORDER BY CAST(ID AS int) ASC" +
                            ";"
                    , null);
        } catch (SQLException mSQLException) {
            Log.e("Loginerror", "getproductData >>" + mSQLException.toString());
        }
        return res;
    }

    public Cursor getPlaylist (String playlistID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT Name, Songs " +
                        "FROM table1 WHERE " +
                        "ID='"+playlistID+"' " +
                        //"ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public void updatePlaylistFull (String playlistID, String playlistName, String playlistSongsString) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE table1 SET" +
                "Name = '" + playlistName + "'," +
                "Songs = '" + playlistSongsString + "' " +
                "WHERE ID='"+ playlistID +"' ;"
        );
    }

    public void updatePlaylistName (String playlistID, String playlistName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE table1 SET" +
                "Name = '" + playlistName + "'," +
                "WHERE ID='"+ playlistID +"' ;"
        );
    }

    public void updatePlaylistSongs (String playlistID, String playlistSongsString) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE table1 SET " +
                "Songs = '" + playlistSongsString + "' " +
                "WHERE ID='"+ playlistID +"' ;"
        );
    }

    public void deletePlaylist (String playlistID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM table1 " +
                "WHERE ID='"+ playlistID +"' ;"
        );
    }

    public void insertPlaylist (String playlistID, String playlistName, String playlistSongsString) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO table1 (ID, Name, Songs) VALUES(" +
                "'" + playlistID + "', " +
                "'" + playlistName + "', " +
                "'" + playlistSongsString + "');"
        );
    }

}
