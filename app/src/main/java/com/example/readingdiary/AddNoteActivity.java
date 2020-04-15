package com.example.readingdiary;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.readingdiary.data.LiteratureContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.readingdiary.data.LiteratureContract.NoteTable;
import com.example.readingdiary.data.OpenHelper;

public class AddNoteActivity extends AppCompatActivity {
    SQLiteDatabase sdb;
//    SQLiteDatabase sdb;

    OpenHelper dbHelper;
    public static final String DATABASE_NAME = "Literature";
    public static final String DATABASE_TABLE = "Notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_PATH = "path";

    public static final int NUM_COLUMN_ID = 0;
    public static final int NUM_COLUMN_TITLE = 3;
    public static final int NUM_COLUMN_AUTHOR = 2;
    public static final int NUM_COLUMN_PATH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d("DATA", Environment.getDataDirectory().toString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        dbHelper = new OpenHelper(this);
        sdb = dbHelper.getWritableDatabase();

        Log.d("DATA", Environment.getDataDirectory().toString());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton cancelAddingNote = (FloatingActionButton)findViewById(R.id.cancelAddingNote);
        FloatingActionButton acceptAddingNote = (FloatingActionButton)findViewById(R.id.acceptAddingNote);
        final EditText pathField = (EditText) findViewById(R.id.pathField);
        final EditText authorField = (EditText) findViewById(R.id.authorField);
        final EditText titleField = (EditText) findViewById(R.id.titleField);



        cancelAddingNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeActivity();

            }
        });

        acceptAddingNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sdb.execSQL("INSERT INTO " + OpenHelper.DATABASE_TABLE + "(" + OpenHelper.COLUMN_PATH + ", " +
//                        OpenHelper.COLUMN_AUTHOR + ", " + OpenHelper.COLUMN_TITLE + ") " +
//                        "VALUES (" + pathField.getText() + ", " + authorField.getText() + ", "
//                        + titleField.getText() + ");"); // todo execute
//                pathField.g
                insert(pathField.getText().toString(), authorField.getText().toString(), titleField.getText().toString());
                displayDatabaseInfo();
            }
        });

    }

    private void closeActivity(){
        finish();
    }


    public long insert(String path, String author, String title) {
        sdb = dbHelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(NoteTable.COLUMN_PATH, path);
        cv.put(NoteTable.COLUMN_AUTHOR, author);
        cv.put(NoteTable.COLUMN_TITLE, title);
//        sdb.close();
        return sdb.insert(NoteTable.TABLE_NAME, null, cv);
    }


    private void displayDatabaseInfo() {

        // Создадим и откроем для чтения базу данных

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE,
        };
        // Делаем запрос
        Cursor cursor = db.query(
                NoteTable.TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки
        TextView displayTextView = (TextView) findViewById(R.id.text_view_info);

        try {
            displayTextView.setText("Таблица содержит " + cursor.getCount() + " гостей.\n\n");
            displayTextView.append(
                    NoteTable._ID  + " - " +
                            NoteTable.COLUMN_PATH  + " - " +
                            NoteTable.COLUMN_AUTHOR  + " - " +
                            NoteTable.COLUMN_TITLE  + "\n");

            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
            int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
            int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
            int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);

            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentPath = cursor.getString(pathColumnIndex);
                String currentAuthor = cursor.getString(authorColumnIndex);
                String currentTitle = cursor.getString(titleColumnIndex);

                // Выводим значения каждого столбца
                displayTextView.append(("\n" + currentID + " - " +
                        currentPath + " - " +
                        currentAuthor + " - " +
                        currentTitle));
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
    }

}
