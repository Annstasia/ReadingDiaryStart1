package com.example.readingdiary;

import androidx.annotation.Nullable;
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
    String parent = "./";
    List<Note> notes;
    List<String> buttons;
    List<String> directories;
    RecyclerView recyclerView;
    RecyclerView buttonView;
    CatalogButtonAdapter buttonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_catalog);
        dbHelper = new OpenHelper(this);
        sdb = dbHelper.getReadableDatabase();
        notes = new ArrayList<Note>();
        buttons = new ArrayList<String>();
        buttons.add(parent);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCatalog);
        buttonView = (RecyclerView) findViewById(R.id.buttonViewCatalog);
        selectAll();
        createRecyclerView();
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int type = notes.get(position).getItemType();
                if (type == 0){
                    RealNote realNote = (RealNote) notes.get(position);
                    Intent intent = new Intent(CatalogActivity.this, NoteActivity.class);
                    intent.putExtra("id", realNote.getID());
                    startActivityForResult(intent, 12345);
                }
                if (type == 1){
                    Directory directory = (Directory) notes.get(position);
                    parent = directory.getDirectory();
                    notes.clear();
                    buttons.add(parent);
                    Log.d("parentz", parent);
                    buttonAdapter.notifyDataSetChanged();
                    selectAll();
                    mAdapter.notifyDataSetChanged();
                }
                Toast.makeText(getApplicationContext(), "Hi" + position, Toast.LENGTH_LONG).show();
            }
        });

        buttonAdapter.setOnItemClickListener(new CatalogButtonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (String str : buttons){
                    Log.d("parentz1", str);
                }
                parent = buttons.get(position);
                reloadButtonsView();
////                List<String> helpButton = buttons.subList(0, position + 1);
////                buttons = buttons.subList(0, position + 1);
//                buttons.clear();
//                buttons = helpButton;
//                Log.d("POSITION", ""+  position);
//                buttonAdapter.notifyDataSetChanged();
//                for (String str : buttons){
//                    Log.d("parentz0", str);
//                }
//                Log.d("POSITION", ""+  position + "." + parent);
                reloadRecyclerView();


            }
        });
        FloatingActionButton addNote = (FloatingActionButton) findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, 12346);
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
//        String[] where = {parent};
        Cursor mCursor1 = sdb.query(LiteratureContract.PathTable.TABLE_NAME, projection1,
                LiteratureContract.PathTable.COLUMN_PARENT + " = ?", new String[] {parent},
                null, null, null);
        int idColumnIndex1 = mCursor1.getColumnIndex(LiteratureContract.PathTable._ID);
        int childColumnIndex = mCursor1.getColumnIndex(LiteratureContract.PathTable.COLUMN_CHILD);
        while (mCursor1.moveToNext()){
            long currentId = mCursor1.getLong(idColumnIndex1);
            String currentChild = mCursor1.getString(childColumnIndex);
            notes.add(new Directory(currentId, currentChild));
        }
        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE
        };
        Cursor cursor = sdb.query(
                NoteTable.TABLE_NAME,   // таблица
                projection,            // столбцы
                LiteratureContract.NoteTable.COLUMN_PATH + " = ?",                  // столбцы для условия WHERE
                new String[] {parent},                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);

        int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
        int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
        int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
        int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(idColumnIndex);
            String currentPath = cursor.getString(pathColumnIndex);
            String currentAuthor = cursor.getString(authorColumnIndex);
            String currentTitle = cursor.getString(titleColumnIndex);
            notes.add(new RealNote(currentID, currentPath, currentAuthor, currentTitle));
        }
        mCursor1.close();
        cursor.close();

    }

    protected void createRecyclerView(){
        mAdapter = new RecyclerViewAdapter(notes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        buttonAdapter = new CatalogButtonAdapter(buttons);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.ItemAnimator itemAnimator1 = new DefaultItemAnimator();
        buttonView.setAdapter(buttonAdapter);
        buttonView.setLayoutManager(layoutManager1);
        buttonView.setItemAnimator(itemAnimator1);

    }

    protected void reloadRecyclerView(){
        notes.clear();
//        buttons.clear();
        selectAll();
        mAdapter.notifyDataSetChanged();
    }

    protected void reloadButtonsView(){
        buttons.clear();
        String pathTokens[] = (parent).split("/");
        Log.d("PATHQ", parent);
        String prev = "";
        for (int i = 0; i < pathTokens.length; i++){
            if (pathTokens[i].equals("")){
                continue;
            }
            prev = prev + pathTokens[i] + "/";
            buttons.add(prev);
        }
        buttonAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("IDPARENT", "NOPARENT" + (data.getExtras().get("path")==null));
        if (data != null && data.getExtras().get("path") != null){
            parent = data.getExtras().get("path").toString();

            Log.d("IDPARENT", "!!!!" + parent);
            reloadRecyclerView();
            reloadButtonsView();
        }

    }


}
