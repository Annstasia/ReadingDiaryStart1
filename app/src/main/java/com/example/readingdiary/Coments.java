package com.example.readingdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Path;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.GregorianCalendar;
import com.google.android.material.textfield.TextInputEditText;

public class Coments extends AppCompatActivity {
    private boolean shouldSave = true;
    private String id;
    private String type;
    private TextInputEditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coments);
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        type = args.get("type").toString();
        if (type.equals("description")){
            TextView textView12 = (TextView) findViewById(R.id.textView12);
            textView12.setText("Описание");
        }

        text = (TextInputEditText) findViewById(R.id.editTextComments);
        try{
            openText();
        }
        catch (Exception e){
            Log.d("openException", e.toString());
        }




    }

    @Override
    protected void onDestroy() {
        Log.d("open1234", "onDestroy");
        saveText();
        super.onDestroy();
    }

    private void openText() throws Exception{
        Log.d("open1234", "startOpen1 " + id);
        File fileDir1 = getApplicationContext().getDir(type, MODE_PRIVATE);
        Log.d("open1234", "startOpen2 " + id);
        if (!fileDir1.exists()) fileDir1.mkdirs();
        Log.d("open1234", "startOpen3 " + id);
        File file = new File(fileDir1, id+".txt");
        Log.d("open1234", "startOpen4 " + id);
        if (!file.exists()) file.createNewFile();
        Log.d("open1234", "startOpen5 " + id);
        BufferedReader br = new BufferedReader(new FileReader(file));
        Log.d("open1234", "startOpen6 " + id);
        StringBuilder str = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null){
            str.append(line);
            str.append('\n');
        }
        Log.d("open1234", "startOpen6.2 " + id + " " + str.toString());
        text.setText(str.toString());
//        Log.d("open1234")br.read
//        text.setText(FileUtils.rea);
        Log.d("open1234", "startOpen7 " + id);
        br.close();
        Log.d("open1234", "startOpen8 " + id);
    }

    private void saveText(){
//        File fileDir1 = getApplicationContext().getDir("coments" + File.pathSeparator + id, MODE_PRIVATE);
//        if (!fileDir1.exists()) fileDir1.mkdirs();
//        File file = new File(fileDir1, new GregorianCalendar().getTimeInMillis() + ".txt");
        try{
            File fileDir1 = getApplicationContext().getDir(type, MODE_PRIVATE);
            if (!fileDir1.exists()) fileDir1.mkdirs();
            File file = new File(fileDir1, id+".txt");
            if (!file.exists()) file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            // пишем данные
            bw.write(text.getText().toString());
            // закрываем поток
            bw.close();

        }
        catch (Exception e){
                Log.e("openException", e.toString());
        }
    }
}
