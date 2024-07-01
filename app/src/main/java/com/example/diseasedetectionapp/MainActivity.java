package com.example.diseasedetectionapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.ActivityViewModelLazyKt;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.diseasedetectionapp.databinding.ActivityMainBinding;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREF_NAME = "DD_APP";
    ActivityMainBinding binding;
    SharedPreferences sharedPreferences;

    static final String KEY_API_RESULT = "apiResult";
    static final String KEY_IS_ONGOING = "isOngoing";
    static final String KEY_VEGSTANDINGWATER = "vegStandingWater";
    static final String KEY_VEGAWD = "vegSafeAWD";
    static final String KEY_REPSTANDINGWATER = "repStandingWater";
    static final String KEY_REPASWD = "repSafeAWD";
    static final String KEY_REPSTANDINGWATER1 = "repStandingWater1";
    static final String KEY_RIPSTANDINGWATER = "ripStandingWater";
    static final String KEY_RIPASWD = "ripSafeAWD";
    static final String KEY_RIPTERMINALDRAINAGE = "ripTerminalDrainage";
    static final String KEY_START_DATE = "startingDate";
    static final String KEY_PROFILES = "profileKey";
    static final String KEY_ACTIVE_PROFILE = "activeProfile";




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        boolean isOngoing = sharedPreferences.getBoolean(KEY_IS_ONGOING, false);
        String api_results = sharedPreferences.getString(KEY_API_RESULT, "");
        System.out.println(api_results);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.data) {
                selectedFragment = new DataFragment();
            } else if (itemId == R.id.set_values) {
                if(isOngoing)  selectedFragment = new SetFragment(); // change layout
                else selectedFragment = new SetFragment();
            } else if (itemId ==  R.id.detect) {
                selectedFragment = new DetectFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();
            }
            return true;
        });
    }
}