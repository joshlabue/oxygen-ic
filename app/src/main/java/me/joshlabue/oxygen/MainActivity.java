package me.joshlabue.oxygen;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<ClassData> data = new ArrayList<ClassData>();
    ListView list;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.gradeListView);
        listAdapter = new ListAdapter(MainActivity.this, data);
        list.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();


        getSupportActionBar().setTitle("Oxygen");

    }
}
