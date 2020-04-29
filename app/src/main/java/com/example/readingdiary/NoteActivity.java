package com.example.readingdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;

import com.example.readingdiary.data.LiteratureContract.NoteTable;
import com.example.readingdiary.data.LiteratureContract;
import com.example.readingdiary.data.OpenHelper;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class NoteActivity extends AppCompatActivity {
    TextView titleNoteActivity;
    TextView authorNoteActivity;
    SQLiteDatabase sdb;
    OpenHelper dbHelper;
    String id;
    String path;
    boolean change = false;
    private ImageView imageView;
    private final int Pick_image = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        dbHelper = new OpenHelper(this);
        sdb = dbHelper.getReadableDatabase();
        titleNoteActivity = (TextView) findViewById(R.id.titleNoteActivity);
        authorNoteActivity = (TextView) findViewById(R.id.authorNoteActivity);
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        select(id); // Заполнение полей из бд
        imageView = (ImageView) findViewById(R.id.image_view);
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

//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//
//        switch(requestCode) {
//            case Pick_image:
//                if(resultCode == RESULT_OK){
//                    try {
//
//                        //Получаем URI изображения, преобразуем его в Bitmap
//                        //объект и отображаем в элементе ImageView нашего интерфейса:
//                        final Uri imageUri = imageReturnedIntent.getData();
//                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//
//                        File folder = new File(Environment.getExternalStorageDirectory() + "/images");
//                        boolean success = true;
//                        if (!folder.exists()) {
//                            success = folder.mkdir();
//                        }
//                        File folder1 = new File(folder + "/id");
//                        if (!folder1.exists()) {
//                            success = folder1.mkdir();
//                        }
//                        File imageFile;
//                        try{
//                            Log.d("IMAGE1", "start");
//                            imageFile = new File(folder1, "1.png");
//                            if (!imageFile.exists()){
//                                Log.d("IMAGE1", "start1");
//                                imageFile.mkdir();
//                                Log.d("IMAGE1", "start2");
//                                imageFile.createNewFile();
//                                Log.d("IMAGE1", "start3");
//                            }
//                            Log.d("IMAGE1", "save0");
//                            Log.d("IMAGE1", "save1" + imageFile.getAbsolutePath());
//
//
//                            if (!imageFile.exists()){
//                                FileOutputStream out = new FileOutputStream(imageFile);
////                                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, out);
//                                Log.d("IMAGE1", "save");
////                                out.write()
//                                out.flush();
//                                out.close();
//                            }
//
////                            imageView.setImageDrawable(Drawable.createFromPath(imageFile.getAbsolutePath()));
////
//
//                        }
//                        catch (Exception e){
//                            Log.e("input", e.toString());
//                        }
//
////                        imageView.setImageDrawable(Drawable.createFromPath(imageFile.getAbsolutePath()));
//
//
//
//
////                        imageView.setImageBitmap(selectedImage);
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//        }
//    }
//

    public void select(String id){
        // Выбор полей из бд
        // Сейчас тут выбор не всех полей

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
                path = currentPath;
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
}
