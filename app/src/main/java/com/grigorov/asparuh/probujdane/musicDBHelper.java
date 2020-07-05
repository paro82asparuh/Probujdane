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
        Cursor res =  db.rawQuery(  "SELECT Vocal_File_Name, Instrumental_File_Name, Files_Downloaded FROM table1 " +
                        "WHERE Type_='Panevrtimia' " +
                        "ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor getSongsInfo (String inputType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT ID, Title, Vocal_File_Name, Instrumental_File_Name, Files_Downloaded " +
                        "FROM table1 WHERE " +
                        "Type_='"+inputType+"' " +
                        "ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor getSongsInfo () {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT ID, Title, Type_, Vocal_File_Name, Instrumental_File_Name, Files_Downloaded " +
                        "FROM table1 " +
                        "ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor getSongSingle (String songID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT Title, Text, Type_, Vocal_File_Name, Instrumental_File_Name, Files_Downloaded, Vocal_File_Link, Instrumental_File_Link " +
                        "FROM table1 WHERE " +
                        "ID='"+songID+"' " +
                        //"ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor searchInMusic (String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT " +
                        "offsets(table1) AS offs, ID, Title, Text, Type_, Vocal_File_Name, Instrumental_File_Name, Files_Downloaded " +
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

    public void updateSongDownloaded (String songID, String newSongDownloaded) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE table1 SET " +
                "Files_Downloaded = '" + newSongDownloaded + "' " +
                "WHERE ID='"+ songID +"' ;"
        );
    }

    public Cursor getSongsNotDownloaded () {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT ID, Title, Text, Type_, Vocal_File_Name, Instrumental_File_Name, Vocal_File_Link, Instrumental_File_Link " +
                        "FROM table1 WHERE " +
                        "Files_Downloaded='0' " +
                        "AND ( (NOT(Vocal_File_Name='')) OR (NOT(Instrumental_File_Name='')) )"+
                        //"ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor getSongsDownloaded () {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT ID, Title, Text, Type_, Vocal_File_Name, Instrumental_File_Name, Vocal_File_Link, Instrumental_File_Link " +
                        "FROM table1 WHERE " +
                        "Files_Downloaded='1' " +
                        //"ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor getSongsDownloaded (String inputType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(  "SELECT ID, Title, Text, Type_, Vocal_File_Name, Instrumental_File_Name, Vocal_File_Link, Instrumental_File_Link " +
                        "FROM table1 WHERE " +
                        "Files_Downloaded='1' AND " +
                        "Type_='"+inputType+"' " +
                        //"ORDER BY CAST(ID AS int) ASC" +
                        ";"
                , null );
        return res;
    }

}
