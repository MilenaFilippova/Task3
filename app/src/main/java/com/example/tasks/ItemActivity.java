package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ItemActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    EditText noteData;
    TextView noteTitle;
    SQLiteDatabase db;
    long itemData;
    Cursor userCursor;

    MediaPlayer mediaPlayer;
    Button play, pause, stop;
    Spinner spinner;
    int prev_song = -1;
    ArrayList<MediaPlayer> players = new ArrayList<>();
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Intent intent = getIntent();
        itemData = intent.getLongExtra("id", -1);
        noteData = findViewById(R.id.noteData);
        noteTitle = findViewById(R.id.noteTitle);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        if(itemData != -1){
            setNoteData();
        }

        players.add(MediaPlayer.create(getApplicationContext(), R.raw.comp1));
        players.add(MediaPlayer.create(getApplicationContext(), R.raw.comp2));
        players.add(MediaPlayer.create(getApplicationContext(), R.raw.comp3));

        play = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);
        spinner = findViewById(R.id.compos);

    }

    private void setNoteData(){
        db = databaseHelper.getReadableDatabase();
        userCursor =  db.rawQuery("select _id, name, create_date, text from "+ DatabaseHelper.TABLE + " WHERE _id = " + itemData + ";", null);
        userCursor.moveToFirst();
        noteTitle.setText(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
        noteData.setText(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT)));
    }

    public void onClick2(View v){
        switch (v.getId()){
            case R.id.btn_back:
                String text = noteData.getText().toString();
                db.execSQL("UPDATE " + DatabaseHelper.TABLE + " SET " + DatabaseHelper.COLUMN_TEXT + " = '" + text + "', " + DatabaseHelper.COLUMN_DATE + " = " + "datetime()" + " WHERE _id = " + itemData);
                Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
            case R.id.compos:
                break;
            case R.id.start:
                updateIndex();
                prev_song = index;
                if(index != 0) {
                    players.get(index - 1).start();
                    play.setEnabled(false);
                    pause.setEnabled(true);
                    stop.setEnabled(true);
                }
                break;
            case R.id.pause:
                updateIndex();
                if(index != 0) {
                    players.get(index - 1).pause();
                    play.setEnabled(true);
                    pause.setEnabled(false);
                    stop.setEnabled(true);
                }
                break;
            case R.id.stop:
                if(prev_song != -1) {
                    players.get(prev_song - 1).stop();
                    pause.setEnabled(false);
                    stop.setEnabled(false);
                    play.setEnabled(true);
                }

                break;
        }
    }

    public void updateIndex(){
        String word = spinner.getSelectedItem().toString();
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        index = adapter.getPosition(word);
        Log.d("mytag", index + "");
    }
}
