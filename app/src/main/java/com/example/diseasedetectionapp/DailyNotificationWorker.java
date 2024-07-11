package com.example.diseasedetectionapp;

import static com.example.diseasedetectionapp.MainActivity.KEY_IS_ONGOING;
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

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DailyNotificationWorker extends Worker {

    public DailyNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Fetch current AWD stage and phase from SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(NotificationHelper.CHANNEL_ID, Context.MODE_PRIVATE);
        boolean isOngoing = sharedPreferences.getBoolean(KEY_IS_ONGOING, false);

        if (!isOngoing) {
            // construct your notification message based on the current stage and phase
            String notificationTitle = "AWD Daily Update";
            String notificationMessage = "Today's AWD Action: " + getAction();

            NotificationHelper.createNotificationChannel(getApplicationContext());
            NotificationHelper.sendNotification(getApplicationContext(), notificationTitle, notificationMessage);

            // notify stage for NPK;
            if(isStillNPK()) {
                String npkMessage = "Today's NPK Action: " + getNPKAction();
                NotificationHelper.sendNotification(getApplicationContext(), "AWD NPK Daily Notification", npkMessage);
            }

        } else {
            System.out.println("Not working.");
        }

        return Result.success();
    }

    private String getNPKAction() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        // get starting date
        String startDate = sharedPreferences.getString(KEY_START_DATE, "");
        // compute number of days elapsed
        long diff = 0;
        try {
            diff = System.currentTimeMillis() - new SimpleDateFormat("MM/dd/yyyy").parse(startDate).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        int days = (int) diff / (24 * 60 * 60 * 1000);

        int vegStandingWater = sharedPreferences.getInt(KEY_VEGSTANDINGWATER, 0);
        int vegSafeAWD = sharedPreferences.getInt(KEY_VEGAWD, 0) + vegStandingWater;
        int nitrogenInterval = (int) (vegSafeAWD) / 3;

        if(days == 0) {
            return "Maglagay ng Nitrogen, Phosphorus, at Potassium";
        } else if ((days+1) > (vegSafeAWD)) {
            return "";
        } else if(((days+1) % nitrogenInterval) == 0) {
            return "Maglagay ng Nitrogen";
        } else if (((days+1) % nitrogenInterval) != 0) {
            return ("Maghintay ng " + String.valueOf( nitrogenInterval - ((days + 1) % nitrogenInterval)) + " days bago maglagay ng Nitrogen");
        }
        return "";
    }

    private boolean isStillNPK() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        // get starting date
        String startDate = sharedPreferences.getString(KEY_START_DATE, "");
        // compute number of days elapsed
        long diff = 0;
        try {
            diff = System.currentTimeMillis() - new SimpleDateFormat("MM/dd/yyyy").parse(startDate).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        int days = (int) (diff / (24 * 60 * 60 * 1000));

        int vegStandingWater = sharedPreferences.getInt(KEY_VEGSTANDINGWATER, 0);
        int vegSafeAWD = sharedPreferences.getInt(KEY_VEGAWD, 0) + vegStandingWater;

        return (days < vegSafeAWD);
    }

    private String getAction() {
        // Fetch current AWD stage and phase from SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean isOngoing = sharedPreferences.getBoolean(KEY_IS_ONGOING, false);
        System.out.println(isOngoing);
        if(isOngoing) {
            // get number of days between start date and current date
            String startDate = sharedPreferences.getString(KEY_START_DATE, "");
            String currentDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            long diff;
            try {
                diff = new SimpleDateFormat("MM/dd/yyyy").parse(currentDate).getTime() - new SimpleDateFormat("MM/dd/yyyy").parse(startDate).getTime();
            } catch (ParseException e) {
                diff = 0;
                e.printStackTrace();
            }
            int days = (int) (diff / (24 * 60 * 60 * 1000));
            // get notif strings
            String standingWater = "Panatilihing HIGH ang water status";
            String safeAWD = "(1) Patubigan hanggang maging HIGH ang water status. \n" +
                    "(2) Hayaan lang matuyo kung NORMAL ang water status. \n" +
                    "(3) Kapag umabot na ng LOW ang water status, kailangan nang magpatubig hanggang maabot uli ang HIGH na water status. \n" +
                    "\n" +
                    "Note: Iwasang umabot sa OVER ang water status dahil maaaring maabot ng tubig at masira ang device. Maiging tanggalin na ang device kung hindi mapipigilan ang pagtaas ng tubig.";
            String terminalDrainage = "Huwag na magpatubig at panatilihing tuyo lang ang palayan sa loob ng (15) days. \n" +
                    "\n" +
                    "Note: Iwasang umabot sa OVER ang water status dahil maaaring maabot ng tubig at masira ang device. Maiging tanggalin na ang device kung hindi mapipigilan ang pagtaas ng tubig. \n";


            // get current AWD stage based on the number of days
            int vegStandingWater = sharedPreferences.getInt(KEY_VEGSTANDINGWATER, 0);
            int vegSafeAWD = sharedPreferences.getInt(KEY_VEGAWD, 0) + vegStandingWater;
            int repStandingWater = sharedPreferences.getInt(KEY_REPSTANDINGWATER, 0) + vegSafeAWD;
            int repSafeAWD = sharedPreferences.getInt(KEY_REPASWD, 0) + repStandingWater;
            int repStandingWater1 = sharedPreferences.getInt(KEY_REPSTANDINGWATER1, 0) + repSafeAWD;
            int ripStandingWater = sharedPreferences.getInt(KEY_RIPSTANDINGWATER, 0) + repStandingWater1;
            int ripSafeAWD = sharedPreferences.getInt(KEY_RIPASWD, 0) + ripStandingWater;
            int ripTerminalDrainage = sharedPreferences.getInt(KEY_RIPTERMINALDRAINAGE, 0) + ripSafeAWD;
            days++;
            if (days < vegStandingWater) {
                return "Vegatative Growth Stage - STANDING WATER: " + standingWater;
            } else if (days < vegSafeAWD) {
                return "Vegatative Growth Stage - SAFE AWD: " + safeAWD;
            } else if (days < repStandingWater) {
                return "Reproductive Stage - STANDING WATER: " + standingWater;
            } else if (days< repSafeAWD) {
                return "Reproductive Stage - SAFE AWD: " + safeAWD;
            } else if (days< repStandingWater1) {
                return "Reproductive Stage - STANDING WATER : " + standingWater;
            } else if (days< ripStandingWater) {
                return "Ripening Stage Stage - STANDING WATER: " + standingWater;
            } else if (days< ripSafeAWD) {
                return "Ripening Stage - SAFE AWD: " + safeAWD;
            } else if (days < ripTerminalDrainage) {
                return "Ripening Stage - TERMINAL DRAINAGE: " + terminalDrainage;
            }
            return "Harvesting Stage";
        } else {
            return "Hindi pa nagsisimula ang pagtatanim ng punla";
        }
    }

    public static void scheduleDailyNotifications(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelAllWorkByTag("daily_awd_notifications");
        long initialDelay = calculateInitialDelay();
        // print the delay in hours
        PeriodicWorkRequest dailyNotificationRequest = new PeriodicWorkRequest.Builder(DailyNotificationWorker.class, 1, TimeUnit.DAYS)
                .addTag("daily_awd_notifications")
//                .setInitialDelay(initialDelay, TimeUnit.SECONDS)
                .build();

        workManager.enqueue(dailyNotificationRequest);
    }

    private static long calculateInitialDelay() {
        Calendar calendar = Calendar.getInstance();

        Calendar nextSixAM = (Calendar) calendar.clone();
        nextSixAM.set(Calendar.HOUR_OF_DAY, 6);
        nextSixAM.set(Calendar.MINUTE, 0);
        nextSixAM.set(Calendar.SECOND, 0);
        nextSixAM.set(Calendar.MILLISECOND, 0);

        if (calendar.after(nextSixAM)) {
            nextSixAM.add(Calendar.DAY_OF_YEAR, 1);
        }

        return nextSixAM.getTimeInMillis() - calendar.getTimeInMillis();
    }
}
