package com.example.homelessspot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by nenghui on 11/30/17.
 */

public class ReportSubmit extends AppCompatActivity {

    ImageView Image;
    Button ButtonUpload;
    EditText InputText;
    String inputString;
    String reportPostUrl = "http://52.206.200.215:3000/api/v1/report";

    JSONObject JSObject= new JSONObject();;
    int reporter_id, kind_id;
    String location, description;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportsubmit);

        Bundle bundle=getIntent().getExtras();
        this.setTitle("Welcome! " + bundle.getString("name"));


        InputText = (EditText)findViewById(R.id.editText);
        Image = (ImageView) findViewById(R.id.imageView);
        ButtonUpload = (Button)findViewById(R.id.button);




//        Intent intent = getIntent();
//        final String picUrl = intent.getExtras().getString("PicUrl");
//
//        Log.d("Re URL", picUrl);
//        Image.setImageBitmap(BitmapFactory.decodeFile(picUrl));

        ButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputString = InputText.getText().toString();
                Log.d("Input:", inputString);

                try {
                    JSObject.put("reporter_id", new Integer(1));
                    JSObject.put("report_kind", new Integer(1));
                    JSObject.put("location", "Boston");
                    JSObject.put("description", inputString);

                }catch (JSONException e){
                    e.printStackTrace();
                }


               String json_str = JSObject.toString();
                new httpPost().execute(reportPostUrl,json_str);
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

    public class httpPost extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String urlString = params[0]; // URL to call

            String data = params[1]; //data to post

            OutputStream out = null;
            try {

                URL url = new URL(urlString);

                 urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();
                urlConnection.connect();

                int responseCode=urlConnection.getResponseCode();

                if (responseCode == HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            urlConnection.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
        }
        return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.equals(null)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Waiting for Server response... ", Toast.LENGTH_SHORT);
                    toast.show();

                    try {
                        int reportID;
                        JSONObject json = new JSONObject(result);

                        reportID = json.getInt("@reportId");
                        Log.d("PostExecute", ""+ reportID);


                            Intent intent = new Intent(ReportSubmit.this, MainActivity.class);
//                            intent.putExtra("reportKindname", reportKindname);
                            intent.putExtra("name", "test");
                            startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
        }
    }


//    public class ImageUploader {
//
//        public static final String HTTP_LOCALHOST_8081_FILE_UPLOAD = "http://192.168.1.104:8081/file_upload";
//
//        public void upload(InputStream inputStream, String extension) {
//            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//            RequestParams requestParams = prepareRequestParams(inputStream, extension);
//
//            asyncHttpClient.post(HTTP_LOCALHOST_8081_FILE_UPLOAD, requestParams, new AsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, PreferenceActivity.Header[] headers, byte[] responseBody) {
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                    error.printStackTrace();
//                }
//            });
//
//        }
//
//        private RequestParams prepareRequestParams(InputStream inputStream, String extension) {
//            RequestParams requestParams = new RequestParams();
//            requestParams.put("image", inputStream, "image." + extension, "image/jpeg");
//            return requestParams;
//        }}


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
            if (status == HTTP_OK) {
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


                java.net.URL url = new URL("http://52.206.200.215:3000/api/v1/report");
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
