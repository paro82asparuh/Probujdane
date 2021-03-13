package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class playlistsDBhelperWriter extends SQLiteOpenHelper {

    private static Context contextFromConstructor;

    SQLiteDatabase db;

    public playlistsDBhelperWriter(Context context) {
        super(context, new File(context.getExternalFilesDir(null), "playlists_sqlite.db").getAbsolutePath() , null, 1);
        contextFromConstructor = context;
        db = this.getWritableDatabase();
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

    public void initializePlaylistsDB (Context context) {
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

    public void updatePlaylistFull (String playlistID, String playlistName, String playlistSongsString, String playlistPlayTypeString) {
        db.execSQL("UPDATE table1 SET" +
                "Name = '" + playlistName + "'," +
                "Songs = '" + playlistSongsString + "' " +
                "PlayType = '" + playlistPlayTypeString + "' " +
                "WHERE ID='"+ playlistID +"' ;"
        );
    }

    public void updatePlaylistName (String playlistID, String playlistName) {
        db.execSQL("UPDATE table1 SET" +
                "Name = '" + playlistName + "' " +
                "WHERE ID='"+ playlistID +"' ;"
        );
    }

    public void updatePlaylistSongs (String playlistID, String playlistSongsString, String playlistPlayTypeString) {
        db.execSQL("UPDATE table1 SET " +
                "Songs = '" + playlistSongsString + "', " +
                "PlayType = '" + playlistPlayTypeString + "' " +
                "WHERE ID='"+ playlistID +"' ;"
        );
    }

    public void deletePlaylist (String playlistID) {
        db = this.getWritableDatabase();
        db.execSQL("DELETE FROM table1 " +
                "WHERE ID='"+ playlistID +"' ;"
        );
    }

    public void insertPlaylist (String playlistID, String playlistName, String playlistSongsString, String playlistPlayTypeString) {
        db.execSQL("INSERT INTO table1 (ID, Name, Songs, PlayType) VALUES(" +
                "'" + playlistID + "', " +
                "'" + playlistName + "', " +
                "'" + playlistSongsString + "', " +
                "'" + playlistPlayTypeString +
                "');"
        );
    }

    @Override
    public synchronized void close () {
        super.close();
        if (db != null) {
            db.close();
        }
    }

}
