package com.example.homelessspot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ProgressDialog pd;
    String name;
    String reportkindUrl = "http://52.206.200.215:3000/api/v1/reportkind";
    ImageView buttonRoport, buttonMap, buttonHelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle=getIntent().getExtras();
        name = bundle.getString("name");
        this.setTitle("Welcome " + name +"!");


        buttonRoport = (ImageView) findViewById(R.id.imageView4);
        buttonMap = (ImageView) findViewById(R.id.map);
        buttonHelp = (ImageView) findViewById(R.id.help);

        buttonRoport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new JsonTask().execute(reportkindUrl);
//                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
//                intent.putExtra("name", name);
//                startActivity(intent);

                //SendDataToServer(GetUsername, GetPassword);
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//                startActivity(intent);

            }
        });

        buttonHelp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
//                startActivity(intent);

            }
        });
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        final String UserNotFound = "User not found";

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int status = connection.getResponseCode();
                Log.d("ResponseCode",""+status);

                if (status == 200) {
                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                        Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                    }
                    return buffer.toString();
                }
                if (status ==404)
                {
                    String str=UserNotFound;
                    return str;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (pd.isShowing()){
                pd.dismiss();
            }
            if(!result.equals(null)) {
                if (result.equals(UserNotFound)) {
                    Log.d("UserNotFound", result);
//                    Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
//                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Displaying Available Report Kind... ", Toast.LENGTH_SHORT);
                    toast.show();

                    try {

                        JSONArray jsonarray = new JSONArray(result);
                        String[] reportKindname = new String[jsonarray.length()] ;
                        for(int i=0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            reportKindname[i]  = jsonobject.getString("name");
                            Log.d("reportKindname", reportKindname[i]);

                            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                            intent.putExtra("reportKindname", reportKindname);
                            intent.putExtra("name", name);
                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
    }
}
