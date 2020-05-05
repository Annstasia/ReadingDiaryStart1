package com.example.readingdiary.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import com.example.readingdiary.R;
import com.google.android.material.textfield.TextInputEditText;

public class VariousNotebook extends AppCompatActivity {
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
    }

    @Override
    public void onBackPressed() {
        returnResult(saveText());
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void openText() throws Exception{
        File fileDir1 = getApplicationContext().getDir(type, MODE_PRIVATE);
        if (!fileDir1.exists()) fileDir1.mkdirs();
        File file = new File(fileDir1, id+".txt");
        if (!file.exists()) file.createNewFile();
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder str = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null){
            str.append(line);
            str.append('\n');
        }
        text.setText(str.toString());
        br.close();
    }

    private long saveText(){

        try{
            File fileDir1 = getApplicationContext().getDir(type + File.pathSeparator + id, MODE_PRIVATE);
            if (!fileDir1.exists()) fileDir1.mkdirs();
            long time = new GregorianCalendar().getTimeInMillis();
            File file = new File(fileDir1, time+".txt");
            if (!file.exists()) file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(text.getText().toString());
            bw.close();
            return time;


        }
        catch (Exception e){
                Log.e("openException", e.toString());
        }
        return -1;
    }

    private void returnResult(long time){
        if (time == -1) return;
        Intent resultIntent = new Intent();
        resultIntent.putExtra("time", time+"");
        setResult(RESULT_OK, resultIntent);

    }


}
