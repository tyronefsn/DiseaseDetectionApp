package com.example.diseasedetectionapp;

import static com.example.diseasedetectionapp.MainActivity.KEY_ACTIVE_PROFILE;
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

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {
    TextView numDays;
    TextView stageText;
    TextView phaseText;
    TextView waterStatus;
    TextView soilStatus;
    TextView vegStandingWaterText;
    TextView vegSafeAWDText;
    TextView repStandingWaterText;
    TextView repSafeAWDText;
    TextView repStandingWater1Text;
    TextView ripStandingWaterText;
    TextView ripSafeAWDText;
    TextView ripTerminalDrainageText;
    TextView harvestingText;
    Button cancelButton;
    SharedPreferences sharedPreferences;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
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
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        numDays = view.findViewById(R.id.numDays);
        stageText = view.findViewById(R.id.stageText);
        phaseText = view.findViewById(R.id.phaseText);
        waterStatus = view.findViewById(R.id.waterStatus);
        soilStatus = view.findViewById(R.id.soilStatus);
        sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        vegStandingWaterText = view.findViewById(R.id.vegStandingWater);
        vegSafeAWDText = view.findViewById(R.id.vegSafeAWD);
        repStandingWaterText = view.findViewById(R.id.repStandingWater);
        repSafeAWDText = view.findViewById(R.id.repSafeAWD);
        repStandingWater1Text = view.findViewById(R.id.repStandingWater1);
        ripStandingWaterText = view.findViewById(R.id.ripStandingWater);
        ripSafeAWDText = view.findViewById(R.id.ripSafeAWD);
        ripTerminalDrainageText = view.findViewById(R.id.ripTerminalDrainage);
        harvestingText = view.findViewById(R.id.harvesting);
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
        int repStandingWater = sharedPreferences.getInt(KEY_REPSTANDINGWATER, 0) + vegSafeAWD;
        int repSafeAWD = sharedPreferences.getInt(KEY_REPASWD, 0) + repStandingWater;
        int repStandingWater1 = sharedPreferences.getInt(KEY_REPSTANDINGWATER1, 0) + repSafeAWD;
        int ripStandingWater = sharedPreferences.getInt(KEY_RIPSTANDINGWATER, 0) + repStandingWater1;
        int ripSafeAWD = sharedPreferences.getInt(KEY_RIPASWD, 0) + ripStandingWater;
        int ripTerminalDrainage = sharedPreferences.getInt(KEY_RIPTERMINALDRAINAGE, 0) + ripSafeAWD;
        // set text
        vegStandingWaterText.setText(String.valueOf(vegStandingWater));
        vegSafeAWDText.setText(String.valueOf(vegSafeAWD - vegStandingWater));
        repStandingWaterText.setText(String.valueOf(repStandingWater));
        repSafeAWDText.setText(String.valueOf(repSafeAWD - repStandingWater));
        repStandingWater1Text.setText(String.valueOf(repStandingWater1 - repSafeAWD));
        ripStandingWaterText.setText(String.valueOf(ripStandingWater - repStandingWater1));
        ripSafeAWDText.setText(String.valueOf(ripSafeAWD - ripStandingWater));
        ripTerminalDrainageText.setText(String.valueOf(ripTerminalDrainage - ripSafeAWD));

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
            vegStandingWaterText.setTextColor(Color.GREEN);
            waterStatus.setText(standingWater);
        } else if (days < vegSafeAWD) {
            stageText.setText("Vegatative Growth Stage");
            phaseText.setText("SAFE AWD");
            waterStatus.setText(safeAWD);
            vegSafeAWDText.setTextColor(Color.GREEN);

        } else if (days < repStandingWater) {
            stageText.setText("Reproductive Stage");
            phaseText.setText("STANDING WATER");
            waterStatus.setText(standingWater);

            repStandingWaterText.setTextColor(Color.GREEN);
        } else if (days< repSafeAWD) {
            stageText.setText("Reproductive Stage");
            phaseText.setText("SAFE AWD");
            waterStatus.setText(safeAWD);

            repSafeAWDText.setTextColor(Color.GREEN);
        } else if (days< repStandingWater1) {
            stageText.setText("Reproductive Stage");
            phaseText.setText("STANDING WATER");
            waterStatus.setText(standingWater);
            repStandingWater1Text.setTextColor(Color.GREEN);

        } else if (days< ripStandingWater) {
            stageText.setText("Ripening Stage");
            phaseText.setText("STANDING WATER");
            waterStatus.setText(standingWater);
            ripStandingWaterText.setTextColor(Color.GREEN);

        } else if (days< ripSafeAWD) {
            stageText.setText("Ripening Stage");
            phaseText.setText("SAFE AWD");
            waterStatus.setText(safeAWD);
            ripSafeAWDText.setTextColor(Color.GREEN);

        } else if (days < ripTerminalDrainage) {
            stageText.setText("Ripening Stage");
            phaseText.setText("TERMINAL DRAINAGE");
            waterStatus.setText(terminalDrainage);
            ripTerminalDrainageText.setTextColor(Color.GREEN);
        } else {
            stageText.setText("HARVESTING STAGE");
            phaseText.setText("");
            waterStatus.setText("Maaari mo na i-cancel ang pag-track.");
            harvestingText.setTextColor(Color.GREEN);
        }

        cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activeProfile = sharedPreferences.getString(KEY_ACTIVE_PROFILE, null);
                String profiles = sharedPreferences.getString(KEY_PROFILES, null);
                sharedPreferences.edit().clear().apply();
                sharedPreferences.edit().putString(KEY_ACTIVE_PROFILE, activeProfile).apply();

                sharedPreferences.edit().putString(KEY_PROFILES, profiles).apply();
                NotificationHelper.sendNotification(container.getContext(), "AWD Monitoring Cancelled", "Na-cancel ang AWD monitoring.");
                SetFragment setFragment = new SetFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, setFragment)
                        .commit();
            }
        });
        return view;
    }
}