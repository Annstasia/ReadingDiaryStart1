package com.example.readingdiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readingdiary.data.LiteratureContract.NoteTable;

import com.example.readingdiary.data.OpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import javax.xml.transform.Result;
import com.example.readingdiary.data.LiteratureContract.PathTable;

public class EditNoteActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_edit_note);
        dbHelper = new OpenHelper(this);
        sdb = dbHelper.getReadableDatabase();
        findViews();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle args = getIntent().getExtras();
        if (args != null){
            id = args.get("id").toString();
            Log.d("EDIT1", id);
            select(id);
        }

        FloatingActionButton accept =  (FloatingActionButton) findViewById(R.id.acceptAddingNote2);
        FloatingActionButton cancel =  (FloatingActionButton) findViewById(R.id.cancelAddingNote2);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    public void findViews(){
//        TextView path;
        pathView = (TextView) findViewById(R.id.editPath);
        titleView = (TextView) findViewById(R.id.editTitleNoteActivity);
        authorView = (TextView) findViewById(R.id.editAuthorNoteActivity);
        ratingView = (RatingBar) findViewById(R.id.editRatingBar);
        genreView = (TextView) findViewById(R.id.editGenre);
        timeView = (TextView) findViewById(R.id.editTime);
        placeView = (TextView) findViewById(R.id.editPlace);
        shortCommentView = (TextView) findViewById(R.id.editShortComment);
        coverView = (ImageView) findViewById(R.id.editCoverImage);


    }

    public void setViews(String path, String author, String title, String rating, String genre,
                         String time, String place, String shortComment, String imagePath){
        this.pathView.setText(path);
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
                Log.d("EDIT1", "cursor");
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

    public void saveChanges(){

        Log.d("Edit1", "saved");
        ContentValues cv = new ContentValues();
//        cv.put(NoteTable.COLUMN_PATH, );
        path = pathView.getText().toString();
        Log.d("PATH1", path);
        fixPath();
        cv.put(NoteTable.COLUMN_PATH, path);
        cv.put(NoteTable.COLUMN_AUTHOR, authorView.getText().toString());
        cv.put(NoteTable.COLUMN_TITLE, titleView.getText().toString());
        cv.put(NoteTable.COLUMN_COVER_IMAGE, imagePath);
        cv.put(NoteTable.COLUMN_RATING, String.valueOf(ratingView.getRating()));
        cv.put(NoteTable.COLUMN_GENRE,genreView.getText().toString());
        cv.put(NoteTable.COLUMN_TIME, timeView.getText().toString());
        cv.put(NoteTable.COLUMN_PLACE, placeView.getText().toString());
        cv.put(NoteTable.COLUMN_SHORT_COMMENT, shortCommentView.getText().toString());

        Log.d("Edit1", "saved1");
        if (id != null){

            sdb.update(NoteTable.TABLE_NAME, cv, "_id=" + id, null);
            changedIntent();
        }
        else{
            id = sdb.insert(NoteTable.TABLE_NAME, null, cv) + "";
            cv.clear();
            String pathTokens[] = ((String) path).split("/");
            String prev = pathTokens[0] + "/";
            for (int i = 1; i < pathTokens.length; i++){
                if (pathTokens[i].equals("")){
                    continue;
                }
                cv.put(PathTable.COLUMN_PARENT, prev);
                prev = prev + pathTokens[i] + "/";
                cv.put(PathTable.COLUMN_CHILD, prev);
                sdb.insert(PathTable.TABLE_NAME, null, cv);
                cv.clear();

            }
//            id = sdb.insert(NoteTable.TABLE_NAME, null, cv) + "";
            insertIntent();
        }
    }

    public void fixPath(){
        if (path.equals("") || path.equals("/")) path = "./";
        else{
            if (path.charAt(path.length() - 1) != '/'){
                path = path + "/";
            }
            if (path.charAt(0) == '/'){
                path = "." + path;
            }
            if (path.charAt(0) != '.'){
                path = "./" + path;
            }
        }
    }


    public void changedIntent(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("change", "true");
        setResult(RESULT_OK, returnIntent);
        Log.d("EDIT1", "change");
    }

    public void insertIntent(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id", id);
        returnIntent.putExtra("path", path);
        setResult(RESULT_OK, returnIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        saveChanges();
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Destroy", Toast.LENGTH_LONG).show();
//        if (change){
//            Intent returnIntent = new Intent();
////            returnIntent.putExtra("path", path);
//            setResult(RESULT_OK, returnIntent);
//        }
    }
}

