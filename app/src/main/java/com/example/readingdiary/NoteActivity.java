package com.example.readingdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.CaseMap;
import android.os.Bundle;

import com.example.readingdiary.data.LiteratureContract.NoteTable;
import com.example.readingdiary.data.LiteratureContract;
import com.example.readingdiary.data.OpenHelper;

import android.util.Log;
import android.widget.TextView;

public class NoteActivity extends AppCompatActivity {
    TextView titleNoteActivity;
    TextView authorNoteActivity;
    SQLiteDatabase sdb;
    OpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        dbHelper = new OpenHelper(this);
        sdb = dbHelper.getReadableDatabase();
        titleNoteActivity = (TextView) findViewById(R.id.titleNoteActivity);
        authorNoteActivity = (TextView) findViewById(R.id.authorNoteActivity);

        Bundle args = getIntent().getExtras();
        String id = args.get("id").toString();
        select(id);


    }


    public void select(String id){

        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE
        };
        Cursor cursor = sdb.query(
                NoteTable.TABLE_NAME,   // таблица
                projection,            // столбцы
                NoteTable._ID + " = ?",                  // столбцы для условия WHERE
                new String[] {id},                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);
        try{
            int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
            int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
            int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
            int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentPath = cursor.getString(pathColumnIndex);
                String currentAuthor = cursor.getString(authorColumnIndex);
                String currentTitle = cursor.getString(titleColumnIndex);
                authorNoteActivity.setText(currentAuthor);
                titleNoteActivity.setText(currentTitle);
            }
        }
        finally{
            cursor.close();
        }
    }

}
