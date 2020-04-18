package com.example.readingdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.CaseMap;
import android.os.Bundle;
import android.widget.TextView;

public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        TextView titleNoteActivity = (TextView) findViewById(R.id.titleNoteActivity);
        TextView authorNoteActivity = (TextView) findViewById(R.id.authorNoteActivity);
        Bundle args = getIntent().getExtras();
        titleNoteActivity.setText(args.get("title").toString());
        authorNoteActivity.setText(args.get("author").toString());


    }
}
