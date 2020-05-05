package com.example.readingdiary.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.readingdiary.Classes.VariousNotes;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.VariousViewAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class VariousShow extends AppCompatActivity {
    private String id;
    private String type;
    VariousViewAdapter viewAdapter;
    RecyclerView recyclerView;
    ArrayList<VariousNotes> variousNotes;
    private final int ADD_VIEW_RESULT_CODE = 666;
    File fileDir1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_various_show);

        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        type = args.get("type").toString();
        variousNotes = new ArrayList<>();
        fileDir1 = getApplicationContext().getDir(type + File.pathSeparator + id, MODE_PRIVATE);
        openNotes();
        findViews();
        setAdapters();
        setButtons();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == ADD_VIEW_RESULT_CODE && resultCode == RESULT_OK){
                Bundle args = data.getExtras();
                long time = Long.parseLong(args.get("time").toString());
                File file = new File(fileDir1, time+".txt");
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder str = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null){
                    str.append(line);
                    str.append('\n');
                }
                variousNotes.add(new VariousNotes(str.toString(), file.getAbsolutePath(), time, false));
                viewAdapter.notifyDataSetChanged();
            }
        }
        catch (Exception e){
            Log.e("resultShowException", e.toString());
        }

    }

    private void findViews(){
        recyclerView = (RecyclerView) findViewById(R.id.various_recycler_view);
    }

    private void setAdapters(){
        viewAdapter = new VariousViewAdapter(variousNotes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(viewAdapter);
    }

    private void setButtons(){
        Button addVariousItem = (Button) findViewById(R.id.addVariousItem);
        addVariousItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VariousShow.this, VariousNotebook.class);
                intent.putExtra("id", id);
                intent.putExtra("type", type);
                startActivityForResult(intent, ADD_VIEW_RESULT_CODE);
            }
        });
    }


    private void openNotes(){
        try{


            File[] files = fileDir1.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    BufferedReader br = new BufferedReader(new FileReader(files[i]));
                    StringBuilder str = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        str.append(line);
                        str.append('\n');
                    }
                    String[] pathTokens = files[i].getAbsolutePath().split(File.pathSeparator);


                    variousNotes.add(new VariousNotes(str.toString(), files[i].getAbsolutePath(),
                            Long.parseLong(pathTokens[pathTokens.length - 1].split("\\.")[0].split("/")[1]),
                            false));

                }
            }
        }
        catch (Exception e){
            Log.e("openShowException", e.toString());
        }
    }

    private void saveChanges(){
        try {
            for (VariousNotes note : variousNotes) {
                if (note.isChanged()) {
                    if (!fileDir1.exists()) fileDir1.mkdirs();
                    File file = new File(fileDir1, note.getTime() + ".txt");
                    if (!file.exists()) file.createNewFile();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                    // пишем данные
                    bw.write(note.getText());
                    // закрываем поток
                    bw.close();
                }
            }
        }
        catch (Exception e){
            Log.e("saveShowException", e.toString());
        }
    }

}
