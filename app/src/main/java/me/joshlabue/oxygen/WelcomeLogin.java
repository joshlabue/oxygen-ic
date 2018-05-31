package me.joshlabue.oxygen;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;

import me.joshlabue.ictest.Campus;
import me.joshlabue.ictest.ICLoginException;
import me.joshlabue.ictest.ICRequestException;
import me.joshlabue.ictest.ScheduleItem;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WelcomeLogin extends AppCompatActivity {

    Campus ic = new Campus("https://infinitecampus.penfield.edu/campus", "penfield");

    ProgressBar nameBar;
    TextView nameText;
    ImageView nameDone;

    ProgressBar dataBar;
    TextView dataText;
    ImageView dataDone;

    ConstraintLayout statusName;
    ConstraintLayout statusData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome_login);

        nameBar = findViewById(R.id.gettingNameProgress);
        nameText = findViewById(R.id.textName);
        nameDone = findViewById(R.id.nameDone);

        dataBar = findViewById(R.id.gettingDataProgress);
        dataText = findViewById(R.id.textData);
        dataDone = findViewById(R.id.dataDone);

        statusName = findViewById(R.id.statusName);
        statusData = findViewById(R.id.statusData);

        statusName.setVisibility(View.INVISIBLE);
        statusData.setVisibility(View.INVISIBLE);
        
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                statusName.setVisibility(View.VISIBLE);
                statusData.setVisibility(View.INVISIBLE);

                nameBar.setVisibility(View.VISIBLE);
                nameText.setVisibility(View.VISIBLE);
                nameText.setText("Loading user data...");
                nameDone.setVisibility(View.INVISIBLE);
                nameDone.setImageResource(R.drawable.loading_complete);

                dataBar.setVisibility(View.VISIBLE);
                dataText.setVisibility(View.VISIBLE);
                dataDone.setVisibility(View.VISIBLE);

                EditText usernameField = findViewById(R.id.usernameField);
                EditText passwordField = findViewById(R.id.passwordField);

                ic.setCredentials(usernameField.getText().toString(), passwordField.getText().toString());

                ic.context.appName = "penfield";

                start();
            }
        });
    }

    private void start() {
        ic.context.client.newCall(ic.login.request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response loginResponse) throws IOException {

                try {
                    ic.login.feedData(loginResponse);
                }
                catch(ICLoginException e) {
                    WelcomeLogin.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nameText.setText("Login failed");
                            nameBar.setVisibility(View.INVISIBLE);
                            nameDone.setVisibility(View.VISIBLE);
                            nameDone.setImageResource(R.drawable.loading_error);
                        }
                    });
                }

                if(ic.login.success == true) {
                    constructUser();
                }
            }
        });
    }

    private void constructUser() {
        ic.prepareUserData();

        ic.context.client.newCall(ic.userData.request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response dataResponse) throws IOException {
                ic.userData.feedData(dataResponse);

                ic.initUser();

                WelcomeLogin.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nameText.setText("Hello " + ic.user.firstName);
                        statusData.setVisibility(View.VISIBLE);
                        nameBar.setVisibility(View.INVISIBLE);
                        nameDone.setVisibility(View.VISIBLE);
                    }
                });

                constructSchedule();
            }
        });
    }

    private void constructSchedule() {
        try {
            ic.prepareSchedule();

            ic.context.client.newCall(ic.scheduleData.request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ic.scheduleData.feedData(response);
                    ic.initSchedule();

                    closeActivity();
                }
            });
        }
        catch(ICRequestException e) {
            e.printStackTrace();
        }
    }


    private void closeActivity() {
        Intent finishIntent = new Intent();
        JSONArray periods = new JSONArray();
        for(int i = 0; i < ic.user.schedule.periods.size(); i++) {
            ScheduleItem tempScheduleItem = ic.user.schedule.periods.get(i);
            JSONObject tempObject = new JSONObject();
            try {
                tempObject.put("className", tempScheduleItem.className);
                tempObject.put("teacherName", tempScheduleItem.teacherName);
                periods.put(tempObject);
            }
            catch(JSONException e) {
                e.printStackTrace();
            }

        }
        finishIntent.putExtra("schedule", periods.toString());
        setResult(RESULT_OK, finishIntent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
