package me.joshlabue.oxygen;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressBar nameBar = findViewById(R.id.gettingNameProgress);
                final TextView nameText = findViewById(R.id.textName);
                nameBar.setVisibility(View.VISIBLE);
                nameText.setVisibility(View.VISIBLE);
                nameText.setText("Loading user data...");

                Toast.makeText(view.getContext(), "Signing in...", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(dispView.getContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
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
                                            Toast.makeText(dispView.getContext(), "Signed in as " + ic.user.firstName + " " + ic.user.lastName, Toast.LENGTH_LONG).show();
                                            nameText.setText("Hello " + ic.user.firstName);
                                            nameBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading_complete));
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
