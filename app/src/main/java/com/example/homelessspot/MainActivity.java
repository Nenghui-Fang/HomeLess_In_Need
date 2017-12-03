package com.example.homelessspot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonRoport, buttonMap, buttonHelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        buttonRoport = (Button) findViewById(R.id.button_report);
        buttonMap = (Button) findViewById(R.id.button_map);
        buttonHelp = buttonMap = (Button) findViewById(R.id.button_help);

        buttonRoport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
                //SendDataToServer(GetUsername, GetPassword);
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                //SendDataToServer(GetUsername, GetPassword);
            }
        });

        buttonHelp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
                //SendDataToServer(GetUsername, GetPassword);
            }
        });
    }
}
