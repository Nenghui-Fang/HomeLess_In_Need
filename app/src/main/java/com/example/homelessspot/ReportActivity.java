package com.example.homelessspot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by User on 2017/11/28.
 */

public class ReportActivity extends AppCompatActivity {

//
//    private static final int REQUEST_CAMERA = 0;
//    String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
//    requestPermissions(permissions, RC_PERMISSIONS);

    String name;
    String[] reportKindname;
    ImageView Image_1, Image_2, Image_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Bundle bundle=getIntent().getExtras();
        name = bundle.getString("name");
        reportKindname = bundle.getStringArray("reportKindname");
        for(int i =0 ; i< reportKindname.length; i++){
            Log.d("reportKindname",reportKindname[i]);
        }

        this.setTitle("Welcome! " + name);


        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

        Image_1 = (ImageView) findViewById(R.id.imageView2);

        Image_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, ReportSubmit.class);
                intent.putExtra("name", name);
                startActivity(intent);
               // dispatchTakePictureIntent();
            }
        });

        Image_2 = (ImageView) findViewById(R.id.imageView3);

        Image_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, ReportSubmit.class);
                intent.putExtra("name", name);
                startActivity(intent);
//                dispatchTakePictureIntent();
            }
        });

        Image_3 = (ImageView) findViewById(R.id.imageView6);

        Image_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, ReportSubmit.class);
                intent.putExtra("name", name);
                startActivity(intent);
//                dispatchTakePictureIntent();
            }
        });

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == permsRequestCode) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQUEST_CAMERA);
//            }
//        }
//    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_IMAGE_CAPTURE)
        {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),"temp.jpg");
            FileOutputStream fo;
            Log.e("Picture Taken", "onActivityResult: Picture Taken" + destination);
            try {
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent picDisplayIntent = new Intent(this, ReportSubmit.class);
            picDisplayIntent.putExtra("PicUrl", destination.getAbsolutePath());
            startActivity(picDisplayIntent);


//             new uploadFileToServerTask().execute(destination.getAbsolutePath());

        }
    }


    private class uploadFileToServerTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... args) {
            try {
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                @SuppressWarnings("PointlessArithmeticExpression")
                int maxBufferSize = 1 * 1024 * 1024;


                java.net.URL url = new URL("http://52.206.200.215:3000/api/recognize/1");
                Log.d("URL", "url " + url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Allow Inputs &amp; Outputs.
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                // Set HTTP method to POST.
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                FileInputStream fileInputStream;
                DataOutputStream outputStream;
                {
                    outputStream = new DataOutputStream(connection.getOutputStream());

                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    String filename = args[0];
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + filename + "\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    Log.d("FileName", "filename " + filename);

                    fileInputStream = new FileInputStream(filename);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    buffer = new byte[bufferSize];

                    // Read file
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                }

                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();
                Log.d("serverResponseCode", "" + serverResponseCode);
                Log.d("serverResponseMessage", "" + serverResponseMessage);

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                if (serverResponseCode == 200) {
                    return "true";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "false";
        }

        @Override
        protected void onPostExecute(Object result) {

        }
    }

}
