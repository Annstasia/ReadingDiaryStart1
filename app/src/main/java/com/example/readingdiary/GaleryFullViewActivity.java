package com.example.readingdiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.lang.Runnable;



public class GaleryFullViewActivity extends AppCompatActivity {
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

        File fileDir1 = getApplicationContext().getDir("images" + File.pathSeparator + id, MODE_PRIVATE);
        File[] files = fileDir1.listFiles();
        if (files != null){
            for (int i = 0; i < files.length; i++){
                images.add(BitmapFactory.decodeFile(files[i].getAbsolutePath()));
                names.add(files[i].getAbsolutePath());
            }
        }


        Button deleteButton = (Button) findViewById(R.id.deleteFullImageButton);
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
        });





    }


    private void setResultChanged(){
        // создание возвращаемого интента
        Intent returnIntent = new Intent();
        returnIntent.putExtra("changed", changed);
        setResult(RESULT_OK, returnIntent);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
