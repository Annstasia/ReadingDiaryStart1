package com.example.readingdiary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;

import com.example.readingdiary.data.LiteratureContract.NoteTable;
import com.example.readingdiary.data.LiteratureContract;
import com.example.readingdiary.data.OpenHelper;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class NoteActivity extends AppCompatActivity {
    TextView pathView;
    TextView titleView;
    TextView authorView;
    RatingBar ratingView;
    TextView genreView;
    TextView timeView;
    TextView placeView;
    TextView shortCommentView;
    ImageView coverView;
    String imagePath;

    SQLiteDatabase sdb;
    OpenHelper dbHelper;
    String id;
    String path;
    boolean change = false;
    private ImageView imageView;
    private final int Pick_image = 1;
    private final int EDIT_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        dbHelper = new OpenHelper(this);
        sdb = dbHelper.getReadableDatabase();
        findViews();
        ratingView.setEnabled(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        select(id); // Заполнение полей из бд
//        imageView = (ImageView) findViewById(R.id.image_view);
        Button pickImage = (Button) findViewById(R.id.button); // переход в галерею
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this, GaleryActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });


    }


    public void setViews(String path, String author, String title, String rating, String genre,
                         String time, String place, String shortComment, String imagePath){
        this.path = path;
        this.authorView.setText(author);
        this.titleView.setText(title);
        if (rating != null){
            this.ratingView.setRating(Float.parseFloat(rating));
        }

        this.genreView.setText(genre);
        this.timeView.setText(time);
        this.placeView.setText(place);
        this.shortCommentView.setText(shortComment);
//        File file = new File(imagePath);
        if (imagePath != null){
            this.coverView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            this.imagePath = imagePath;
        }
    }


    public void findViews(){
//        TextView path;
        titleView = (TextView) findViewById(R.id.titleNoteActivity);
        authorView = (TextView) findViewById(R.id.authorNoteActivity);
        ratingView = (RatingBar) findViewById(R.id.ratingBar);
        genreView = (TextView) findViewById(R.id.genre);
        timeView = (TextView) findViewById(R.id.time);
        placeView = (TextView) findViewById(R.id.place);
        shortCommentView = (TextView) findViewById(R.id.shortComment);
        coverView = (ImageView) findViewById(R.id.coverImage);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
        int it = item.getItemId();
        if (it == R.id.edit_note) {
            Intent intent = new Intent(NoteActivity.this, EditNoteActivity.class);
            intent.putExtra("id", id);
            startActivityForResult(intent, EDIT_REQUEST_CODE);
            return super.onOptionsItemSelected(item);
        }
        return true;
    }






    public void select(String id){
        // Выбор полей из бд
        // Сейчас тут выбор не всех полей

        String[] projection = {
                NoteTable._ID,
                NoteTable.COLUMN_PATH,
                NoteTable.COLUMN_AUTHOR,
                NoteTable.COLUMN_TITLE,
                NoteTable.COLUMN_COVER_IMAGE,
                NoteTable.COLUMN_RATING,
                NoteTable.COLUMN_GENRE,
                NoteTable.COLUMN_TIME,
                NoteTable.COLUMN_PLACE,
                NoteTable.COLUMN_SHORT_COMMENT

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
            int coverColumnIndex =  cursor.getColumnIndex(NoteTable.COLUMN_COVER_IMAGE);
            int ratingColumnIndex =  cursor.getColumnIndex(NoteTable.COLUMN_RATING);
            int genreColumnIndex =  cursor.getColumnIndex(NoteTable.COLUMN_GENRE);
            int timeColumnIndex =  cursor.getColumnIndex(NoteTable.COLUMN_TIME);
            int placeColumnIndex =  cursor.getColumnIndex(NoteTable.COLUMN_PLACE);
            int shortCommentIndex =  cursor.getColumnIndex(NoteTable.COLUMN_SHORT_COMMENT);

            while (cursor.moveToNext()) {
                Log.d("EDIT1", "select");
                setViews(cursor.getString(pathColumnIndex), cursor.getString(authorColumnIndex),
                        cursor.getString(titleColumnIndex), cursor.getString(ratingColumnIndex),
                        cursor.getString(genreColumnIndex), cursor.getString(timeColumnIndex),
                        cursor.getString(placeColumnIndex), cursor.getString(shortCommentIndex),
                        cursor.getString(shortCommentIndex));
            }
        }
        finally{
            cursor.close();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Toast.makeText(getApplicationContext(), "onBackPressed", Toast.LENGTH_LONG).show();
        // нужно сделать проверку ответа
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Destroy", Toast.LENGTH_LONG).show();
        if (change){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("path", path);
            setResult(RESULT_OK, returnIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("EDIT1", "all result " + id);
        if (requestCode==EDIT_REQUEST_CODE){
                select(id);

        }

    }
}
