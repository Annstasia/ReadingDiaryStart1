package com.example.readingdiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.readingdiary.data.LiteratureContract;
import com.example.readingdiary.data.LiteratureContract.NoteTable;
import com.example.readingdiary.data.OpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CatalogActivity extends AppCompatActivity {
    OpenHelper dbHelper;
    RecyclerViewAdapter mAdapter;
    SQLiteDatabase sdb;
    String parent = ".";
    List<Note> notes;
    List<String> directories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_catalog);

        Log.d("CATALOGW", "create");
        dbHelper = new OpenHelper(this);
        Log.d("CATALOGW", "create0");
        sdb = dbHelper.getReadableDatabase();
        Log.d("CATALOGW", "create02");
        notes = new ArrayList<Note>();

        Log.d("CATALOGW", "create1");

        selectAll();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCatalog);
        mAdapter = new RecyclerViewAdapter(notes);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                Toast.makeText(this, "Hi" + position, Toast.LENGTH_LONG).show();
                parent = notes.get(position).getPath();
                notes.clear();
                selectAll();
                mAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "Hi" + position, Toast.LENGTH_LONG).show();
            }
        });
        Log.d("CATALOGN", "number" + notes.size());
        Log.d("CATALOGW", "setAdapter");

        FloatingActionButton addNote = (FloatingActionButton) findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });

    }



    public void selectAll() {
        sdb = dbHelper.getReadableDatabase();
        String[] projection1 = {
                LiteratureContract.PathTable._ID,
                LiteratureContract.PathTable.COLUMN_PARENT,
                LiteratureContract.PathTable.COLUMN_CHILD

        };
        String[] where = {parent};
        Cursor mCursor1 = sdb.query(LiteratureContract.PathTable.TABLE_NAME, projection1,
                LiteratureContract.PathTable.COLUMN_PARENT + " = ?", new String[] {parent},
                null, null, null);
        int childColumnIndex = mCursor1.getColumnIndex(LiteratureContract.PathTable.COLUMN_CHILD);
        while (mCursor1.moveToNext()){
            String currentChild = mCursor1.getString(childColumnIndex);
            notes.add(new Note(currentChild, null, null));
        }



        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE
//                NoteTable.COLUMN_DIRECTORY
        };

        Cursor cursor = sdb.query(
                NoteTable.TABLE_NAME,   // таблица
                projection,            // столбцы
                LiteratureContract.NoteTable.COLUMN_PATH + " = ?",                  // столбцы для условия WHERE
                new String[] {parent},                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки

        int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
        int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
        int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
        int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(idColumnIndex);
            String currentPath = cursor.getString(pathColumnIndex);
            String currentAuthor = cursor.getString(authorColumnIndex);
            String currentTitle = cursor.getString(titleColumnIndex);
            notes.add(new Note(currentPath, currentAuthor, currentTitle));
        }

        mCursor1.close();
        cursor.close();







//
//        sdb = dbHelper.getReadableDatabase();
//        String[] projection = {
//                NoteTable.COLUMN_PATH,
//                NoteTable.COLUMN_AUTHOR,
//                NoteTable.COLUMN_TITLE,
//                NoteTable.COLUMN_DIRECTORY
//        };
//        Log.d("CATALOGW", "select1");
//
//        Cursor mCursor = sdb.query(NoteTable.TABLE_NAME, projection, null, null, null, null,
//                null);
//        Log.d("CATALOGW", "select2");
//
//        int pathColumnIndex = mCursor.getColumnIndex(NoteTable.COLUMN_PATH);
//        int authorColumnIndex = mCursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
//        int titleColumnIndex = mCursor.getColumnIndex(NoteTable.COLUMN_TITLE);
//        int titleColumnDirectory = mCursor.getColumnIndex(NoteTable.COLUMN_DIRECTORY);
//
//        while (mCursor.moveToNext()){
//            String currentPath = mCursor.getString(pathColumnIndex);
//            String currentAuthor = mCursor.getString(authorColumnIndex);
//            String currentTitle = mCursor.getString(titleColumnIndex);
//            String currentDirectory = mCursor.getString(titleColumnDirectory);
//
//            Log.d("CATALOGW", "select3" + currentPath);
//            notes.add(new Note(currentPath, currentAuthor, currentTitle));
//            Log.d("CATALOGW", "select3" + currentPath);
//        }
//        mCursor.close();

//        sdb.close();
    }


}
