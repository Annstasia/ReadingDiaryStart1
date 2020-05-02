package com.example.readingdiary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.example.readingdiary.data.LiteratureContract;
import com.example.readingdiary.data.LiteratureContract.NoteTable;
import com.example.readingdiary.data.OpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class CatalogActivity extends AppCompatActivity {
    // класс отвечает за активность с каталогами
    OpenHelper dbHelper;
    RecyclerViewAdapter mAdapter;
    SQLiteDatabase sdb;
    String parent = "./";
    ArrayList<Note> notes;
    ArrayList<String> buttons;
    ArrayList<String> directories;
    RecyclerView recyclerView;
    RecyclerView buttonView;
    CatalogButtonAdapter buttonAdapter;
    CatalogSortsSpinnerAdapter sortsAdapter;
    ArrayList<String> sortsList;

    String sortTitles1 = "Сортировка по названиям в лексикографическом порядке";
    String sortTitles2 = "Сортировка по названиям в обратном лексикографическим порядке";
    String sortAuthors1 = "Сортировка по автору в лексиграфическом порядке";
    String sortAuthors2 = "Сортировка по автору в обратном лексиграфическим порядке";
    String sortRating1 = "Сортировка по возрастанию рейтинга";
    String sortRating2 = "Сортировка по убыванию рейтинга";
    String comp="";
    int order;
    int startPos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

//        Получение разрешений на чтение и запись
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("FILE3", "Permission is granted");

        } else {
            Log.v("FILE3", "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("FILE3", "Permission is granted");

        } else {

            Log.v("FILE3", "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }



        dbHelper = new OpenHelper(this);

        sdb = dbHelper.getReadableDatabase();
        notes = new ArrayList<Note>(); // список того, что будет отображаться в каталоге.
        buttons = new ArrayList<String>(); // Список пройденный каталогов до текущего
        initSortsList();
        buttons.add(parent);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCatalog);  // здесь будут отображаться каталоги и файлы notes
        buttonView = (RecyclerView) findViewById(R.id.buttonViewCatalog);  // здесь будут отображаться пройденные поддиректории buttons
        selectAll(); // чтение данных из бд
        createRecyclerView(); // создание и присоединение адаптеров для recyclerView и buttonView

        Spinner sortsSpinner = (Spinner) findViewById(R.id.spinnerSorts);
        sortsAdapter = new CatalogSortsSpinnerAdapter(this, sortsList);
        sortsSpinner.setAdapter(sortsAdapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);

        Button findButton = (Button) findViewById(R.id.findButton);
        final EditText findText = (EditText) findViewById(R.id.findText);


        // Обработчик нажатия на элемент адаптера каталогов
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // В notes хранятся объекты двух классов, имплементирующих Note - RealNote и Directory
                // RealNote - собственно запись пользователя. При клике нужно перейти к записи, т.е к NoteActivity
                // Directory - директория. При клике нужно перейти в эту директорию.
                int type = notes.get(position).getItemType();
                if (type == 0){
                    RealNote realNote = (RealNote) notes.get(position);
                    Intent intent = new Intent(CatalogActivity.this, NoteActivity.class);
                    // чтобы понять какую запись нужно отобразить в NoteActivity, запихиваем в intent id записи из бд
                    intent.putExtra("id", realNote.getID());
                    startActivityForResult(intent, 12345); // в NoteActivity пользователь может изменить путь.
                    //Если изменит, то вернется intent, чтобы можно было изменить отображение каталогов
                }
                if (type == 1){
                    Directory directory = (Directory) notes.get(position);
                    parent = directory.getDirectory(); // устанавливаем директорию, на которую нажали в качестве отправной
                    notes.clear();
                    buttons.add(parent);
                    buttonAdapter.notifyDataSetChanged();
                    selectAll(); // выбираем новые данные из бд
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        sortsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = (String) parent.getItemAtPosition(position);
//                String clickedCountryName = clickedItem.getCountryName();
                startSort(clickedItem);
                Toast.makeText(getApplicationContext(), clickedItem + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // адаптер отвесает за вывод пройденных директорий сверху и перемещение обратно.
        // При нажатии на путь перемещается в соответствующую директорию
        buttonAdapter.setOnItemClickListener(new CatalogButtonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                parent = buttons.get(position);
                reloadButtonsView();
                reloadRecyclerView();


            }
        });


        // Кнопка добавление новой активности
        FloatingActionButton addNote = (FloatingActionButton) findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // Возвращается intent, если пользователь действительно добавил активность
//                Intent intent = new Intent(CatalogActivity.this, AddNoteActivity.class);
//                startActivityForResult(intent, 12346);
                Intent intent = new Intent(CatalogActivity.this, EditNoteActivity.class);
                startActivityForResult(intent, 12346);


            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notes.clear();
//                buttons.clear();
                selectTitle(findText.getText().toString());
                mAdapter.notifyDataSetChanged();
//                buttonAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    public void selectAll() {

        sdb = dbHelper.getReadableDatabase();

        // Выбор директорий и добавление, находящихся в текущей директории parent.
        String[] projection1 = {
                LiteratureContract.PathTable._ID,
                LiteratureContract.PathTable.COLUMN_PARENT,
                LiteratureContract.PathTable.COLUMN_CHILD

        };
        Cursor mCursor1 = sdb.query(LiteratureContract.PathTable.TABLE_NAME, projection1,
                LiteratureContract.PathTable.COLUMN_PARENT + " = ?", new String[] {parent},
                null, null, LiteratureContract.PathTable.COLUMN_CHILD);
        int idColumnIndex1 = mCursor1.getColumnIndex(LiteratureContract.PathTable._ID);
        int childColumnIndex = mCursor1.getColumnIndex(LiteratureContract.PathTable.COLUMN_CHILD);
        while (mCursor1.moveToNext()){
            long currentId = mCursor1.getLong(idColumnIndex1);
            String currentChild = mCursor1.getString(childColumnIndex);
            notes.add(new Directory(currentId, currentChild));
        }
        mCursor1.close();
        startPos = notes.size();


        // Выбор и добавление записей, находящихся в текущей дирректории parent
        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE,
                NoteTable.COLUMN_RATING
        };
        Cursor cursor = sdb.query(
                NoteTable.TABLE_NAME,
                projection,
                LiteratureContract.NoteTable.COLUMN_PATH + " = ?",
                new String[] {parent},
                null,
                null,
                null);
        int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
        int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
        int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
        int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
        int ratingColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_RATING);
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(idColumnIndex);
            String currentPath = cursor.getString(pathColumnIndex);
            String currentAuthor = cursor.getString(authorColumnIndex);
            String currentTitle = cursor.getString(titleColumnIndex);
            double currentRating = Double.valueOf(cursor.getString(ratingColumnIndex));
            notes.add(new RealNote(currentID, currentPath, currentAuthor, currentTitle, currentRating));
        }
        cursor.close();

    }

    public void selectTitle(String title){
        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE,
                NoteTable.COLUMN_RATING
        };

        Cursor cursor = sdb.query(
                NoteTable.TABLE_NAME,
                projection,
                NoteTable.COLUMN_TITLE + " = ?",
                new String[] {title},
                null,
                null,
                null);
        int idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
        int pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
        int authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
        int titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
        int ratingColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_RATING);
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(idColumnIndex);
            String currentPath = cursor.getString(pathColumnIndex);
            String currentAuthor = cursor.getString(authorColumnIndex);
            String currentTitle = cursor.getString(titleColumnIndex);
            double currentRating = Double.valueOf(cursor.getString(ratingColumnIndex));
            notes.add(new RealNote(currentID, currentPath, currentAuthor, currentTitle, currentRating));
        }
        cursor.close();

        String[] titles = new String[title.length() + 1];
        titles[0] = title;
        titles[1] = "_" + title.substring(1);
        String quest = NoteTable.COLUMN_TITLE + "!= ? AND (";
        quest += NoteTable.COLUMN_TITLE + " LIKE ?" + " OR ";
        for (int i = 2; i < titles.length - 1; i++){
            titles[i] = title.substring(0, i - 1) + "%" + title.substring(i, title.length());
            quest += NoteTable.COLUMN_TITLE + " LIKE ?" + " OR ";
        }

        titles[titles.length - 1] = title.substring(0, title.length() - 1) + "%";
        quest += NoteTable.COLUMN_TITLE + " LIKE ?)";


        cursor = sdb.query(
                NoteTable.TABLE_NAME,
                projection,
                quest,
                titles,
                null,
                null,
                null);
        idColumnIndex = cursor.getColumnIndex(NoteTable._ID);
        pathColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_PATH);
        authorColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_AUTHOR);
        titleColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_TITLE);
        ratingColumnIndex = cursor.getColumnIndex(NoteTable.COLUMN_RATING);
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(idColumnIndex);
            String currentPath = cursor.getString(pathColumnIndex);
            String currentAuthor = cursor.getString(authorColumnIndex);
            String currentTitle = cursor.getString(titleColumnIndex);
            double currentRating = Double.valueOf(cursor.getString(ratingColumnIndex));
            notes.add(new RealNote(currentID, currentPath, currentAuthor, currentTitle, currentRating));
        }
        cursor.close();

    }

    protected void createRecyclerView(){
        // Подключение адаптеров и не только к recyclerView и buttonView
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


    public void initSortsList(){
        sortsList = new ArrayList<>();
        sortsList.add("");
        sortsList.add("Сортировка по названиям в лексикографическом порядке");
        sortsList.add("Сортировка по названиям в обратном лексикографическим порядке");
        sortsList.add("Сортировка по автору в лексиграфическом порядке");
        sortsList.add("Сортировка по автору в обратном лексиграфическим порядке");
        sortsList.add("Сортировка по возрастанию рейтинга");
        sortsList.add("Сортировка по убыванию рейтинга");
    }

    protected void reloadRecyclerView(){
        // перезагрузка recyclerView. Удаляются все элементы notes, выбираются новые из бд
        notes.clear();
        selectAll();
        mAdapter.notifyDataSetChanged();
    }

    protected void reloadButtonsView(){
        // перезагрузка buttonView. Удаляются все элементы button, выбираются новые из текущего пути
        buttons.clear();
        String pathTokens[] = (parent).split("/");
        // текущий путь - строка из названий директорий
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
//        Log.d("DATA1", data.toString());
//        Log.d("DATA1", data.getExtras().get("path").toString());
        if (data != null && data.getExtras().get("path") != null){
            // если изменился путь до записи, добавилась новая запись, то переходим к этой записи
            parent = data.getExtras().get("path").toString();
            reloadRecyclerView();
            reloadButtonsView();
        }
        if (requestCode==12346 && resultCode == RESULT_OK){
            Intent intent = new Intent(CatalogActivity.this, NoteActivity.class); // вызов активности записи
            intent.putExtra("id", data.getExtras().get("id").toString()); // передаем id активности в бд, чтобы понять какую активность надо показывать
            startActivity(intent);
        }



    }


    public void startSort(String sortType) {
        if (sortType.equals(sortTitles1)){
            comp = "title";
            order = 1;
        }
        if (sortType.equals(sortTitles2)){
            comp = "title";
            order = -1;
        }
        if (sortType.equals(sortAuthors1)){
            comp = "author";
            order = 1;
        }
        if (sortType.equals(sortAuthors2)){
            comp = "author";
            order = -1;
        }
        if (sortType.equals(sortRating1)){
            comp = "rating";
            order = 1;
        }
        if (sortType.equals(sortRating2)){
            comp = "rating";
            order = -1;
        }
        quickSort(startPos, notes.size() - 1);
        mAdapter.notifyDataSetChanged();


    }

    public void quickSort(int from, int to) {
        if (from < to) {
            int divideIndex;
            if (comp != "rating"){
                divideIndex = partitionString(from, to);
            }
            else{
                divideIndex = partitionDouble(from, to);
            }

            quickSort(from, divideIndex - 1);
            quickSort(divideIndex, to);
        }
    }
    private int partitionString(int from, int to)
    {
        int rightIndex = to;
        int leftIndex = from;

        String pivot = getComparable((RealNote) notes.get(from + (to - from) / 2));
        while (leftIndex <= rightIndex)
        {

            while (order * (getComparable((RealNote) notes.get(leftIndex)).compareTo(pivot)) < 0)
            {
                leftIndex++;
            }

            while (order * (getComparable((RealNote) notes.get(rightIndex)).compareTo(pivot)) > 0)
            {
                rightIndex--;
            }

            if (leftIndex <= rightIndex)
            {
                swap(rightIndex, leftIndex);
                leftIndex++;
                rightIndex--;
            }
        }
        return leftIndex;
    }

    private int partitionDouble(int from, int to){
        int rightIndex = to;
        int leftIndex = from;

        Double pivot = getComparableDouble((RealNote) notes.get(from + (to - from) / 2));
        while (leftIndex <= rightIndex)
        {

            while (order * (getComparableDouble((RealNote) notes.get(leftIndex)).compareTo(pivot)) < 0)
            {
                leftIndex++;
            }

            while (order * (getComparableDouble((RealNote) notes.get(rightIndex)).compareTo(pivot)) > 0)
            {
                rightIndex--;
            }

            if (leftIndex <= rightIndex)
            {
                swap(rightIndex, leftIndex);
                leftIndex++;
                rightIndex--;
            }
        }
        return leftIndex;
    }


    public String getComparable(RealNote realNote){
        if (comp.equals("title")){
            return realNote.getTitle();
        }
        if (comp.equals("author")){
            return realNote.getAuthor();
        }

        return "";
    }

    private void swap(int index1, int index2)
    {
        Note tmp  = notes.get(index1);
        notes.set(index1, notes.get(index2));
        notes.set(index2, tmp);
    }

    private Double getComparableDouble(RealNote realNote){
        return realNote.getRating();
    }




}
