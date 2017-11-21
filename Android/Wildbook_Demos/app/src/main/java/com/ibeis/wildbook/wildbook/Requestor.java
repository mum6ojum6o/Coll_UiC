package com.ibeis.wildbook.wildbook;
import android.os.StrictMode;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
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

/**
 * Created by Arjan on 11/16/2017.
 *
 * This application creates the http request and uploads the mutlipart/form-data to the database API!!
 */

public class Requestor {
    public static final String TAG="Requestor";
    private String mBoundary;
    private HttpURLConnection mHttpURLConnection;
    private String mCharset;
    private OutputStream mOutputStream;
    private PrintWriter mWriter;
    private JSONObject jsonResponse;
    public Requestor(String applUrl,String charset,String requestMethod){
        jsonResponse= new JSONObject();
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            this.mCharset=charset;
            mBoundary="==="+System.currentTimeMillis()+"===";
            URL url = new URL(applUrl);
            mHttpURLConnection = (HttpURLConnection) url.openConnection();
            mHttpURLConnection.setUseCaches(false);
            mHttpURLConnection.setDoOutput(true);
            mHttpURLConnection.setDoInput(true);
            mHttpURLConnection.setRequestMethod(requestMethod);
            mHttpURLConnection.setRequestProperty("Content-Type","multipart/form-data; boundary="+mBoundary);
            mHttpURLConnection.setRequestProperty("User-Agent","Wildbook.Agent");
            mOutputStream = mHttpURLConnection.getOutputStream();
            mWriter = new PrintWriter(new OutputStreamWriter(mOutputStream,charset),true);

        }catch(Exception e){
            e.printStackTrace();
            Log.i(TAG,"Something went wrong!!");
        }
    }
    public void addHeaderField(String name, String value) {
        mWriter.append(name + ": " + value).append("\r\n");
        mWriter.flush();
    }
    public void addFormField(String key,String value){
        mWriter.append("--"+mBoundary).append("\r\n");
        mWriter.append("Content-Disposition: form-data;name=\""+ key+"\"").append("\r\n");
        mWriter.append("Content-Type: text/plain; charset=\""+mCharset+"\"").append("\r\n");
        mWriter.append("\r\n");
        mWriter.append(value).append("\r\n");
        mWriter.flush();
    }
    public void addFile(String fieldname,String filename){
        File file =new File(filename);
        String absFileName=file.getName();
        mWriter.append("--"+mBoundary).append("\r\n");
        mWriter.append("Content-Disposition: form-data; name=\""+fieldname+"\"; filename=\""+absFileName+"\"").append("\r\n");
        mWriter.append("Content-Type: "+ URLConnection.guessContentTypeFromName(absFileName)).append("\r\n");
        mWriter.append("Content-Transfer-Encoding: binary").append("\r\n");
        mWriter.append("\r\n");
        mWriter.flush();
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer= new byte[4096];
            int bytesread=-1;
            while((bytesread=inputStream.read(buffer))!=-1){
                mOutputStream.write(buffer,0,bytesread);
            }
            mOutputStream.flush();
            inputStream.close();
            mWriter.append("\r\n");
            mWriter.flush();
        }catch (Exception e){
            Log.i(TAG,"methog: addFileError: Error in addFile!!");
            e.printStackTrace();
        }
    }
    public void finishRequesting() throws IOException,JSONException {
        mWriter.append("\r\n").flush();
        mWriter.append("--" + mBoundary + "--").append("\r\n");
        mWriter.close();

        // checks server's status code first
        int status = mHttpURLConnection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    mHttpURLConnection.getInputStream()));
            String line = null;
            JSONObject json;
            while ((line = reader.readLine()) != null) {
                Log.i(TAG, line);
                jsonResponse = new JSONObject(line);

                if (jsonResponse == null) {
                    Log.i(TAG,"Throwing Exception!!");
                    throw new JSONException("Server returned empty JSON response");
                }
            }
            reader.close();


        } else {
            Log.i(TAG,"Throwing Exception!! IOException");
            throw new IOException("Server returned non-OK status: " + status);
        }
        Log.i(TAG,"Disconnecting httpURLConnection!!");
        mHttpURLConnection.disconnect();

    }
public JSONObject getResponse(){
        return jsonResponse;
    }

}
