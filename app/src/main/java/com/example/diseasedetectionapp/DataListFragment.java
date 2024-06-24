package com.example.diseasedetectionapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_DATA_NAME = "dataName";
    private static final String ARG_TIME = "time";
    private static final String ARG_UNIT_NAME = "unitName";
    private static final String ARG_DATES = "dates";
    private static final String ARG_VALUES = "values";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DataListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dataName Data to tabulate.
     * @param unitName Unit for table.
     * @param dates String of dates.
     * @param values String of values.
     * @return A new instance of fragment DataListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataListFragment newInstance(String dataName, String unitName, String[] dates, String[] time,  String[] values) {
        DataListFragment fragment = new DataListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA_NAME, dataName);
        args.putString(ARG_UNIT_NAME, unitName);
        args.putStringArray(ARG_TIME, time);
        args.putStringArray(ARG_DATES, dates);
        args.putStringArray(ARG_VALUES, values);
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
        return inflater.inflate(R.layout.fragment_data_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        if (getArguments() != null) {
            String dataName = getArguments().getString(ARG_DATA_NAME);
            String unitName = getArguments().getString(ARG_UNIT_NAME);
            String[] dates = getArguments().getStringArray(ARG_DATES);
            String[] times = getArguments().getStringArray(ARG_TIME);
            String[] values = getArguments().getStringArray(ARG_VALUES);

            MaterialToolbar toolbar = view.findViewById(R.id.materialToolbar4);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // go to data fragment
                    Fragment DataFragment = new DataFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, DataFragment).commit();
                }
            });
            toolbar.setTitle(dataName);

            TableLayout tableLayout = view.findViewById(R.id.tableLayout);
            TableRow headerRow = new TableRow(getContext());
            TextView dateHeader = new TextView(getContext());
            TextView textHeader = new TextView(getContext());
            TextView valueHeader = new TextView(getContext());

            dateHeader.setText("Date");
            textHeader.setText("Time");
            valueHeader.setText(unitName);

            dateHeader.setBackgroundResource(R.drawable.table_bg);
            textHeader.setBackgroundResource(R.drawable.table_bg);
            valueHeader.setBackgroundResource(R.drawable.table_bg);

            dateHeader.setPadding(24,24,24,24);
            textHeader.setPadding(24,24,24,24);
            valueHeader.setPadding(24,24,24,24);
            dateHeader.setTextSize(15.0f);
            textHeader.setTextSize(15.0f);
            valueHeader.setTextSize(15.0f);

            headerRow.addView(dateHeader);
            headerRow.addView(textHeader);
            headerRow.addView(valueHeader);
            headerRow.setGravity(Gravity.CENTER_HORIZONTAL);
            tableLayout.addView(headerRow);

            if (dates != null && values != null && dates.length == values.length) {
                for (int i = 0; i < dates.length; i++) {
                    TableRow tableRow = new TableRow(getContext());
                    tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
                    TextView dateTextView = new TextView(getContext());
                    TextView timeTextView = new TextView(getContext());
                    TextView valueTextView = new TextView(getContext());

                    // add margin to text views
                    dateTextView.setPadding(24, 24, 24, 24);
                    timeTextView.setPadding(24, 24, 24, 24);
                    valueTextView.setPadding(24, 24, 24, 24);

                    // add background to each text view
                    dateTextView.setBackgroundResource(R.drawable.table_bg);
                    timeTextView.setBackgroundResource(R.drawable.table_bg);
                    valueTextView.setBackgroundResource(R.drawable.table_bg);

                    dateTextView.setText(dates[i]);
                    timeTextView.setText(times[i]);
                    valueTextView.setText(values[i]);

                    tableRow.addView(dateTextView);
                    tableRow.addView(timeTextView);
                    tableRow.addView(valueTextView);

                    tableLayout.addView(tableRow);
                }
            }
        }
    }
}