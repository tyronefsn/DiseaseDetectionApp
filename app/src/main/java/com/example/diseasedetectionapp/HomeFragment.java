package com.example.diseasedetectionapp;

import static com.example.diseasedetectionapp.MainActivity.KEY_ACTIVE_PROFILE;
import static com.example.diseasedetectionapp.MainActivity.KEY_IS_ONGOING;
import static com.example.diseasedetectionapp.MainActivity.KEY_PROFILES;
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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    TextView numDays;
    TextView stageText;
    TextView phaseText;
    TextView waterStatus;
    TextView soilStatus;
    ImageView appIcon;
    WebView webView;
    NavigationView navigationView;
    WebChromeClient.CustomViewCallback customViewCallback;
    List<String> profiles = new ArrayList<>();
    DrawerLayout drawerLayout;
    SharedPreferences sharedPreferences;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        numDays = view.findViewById(R.id.numDays);
        stageText = view.findViewById(R.id.stageText);
        phaseText = view.findViewById(R.id.phaseText);
        waterStatus = view.findViewById(R.id.waterStatus);
        soilStatus = view.findViewById(R.id.soilStatus);
        navigationView = view.findViewById(R.id.navigation_view);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        appIcon = view.findViewById(R.id.appIcon);
        webView = view.findViewById(R.id.video);
        sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String activeProfile = sharedPreferences.getString(KEY_ACTIVE_PROFILE, "");
        String videoUrl = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/eH-0roQEXA0?si=7EsyJlZuDA-p4Skj\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
        webView.loadData(videoUrl, "text/html", "UTF-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            View fullscreen = null;
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
               webView.setVisibility(View.GONE);

               if (fullscreen != null) {
                   ((FrameLayout)getActivity().getWindow().getDecorView()).removeView(fullscreen);
               }
               fullscreen = view;
               ((FrameLayout)getActivity().getWindow().getDecorView()).addView(fullscreen, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
               fullscreen.setVisibility(View.VISIBLE);
            }

            @Override
            public void onHideCustomView() {
                fullscreen.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        if(activeProfile.isEmpty()) {
            sharedPreferences.edit().putString(KEY_ACTIVE_PROFILE, "default_profile").apply();
        }
        loadProfiles();
        populateProfiles();

        boolean isOngoing = sharedPreferences.getBoolean(KEY_IS_ONGOING, false);
        appIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                    // Handle default profile click
                    sharedPreferences.edit().putString(KEY_ACTIVE_PROFILE, "default_profile").apply();
                    HomeFragment newHomeFragment = new HomeFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, newHomeFragment).commit();
                } else if (id == profiles.size()) {
                    // Create new intent for adding profile
                    AddPrototypeFragment addPrototypeFragment = new AddPrototypeFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, addPrototypeFragment).commit();
                } else {
                    sharedPreferences.edit().putString(KEY_ACTIVE_PROFILE, "mock_profile").apply();
                    HomeFragment newHomeFragment = new HomeFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, newHomeFragment).commit();
                }
                // close drawer
                drawerLayout.closeDrawer(GravityCompat.END);

                return true;
            }
        });
        if(!isOngoing || !activeProfile.equals("default_profile")) {
            numDays.setText("");
            stageText.setText("");
            phaseText.setText("");
            waterStatus.setText("");
            soilStatus.setText("");
        } else {
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
            System.out.println(days);
            int vegStandingWater = sharedPreferences.getInt(KEY_VEGSTANDINGWATER, 0);
            int vegSafeAWD = sharedPreferences.getInt(KEY_VEGAWD, 0) + vegStandingWater;
            int repStandingWater = sharedPreferences.getInt(KEY_REPSTANDINGWATER, 0) + vegSafeAWD;
            int repSafeAWD = sharedPreferences.getInt(KEY_REPASWD, 0) + repStandingWater;
            int repStandingWater1 = sharedPreferences.getInt(KEY_REPSTANDINGWATER1, 0) + repSafeAWD;
            int ripStandingWater = sharedPreferences.getInt(KEY_RIPSTANDINGWATER, 0) + repStandingWater1;
            int ripSafeAWD = sharedPreferences.getInt(KEY_RIPASWD, 0) + ripStandingWater;
            int ripTerminalDrainage = sharedPreferences.getInt(KEY_RIPTERMINALDRAINAGE, 0) + ripSafeAWD;
            System.out.println(ripTerminalDrainage);
            String standingWater = "Panatilihing HIGH ang water status";
            String safeAWD = "(1) Patubigan hanggang maging HIGH ang water status. \n" +
                    "(2) Hayaan lang matuyo kung NORMAL ang water status. \n" +
                    "(3) Kapag umabot na ng LOW ang water status, kailangan nang magpatubig hanggang maabot uli ang HIGH na water status. \n" +
                    "\n" +
                    "Note: Iwasang umabot sa OVER ang water status dahil maaaring maabot ng tubig at masira ang device. Maiging tanggalin na ang device kung hindi mapipigilan ang pagtaas ng tubig.";
            String terminalDrainage = "Huwag na magpatubig at panatilihing tuyo lang ang palayan sa loob ng (15) days. \n" +
                    "\n" +
                    "Note: Iwasang umabot sa OVER ang water status dahil maaaring maabot ng tubig at masira ang device. Maiging tanggalin na ang device kung hindi mapipigilan ang pagtaas ng tubig. \n";

            numDays.setText("Day " + String.valueOf(days + 1));
            int nitrogenInterval = (int) (vegSafeAWD) / 3;

            if(days == 0) {
                soilStatus.setText("Maglagay ng Nitrogen, Phosphorus, at Potassium");
            } else if ((days+1) > (vegSafeAWD)) {
                soilStatus.setText("");
            } else if(((days+1) % nitrogenInterval) == 0) {
                soilStatus.setText("Maglagay ng Nitrogen");
            } else if (((days+1) % nitrogenInterval) != 0) {
                soilStatus.setText("Maghintay ng " + String.valueOf( nitrogenInterval - ((days+1) % nitrogenInterval) ) + " days bago maglagay ng Nitrogen");
            } else {
                soilStatus.setText("");
            }
            days++;
            if (days < vegStandingWater) {
                stageText.setText("Vegatative Growth Stage");
                phaseText.setText("STANDING WATER");
                waterStatus.setText(standingWater);
            } else if (days < vegSafeAWD) {
                stageText.setText("Vegatative Growth Stage");
                phaseText.setText("SAFE AWD");
                waterStatus.setText(safeAWD);

            } else if (days < repStandingWater) {
                stageText.setText("Reproductive Stage");
                phaseText.setText("STANDING WATER");
                waterStatus.setText(standingWater);

            } else if (days< repSafeAWD) {
                stageText.setText("Reproductive Stage");
                phaseText.setText("SAFE AWD");
                waterStatus.setText(safeAWD);

            } else if (days< repStandingWater1) {
                stageText.setText("Reproductive Stage");
                phaseText.setText("STANDING WATER");
                waterStatus.setText(standingWater);

            } else if (days< ripStandingWater) {
                stageText.setText("Ripening Stage");
                phaseText.setText("STANDING WATER");
                waterStatus.setText(standingWater);

            } else if (days< ripSafeAWD) {
                stageText.setText("Ripening Stage");
                phaseText.setText("SAFE AWD");
                waterStatus.setText(safeAWD);

            } else if (days < ripTerminalDrainage) {
                stageText.setText("Ripening Stage");
                phaseText.setText("TERMINAL DRAINAGE");
                waterStatus.setText(terminalDrainage);
            } else {
                stageText.setText("HARVESTING STAGE");
                phaseText.setText("");
                waterStatus.setText("Maaari mo na i-cancel ang pag-track.");
            }
        }

        return view;
    }
    private void populateProfiles() {
        Menu menu = navigationView.getMenu();
        menu.clear();
        for (int i = 0; i < profiles.size(); i++) {
            String profile = profiles.get(i);

            MenuItem profileItem = menu.add(Menu.NONE, i, Menu.NONE, profile);
            profileItem.setActionView(R.layout.nav_menu_item);

            View actionView = profileItem.getActionView();
//            TextView profileName = actionView.findViewById(R.id.profile_name);
            ImageButton deleteButton = actionView.findViewById(R.id.delete_button);

//            profileName.setText(profile);

            if(!profile.equals("Prototype 1") && !profile.equals("Add Prototype")) {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProfile(profile);
                        HomeFragment newHomeFragment = new HomeFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, newHomeFragment).commit();
                    }
                });
            }

        }
        menu.add(Menu.NONE, profiles.size(), Menu.NONE, "Add Prototype");
    }

    private void deleteProfile(String profile) {
        profiles.remove(profile);
        saveProfiles();
        populateProfiles();
        sharedPreferences.edit().putString(KEY_ACTIVE_PROFILE, "default_profile").apply();
    }

    private void saveProfiles() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray(profiles);
        editor.putString(KEY_PROFILES, jsonArray.toString());
        editor.apply();
    }

    private void loadProfiles() {
        String json = sharedPreferences.getString(KEY_PROFILES, null);
        if (json != null) {
            profiles.clear();
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    profiles.add(jsonArray.getString(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            profiles.add("Prototype 1");
        }
    }

}