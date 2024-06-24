package com.example.diseasedetectionapp;

import static android.content.Context.MODE_PRIVATE;

import static com.example.diseasedetectionapp.MainActivity.KEY_API_RESULT;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataFragment extends Fragment implements GetJsonDataCallback {
    private TextView waterLevelText;
    private TextView nitrogenText;
    private TextView phosphorusText;
    private TextView potassiumText;
    private TextView dateText;
    SharedPreferences sharedPreferences;
    private final Timer timer = new Timer();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataFragment newInstance(String param1, String param2) {
        DataFragment fragment = new DataFragment();
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
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        sharedPreferences = getActivity().getSharedPreferences("DD_APP", MODE_PRIVATE);
        waterLevelText = view.findViewById(R.id.waterStatus);
        nitrogenText = view.findViewById(R.id.nitrogenStatus);
        phosphorusText = view.findViewById(R.id.phosphorusStatus);
        potassiumText = view.findViewById(R.id.potassiumStatus);
        dateText = view.findViewById(R.id.curDate);

        String api_results = sharedPreferences.getString(KEY_API_RESULT, "");
        // get individual data from results
        String[] api_results_arr = api_results.split("\n");
        String[] dates = new String[api_results_arr.length];
        String[] timeArr = new String[api_results_arr.length];
        String[] waterStatus = new String[api_results_arr.length];
        String[] nitrogenStatus = new String[api_results_arr.length];
        String[] phosphorusStatus = new String[api_results_arr.length];
        String[] potassiumStatus = new String[api_results_arr.length];
        String[] json = new String[api_results_arr.length];
        JSONObject[] jsonObjects = new JSONObject[api_results_arr.length];
        int arr_size = api_results_arr.length;

        for(int i = 0; i < arr_size-1; i++) {
          String[] data = api_results_arr[i].split(";");
          System.out.println(data.length);
          dates[i] = data[0];
          timeArr[i] = data[1];
          try {
              JSONObject tempJson = new JSONObject(data[2]);
              waterStatus[i] = tempJson.getString("v2");
              nitrogenStatus[i] = tempJson.getString("v3");
              phosphorusStatus[i] = tempJson.getString("v4");
              potassiumStatus[i] = tempJson.getString("v5");
          } catch (JSONException e) {
              throw new RuntimeException(e);
          }

        };

        waterLevelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something
                // display data on data list fragment
                DataListFragment dataListFragment = DataListFragment.newInstance("Water Status Monitoring", "Status",  dates, timeArr, waterStatus);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, dataListFragment).commit();

            }
        });
        nitrogenText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something
                DataListFragment dataListFragment = DataListFragment.newInstance("Nitrogen", "Content (In mg/ha)",  dates, timeArr, nitrogenStatus);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, dataListFragment).commit();
            }
        });
        phosphorusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something
                DataListFragment dataListFragment = DataListFragment.newInstance("Phosphorus", "Content (In mg/ha)",  dates, timeArr, phosphorusStatus);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, dataListFragment).commit();
            }
            });
        potassiumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something
                DataListFragment dataListFragment = DataListFragment.newInstance("Potassium", "Content (In mg/ha)",  dates, timeArr, potassiumStatus);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, dataListFragment).commit();
            }
        });

        callAsynchronousTask(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onJsonDataReceived(JSONObject jsonObject) {
        System.out.println(jsonObject.toString());
        try {
            waterLevelText.setText(jsonObject.getString("v2"));
            nitrogenText.setText(jsonObject.getString("v3") + " mg/ha");
            phosphorusText.setText(jsonObject.getString("v4") + " mg/ha");
            potassiumText.setText(jsonObject.getString("v5") + " mg/ha");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String error) {
        System.out.println(error);
    }

    public void callAsynchronousTask(View view) {
        final Handler handler = new Handler();
        TimerTask doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new GetJsonDataTask(DataFragment.this).execute();
                            Calendar cal = Calendar.getInstance();
                            int day = cal.get(Calendar.DAY_OF_MONTH);
                            int month = cal.get(Calendar.MONTH) + 1;
                            int year = cal.get(Calendar.YEAR);
                            // display current date as "Data Monitoring: Month Day, Year
                            dateText.setText("Data Monitoring: " + month + "/" + day + "/" + year);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                }
            };
        timer.schedule(doAsyncTask, 0, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        timer.cancel();
    }
}