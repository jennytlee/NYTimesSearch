package com.example.jennytlee.nytimessearcher.dialogs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.jennytlee.nytimessearcher.R;
import com.example.jennytlee.nytimessearcher.models.SearchFilters;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by jennytlee on 6/23/16.
 */
public class SearchFilterDialogFragment extends DialogFragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    EditText etDate;
    Button btnSave;
    SearchFilters allFilters;
    String urlDate;
    String newsdesk;
    String spnOrder;

    public interface OnFilterSearchListener {
        void onUpdateFilters(SearchFilters filters);
    }

    public static SearchFilterDialogFragment newInstance(SearchFilters filters) {
        SearchFilterDialogFragment frag = new SearchFilterDialogFragment();
        // Store this filters object inside a bundle to be accessed later
        Bundle args = new Bundle();
        args.putParcelable("filters", filters);
        // or putParcelable(...) and then later getParcelable(...)
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_filter, container, false);
        getDialog().setTitle("Search Filter");

        allFilters = new SearchFilters();
        etDate = (EditText) rootView.findViewById(R.id.etDate);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        urlDate = "";
        spnOrder = "Oldest";
        newsdesk = "";

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Store the filters to a member variable
        allFilters = (SearchFilters) getArguments().getParcelable("filters");

        // ... any other view lookups here...
        // Get access to the button
        btnSave = (Button) view.findViewById(R.id.btnSave);
        // 2. Attach a callback when the button is pressed
        btnSave.setOnClickListener(this);
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(this, 300);
        newFragment.show(getChildFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // Get the beginDate here from the calendar parsed to correct format
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatView = new SimpleDateFormat("MM/dd/yyyy");

        urlDate = format.format(c.getTime());
        etDate.setText(formatView.format(c.getTime()));

        allFilters.setBeginDate(urlDate);

    }

    public String setFilters(View v) {


        String listFilters = "";
        int checkBoxIds[] = new int[]{R.id.checkbox_Business, R.id.checkbox_Entertainment, R.id.checkbox_food,
                R.id.checkbox_health, R.id.checkbox_opinion, R.id.checkbox_politics, R.id.checkbox_science,
                R.id.checkbox_sports, R.id.checkbox_style, R.id.checkbox_Tech, R.id.checkbox_travel};

        CheckBox[] boxList = new CheckBox[checkBoxIds.length];

        for (int i = 0; i < 11; i++) {
            boxList[i] = (CheckBox) getView().findViewById(checkBoxIds[i]);
        }

        for (int i = 0; i < 11; i++) {
            if (boxList[i].isChecked()) {
                listFilters += boxList[i].getText().toString() + "%20";
            }
        }

        if(listFilters.length() > 0) {
            listFilters = listFilters.substring(0, listFilters.length()-3);
        }

        return listFilters;

    }

    @Override
    public void onClick(View v) {

        allFilters.setNewsDesks(setFilters(v));
        Spinner spn = (Spinner) getView().findViewById(R.id.spinner);
        allFilters.setSort(spn.getSelectedItem().toString());

        // Return filters back to activity through the implemented listener
        OnFilterSearchListener listener = (OnFilterSearchListener) getActivity();
        listener.onUpdateFilters(allFilters);
        // Close the dialog to return back to the parent activity
        dismiss();
    }


}
