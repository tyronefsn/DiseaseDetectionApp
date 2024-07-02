package com.example.diseasedetectionapp;

import static com.example.diseasedetectionapp.MainActivity.KEY_ACTIVE_PROFILE;
import static com.example.diseasedetectionapp.MainActivity.KEY_PROFILES;
import static com.example.diseasedetectionapp.MainActivity.SHARED_PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPrototypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPrototypeFragment extends Fragment {
    Button submitButton;
    List<String> profiles= new ArrayList<>();
    Button cancelButton;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddPrototypeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPrototypeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPrototypeFragment newInstance(String param1, String param2) {
        AddPrototypeFragment fragment = new AddPrototypeFragment();
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
        View view =  inflater.inflate(R.layout.fragment_add_prototype, container, false);
        submitButton = view.findViewById(R.id.button2);
        cancelButton = view.findViewById(R.id.button3);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if api key and prototype name is not empty
                EditText prototypeTitle = view.findViewById(R.id.editTextText);
                EditText apiKey = view.findViewById(R.id.editTextText2);

                String pTitle = prototypeTitle.getText().toString();
                String aKey = apiKey.getText().toString();

                if(pTitle.isEmpty() || aKey.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    loadProfiles();
                    profiles.add(pTitle);
                    saveProfiles();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString(KEY_ACTIVE_PROFILE, pTitle).apply();
                    HomeFragment newHomeFragment = new HomeFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, newHomeFragment).commit();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment newHomeFragment = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, newHomeFragment).commit();
            }
        });
        return view;
    }
    private void saveProfiles() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray(profiles);
        editor.putString(KEY_PROFILES, jsonArray.toString());
        editor.apply();
    }
    private void loadProfiles() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
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