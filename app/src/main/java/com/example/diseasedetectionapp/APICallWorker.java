package com.example.diseasedetectionapp;

import static com.example.diseasedetectionapp.MainActivity.KEY_API_RESULT;
import static com.example.diseasedetectionapp.MainActivity.KEY_REPASWD;
import static com.example.diseasedetectionapp.MainActivity.KEY_REPSTANDINGWATER;
import static com.example.diseasedetectionapp.MainActivity.KEY_REPSTANDINGWATER1;
import static com.example.diseasedetectionapp.MainActivity.KEY_RIPASWD;
import static com.example.diseasedetectionapp.MainActivity.KEY_RIPSTANDINGWATER;
import static com.example.diseasedetectionapp.MainActivity.KEY_RIPTERMINALDRAINAGE;
import static com.example.diseasedetectionapp.MainActivity.KEY_START_DATE;
import static com.example.diseasedetectionapp.MainActivity.KEY_VEGAWD;
import static com.example.diseasedetectionapp.MainActivity.KEY_VEGSTANDINGWATER;
import static com.example.diseasedetectionapp.MainActivity.SHARED_PREF_NAME;

import android.app.Notification;
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
import java.text.ParseException;
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
                return Result.failure();
            }

        } catch(Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    private void notifyAndSaveLocally(JSONObject jsonObject) {

        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String startDate = sharedPreferences.getString(KEY_START_DATE, "");

        // compute number of days elapsed
        long diff = 0;
        try {
            diff = System.currentTimeMillis() - new SimpleDateFormat("MM/dd/yyyy").parse(startDate).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        int days = (int) diff / (24 * 60 * 60 * 1000);

        if(days < 0) {
            return;
        }
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

        // notify if the water level is not according to the stage and phase
        // get starting date




        // parse JSON object
        try {
            String waterLevel = jsonObject.getString("v1");
            String waterStatus = jsonObject.getString("v2");

            NotificationHelper.createNotificationChannel(getApplicationContext());
            if (waterStatus.equals("OVER")) {
                NotificationHelper.sendNotification(getApplicationContext(), "Water Level Warning", "Water level is over. Maaaring may pagbaha. Tanggalin muna ang device upang hindi ito mapinsala.");
            } else {
                // get what phase the AWD is in
                String stage = "";

                int vegStandingWater = sharedPreferences.getInt(KEY_VEGSTANDINGWATER, 0);
                int vegSafeAWD = sharedPreferences.getInt(KEY_VEGAWD, 0) + vegStandingWater;
                int repStandingWater = sharedPreferences.getInt(KEY_REPSTANDINGWATER, 0) + vegSafeAWD;
                int repSafeAWD = sharedPreferences.getInt(KEY_REPASWD, 0) + repStandingWater;
                int repStandingWater1 = sharedPreferences.getInt(KEY_REPSTANDINGWATER1, 0) + repSafeAWD;
                int ripStandingWater = sharedPreferences.getInt(KEY_RIPSTANDINGWATER, 0) + repStandingWater1;
                int ripSafeAWD = sharedPreferences.getInt(KEY_RIPASWD, 0) + ripStandingWater;
                int ripTerminalDrainage = sharedPreferences.getInt(KEY_RIPTERMINALDRAINAGE, 0) + ripSafeAWD;


                if (days < vegStandingWater) {
                    stage = "STANDING";
                } else if (days < vegSafeAWD) {
                    stage = "SAFE";
                } else if (days < repStandingWater) {
                    stage = "STANDING";
                } else if (days< repSafeAWD) {
                    stage = "SAFE";
                } else if (days< repStandingWater1) {
                    stage = "STANDING";
                } else if (days< ripStandingWater) {
                    stage = "STANDING";
                } else if (days< ripSafeAWD) {
                    stage = "SAFE";
                } else if (days < ripTerminalDrainage) {
                    stage = "TERMINAL";
                }

                switch (stage) {
                    case "STANDING":
                        if (waterStatus.equals("NORMAL") || waterStatus.equals("LOW")) {
                            NotificationHelper.sendNotification(getApplicationContext(), "Water Level Warning", "Water level is normal or low. Please add water until water status is high.");
                        }
                        break;
                    case "SAFE":
                        if(waterStatus.equals("HIGH")) {
                            NotificationHelper.sendNotification(getApplicationContext(), "Water Status Notification", "Water status is high. Please maintain water level.");
                        } else if (waterStatus.equals("LOW")){
                            NotificationHelper.sendNotification(getApplicationContext(), "Water Level Warning", "Water level is low. Please add water.");
                        }
                        break;
                    case "TERMINAL":
                        // check recent water status from shared preferences
                        String recentWaterStatus = sharedPreferences.getString(KEY_API_RESULT, "");
                        JSONObject json = new JSONObject((((recentWaterStatus.split("\n"))[0]).split(";"))[2]);
                        String prevWaterLevel = json.getString("v1");

                        if(Integer.parseInt(waterLevel) > Integer.parseInt(prevWaterLevel))  {
                            NotificationHelper.sendNotification(getApplicationContext(), "Water Level Warning", "Huwag na magdagdag ng tubig. Hayaan bumaba ang water level.");
                        }
                        break;
                    default:
                        NotificationHelper.sendNotification(getApplicationContext(), "Water Level Warning", "Hindi makuha ng app ang water level.");

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    public static void scheduleAPICallWorker(Context context){
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelAllWorkByTag("api_call");
        PeriodicWorkRequest apiCallRequest = new PeriodicWorkRequest.Builder(APICallWorker.class, 900000, TimeUnit.MILLISECONDS)
                .addTag("api_call")
                .build();
        WorkManager.getInstance(context).enqueue(apiCallRequest);




    }
}
