package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button btn_add, btn_clean;
    ListView listView;
    SimpleCursorAdapter userAdapter;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    DatabaseHelper databaseHelper;
    Cursor userCursor;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        btn_add = findViewById(R.id.button);
        btn_clean = findViewById(R.id.button2);
        listView = findViewById(R.id.listview);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        setDataToAdaper();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), id + "", Toast.LENGTH_SHORT).show();
            }
        });
        registerForContextMenu(listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataToAdaper();
    }

    public void setDataToAdaper(){
        db = databaseHelper.getReadableDatabase();
        userCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE, null);
        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_DATE};
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        listView.setAdapter(userAdapter);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.button:
                String name = editText.getText().toString();
                if(!name.equals("")){
                    databaseHelper.addNote(db, name);
                    setDataToAdaper();
                    userAdapter.notifyDataSetChanged();
                    editText.setText("");
                }
                break;
            case R.id.button2:
                databaseHelper.cleanTable(db);
                setDataToAdaper();
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
        userCursor.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long itemId = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).id;
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                intent.putExtra("id", itemId);
                startActivity(intent);
                return true;
            case R.id.delete:
                db = databaseHelper.getReadableDatabase();
                db.delete(DatabaseHelper.TABLE, "_id = " + itemId, null);
                userAdapter.notifyDataSetChanged();
                setDataToAdaper();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}