package com.test.omdbviewer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetJsonTask extends AsyncTask<String, Void, String> {

    private ProcessCompletedListener processCompletedListener;
    private ProcessFailedListener processFailedListener;

    //Listeners must be added to set the process when the task will be completed
    GetJsonTask(ProcessCompletedListener processCompletedListener, ProcessFailedListener processFailedListener) {
        this.processCompletedListener = processCompletedListener;
        this.processFailedListener = processFailedListener;
    }

    //Interface when the process will be completed without errors
    public interface ProcessCompletedListener {
        void onProcessCompleted(String jsonData);
    }

    //Interface when the process will be completed with errors
    public interface ProcessFailedListener {
        void onProcessFailed();
    }

    @Override
    protected String doInBackground(String... urls) {
        //Get the json file as a string in the background
        //The url is one in this example, so take the first url of the array
        return getJSON(urls[0]);
    }

    @Override
    protected void onPostExecute(String jsonString) {
        super.onPostExecute(jsonString);
        //If a string is returned, the process have been completed
        if (jsonString != null) {
            processCompletedListener.onProcessCompleted(jsonString);
        } else {
            processFailedListener.onProcessFailed();
        }
    }

    private String getJSON(String url) {
        HttpsURLConnection connection = null;
        try {
            //Open a new connection with the apiUrl
            URL u = new URL(url);
            connection = (HttpsURLConnection) u.openConnection();
            connection.connect();

            //Get the json from the url and convert it to string
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            return sb.toString();

        } catch (IOException e) {
            Log.e("Error:", "Open connection failed!");
        } finally {
            //Close the connection when the process is completed
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    Log.e("Error:", "Connection not disconnected!");
                    processFailedListener.onProcessFailed();
                }
            }
        }
        return null;
    }
}
