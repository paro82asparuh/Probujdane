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
    private SQLiteDatabase db;

    public besediDBHelper(Context context) {
        //super(context, getDBpath() , null, 1);
        super(context, new File(context.getExternalFilesDir(null), "besedi_sqlite.db").getAbsolutePath() , null, 1);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getbesediInfo (String besediType) {
        Cursor res =  db.rawQuery(  "SELECT Link, Name, Day_of_Month, Month_, Year_ " +
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
        Cursor res =  db.rawQuery(  "SELECT Link, Name, Day_of_Month, Month_, Year_ " +
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

    public Cursor getbesediInfoFromYears (String besediType, Integer selectedFromYears, Integer selectedToYears) {
        Cursor res =  db.rawQuery(  "SELECT Link, Name, Day_of_Month, Month_, Year_ " +
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

    public Cursor getbesediInfoFromYearsDay (String besediType, Integer selectedFromYears, Integer selectedToYears, Integer selectedDayInDate) {
        Cursor res =  db.rawQuery(  "SELECT Link, Name, Day_of_Month, Month_, Year_ " +
                        "FROM table1 WHERE Variant='1' " +
                        "And " +
                        "CAST(Year_ AS int)>="+selectedFromYears.toString() +" " +
                        "And " +
                        "CAST(Year_ AS int)<="+selectedToYears.toString() +" " +
                        "And " +
                        "CAST(Day_of_Month AS int)="+selectedDayInDate.toString() +" " +
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

    public Cursor getbesediInfoFromYearsMonth (String besediType, Integer selectedFromYears, Integer selectedToYears, Integer selectedMonth) {
        Cursor res =  db.rawQuery(  "SELECT Link, Name, Day_of_Month, Month_, Year_ " +
                        "FROM table1 WHERE Variant='1' " +
                        "And " +
                        "CAST(Year_ AS int)>="+selectedFromYears.toString() +" " +
                        "And " +
                        "CAST(Year_ AS int)<="+selectedToYears.toString() +" " +
                        "And " +
                        "CAST(Month_ AS int)="+selectedMonth.toString() +" " +
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

    public Cursor getbesediInfoFromYearsMonthDay (String besediType, Integer selectedFromYears, Integer selectedToYears,
                                                  Integer selectedMonth, Integer selectedDayInDate) {
        Cursor res =  db.rawQuery(  "SELECT Link, Name, Day_of_Month, Month_, Year_ " +
                        "FROM table1 WHERE Variant='1' " +
                        "And " +
                        "CAST(Year_ AS int)>="+selectedFromYears.toString() +" " +
                        "And " +
                        "CAST(Year_ AS int)<="+selectedToYears.toString() +" " +
                        "And " +
                        "CAST(Month_ AS int)="+selectedMonth.toString() +" " +
                        "And " +
                        "CAST(Day_of_Month AS int)="+selectedDayInDate.toString() +" " +
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

    public Cursor getbeseda (String besedaLink, String besedaDateYear, String besedaDateMonth, String besedaDateDay) {
        Cursor res =  db.rawQuery(  "SELECT * " +
                        "FROM table1 WHERE " +
                        "Link='"+besedaLink+"' " +
                        "And " +
                        "Year_='"+besedaDateYear+"' " +
                        "And " +
                        "Month_='"+besedaDateMonth+"' " +
                        "And " +
                        "Day_of_Month='"+besedaDateDay+"' " +
                        "ORDER BY CAST(Variant AS int) ASC" +
                        ";"
                , null );
        return res;
    }

    public Cursor searchInBesedi (String query) {
        Cursor res =  db.rawQuery(  "SELECT " +
                        "offsets(table1) AS offs, Link, Variant, Name, Day_of_Month, Month_, Year_, " +
                        getAllTextColumnIDs() +
                        "FROM table1 WHERE table1 MATCH " +
                        "'" + query + "' " +
                        "ORDER BY CAST(length(offs) AS int) DESC " +
                        "LIMIT 50" +
                        ";"
                , null );
        return res;
    }

//    public Cursor searchInBesedi (String query) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery(  "SELECT " +
//                        "offsets(table1) AS offs, Link, Variant, Name, Day_of_Month, Month_, Year_, " +
//                        getAllTextColumnIDs() +
//                        "FROM table1 WHERE table1 MATCH " +
//                        "'" + getSearchMatchString(query) + "' " +
//                        "ORDER BY CAST(length(offs) AS int) DESC " +
//                        "LIMIT 50" +
//                        ";"
//                , null );
//        return res;
//    }

//    private String getSearchMatchString (String query) {
//        String result = "";
//        result = result + "Name:" + query + " OR ";
//        for (Integer i= new Integer(0);i<68;i++) {
//            result = result + "Text" + i.toString() +":"+ query + " OR ";
//        }
//        result = result + "Text68:"+ query;
//        return result;
//    }

    public Cursor searchInBeseda (String query, String besedaLink, String besedaDateYear,
                                  String besedaDateMonth, String besedaDateDay, String besedaVariant) {
        Cursor res =  db.rawQuery(  "SELECT " +
                        "offsets(table1) AS offs, Link, Variant, Name, Day_of_Month, Month_, Year_, " +
                        getAllTextColumnIDs() +
                        "FROM table1 WHERE " +
                        "Link='"+besedaLink+"' " +
                        "And " +
                        "Year_='"+besedaDateYear+"' " +
                        "And " +
                        "Month_='"+besedaDateMonth+"' " +
                        "And " +
                        "Day_of_Month='"+besedaDateDay+"' " +
                        "And " +
                        "Variant='"+besedaVariant+"' " +
                        "And " +
                        "table1 MATCH " +
                        //"'" + getSearchMatchString(query) + "' " +
                        "'" + query + "' " +
                        "ORDER BY CAST(length(offs) AS int) DESC " +
                        "LIMIT 50" +
                        ";"
                , null );
        return res;
    }

    private String getAllTextColumnIDs() {
        String result = "";
        result="Text1, Text2, Text3, Text4, Text5, Text6, Text7, Text8, Text9, Text10, " +
                "Text11, Text12, Text13, Text14, Text15, Text16, Text17, Text18, Text19, Text20, " +
                "Text21, Text22, Text23, Text24, Text25, Text26, Text27, Text28, Text29, Text30, " +
                "Text31, Text32, Text33, Text34, Text35, Text36, Text37, Text38, Text39, Text40, " +
                "Text41, Text42, Text43, Text44, Text45, Text46, Text47, Text48, Text49, Text50, " +
                "Text51, Text52, Text53, Text54, Text55, Text56, Text57, Text58, Text59, Text60, " +
                "Text61, Text62, Text63, Text64, Text65, Text66, Text67, Text68 ";
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
