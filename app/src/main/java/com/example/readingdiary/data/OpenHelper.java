package com.example.readingdiary.data;

//package com.example.databaseproject1;

//package com.example.readingdiary;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.readingdiary.data.LiteratureContract.NoteTable;

import androidx.annotation.Nullable;

public class OpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "literature6.db";
//    public static final String DATABASE_TABLE = "Notes";
//    public static final String COLUMN_ID = "_id";
//    public static final String COLUMN_TITLE = " title ";
//    public static final String COLUMN_AUTHOR = " author ";
//    public static final String COLUMN_PATH = " path ";

//    public static final String COLUMN_YEAR = "year";




    public static final int DATABASE_VERSION = 1;


    public OpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        String query = "";
        String query = "CREATE TABLE " + LiteratureContract.NoteTable.TABLE_NAME + " (" +
                LiteratureContract.NoteTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LiteratureContract.NoteTable.COLUMN_PATH + " TEXT, " +
                LiteratureContract.NoteTable.COLUMN_AUTHOR + " TEXT, " +
                LiteratureContract.NoteTable.COLUMN_TITLE + " TEXT" +
//                NoteTable.COLUMN_DIRECTORY + " TEXT" +
                 ");";
//        String query1 = "CREATE TABLE " + GuestEntry.TABLE_NAME + " ("
//                + HotelContract.GuestEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + GuestEntry.COLUMN_NAME + " TEXT NOT NULL, "
//                + GuestEntry.COLUMN_CITY + " TEXT NOT NULL, "
//                + GuestEntry.COLUMN_GENDER + " INTEGER NOT NULL DEFAULT 3, "
//                + GuestEntry.COLUMN_AGE + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(query);
        String query1 = "CREATE TABLE " + LiteratureContract.PathTable.TABLE_NAME + " (" +
                LiteratureContract.PathTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LiteratureContract.PathTable.COLUMN_PARENT + " TEXT, " +
                LiteratureContract.PathTable.COLUMN_CHILD + " TEXT UNIQUE" + ");";
        db.execSQL(query1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// todo
        db.execSQL("DROP TABLE IF EXISTS " + NoteTable.TABLE_NAME);

        onCreate(db);
    }
}
