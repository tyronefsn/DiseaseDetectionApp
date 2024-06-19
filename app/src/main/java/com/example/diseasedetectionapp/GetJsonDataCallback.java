package com.example.diseasedetectionapp;
import org.json.JSONObject;
public interface GetJsonDataCallback {
    void onJsonDataReceived(JSONObject jsonObject);
    void onError(String error);
}
