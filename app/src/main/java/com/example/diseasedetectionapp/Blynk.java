package com.example.diseasedetectionapp;

import android.os.AsyncTask;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;

public class Blynk {
    private int waterLevel;
    private int nLevel;
    private int pLevel;
    private int kLevel;

    public Blynk(int waterLevel, int nLevel, int pLevel, int kLevel) {
        this.waterLevel = waterLevel;
        this.nLevel = nLevel;
        this.pLevel = pLevel;
        this.kLevel = kLevel;
    }

    public Blynk() {
        this.waterLevel = 0;
        this.nLevel = 0;
        this.pLevel = 0;
        this.kLevel = 0;
    }

}

