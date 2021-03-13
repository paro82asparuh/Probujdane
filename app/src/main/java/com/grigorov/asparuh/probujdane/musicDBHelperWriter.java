package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class musicDBHelperWriter extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    public musicDBHelperWriter(Context context) {
        super(context, new File(context.getExternalFilesDir(null), "music_sqlite.db").getAbsolutePath() , null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getSongsInfo () {
        Cursor res =  db.rawQuery(  "SELECT ID, Title, Type_, Vocal_File_Name, Instrumental_File_Name, Files_Downloaded " +
                        "FROM table1 " +
                        "ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public void updateSongDownloaded (String songID, String newSongDownloaded) {
        db.execSQL("UPDATE table1 SET " +
                "Files_Downloaded = '" + newSongDownloaded + "' " +
                "WHERE ID='"+ songID +"' ;"
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
