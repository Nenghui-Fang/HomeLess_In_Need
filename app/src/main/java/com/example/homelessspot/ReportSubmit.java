package com.example.homelessspot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nenghui on 11/30/17.
 */

public class ReportSubmit extends AppCompatActivity {

    ImageView Image;
    Button ButtonUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reportsubmit);

        Image = (ImageView) findViewById(R.id.imageView);

        ButtonUpload = (Button)findViewById(R.id.button);

        Intent intent = getIntent();
        final String picUrl = intent.getExtras().getString("PicUrl");

        Log.d("Re URL", picUrl);
        Image.setImageBitmap(BitmapFactory.decodeFile(picUrl));

        ButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new uploadFileToServerTask().execute(picUrl);
//                MultipartUtility multipart = new MultipartUtility(picUrl, charset);
//
//                // In your case you are not adding form data so ignore this
//                /*This is to add parameter values */
////                for (int i = 0; i < myFormDataArray.size(); i++) {
////                    multipart.addFormField(myFormDataArray.get(i).getParamName(),
////                            myFormDataArray.get(i).getParamValue());
////                }
//
//
////add your file here.
//                /*This is to add file content*/
//                for (int i = 0; i < myFileArray.size(); i++) {
//                    multipart.addFilePart(myFileArray.getParamName(),
//                            new File(myFileArray.getFileName()));
//                }
//
//                List<String> response = multipart.finish();
//                Debug.e(TAG, "SERVER REPLIED:");
//                for (String line : response) {
//                    Debug.e(TAG, "Upload Files Response:::" + line);
//// get your server response here.
//                    responseString = line;
//                }




            }
        });


    }


    public class MultipartUtility {
        private final String boundary;
        private static final String LINE_FEED = "\r\n";
        private HttpURLConnection httpConn;
        private String charset;
        private OutputStream outputStream;
        private PrintWriter writer;

        /**
         * This constructor initializes a new HTTP POST request with content type
         * is set to multipart/form-data
         *
         * @param requestURL
         * @param charset
         * @throws IOException
         */
        public MultipartUtility(String requestURL, String charset)
                throws IOException {
            this.charset = charset;

            // creates a unique boundary based on time stamp
            boundary = "===" + System.currentTimeMillis() + "===";
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);    // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                    true);
        }

        /**
         * Adds a form field to the request
         *
         * @param name  field name
         * @param value field value
         */
        public void addFormField(String name, String value) {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=" + charset).append(
                    LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.flush();
        }

        /**
         * Adds a upload file section to the request
         *
         * @param fieldName  name attribute in <input type="file" name="..." />
         * @param uploadFile a File to be uploaded
         * @throws IOException
         */
        public void addFilePart(String fieldName, File uploadFile)
                throws IOException {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + fieldName
                            + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append(
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            writer.append(LINE_FEED);
            writer.flush();
        }

        /**
         * Adds a header field to the request.
         *
         * @param name  - name of the header field
         * @param value - value of the header field
         */
        public void addHeaderField(String name, String value) {
            writer.append(name + ": " + value).append(LINE_FEED);
            writer.flush();
        }

        /**
         * Completes the request and receives response from the server.
         *
         * @return a list of Strings as response in case the server returned
         * status OK, otherwise an exception is thrown.
         * @throws IOException
         */
        public List<String> finish() throws IOException {
            List<String> response = new ArrayList<String>();
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    response.add(line);
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }
            return response;
        }
    }

    private class uploadFileToServerTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... args) {
            try {
                String  attachmentName = "temp";
                String  attachmentFileName = "temp.jpg";
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                @SuppressWarnings("PointlessArithmeticExpression")
                int maxBufferSize = 1 * 1024 * 1024;


                java.net.URL url = new URL("http://52.206.200.215:3000/api/recognize/1");
                Log.d("URl_add:", "url " + url);
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
//                    outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + filename + "\"" + lineEnd);
//                    outputStream.writeBytes(" name=\"file\";filename=\"" + filename + "\"" + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                            attachmentName + "\";filename=\"" +
                            filename + "\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    Log.d("FilePath:", "filename " + filename);

                    fileInputStream = new FileInputStream(filename);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    buffer = new byte[bufferSize];

                    // Read file
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    Log.d("FileSize:", "filesize " + bytesRead);
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

//    private class uploadFileToServerTask extends AsyncTask<String, String, Object> {
//        @Override
//        protected String doInBackground(String... args) {
//            try {
//                String lineEnd = "\r\n";
//                String twoHyphens = "--";
////              /**/  String boundary = "*****";
//                String boundary = "----";
//                int bytesRead, bytesAvailable, bufferSize;
//                byte[] buffer;
//                @SuppressWarnings("PointlessArithmeticExpression")
//                int maxBufferSize = 1 * 1024 * 1024;
//
//
//                java.net.URL url = new URL("http://52.206.200.215:3000/api/recognize/1");
//                Log.d("URL", "url " + url);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//                // Allow Inputs &amp; Outputs.
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//                connection.setUseCaches(false);
//
//                // Set HTTP method to POST.
//                connection.setRequestMethod("POST");
//
//                connection.setRequestProperty("Connection", "keep-alive");
//                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
////                connection.setRequestProperty("Content-Type", "multipart/form-data" );
//
//                FileInputStream fileInputStream;
//                DataOutputStream outputStream;
//                {
//                    outputStream = new DataOutputStream(connection.getOutputStream());
//
//                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//                    outputStream.writeBytes(boundary );
//                    String filename = args[0];
//                    outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + filename + "\"" + lineEnd);
//                    outputStream.writeBytes(lineEnd);
//                    Log.d("FileName", "filename " + filename);
//
//                    fileInputStream = new FileInputStream(filename);
//
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//
//                    buffer = new byte[bufferSize];
//
//                    // Read file
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                    while (bytesRead > 0) {
//                        outputStream.write(buffer, 0, bufferSize);
//                        bytesAvailable = fileInputStream.available();
//                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//                    }
//                    outputStream.writeBytes(lineEnd);
//                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//                }
//
//                int serverResponseCode = connection.getResponseCode();
//                String serverResponseMessage = connection.getResponseMessage();
//                Log.d("serverResponseCode", "" + serverResponseCode);
//                Log.d("serverResponseMessage", "" + serverResponseMessage);
//
//                fileInputStream.close();
//                outputStream.flush();
//                outputStream.close();
//
//                if (serverResponseCode == 200) {
//                    return "true";
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return "false";
//        }
//
//        @Override
//        protected void onPostExecute(Object result) {
//
//        }
//    }

}