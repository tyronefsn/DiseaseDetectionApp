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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        boolean isOngoing = sharedPreferences.getBoolean(KEY_IS_ONGOING, false);
        if(!isOngoing) {
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
                diff = System.currentTimeMillis() - new SimpleDateFormat("dd/MM/yyyy").parse(startDate).getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            int days = (int) diff / (24 * 60 * 60 * 1000);
            int vegStandingWater = sharedPreferences.getInt(KEY_VEGSTANDINGWATER, 0);
            int vegSafeAWD = sharedPreferences.getInt(KEY_VEGAWD, 0) + vegStandingWater;
            int repStandingWater = sharedPreferences.getInt(KEY_REPSTANDINGWATER, 0) + vegSafeAWD;
            int repSafeAWD = sharedPreferences.getInt(KEY_REPASWD, 0) + repStandingWater;
            int repStandingWater1 = sharedPreferences.getInt(KEY_REPSTANDINGWATER1, 0) + repSafeAWD;
            int ripStandingWater = sharedPreferences.getInt(KEY_RIPSTANDINGWATER, 0) + repStandingWater1;
            int ripSafeAWD = sharedPreferences.getInt(KEY_RIPASWD, 0) + ripStandingWater;
            int ripTerminalDrainage = sharedPreferences.getInt(KEY_RIPTERMINALDRAINAGE, 0) + ripSafeAWD;
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
            int nitrogenInterval = (int) (vegStandingWater + vegSafeAWD) / 3;

            if(days == 0) {
                soilStatus.setText("Maglagay ng Nitrogen, Phosphorus, at Potassium");
            } else if (days+1 > (vegStandingWater + vegSafeAWD)) {
                soilStatus.setText("");
            } else if(days+1 % nitrogenInterval == 0) {
                soilStatus.setText("Maglagay ng Nitrogen");
            } else if (days+1 % nitrogenInterval != 0) {
                soilStatus.setText("Maghintay ng " + String.valueOf((days+1 % nitrogenInterval) - nitrogenInterval) + " days bago maglagay ng Nitrogen");
            }
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
            }
        }

        return view;
    }
}