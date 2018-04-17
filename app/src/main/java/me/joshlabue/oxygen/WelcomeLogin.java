package me.joshlabue.oxygen;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;

import me.joshlabue.ictest.Campus;
import me.joshlabue.ictest.ICLoginException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WelcomeLogin extends AppCompatActivity {

    Campus ic = new Campus("https://infinitecampus.penfield.edu/campus", "penfield");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome_login);

        final ConstraintLayout statusName = findViewById(R.id.statusName);
        final ConstraintLayout statusData = findViewById(R.id.statusData);

        statusName.setVisibility(View.INVISIBLE);
        statusData.setVisibility(View.INVISIBLE);


        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                statusName.setVisibility(View.VISIBLE);
                final ProgressBar nameBar = findViewById(R.id.gettingNameProgress);
                final TextView nameText = findViewById(R.id.textName);
                final ImageView nameDone = findViewById(R.id.nameDone);
                nameBar.setVisibility(View.VISIBLE);
                nameText.setVisibility(View.VISIBLE);
                nameText.setText("Loading user data...");
                nameDone.setVisibility(View.INVISIBLE);
                nameDone.setImageResource(R.drawable.loading_complete);

                final ProgressBar dataBar = findViewById(R.id.gettingDataProgress);
                final TextView dataText = findViewById(R.id.textData);
                final ImageView dataDone = findViewById(R.id.dataDone);
                dataBar.setVisibility(View.VISIBLE);
                dataText.setVisibility(View.VISIBLE);
                dataDone.setVisibility(View.VISIBLE);

                EditText usernameField = findViewById(R.id.usernameField);
                EditText passwordField = findViewById(R.id.passwordField);
                //ic.login(usernameField.getText().toString(), passwordField.getText().toString());


                ic.setCredentials(usernameField.getText().toString(), passwordField.getText().toString());

                ic.context.appName = "penfield";
                final View dispView = view;


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
                            ic.prepareUserData();

                            ic.context.client.newCall(ic.userData.request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response dataResponse) throws IOException {
                                    ic.userData.feedData(dataResponse);

                                    ic.initUser(ic.userData.body);

                                    WelcomeLogin.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            nameText.setText("Hello " + ic.user.firstName);
                                            statusData.setVisibility(View.VISIBLE);
                                            nameBar.setVisibility(View.INVISIBLE);
                                            nameDone.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
