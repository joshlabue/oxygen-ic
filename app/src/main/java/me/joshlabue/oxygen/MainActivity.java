package me.joshlabue.oxygen;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.joshlabue.ictest.Main;
import me.joshlabue.ictest.ScheduleItem;

public class MainActivity extends AppCompatActivity {

    ArrayList<ClassData> classData = new ArrayList<ClassData>();
    ListView list;
    ListAdapter listAdapter;

    boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.gradeListView);
        listAdapter = new ListAdapter(MainActivity.this, classData);
        list.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();


        getSupportActionBar().setTitle("Oxygen");

        if(!loggedIn) {
            Intent loginIntent = new Intent(this, WelcomeLogin.class);
            startActivityForResult(loginIntent, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                try {
                    JSONArray arr = new JSONArray(data.getStringExtra("schedule"));
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject tempScheduleItem = arr.getJSONObject(i);
                        classData.add(new ClassData(tempScheduleItem.getString("className"),tempScheduleItem.getString("teacherName"), 69.69f));
                    }
                    listAdapter.notifyDataSetChanged();


                }
                catch(JSONException e) {

                }
            }
        }
    }
}
