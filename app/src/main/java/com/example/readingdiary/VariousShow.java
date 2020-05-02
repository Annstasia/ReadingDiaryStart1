package com.example.readingdiary;

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

import org.w3c.dom.Comment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
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
        fileDir1 = getApplicationContext().getDir(type + File.pathSeparator + id, MODE_PRIVATE);
//        if (!fileDir1.exists()) fileDir1.mkdirs();

        variousNotes = new ArrayList<>();
        openNotes();
        recyclerView = (RecyclerView) findViewById(R.id.various_recycler_view);
        viewAdapter = new VariousViewAdapter(variousNotes);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(viewAdapter);

        Button addVariousItem = (Button) findViewById(R.id.addVariousItem);
        addVariousItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VariousShow.this, Coments.class);
                intent.putExtra("id", id);
                intent.putExtra("type", type);
                Log.d("COMENTS1", "add");
                startActivityForResult(intent, ADD_VIEW_RESULT_CODE);
            }
        });
        viewAdapter.notifyDataSetChanged();



    }

    private void openNotes(){
        try{


            File[] files = fileDir1.listFiles();
            Log.d("openNotes1", "nullable");
            if (files != null) {
                Log.d("openNotes1", "notnull " + files.length);
//                Log.d("openNotes1", "notnull " + files[0].getAbsolutePath());

                for (int i = 0; i < files.length; i++) {
                    Log.d("openNotes1", "cycle1");
                    BufferedReader br = new BufferedReader(new FileReader(files[i]));
                    Log.d("openNotes1", "cycle1");
                    StringBuilder str = new StringBuilder();
                    Log.d("openNotes1", "cycle1");
                    String line;
                    Log.d("openNotes1", "cycle1");
                    while ((line = br.readLine()) != null) {
                        str.append(line);
                        str.append('\n');
                    }
                    Log.d("openNotes1", "cycle1");
                    String[] pathTokens = files[i].getAbsolutePath().split(File.pathSeparator);
    //                String timeStr = Long.parseLong(pathTokens[pathTokens.length - 1].split(".")[0]);
                    Log.d("openNotes1", pathTokens[pathTokens.length - 1]);
                    Log.d("openNotes1", pathTokens[pathTokens.length - 1].split("\\.")[0].split("/")[1]);

                    variousNotes.add(new VariousNotes(str.toString(), files[i].getAbsolutePath(),
                            Long.parseLong(pathTokens[pathTokens.length - 1].split("\\.")[0].split("/")[1]),
                            false));
                    Log.d("openNotes1", variousNotes.size() +"");

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            Log.d("COMENTS1", "result1");
            if (requestCode == ADD_VIEW_RESULT_CODE){
                Log.d("COMENTS1", "result2");
                Bundle args = data.getExtras();
                Log.d("COMENTS1", "result3");
                long time = Long.parseLong(args.get("time").toString());
                Log.d("COMENTS1", "result4");
                File file = new File(fileDir1, time+".txt");
                Log.d("COMENTS1", "result5");
                BufferedReader br = new BufferedReader(new FileReader(file));
                Log.d("COMENTS1", "result6");
                StringBuilder str = new StringBuilder();
                Log.d("COMENTS1", "result7");
                String line;
                Log.d("COMENTS1", "result8");
                while ((line = br.readLine()) != null){
                    str.append(line);
                    str.append('\n');
                }
                Log.d("COMENTS1", "result9");
                variousNotes.add(new VariousNotes(str.toString(), file.getAbsolutePath(), time, false));
                Log.d("COMENTS1", "result10");
                viewAdapter.notifyDataSetChanged();
            }
        }
        catch (Exception e){
            Log.e("resultShowException", e.toString());
        }

    }
}
