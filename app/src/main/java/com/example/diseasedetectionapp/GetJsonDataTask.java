package com.example.diseasedetectionapp;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetJsonDataTask extends AsyncTask<String, Void, JSONObject> {
    private GetJsonDataCallback callback;
    private String errorMessage;

    public GetJsonDataTask(GetJsonDataCallback callback) {
        this.callback = callback;

    }
    @Override
    protected JSONObject doInBackground(String... strings) {
        String urlString = "https://blynk.cloud/external/api/getAll?token=INrAMUJL8_CP0pHfQa_fIcjfeUo92xOm";
        StringBuilder result = new StringBuilder();
        JSONObject jsonObject = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
               BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               String inputLine;
               while ((inputLine = in.readLine()) != null) {
                   result.append(inputLine);
               }
               in.close();
               jsonObject = new JSONObject(result.toString());
            } else {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }

            connection.disconnect();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if(jsonObject != null) {
            callback.onJsonDataReceived(jsonObject);
        } else {
            callback.onError(errorMessage != null ? errorMessage : "Failed to fetch JSON data.");
        }
    }
}
