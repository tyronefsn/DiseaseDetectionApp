package com.example.diseasedetectionapp;

import static com.example.diseasedetectionapp.MainActivity.KEY_API_RESULT;
import static com.example.diseasedetectionapp.MainActivity.SHARED_PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class APICallWorker extends Worker {
    public APICallWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
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
                notifyAndSaveLocally(jsonObject);
                connection.disconnect();

                return Result.success();
            } else {
                connection.disconnect();
                return Result.retry();
            }

        } catch(Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private void notifyAndSaveLocally(JSONObject jsonObject) {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        // get currentDay in MMM DD, YYYY
        String currentDay = new SimpleDateFormat("MMM dd, yyyy").format(new Date());
        // get currenttime in HH:MM:SS
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        // check if there are existing api results
        String apiResult = sharedPreferences.getString(KEY_API_RESULT, "");
        // append new api results with current time to existing api results
        String toAppend = currentDay +";"+currentTime+";"+jsonObject.toString()+"\n"+apiResult;
        // save to shared preferences
        sharedPreferences.edit().putString(KEY_API_RESULT, toAppend).apply();

    }
    public static void scheduleAPICallWorker(Context context){
        PeriodicWorkRequest apiCallRequest = new PeriodicWorkRequest.Builder(APICallWorker.class, 15, TimeUnit.MINUTES)
                .addTag("api_call")
                .build();
        WorkManager.getInstance(context).enqueue(apiCallRequest);
    }
}
