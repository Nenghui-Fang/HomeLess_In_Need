package com.example.homelessspot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by User on 2017/11/18.
 */

public class SignupActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    private TextView signintext;
    String GetUsername, GetPassword;

    Button buttonSubmit;

    String DataParseUrl = "http://chakusoza.com/devs/ucu/signup.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        editTextUsername = (EditText) findViewById(R.id.TextUsername);
        editTextPassword = (EditText) findViewById(R.id.TextPassword);

        buttonSubmit = (Button) findViewById(R.id.buttonLogin);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                GetUsername = editTextUsername.getText().toString();
                GetPassword = editTextPassword.getText().toString();

                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                //SendDataToServer(GetUsername, GetPassword);

            }
        });

        signintext = (TextView) findViewById(R.id.SignupText);

        signintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}