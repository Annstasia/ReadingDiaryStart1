package com.example.readingdiary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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
import com.example.readingdiary.data.LiteratureContract.PathTable;

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
                Intent intent = new Intent(AddNoteActivity.this, NoteActivity.class);
                intent.putExtra("title", titleField.getText().toString());
                intent.putExtra("author", authorField.getText().toString());

//                Bundle bundle = new Bundle();
//
//                bundle.putString("title", "title";
//                bundle.putString("author", "sdfgh");

                startActivity(intent);
                displayDatabaseInfo();
            }
        });

    }

    private void closeActivity(){
        finish();
    }


    public void insert(String path, String author, String title) {
        sdb = dbHelper.getWritableDatabase();
        String pathTokens [] = path.split("/");
        ContentValues cv=new ContentValues();
        cv.put(NoteTable.COLUMN_PATH, path);
        cv.put(NoteTable.COLUMN_AUTHOR, author);
        cv.put(NoteTable.COLUMN_TITLE, title);
//        if (pathTokens[0] != ""){
//            cv.put(NoteTable.COLUMN_DIRECTORY, pathTokens[pathTokens.length - 1]);
//        }
//        else{
//            cv.put(NoteTable.COLUMN_DIRECTORY, null);
////            cv.put(NoteTable.COLUMN_DIRECTORY, null);
//        }

        sdb.insert(NoteTable.TABLE_NAME, null, cv);
        String prev = ".";
        for (String s : path.split("/")){
            if (s == ""){
                continue;
            }
            cv.clear();
            cv.put(PathTable.COLUMN_PARENT, prev);
            cv.put(PathTable.COLUMN_CHILD, prev + "/" + s);
            sdb.insert(PathTable.TABLE_NAME, null, cv);
            prev = prev + "/" + s;
        }


    }


    private void displayDatabaseInfo() {

        // Создадим и откроем для чтения базу данных

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE
//                NoteTable.COLUMN_DIRECTORY
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


        String[] projection1 = {
                PathTable._ID,
                PathTable.COLUMN_PARENT,
                PathTable.COLUMN_CHILD
        };
        // Делаем запрос
        Cursor cursor1 = db.query(
                PathTable.TABLE_NAME,   // таблица
                projection1,            // столбцы
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
                            NoteTable.COLUMN_TITLE  + " - "
//                            + //                            NoteTable.COLUMN_DIRECTORY
                            + "\n");

            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
            int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
            int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
            int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
//            int directoryColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_DIRECTORY);

            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentPath = cursor.getString(pathColumnIndex);
                String currentAuthor = cursor.getString(authorColumnIndex);
                String currentTitle = cursor.getString(titleColumnIndex);
//                String currentDirectory = cursor.getString(directoryColumnIndex);


                // Выводим значения каждого столбца
                displayTextView.append(("\n" + currentID + " - " +
                        currentPath + " - " +
                        currentAuthor + " - " +
                        currentTitle + " - "
//                        + currentDirectory
                ));
            }

            displayTextView.append(
                    PathTable._ID  + " - " +
                            PathTable.COLUMN_PARENT  + " - " +
                            PathTable.COLUMN_CHILD  + "\n");

            idColumnIndex = cursor1.getColumnIndex(PathTable._ID);
            int parentColumnIndex = cursor1.getColumnIndex(PathTable.COLUMN_PARENT);
            int childColumnIndex = cursor1.getColumnIndex(PathTable.COLUMN_CHILD);

            while (cursor1.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor1.getInt(idColumnIndex);
                String currentParent = cursor1.getString(parentColumnIndex);
                String currentChild = cursor1.getString(childColumnIndex);

                // Выводим значения каждого столбца
                displayTextView.append("\n" + currentID + " - " +
                        currentParent + " - " +
                        currentChild);
            }




        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
            cursor1.close();
        }
    }

}
