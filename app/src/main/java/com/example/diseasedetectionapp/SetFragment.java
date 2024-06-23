package com.example.diseasedetectionapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetFragment extends Fragment {

    private Button customizeButton;
    private Button submitButton;
    private TextView headerText;
    private boolean onCustomize;
    private EditText[] editTexts = new EditText[8];
    private TextView vegGrowthStage;
    private TextView repStage;
    private TextView ripStage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetFragment newInstance(String param1, String param2) {
        SetFragment fragment = new SetFragment();
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
        View view = inflater.inflate(R.layout.fragment_set, container, false);

        customizeButton = view.findViewById(R.id.customizeButton);
        submitButton = view.findViewById(R.id.submitButton);
        headerText = view.findViewById(R.id.headerText);
        editTexts[0] = view.findViewById(R.id.vegStandingWater);
        editTexts[1] = view.findViewById(R.id.vegSafeAWD);
        editTexts[2] = view.findViewById(R.id.repStandingWater);
        editTexts[3] = view.findViewById(R.id.repSafeAWD);
        editTexts[4] = view.findViewById(R.id.repStandingWater1);
        editTexts[5] = view.findViewById(R.id.ripStandingWater);
        editTexts[6] = view.findViewById(R.id.ripSafeAWD);
        editTexts[7] = view.findViewById(R.id.ripTerminalDrainage);
        vegGrowthStage = view.findViewById(R.id.vegGrowthStage);
        repStage = view.findViewById(R.id.repStage);
        ripStage = view.findViewById(R.id.ripStage);
        // handle customize button
        customizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // toggle customize mode
                onCustomize = !onCustomize;
                customizeButton.setText(onCustomize ? "Recommended" : "Customize");
                headerText.setText(onCustomize ? "Customized AWD Water Management" : "Recommended AWD Water Management");


                for(EditText editText: editTexts) {
                    // disable edit text if customize mode is on
                    editText.setEnabled(onCustomize);
                }
                if(!onCustomize) {
                    editTexts[0].setText("10");
                    editTexts[1].setText("50");
                    editTexts[2].setText("10");
                    editTexts[3].setText("17");
                    editTexts[4].setText("3");
                    editTexts[5].setText("3");
                    editTexts[6].setText("12");
                    editTexts[7].setText("15");

                    int[] res = computeStages(editTexts);

                    vegGrowthStage.setText(String.valueOf(res[0]) + " days");
                    repStage.setText(String.valueOf(res[1]) + " days");
                    ripStage.setText(String.valueOf(res[2]) + " days");
                }

            }
        });

        for(EditText editText: editTexts) {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(editText.getText().toString().isEmpty()) {
                        editText.setText("0");
                    }

                    int[] res = computeStages(editTexts);

                    vegGrowthStage.setText(String.valueOf(res[0]) + " days");
                    repStage.setText(String.valueOf(res[1]) + " days");
                    ripStage.setText(String.valueOf(res[2]) + " days");


                }
            });

        }

        return view;
    }
    private int[] computeStages(EditText[] editTexts) {
        int[] res = {0,0,0};

        for(int i = 0; i < editTexts.length; i++) {
            if(i < 2) {
                res[0] += Integer.parseInt(editTexts[i].getText().toString());
            } else if (i < 4) {
                res[1] += Integer.parseInt(editTexts[i].getText().toString());
            } else {
                res[2] += Integer.parseInt(editTexts[i].getText().toString());
            }
        }

        return res;
    }
}