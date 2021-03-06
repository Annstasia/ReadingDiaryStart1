package com.example.readingdiary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.os.Handler;

import com.example.readingdiary.Fragments.DeleteDialogFragment;
import com.example.readingdiary.Fragments.SetCoverDialogFragment;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.GaleryFullViewAdapter;
import com.example.readingdiary.data.LiteratureContract;
import com.example.readingdiary.data.OpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.lang.Runnable;



public class GaleryFullViewActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogListener,
        SetCoverDialogFragment.SetCoverDialogListener {
    private RecyclerView galeryFullView;;
    int position;
    private GaleryFullViewAdapter adapter;
    private List<Bitmap> images;
    private List<String> names;
    private boolean changed = false;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery_full_view);


        // открываем и сохраняем в список изображения для данной записи
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        position = Integer.parseInt(args.get("position").toString());
        images = new ArrayList<>();
        names = new ArrayList<>();

        File fileDir1 = getApplicationContext().getDir(getResources().getString(R.string.imagesDir) + File.pathSeparator + id, MODE_PRIVATE);
        File[] files = fileDir1.listFiles();
        if (files != null){
            for (int i = 0; i < files.length; i++){
                images.add(BitmapFactory.decodeFile(files[i].getAbsolutePath()));
                names.add(files[i].getAbsolutePath());
            }
        }


        Button deleteButton = (Button) findViewById(R.id.deleteFullImageButton);
        Button coverButton = (Button) findViewById(R.id.setAsCoverButton);
        galeryFullView = (RecyclerView) findViewById(R.id.galery_full_recycle_view);

        // добавляем адаптер
        adapter = new GaleryFullViewAdapter(images, getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        layoutManager.scrollToPosition(position);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        galeryFullView.setAdapter(adapter);
        galeryFullView.setLayoutManager(layoutManager);

        galeryFullView.setItemAnimator(itemAnimator);
        final LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id.full_view_button_layout);

        final Handler uiHandler = new Handler();

        final Runnable makeLayoutGone = new Runnable(){
            @Override
            public void run(){
                buttonsLayout.setVisibility(View.INVISIBLE);
            }
        };

        // при нажатии на картинку появляется менюшка к ней. Там есть кнопки удаления и установки в качестве обложки
        adapter.setOnItemClickListener(new GaleryFullViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                buttonsLayout.setVisibility(View.VISIBLE);
                position = pos;

                // через 8 секунд меню пропадает
                uiHandler.postDelayed(makeLayoutGone, 8000);
            }
        });


        // кнопка удаления. При нажатии изображение удаляется
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(names.get(position));
                if (file.exists()){
                    dialogDeleteOpen();
                }

            }
        });


        coverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetCoverOpen();

            }
        });
    }

    @Override
    public void onDeleteClicked() {
        File file = new File(names.get(position));
        if (file.exists()){
            file.delete();
            names.remove(position);
            images.remove(position);
            adapter.notifyDataSetChanged();
            // Отмечаем, что список изображений был изменен - нужно для возвращаемого интента
            if (!changed){
                changed=true;
                setResultChanged();
            }
        }
    }

    @Override
    public void onSetCover() {
        OpenHelper dbHelper = new OpenHelper(getApplicationContext());
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LiteratureContract.NoteTable.COLUMN_COVER_IMAGE, names.get(position));
        Log.d("IMAGE1", "!!! " + names.get(position));
        sdb.update(LiteratureContract.NoteTable.TABLE_NAME, cv, LiteratureContract.NoteTable._ID + " = " + id, null);
        Log.d("IMAGE1", "!!!end " + id);
    }

    private void dialogDeleteOpen(){
        DeleteDialogFragment dialog = new DeleteDialogFragment();
        dialog.show(getSupportFragmentManager(), "deleteDialog");
    }

    private void dialogSetCoverOpen(){
        SetCoverDialogFragment dialog = new SetCoverDialogFragment();
        dialog.show(getSupportFragmentManager(), "setCover");
    }

    private void setResultChanged(){
        // создание возвращаемого интента
        Log.d("DELETEIMAGE1", "resultChanged");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("changed", changed);
        setResult(RESULT_OK, returnIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
